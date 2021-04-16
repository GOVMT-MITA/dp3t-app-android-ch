package ch.admin.bag.dp3t.interoperability;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.dpppt.android.sdk.DP3T;
import org.dpppt.android.sdk.backend.ResponseCallback;
import org.dpppt.android.sdk.internal.logger.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ch.admin.bag.dp3t.R;
import ch.admin.bag.dp3t.home.model.InteroperabilityMode;
import ch.admin.bag.dp3t.networking.models.EUSharingCountryModel;
import ch.admin.bag.dp3t.onboarding.OnboardingActivity;
import ch.admin.bag.dp3t.onboarding.OnboardingInteroperabilityFragment;
import ch.admin.bag.dp3t.storage.SecureStorage;
import ch.admin.bag.dp3t.util.LanguageUtil;

public class InteroperabilityPromptFragment extends Fragment {

    public static InteroperabilityPromptFragment newInstance() {
        return new InteroperabilityPromptFragment();
    }

    public InteroperabilityPromptFragment() {
        super(R.layout.fragment_interoperability_prompt);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button selectionButton = view.findViewById(R.id.onboarding_interop_button);
        Button skipButton = view.findViewById(R.id.onboarding_interop_skip_button);
        Button continueButton = view.findViewById(R.id.onboarding_interop_continue_button);
        View privacyButton = view.findViewById(R.id.onboarding_disclaimer_conditions_button);

        privacyButton.setOnClickListener(v -> {
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.onboarding_disclaimer_legal_button_url)));
            startActivity(browserIntent);
        });

        selectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InteroperabilityActivity.class);
            startActivity(intent);
            skipButton.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
        });

        skipButton.setOnClickListener(v -> getActivity().finish());
        continueButton.setOnClickListener(v -> getActivity().finish());
    }
}
