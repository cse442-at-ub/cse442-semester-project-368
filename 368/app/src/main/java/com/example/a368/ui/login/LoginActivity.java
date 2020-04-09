package com.example.a368.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private LoginViewModel loginViewModel;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);


        final Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        final Button btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editEmail.getText().toString().length() == 0) {
                    Toast.makeText(LoginActivity.this, "Please enter an email address", Toast.LENGTH_LONG).show();
                }
                if(editPassword.getText().toString().length() < 6) {
                    Toast.makeText(LoginActivity.this, "Password is not long enough", Toast.LENGTH_LONG).show();
                }
                else {
                    signIn(editEmail.getText().toString(), editPassword.getText().toString(), 1);
                }
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editEmail.getText().toString().length() == 0) {
                    Toast.makeText(LoginActivity.this, "Please enter an email address", Toast.LENGTH_LONG).show();
                }
                signIn(editEmail.getText().toString(), editPassword.getText().toString(), 0);
            }
        });
    }

    public void signIn(final String email, final String password, final int register) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        int response = -1;
                        JSONObject json;
                        try {
                            json = new JSONObject(ServerResponse);
                            response = (int) json.get("success");
                            Toast.makeText(LoginActivity.this, json.get("message").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(response == 1) {
                            User user = User.getInstance();
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
                    params.put("register", "1");
                }

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);

    }


}
