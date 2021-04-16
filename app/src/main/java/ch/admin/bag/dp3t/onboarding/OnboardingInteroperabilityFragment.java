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
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import ch.admin.bag.dp3t.R;
import ch.admin.bag.dp3t.home.model.InteroperabilityMode;
import ch.admin.bag.dp3t.interoperability.InteroperabilityActivity;
import ch.admin.bag.dp3t.storage.SecureStorage;

public class OnboardingInteroperabilityFragment extends Fragment {
    public static OnboardingInteroperabilityFragment newInstance() {
        return new OnboardingInteroperabilityFragment();
    }

    public OnboardingInteroperabilityFragment() {
        super(R.layout.fragment_onboarding_interoperability);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SecureStorage secureStorage = SecureStorage.getInstance(getContext());
        boolean euSharingEnabled = secureStorage.getConfigInteroperabilityPossible();

        Button selectionButton = view.findViewById(R.id.onboarding_interop_button);
        Button skipButton = view.findViewById(R.id.onboarding_interop_skip_button);
        Button continueButton = view.findViewById(R.id.onboarding_interop_continue_button);
        LinearLayout interopDisabledLayout = view.findViewById(R.id.onboarding_interop_disabled);

        //Set default interoperability to disabled
        secureStorage.setConfigInteroperabilityMode(InteroperabilityMode.LEGACY);
        secureStorage.setRequirePromptDisplay(false);

        if (euSharingEnabled) {
            selectionButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), InteroperabilityActivity.class);
                startActivity(intent);
                skipButton.setVisibility(View.GONE);
                continueButton.setVisibility(View.VISIBLE);
            });
        } else {
            selectionButton.setVisibility(View.GONE);
            interopDisabledLayout.setVisibility(View.VISIBLE);
        }

        skipButton.setOnClickListener(v -> ((OnboardingActivity) requireActivity()).continueToNextPage());
        continueButton.setOnClickListener(v -> ((OnboardingActivity) requireActivity()).continueToNextPage());
    }
}