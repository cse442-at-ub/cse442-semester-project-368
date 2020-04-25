package com.example.a368.ui.settings;

import android.os.Bundle;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.a368.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SettingsViewModel settingsViewModel;
    private EditText oldPassword;
    private EditText newPassword;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof PasswordDialogPreference) {
            DialogFragment dialogFragment = DialogPrefFragCompat.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), null);
        } else super.onDisplayPreferenceDialog(preference);

    }
}
