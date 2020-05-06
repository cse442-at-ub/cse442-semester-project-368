package com.example.a368.ui.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import androidx.preference.DialogPreference;

import com.example.a368.R;

public class PasswordDialogPreference extends DialogPreference {

    private EditText oldPassword;
    private EditText newPassword;
    private Button btChangePw;
    public PasswordDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_password);
        setNegativeButtonText(null);
        setPositiveButtonText(null);
    }

}
