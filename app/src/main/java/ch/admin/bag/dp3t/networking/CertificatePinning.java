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

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.CertificatePinner;

public class CertificatePinning {

    private static final CertificatePinner CERTIFICATE_PINNER_LIVE = new CertificatePinner.Builder()
            .add("dummy-auth-url.com", "dummy-certificate")
            .add("dummy-config-url.com", "dummy-certificate")
            .add("dummy-ws-url.com", "dummy-certificate")
            .build();

    private static final CertificatePinner CERTIFICATE_PINNER_DISABLED = new CertificatePinner.Builder().build();

    private static final String PREF_NAME_DEBUG = "debug";
    private static final String PREF_KEY_CERT_PINNING_ENABLED = "certificate_pinning_enabled";

    private static boolean isEnabled = true;

    public static CertificatePinner getCertificatePinner() {
        return isEnabled ? CERTIFICATE_PINNER_LIVE : CERTIFICATE_PINNER_DISABLED;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled(boolean enabled, Context context) {
        isEnabled = enabled;
        getDebugPrefs(context).edit().putBoolean(PREF_KEY_CERT_PINNING_ENABLED, enabled).apply();
    }

    public static void initDebug(Context context) {
        isEnabled = getDebugPrefs(context).getBoolean(PREF_KEY_CERT_PINNING_ENABLED, isEnabled);
    }

    private static SharedPreferences getDebugPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME_DEBUG, Context.MODE_PRIVATE);
    }
}
