package com.example.a368.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.PreferenceDialogFragmentCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.MainActivity;
import com.example.a368.R;
import com.example.a368.User;
import com.example.a368.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DialogPrefFragCompat extends PreferenceDialogFragmentCompat {
    private EditText oldPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private Button btChangePw;
    private Button btCancel;
    private static String HttpUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/login/login.php";

    public static DialogPrefFragCompat newInstance(String key) {
        final DialogPrefFragCompat fragment = new DialogPrefFragCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
        }
    }
    @Override
    public void onBindDialogView(View view) {
        oldPassword = view.findViewById(R.id.changePasswordOld);
        newPassword = view.findViewById(R.id.changePasswordNew);
        confirmPassword = view.findViewById(R.id.changePasswordConfirm);
        btChangePw = view.findViewById(R.id.btChangePassword);
        btChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPassword.getText().toString().length() < 6) {
                    Toast.makeText(getContext(), "New password is not long enough.", Toast.LENGTH_LONG).show();
                }
                else if(!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    Toast.makeText(getContext(), "New passwords must match.", Toast.LENGTH_LONG).show();
                }
                else {
                    changePassword(User.getInstance().getEmail(), oldPassword.getText().toString(), newPassword.getText().toString());
                }
            }
        });

        btCancel = view.findViewById(R.id.btPasswordCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public void changePassword(final String email, final String password, final String newPassword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        int response = -1;
                        JSONObject json;
                        try {
                            json = new JSONObject(ServerResponse);
                            response = (int) json.get("success");
                            Toast.makeText(getContext(), json.get("message").toString(), Toast.LENGTH_LONG).show();
                            Log.d("JSON", json.toString());
                            if(response == 1) {
                                dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            JSONObject json = new JSONObject(volleyError.toString());
                            Toast.makeText(getContext(), json.get("message").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("email", email);
                params.put("password", password);
                params.put("newPassword", newPassword);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }
}
