/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.admin.bag.dp3t.whattodo;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import ch.admin.bag.dp3t.R;
import ch.admin.bag.dp3t.home.model.InteroperabilityMode;
import ch.admin.bag.dp3t.inform.InformActivity;
import ch.admin.bag.dp3t.interoperability.InteroperabilityActivity;
import ch.admin.bag.dp3t.networking.models.WhatToDoPositiveTestTextsModel;
import ch.admin.bag.dp3t.storage.SecureStorage;
import ch.admin.bag.dp3t.util.LanguageUtil;
import ch.admin.bag.dp3t.util.UrlUtil;

public class WtdPositiveTestFragment extends Fragment {

    private SecureStorage secureStorage;

    public static WtdPositiveTestFragment newInstance() {
        return new WtdPositiveTestFragment();
    }

    public WtdPositiveTestFragment() {
        super(R.layout.fragment_what_to_do_test);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        secureStorage = SecureStorage.getInstance(getContext());
        Toolbar toolbar = view.findViewById(R.id.wtd_test_toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        fillContentFromConfigServer(view);

        view.findViewById(R.id.wtd_inform_button).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InformActivity.class);
            startActivity(intent);
        });

        setupInteroperability(view);

        view.findViewById(R.id.wtd_inform_faq_button).setOnClickListener(v -> {
            UrlUtil.openUrl(getContext(), getString(R.string.faq_button_url));
        });
    }

    private void fillContentFromConfigServer(View view) {
        Context context = view.getContext();
        SecureStorage secureStorage = SecureStorage.getInstance(context);
        WhatToDoPositiveTestTextsModel textModel =
                secureStorage.getWhatToDoPositiveTestTexts(LanguageUtil.getAppLocale(context));

        if (textModel != null) {
            ((TextView) view.findViewById(R.id.wtd_inform_box_supertitle)).setText(textModel.getEnterCovidcodeBoxSupertitle());
            ((TextView) view.findViewById(R.id.wtd_inform_box_title)).setText(textModel.getEnterCovidcodeBoxTitle());
            ((TextView) view.findViewById(R.id.wtd_inform_box_text)).setText(textModel.getEnterCovidcodeBoxText());
            ((TextView) view.findViewById(R.id.wtd_inform_button)).setText(textModel.getEnterCovidcodeBoxButtonTitle());

            if (textModel.getInfoBox() != null) {
                view.findViewById(R.id.wtd_inform_infobox).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.wtd_inform_infobox_title)).setText(textModel.getInfoBox().getTitle());
                ((TextView) view.findViewById(R.id.wtd_inform_infobox_msg)).setText(textModel.getInfoBox().getMsg());

                if (textModel.getInfoBox().getUrl() != null && textModel.getInfoBox().getUrlTitle() != null) {
                    ((TextView) view.findViewById(R.id.wtd_inform_infobox_link_text)).setText(textModel.getInfoBox().getUrlTitle());
                    view.findViewById(R.id.wtd_inform_infobox_link_layout).setOnClickListener(v -> {
                        UrlUtil.openUrl(v.getContext(), textModel.getInfoBox().getUrl());
                    });
                    view.findViewById(R.id.wtd_inform_infobox_link_layout).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.wtd_inform_infobox_link_layout).setVisibility(View.GONE);
                }
            } else {
                view.findViewById(R.id.wtd_inform_infobox).setVisibility(View.GONE);
            }
        }
    }

    private void setupInteroperability(View view) {
        updateInteroperabilityState(view, secureStorage.getConfigInteroperabilityMode(), secureStorage.getConfigInteroperabilityPossible());

        //Handle changes to interoperability availability
        secureStorage.getConfigInteroperabilityPossibleLiveData().observe(getViewLifecycleOwner(), interoperabilityPossible -> {
            updateInteroperabilityState(view, secureStorage.getConfigInteroperabilityMode(), secureStorage.getConfigInteroperabilityPossible());
        });

        //Handle changes to interoperability mode
        secureStorage.getConfigInteroperabilityModeLiveData().observe(getViewLifecycleOwner(), interoperabilityMode -> {
            updateInteroperabilityState(view, interoperabilityMode, secureStorage.getConfigInteroperabilityPossible());
        });

        view.findViewById(R.id.wtd_inform_interop_button).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InteroperabilityActivity.class);
            startActivity(intent);
        });
    }

    private void updateInteroperabilityState(View view, InteroperabilityMode interoperabilityMode, boolean isInteropPossible) {
        TextView interopTitle = view.findViewById(R.id.wtd_inform_interop_title);
        TextView interopText = view.findViewById(R.id.wtd_inform_interop_text);
        ImageView interopIcon = view.findViewById(R.id.wtd_inform_interop_icon);
        LinearLayout interopButton = view.findViewById(R.id.wtd_inform_interop_button);

        if (!isInteropPossible) {
            interopIcon.setImageResource(R.drawable.ic_warning);
            interopTitle.setText(R.string.interop_mode_unavailable_title);
            interopText.setText(R.string.interop_mode_unavailable_text);
            interopButton.setVisibility(View.GONE);
        } else {
            interopIcon.setImageResource(InteroperabilityMode.getIcon(interoperabilityMode));
            interopTitle.setText(InteroperabilityMode.getTitle(interoperabilityMode));
            interopText.setText(InteroperabilityMode.getText(interoperabilityMode));
        }
    }
}
