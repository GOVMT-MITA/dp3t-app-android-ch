/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.admin.bag.dp3t.networking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.dpppt.android.sdk.DP3T;
import org.dpppt.android.sdk.backend.ResponseCallback;
import org.dpppt.android.sdk.backend.SignatureException;
import org.dpppt.android.sdk.internal.logger.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import ch.admin.bag.dp3t.BuildConfig;
import ch.admin.bag.dp3t.MainActivity;
import ch.admin.bag.dp3t.R;
import ch.admin.bag.dp3t.home.model.InteroperabilityMode;
import ch.admin.bag.dp3t.networking.errors.ResponseError;
import ch.admin.bag.dp3t.networking.models.ConfigResponseModel;
import ch.admin.bag.dp3t.networking.models.EUSharingCountryModel;
import ch.admin.bag.dp3t.networking.models.InfoBoxModel;
import ch.admin.bag.dp3t.storage.SecureStorage;
import ch.admin.bag.dp3t.util.LanguageUtil;
import ch.admin.bag.dp3t.util.NotificationUtil;

public class ConfigWorker extends Worker {

    private static final int REPEAT_INTERVAL_CONFIG_HOURS = 6;
    private static final long MAX_AGE_OF_CONFIG_FOR_RELOAD_AT_APP_START = 12 * 60 * 60 * 1000l;//12h

    private static final String TAG = "ConfigWorker";
    private static final String WORK_TAG = "ch.admin.bag.dp3t.ConfigWorker";

    public static void scheduleConfigWorkerIfOutdated(Context context) {
        SecureStorage secureStorage = SecureStorage.getInstance(context);
        if (secureStorage.getLastConfigLoadSuccess() < System.currentTimeMillis() - MAX_AGE_OF_CONFIG_FOR_RELOAD_AT_APP_START ||
                secureStorage.getLastConfigLoadSuccessAppVersion() != BuildConfig.VERSION_CODE ||
                secureStorage.getLastConfigLoadSuccessSdkInt() != Build.VERSION.SDK_INT) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest periodicWorkRequest =
                    new PeriodicWorkRequest.Builder(ConfigWorker.class, REPEAT_INTERVAL_CONFIG_HOURS, TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .build();

            WorkManager workManager = WorkManager.getInstance(context);
            workManager.enqueueUniquePeriodicWork(WORK_TAG, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
        }
    }

    public ConfigWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Logger.d(TAG, "started");
        DP3T.addWorkerStartedToHistory(getApplicationContext(), "config");
        try {
            loadConfig(getApplicationContext());
        } catch (IOException | ResponseError | SignatureException e) {
            Logger.e(TAG, "failed", e);
            return Result.retry();
        }

        Logger.d(TAG, "finished with success");
        return Result.success();
    }

    private static void loadConfig(Context context) throws IOException, ResponseError, SignatureException {
        ConfigRepository configRepository = new ConfigRepository(context);
        ConfigResponseModel config = configRepository.getConfig(context);

        DP3T.setMatchingParameters(context,
                config.getSdkConfig().getLowerThreshold(), config.getSdkConfig().getHigherThreshold(),
                config.getSdkConfig().getFactorLow(), config.getSdkConfig().getFactorHigh(),
                config.getSdkConfig().getTriggerThreshold());

        SecureStorage secureStorage = SecureStorage.getInstance(context);
        secureStorage.setDoForceUpdate(config.getDoForceUpdate());

        secureStorage.setWhatToDoPositiveTestTexts(config.getWhatToDoPositiveTestTexts());

        InfoBoxModel info = config.getInfoBox(LanguageUtil.getAppLocale(context));
        if (info != null) {
            if (info.getInfoId() == null || !info.getInfoId().equals(secureStorage.getInfoboxId())) {
                //Only update the infobox if it has a new ID.
                secureStorage.setInfoboxTitle(info.getTitle());
                secureStorage.setInfoboxText(info.getMsg());
                secureStorage.setInfoboxLinkTitle(info.getUrlTitle());
                secureStorage.setInfoboxLinkUrl(info.getUrl());
                secureStorage.setInfoboxId(info.getInfoId());
                secureStorage.setInfoboxDismissible(info.getDismissible());
                secureStorage.setHasInfobox(true);
            }
        } else {
            secureStorage.setHasInfobox(false);
        }

        boolean forceUpdate = secureStorage.getDoForceUpdate();
        if (forceUpdate) {
            if (!secureStorage.getForceUpdateLiveData().hasObservers()) {
                showUpdateNotification(context);
            }
        } else {
            cancelUpdateNotification(context);
        }

        //Handle interoperability
        int existingConfigVersion = secureStorage.getConfigVersion();
        int newConfigVersion = config.getConfigVersion();

        if (existingConfigVersion != newConfigVersion) {
            secureStorage.setConfigVersion(newConfigVersion);

            boolean existingInteropPossible = secureStorage.getConfigInteroperabilityPossible();
            boolean configInteropPossible = config.getEUSharingEnabled();

            secureStorage.setConfigInteroperabilityPossible(configInteropPossible);
            DP3T.updateInteropPossible(context, configInteropPossible, new ResponseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean response) {
                    Logger.i("DP3T Interface", "updated interop possible setting");
                }

                @Override
                public void onError(Throwable throwable) {
                    Logger.e("DP3T Interface", "updateInteropPossible", throwable);
                    throwable.printStackTrace();
                }
            });

            if(secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.COUNTRIES || secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.COUNTRIES_UPDATE_PENDING
                    || secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.EU){
                if(existingInteropPossible && !configInteropPossible){
                    showInteropUnavailableServiceNotification(context);
                }else if(!existingInteropPossible && configInteropPossible){
                    showInteropAvailableServiceNotification(context);
                }
            }

            //Update countries, prompt warning if user is using the countries mode
            EUSharingCountryModel[] existingCountries = secureStorage.getConfigInteroperabilityCountries();
            if(!Arrays.equals(existingCountries, config.getEUSharingCountries())) {
                secureStorage.setConfigInteroperabilityCountries(config.getEUSharingCountries());
                if(secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.COUNTRIES){
                    secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.COUNTRIES_UPDATE_PENDING);
                }

                if(secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.COUNTRIES || secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.COUNTRIES_UPDATE_PENDING
                        || secureStorage.getConfigInteroperabilityMode() == InteroperabilityMode.EU){
                    showInteropCountriesUpdateNotification(context);
                }

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("preferences_covid_interop_countries_selection").apply();
                DP3T.clearSelectedCountries(context, new ResponseCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Logger.i("DP3T Interface", "cleared selected countries: " + response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.e("DP3T Interface", "clearSelectedCountries", throwable);
                        throwable.printStackTrace();
                    }
                });

                //todo determine how to handle SDK country updating, probably by clearing existing countries to be safe
                HashSet<String> euCountries = new HashSet<>();
                for (EUSharingCountryModel country : config.getEUSharingCountries())
                {
                    euCountries.add(country.getCountryCode());
                }

                DP3T.updateEuropeanCountries(context, euCountries, new ResponseCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Logger.i("DP3T Interface", "changed interop eu countries: " + response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.e("DP3T Interface", "updateEuropeanCountries", throwable);
                        throwable.printStackTrace();
                    }
                });
            }
        }
    }

    private static void showUpdateNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context);
        }

        String packageName = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.force_update_title))
                        .setContentText(context.getString(R.string.force_update_text))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_begegnungen)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationUtil.NOTIFICATION_ID_UPDATE, notification);
    }

    private static void cancelUpdateNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationUtil.NOTIFICATION_ID_UPDATE);
    }

    private static void showInteropAvailableServiceNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.interop_mode_title))
                        .setContentText(context.getString(R.string.interop_mode_available_text))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_begegnungen)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationUtil.NOTIFICATION_ID_INTEROP_AVAILABLE, notification);
    }

    private static void showInteropUnavailableServiceNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.interop_mode_title))
                .setContentText(context.getString(R.string.interop_mode_unavailable_text))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_begegnungen)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationUtil.NOTIFICATION_ID_INTEROP_UNAVAILABLE, notification);
    }

    private static void showInteropCountriesUpdateNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotificationChannel(context);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NotificationUtil.NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.interop_mode_title))
                        .setContentText(context.getString(R.string.interop_mode_countries_update_pending_text))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_begegnungen)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationUtil.NOTIFICATION_ID_INTEROP_COUNTRIES_CHANGED, notification);
    }
}
