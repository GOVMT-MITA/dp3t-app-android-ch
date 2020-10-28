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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.admin.bag.dp3t.R;
import ch.admin.bag.dp3t.util.LanguageUtil;

public class OnboardingLanguageSelectionFragment extends Fragment {

    public static OnboardingLanguageSelectionFragment newInstance() {
        return new OnboardingLanguageSelectionFragment();
    }

    public OnboardingLanguageSelectionFragment() {
        super(R.layout.fragment_onboarding_language_selection);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button languageEnglishButton = view.findViewById(R.id.onboarding_language_en_button);
        languageEnglishButton.setOnClickListener(v -> {
            LanguageUtil.setAppLocale(getContext(), LanguageUtil.LANGUAGE_EN);
            ((OnboardingActivity) getActivity()).continueToNextPage();
        });
        Button languageMalteseButton = view.findViewById(R.id.onboarding_language_mt_button);
        languageMalteseButton.setOnClickListener(v -> {
            LanguageUtil.setAppLocale(getContext(), LanguageUtil.LANGUAGE_MT);
            ((OnboardingActivity) getActivity()).continueToNextPage();
        });
    }
}