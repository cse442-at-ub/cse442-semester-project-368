package com.example.a368.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.MainActivity;
import com.example.a368.R;
import com.example.a368.User;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/*
Created by: Dave Rodrigues
Activity that allows users to log-in.
 */

public class LoginActivity extends AppCompatActivity {
    private static String HttpUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/login/login.php";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private LoginViewModel loginViewModel;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editUser;
    private TextView userWarning;
    private ImageView ivIcon;
    int register = 0;
    boolean keyboardOpen;

    @Override
    public void onStart() {
        super.onStart();
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        if(pref.getString("User", null) != null) {
            User user = User.getInstance();
            user.setEmail(pref.getString("User", null));
            user.setName(pref.getString("Name", null));
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Customize action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sign in to 368");
        actionBar.setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        ivIcon = findViewById(R.id.ivIcon);
        editEmail = findViewById(R.id.editEmail);

        editPassword = findViewById(R.id.editPassword);
        editUser = findViewById(R.id.editUser);
        userWarning = findViewById(R.id.userWarning);

        final Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        final Button btnRegister = (Button) findViewById(R.id.btnRegister);
        final Button btnCancel = (Button) findViewById(R.id.btnCancel);

        final int height = ivIcon.getLayoutParams().height;

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        keyboardOpen = isOpen;
                        if(isOpen) {
                           if(editUser.getVisibility() == View.VISIBLE) {
                               ivIcon.getLayoutParams().height = 120;
                               ivIcon.requestLayout();
                           }
                        }
                        else {
                            if(editUser.getVisibility() == View.VISIBLE) {
                               ivIcon.getLayoutParams().height = height;
                               ivIcon.requestLayout();
                            }
                        }
                    }
                });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(register == 1) {
                    if(editEmail.getText().toString().length() == 0) {
                        Toast.makeText(LoginActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                    }
                    if(editPassword.getText().toString().length() < 6) {
                        Toast.makeText(LoginActivity.this, "Password is not long enough", Toast.LENGTH_SHORT).show();
                    }
                    if(editUser.getText().toString().length() == 0) {
                        Toast.makeText(LoginActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        signIn(editEmail.getText().toString(), editPassword.getText().toString(), editUser.getText().toString(), 1);
                    }
                    register = 0;
                }
                if(register == 0) {
                    register = 1;
                    ivIcon.requestLayout();
                    btnSignIn.setVisibility(View.GONE);
                    editUser.setVisibility(View.VISIBLE);
                    userWarning.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    editEmail.requestFocus();
                    if(keyboardOpen) {
                        ivIcon.getLayoutParams().height = 120;
                        ivIcon.requestLayout();
                    }
                    else {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editEmail, InputMethodManager.SHOW_IMPLICIT);
                        imm.isActive();
                    }
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register = 0;
                ivIcon.getLayoutParams().height = height;
                btnSignIn.setVisibility(View.VISIBLE);
                editUser.setVisibility(View.GONE);
                userWarning.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                imm.isActive();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editEmail.getText().toString().length() == 0) {
                    Toast.makeText(LoginActivity.this, "Please enter an email address", Toast.LENGTH_LONG).show();
                }
                signIn(editEmail.getText().toString(), editPassword.getText().toString(), "", 0);
            }
        });
    }


    public void signIn(final String email, final String password, final String name, final int register) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        int response = -1;
                        JSONObject json;
                        String name = "";
                        try {
                            json = new JSONObject(ServerResponse);
                            response = (int) json.get("success");
                            if(response == 1) {
                                name = json.get("name").toString();
                            }
                            Toast.makeText(LoginActivity.this, json.get("message").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(response == 1) {
                            Log.d("Name", name);
                            editor.putString("User", email.toString());
                            editor.putString("Name", name);
                            editor.commit();
                            User user = User.getInstance();
                            user.setName(name);
                            user.setEmail(email);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            JSONObject json = new JSONObject(volleyError.toString());
                            Toast.makeText(LoginActivity.this, json.get("message").toString(), Toast.LENGTH_LONG).show();
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
                if(register == 1) {
                    params.put("name", name);
                    params.put("register", "1");
                }

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);

    }


}
