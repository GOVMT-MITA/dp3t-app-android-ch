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
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import ch.admin.bag.dp3t.R;
import ch.admin.bag.dp3t.inform.InformActivity;
import ch.admin.bag.dp3t.networking.models.WhatToDoPositiveTestTextsModel;
import ch.admin.bag.dp3t.storage.SecureStorage;
import ch.admin.bag.dp3t.util.LanguageUtil;
import ch.admin.bag.dp3t.util.UrlUtil;

public class WtdPositiveTestFragment extends Fragment {

    public static WtdPositiveTestFragment newInstance() {
        return new WtdPositiveTestFragment();
    }

    public WtdPositiveTestFragment() {
        super(R.layout.fragment_what_to_do_test);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = view.findViewById(R.id.wtd_test_toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        fillContentFromConfigServer(view);

        view.findViewById(R.id.wtd_inform_button).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InformActivity.class);
            startActivity(intent);
        });

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
}
