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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ch.admin.bag.dp3t.R;

public class OnboardingSlidePageAdapter extends FragmentStateAdapter {

    public OnboardingSlidePageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OnboardingLanguageSelectionFragment.newInstance();
            case 1:
                return OnboardingContentFragment.newInstance(
                        R.string.onboarding_prinzip_title,
                        R.string.onboarding_prinzip_heading,
                        R.drawable.img_start,
                        R.string.onboarding_prinzip_text1,
                        R.drawable.ic_encounters,
                        R.string.onboarding_prinzip_text2,
                        R.drawable.ic_message_alert,
                        null,
                        null,
                        null,
                        false, true);
            case 2:
                return OnboardingContentFragment.newInstance(
                        R.string.onboarding_privacy_title,
                        R.string.onboarding_privacy_heading,
                        R.drawable.img_privacy,
                        R.string.onboarding_privacy_text1,
                        R.drawable.ic_key,
                        R.string.onboarding_privacy_text2,
                        R.drawable.ic_lock,
                        R.string.onboarding_privacy_text3,
                        R.drawable.ic_tracking,
                        R.string.onboarding_privacy_link3,
                        true, false);
            case 3:
                return OnboardingContentFragment.newInstance(
                        R.string.onboarding_begegnungen_title,
                        R.string.onboarding_begegnungen_heading,
                        R.drawable.img_bluetooth,
                        R.string.onboarding_begegnungen_text1,
                        R.drawable.ic_encounters,
                        R.string.onboarding_begegnungen_text2,
                        R.drawable.ic_bluetooth,
                        null,
                        null,
                        null,
                        false, false);
            case 4:
                return OnboardingContentFragment.newInstance(
                        R.string.onboarding_meldung_title,
                        R.string.onboarding_meldung_heading,
                        R.drawable.img_report,
                        R.string.onboarding_meldung_text1,
                        R.drawable.ic_message_alert,
                        R.string.onboarding_meldung_text2,
                        R.drawable.ic_home,
                        null,
                        null,
                        null,
                        false, false);
            case 5:
                return OnboardingDisclaimerFragment.newInstance();
            case 6:
                return OnboardingBatteryPermissionFragment.newInstance();
            case 7:
                return OnboardingInteroperabilityFragment.newInstance();
            case 8:
                return OnboardingGaenPermissionFragment.newInstance();
            case 9:
                return OnboardingFinishedFragment.newInstance();
        }
        throw new IllegalArgumentException("There is no fragment for view pager position " + position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

}
