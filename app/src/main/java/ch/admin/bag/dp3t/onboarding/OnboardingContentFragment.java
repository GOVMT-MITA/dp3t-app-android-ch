/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.admin.bag.dp3t.onboarding;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import ch.admin.bag.dp3t.R;

public class OnboardingContentFragment extends Fragment {

    private static final String ARG_RES_TITLE = "RES_TITLE";
    private static final String ARG_RES_SUBTITLE = "RES_SUBTITLE";
    private static final String ARG_RES_DESCR_ICON_1 = "ARG_RES_DESCR_ICON_1";
    private static final String ARG_RES_DESCRIPTION_1 = "RES_DESCRIPTION_1";
    private static final String ARG_RES_DESCR_ICON_2 = "ARG_RES_DESCR_ICON_2";
    private static final String ARG_RES_DESCRIPTION_2 = "RES_DESCRIPTION_2";
    private static final String ARG_RES_DESCR_ICON_3 = "ARG_RES_DESCR_ICON_3";
    private static final String ARG_RES_DESCRIPTION_3 = "RES_DESCRIPTION_3";
    private static final String ARG_RES_LINK_3 = "RES_LINK_3";
    private static final String ARG_RES_ILLUSTRATION = "RES_ILLUSTRATION";
    private static final String ARG_STYLE_GREEN = "ARG_STYLE_GREEN";
    private static final String ARG_STYLE_YELLOW = "ARG_STYLE_YELLOW";

    public static OnboardingContentFragment newInstance(@StringRes int title, @StringRes int subtitle,
                                                        @DrawableRes int illustration, @StringRes int description1, @DrawableRes int iconDescription1,
                                                        @StringRes int description2, @DrawableRes int iconDescription2, @StringRes Integer description3, @DrawableRes Integer iconDescription3, @StringRes Integer link3, boolean greenStyle, boolean yellowStyle) {
        Bundle args = new Bundle();
        args.putInt(ARG_RES_TITLE, title);
        args.putInt(ARG_RES_SUBTITLE, subtitle);
        args.putInt(ARG_RES_ILLUSTRATION, illustration);
        args.putInt(ARG_RES_DESCR_ICON_1, iconDescription1);
        args.putInt(ARG_RES_DESCRIPTION_1, description1);
        args.putInt(ARG_RES_DESCR_ICON_2, iconDescription2);
        args.putInt(ARG_RES_DESCRIPTION_2, description2);
        if (iconDescription3 != null) {
            args.putInt(ARG_RES_DESCR_ICON_3, iconDescription3);
        }
        if (description3 != null) {
            args.putInt(ARG_RES_DESCRIPTION_3, description3);
        }
        if (link3 != null) {
            args.putInt(ARG_RES_LINK_3, link3);
        }
        args.putBoolean(ARG_STYLE_GREEN, greenStyle);
        args.putBoolean(ARG_STYLE_YELLOW, yellowStyle);
        OnboardingContentFragment fragment = new OnboardingContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public OnboardingContentFragment() {
        super(R.layout.fragment_onboarding_content);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = requireArguments();

        ((TextView) view.findViewById(R.id.onboarding_title)).setText(args.getInt(ARG_RES_TITLE));

        TextView subtitle = view.findViewById(R.id.onboarding_subtitle);
        subtitle.setText(args.getInt(ARG_RES_SUBTITLE));

        ((ImageView) view.findViewById(R.id.onboarding_illustration)).setImageResource(args.getInt(ARG_RES_ILLUSTRATION));

        ImageView icon1 = view.findViewById(R.id.onboarding_description_1_icon);
        icon1.setImageResource(args.getInt(ARG_RES_DESCR_ICON_1));
        ((TextView) view.findViewById(R.id.onboarding_description_1)).setText(args.getInt(ARG_RES_DESCRIPTION_1));

        ImageView icon2 = view.findViewById(R.id.onboarding_description_2_icon);
        icon2.setImageResource(args.getInt(ARG_RES_DESCR_ICON_2));
        ((TextView) view.findViewById(R.id.onboarding_description_2)).setText(args.getInt(ARG_RES_DESCRIPTION_2));

        ImageView icon3 = view.findViewById(R.id.onboarding_description_3_icon);
        icon3.setImageResource(args.getInt(ARG_RES_DESCR_ICON_3));

        if (args.containsKey(ARG_RES_DESCRIPTION_3)) {
            TextView txtvw3 = (TextView) view.findViewById(R.id.onboarding_description_3);
            txtvw3.setText(args.getInt(ARG_RES_DESCRIPTION_3));
            if (args.containsKey(ARG_RES_LINK_3)) {
                txtvw3.setOnClickListener(v -> openLink(R.string.onboarding_privacy_link3));
            }
        } else {
            view.findViewById(R.id.onboarding_description_3_icon).setVisibility(View.GONE);
            view.findViewById(R.id.onboarding_description_3).setVisibility(View.GONE);
        }

        Button continueButton = view.findViewById(R.id.onboarding_continue_button);
        continueButton.setOnClickListener(v -> {
            ((OnboardingActivity) getActivity()).continueToNextPage();
        });

        if (args.getBoolean(ARG_STYLE_YELLOW)) {
            subtitle.setTextColor(getResources().getColor(R.color.yellow_main, null));
            icon1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow_main, null)));
            icon2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow_main, null)));
            icon3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow_main, null)));
            continueButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow_main, null)));
            if (args.containsKey(ARG_RES_LINK_3)) {
                ((TextView) view.findViewById(R.id.onboarding_description_3)).setTextColor(getResources().getColor(R.color.yellow_main, null));
            }
        } else if (args.getBoolean(ARG_STYLE_GREEN)) {
            subtitle.setTextColor(getResources().getColor(R.color.green_main, null));
            icon1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_main, null)));
            icon2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_main, null)));
            icon3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_main, null)));
            if (args.containsKey(ARG_RES_LINK_3)) {
                ((TextView) view.findViewById(R.id.onboarding_description_3)).setTextColor(getResources().getColor(R.color.green_main, null));
            }
            continueButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_main, null)));
        }
    }

	private void openLink(@StringRes int stringRes) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(stringRes)));
		startActivity(browserIntent);
	}
}
