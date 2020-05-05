package com.example.a368.ui.friends;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Map;

public class FriendRequestFragment extends Fragment {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend_request/fetch_friend_request.php";
    private static String url_delete_request = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend_request/delete_friend_request.php";
    private static String url_add_friend = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend/insert_friend.php";
    private static String url_friend_request = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend_request/insert_friend_request.php";
    private RecyclerView rList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private ArrayList<FriendRequest> requestList;
    private FriendRequestAdapter requestAdapter;

    private ArrayList<FriendRequest> pair_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friend_request, container, false);
        pair_id = new ArrayList<>();

        rList = (RecyclerView)root.findViewById(R.id.friend_request_recycler);
        requestList = new ArrayList<>();
        requestAdapter = new FriendRequestAdapter(getContext(), requestList, new FriendRequestAdapter.FriendRequestAdapterListener() {
            @Override
            public void statusOnClick(View v, int position) {
                /***
                 * In case where we need to add friend to the both sides
                 * Status: Confirm
                 */

                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                // Add to the friend list
                add_friend(requestList.get(position).getSender_email(),
                        requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email());
                add_friend(requestList.get(position).getReceiver_email(),
                        requestList.get(position).getSender_name(), requestList.get(position).getSender_email());
                getData();

                // Update request status
                delete_request(requestList.get(position).getID());
                request_update(requestList.get(position).getSender_name(), requestList.get(position).getSender_email(),
                        requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email(),
                        "Accepted");
                search_pair(requestList.get(position).getReceiver_email(),
                        requestList.get(position).getSender_email());
                request_update(requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email(),
                        requestList.get(position).getSender_name(), requestList.get(position).getSender_email(), "Accepted");

                progressDialog.dismiss();

                // show message
                Toast.makeText(getContext(), requestList.get(position).getReceiver_name() +
                        " is added to your friend list", Toast.LENGTH_LONG).show();

            }

            @Override
            public void actionOnClick(View v, int position) {
                /***
                 * In case where friend request gets deleted from request DB
                 * Status: Accepted; Rejected; Pending
                 * Action: Delete; Delete; Cancel
                ***/
                if (requestList.get(position).getStatus().equals("Accepted") ||
                        requestList.get(position).getStatus().equals("Rejected") ||
                        requestList.get(position).getStatus().equals("Pending")) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_request,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String ServerResponse) {
                                    getData();
                                    // Showing response message:
                                    if (requestList.get(position).getStatus().equals("Pending")) {
                                        // TODO: delete both
                                        Toast.makeText(FriendRequestFragment.this.getContext(),
                                                "Canceled friend request", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(FriendRequestFragment.this.getContext(),
                                                "The selected request status deleted from the request list.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    // Showing error message if something goes wrong.
                                    Toast.makeText(FriendRequestFragment.this.getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // Creating Map String Params.
                            Map<String, String> params = new HashMap<String, String>();
                            // Adding All values to Params.
                            params.put("id", "" + requestList.get(position).getID());
                            return params;
                        }

                    };
                    // Creating RequestQueue.
                    RequestQueue requestQueue = Volley.newRequestQueue(FriendRequestFragment.this.getContext());

                    // Adding the StringRequest object into requestQueue.
                    requestQueue.add(stringRequest);

                }
                /***
                 * In case where we still need to show in the request DB (request rejected)
                 * Status: Confirm
                 * Action: Reject
                 */
                else {

                }

            }
        });

        linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(rList.getContext(), linearLayoutManager.getOrientation());

        rList.setLayoutManager(linearLayoutManager);
        rList.addItemDecoration(dividerItemDecoration);
        rList.setAdapter(requestAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    // Fetch JSON data to display existing friends list
    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                requestList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("sender_email").equals(User.getInstance().getEmail())) {

                            FriendRequest request = new FriendRequest();

                            request.setID((jsonObject.getInt("id")));
                            request.setSender_name(User.getInstance().getName());
                            request.setSender_email(User.getInstance().getEmail());
                            request.setReceiver_name(jsonObject.getString("receiver_name"));
                            request.setReceiver_email(jsonObject.getString("receiver_email"));
                            request.setStatus(jsonObject.getString("status"));
                            requestList.add(request);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                // Sort by alphabetical order
                // TODO

                requestAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void add_friend(String email_a, String name_b, String email_b) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_add_friend,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        // update adapter list
                        getData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Showing error message if something goes wrong.
                        Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

    private void delete_request (int id) {
        // delete old status data
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_request,
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

                params.put("id", ""+id);
                return params;
            }

        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

    private void request_update(String sender_name, String sender_email,
                                String receiver_name, String receiver_email, String status) {
        // Insert updated data
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_friend_request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        // Showing response message:
//                        Toast.makeText(AddFriendActivity.this, "Friend request sent to " +
//                                userList.get(position).getName() + ".", Toast.LENGTH_LONG).show();

                        // update adapter list
                        getData();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Showing error message if something goes wrong.
                        Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("sender_name", sender_name);
                params.put("sender_email", sender_email);
                params.put("receiver_name", receiver_name);
                params.put("receiver_email", receiver_email);
                params.put("status", status);

                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

    private void search_pair (String sender_email, String receiver_email) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        if (jsonObject.getString("sender_email").equals(sender_email) &&
                                jsonObject.getString("receiver_email").equals(receiver_email)) {

                            delete_request(jsonObject.getInt("id"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    public static FriendRequestFragment newInstance(String text) {
        FriendRequestFragment f = new FriendRequestFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

}
