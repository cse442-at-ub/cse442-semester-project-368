package com.example.a368.ui.friends;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
        requestAdapter = new FriendRequestAdapter(getContext(), requestList, new FriendRequestAdapter.onClickListener() {
            @Override
            public void onClickRequest(int position) {
                if (requestList.get(position).getStatus().equals("Pending")) {
                    AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getContext());
                    confirmBuilder.setTitle("Pending Friend Request");
                    confirmBuilder.setMessage("Do you want to undo this friend request sent to " +
                            requestList.get(position).getReceiver_name() + " (" +
                            requestList.get(position).getReceiver_email() + ")" +  "?");
                    confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_request,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String ServerResponse) {
                                            getData();
                                            // Showing response message:
                                            search_pair(requestList.get(position).getReceiver_email(),
                                                    requestList.get(position).getSender_email());

                                            Toast.makeText(FriendRequestFragment.this.getContext(),
                                                    "Canceled friend request sent to " +
                                                            requestList.get(position).getReceiver_name(), Toast.LENGTH_LONG).show();
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
                    });
                    confirmBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }

                    });
                    confirmBuilder.show();

                } else if (requestList.get(position).getStatus().equals("Confirm")) {
                    AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getContext());
                    confirmBuilder.setTitle("Accept Friend Request");
                    confirmBuilder.setMessage("Do you want to accept the friend request from " +
                            requestList.get(position).getReceiver_name() + " (" + requestList.get(position).getReceiver_email() + ")"
                            + "?");
                    confirmBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Add to the friend list
                            add_friend(requestList.get(position).getSender_email(),
                                    requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email());
                            add_friend(requestList.get(position).getReceiver_email(),
                                    requestList.get(position).getSender_name(), requestList.get(position).getSender_email());

                            // Update request status
                            delete_request(requestList.get(position).getID());
//                            request_update(requestList.get(position).getSender_name(), requestList.get(position).getSender_email(),
//                                    requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email(),
//                                    "Accepted");
                            search_pair(requestList.get(position).getReceiver_email(),
                                    requestList.get(position).getSender_email());
//                            request_update(requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email(),
//                                    requestList.get(position).getSender_name(), requestList.get(position).getSender_email(), "Accepted");

                            getData();

                            // show message
                            Toast.makeText(getContext(), requestList.get(position).getReceiver_name() +
                                    " is added to your friend list", Toast.LENGTH_LONG).show();
                        }
                    });
                    confirmBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete_request(requestList.get(position).getID());
                            request_update(requestList.get(position).getSender_name(), requestList.get(position).getSender_email(),
                                    requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email(),
                                    "Rejected");
                            search_pair(requestList.get(position).getReceiver_email(),
                                    requestList.get(position).getSender_email());
                            request_update(requestList.get(position).getReceiver_name(), requestList.get(position).getReceiver_email(),
                                    requestList.get(position).getSender_name(), requestList.get(position).getSender_email(), "Rejected");

                            getData();

                            Toast.makeText(getContext(), "Rejected a friend request from "
                                    + requestList.get(position).getReceiver_name() + ".", Toast.LENGTH_LONG).show();
                        }

                    });
                    confirmBuilder.show();

                } else if (requestList.get(position).getStatus().equals("Rejected")) {
                    AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getContext());
                    confirmBuilder.setTitle("Rejected Friend Request");
                    confirmBuilder.setMessage("Do you want to delete this request from your feed?");
                    confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_request,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String ServerResponse) {
                                            getData();
                                            // Showing response message:
                                            Toast.makeText(FriendRequestFragment.this.getContext(),
                                                        "The selected status deleted from the friend request feed.",
                                                        Toast.LENGTH_LONG).show();
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
                    });
                    confirmBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }

                    });
                    confirmBuilder.show();

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
                        getData();
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
