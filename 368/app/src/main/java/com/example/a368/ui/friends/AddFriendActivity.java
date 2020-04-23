package com.example.a368.ui.friends;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.SearchManager;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.example.a368.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity implements FriendSearchAdapter.onClickListener {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/user/fetch_user.php";
    private static String url_friend = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend/fetch_friend.php";
    private static String url_add_friend = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend/insert_friend.php";
    private RecyclerView uList;
    private ArrayList<String> friendList;
    private ArrayList<Friend> userList;
    private FriendSearchAdapter mAdapter;
    private SearchView searchView;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private ProgressDialog progressDialog;

    // Add customized menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // Customize action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Friend");
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(AddFriendActivity.this);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });

        friendList = new ArrayList<>();
        getFriend();
        uList = (RecyclerView) findViewById(R.id.friendsRecyclerView);
        userList = new ArrayList<>();
        mAdapter = new FriendSearchAdapter(this, userList, this);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(uList.getContext(), linearLayoutManager.getOrientation());

        uList.setHasFixedSize(true);
        uList.setLayoutManager(linearLayoutManager);
        uList.addItemDecoration(dividerItemDecoration);
        uList.setAdapter(mAdapter);

    }

    // Go back to Friends Fragment
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    // On click to add friend
    @Override
    public void onClickFriend(int position) {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle("Add Friend");
        confirmBuilder.setMessage("Are you sure to add:\n" + userList.get(position).getName() +
                " (" + userList.get(position).getEmail() + ") ?\n\nYou may be visible to your friend's list.");
        confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Creating string request with post method.
                if(getIntent().hasExtra("id")) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url_add_friend,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String ServerResponse) {
                                    // Showing response message coming from server.
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    // Showing error message if something goes wrong.
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // Creating Map String Params.
                            Map<String, String> params = new HashMap<String, String>();
                            // Adding All values to Params.

                            params.put("id", ""+userList.get(position).getID());
                            return params;
                        }

                    };
                    // Creating RequestQueue.
                    RequestQueue requestQueue = Volley.newRequestQueue(AddFriendActivity.this);

                    // Adding the StringRequest object into requestQueue.
                    requestQueue.add(stringRequest);
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url_add_friend,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String ServerResponse) {

                                // Hiding the progress dialog after all task complete.
                                progressDialog.dismiss();

                                // Showing response message:
                                Toast.makeText(AddFriendActivity.this, userList.get(position).getName() +
                                        " is added to your friend list", Toast.LENGTH_LONG).show();

                                // update adapter list
                                getFriend();
                                getData();
                                add_both(userList.get(position).getEmail(), User.getInstance().getName(), User.getInstance().getEmail());
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                // Hiding the progress dialog after all task complete.
                                progressDialog.dismiss();

                                // Showing error message if something goes wrong.
                                Toast.makeText(AddFriendActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();

                        // Adding All values to Params.
                        params.put("email_a", User.getInstance().getEmail());
                        params.put("name_b", userList.get(position).getName());
                        params.put("email_b", userList.get(position).getEmail());

                        return params;
                    }

                };

                // Creating RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(AddFriendActivity.this);

                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);
            }

        });
        confirmBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        confirmBuilder.show();
    }

    // Updates view schedule list
    @Override
    public void onResume() {
        super.onResume();
        getFriend();
        getData();
    }

    // Fetch JSON data to display existing friend list
    private void getFriend() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url_friend, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                friendList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if (jsonObject.getString("email_a").equals(User.getInstance().getEmail())) {
                            friendList.add(jsonObject.getString("email_b"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    // Fetch JSON data to display registered user list
    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                userList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (!(jsonObject.getString("email").equals(User.getInstance().getEmail())) &&
                            !(friendList.contains(jsonObject.getString("email")))) {

                            Friend friend = new Friend();
                            friend.setName(jsonObject.getString("name"));
                            friend.setEmail(jsonObject.getString("email"));
                            userList.add(friend);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                // Sort by start time
//                sortArray(scheduleList);

                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void add_both(String email_a, String name_b, String email_b) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_add_friend,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // update adapter list
                        getFriend();
                        getData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing error message if something goes wrong.
                        Toast.makeText(AddFriendActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("email_a", email_a);
                params.put("name_b", name_b);
                params.put("email_b", email_b);

                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(AddFriendActivity.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }
}
