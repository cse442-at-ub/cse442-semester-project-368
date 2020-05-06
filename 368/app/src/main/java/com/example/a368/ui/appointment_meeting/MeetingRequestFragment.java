package com.example.a368.ui.appointment_meeting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.a368.ui.friends.Friend;
import com.example.a368.ui.login.LoginActivity;
import com.example.a368.ui.settings.SettingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MeetingRequestFragment extends Fragment {

    LinearLayoutManager layoutManager;
    LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private MeetingRequestAdapter mAdapter;
    private ArrayList<MeetingRequest> reqList;
    private ArrayList<String> pendingList;
    private ArrayList<Integer> idList;
    private ArrayList<String> statusList;
    private ArrayList<String> emailList;
    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/meeting/fetch_meeting_request.php";
    private static String updateUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/update_status_request.php";
    private static String insertUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/insert_schedule.php";
    private static String deleteUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/meeting/delete_meeting_request.php";

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.meeting_request_layout, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.meetingRequestRecycler);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        getData();
        linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        reqList = new ArrayList<>();

        mAdapter = new MeetingRequestAdapter (reqList, getContext(), new MeetingRequestAdapter.MeetingRequestAdapterListener() {
            @Override
            public void statusOnClick(View v, int position) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                idList = new ArrayList<>();
                statusList = new ArrayList<>();
                emailList = new ArrayList<>();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String ServerResponse) {

                                // Hiding the progress dialog after all task complete.
                                progressDialog.dismiss();
                                UpdateRequest(position, reqList.get(position));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                // Hiding the progress dialog after all task complete.
                                progressDialog.dismiss();

                                // Showing error message if something goes wrong.
                                Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        // Creating Map String Params.
                        Map<String, String> params = new HashMap<String, String>();
                        String s = ""+reqList.get(position).getId();
                        // Adding All values to Params.
                        params.put("id", s);
                        params.put("status", "pending");

                        return params;
                    }
                };
                // Creating RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                // Adding the StringRequest object into requestQueue.
                requestQueue.add(stringRequest);

            }

            @Override
            public void actionOnClick(View v, int position) {
                if (reqList.get(position).getStatus().equals("Accepted") ||
                        reqList.get(position).getStatus().equals("Rejected") ||
                        reqList.get(position).getStatus().equals("Pending")) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String ServerResponse) {
                                    getData();
                                    // Showing response message:
                                    if (reqList.get(position).getStatus().equals("Pending")) {
                                        int people = count_participant(reqList.get(position).getParticipants());

                                        // TODO: delete both
                                        search_pair(reqList.get(position));

                                        Toast.makeText(MeetingRequestFragment.this.getContext(),
                                                "Canceled meeting request sent to " +
                                                        reqList.get(position).getReceiver_name(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MeetingRequestFragment.this.getContext(),
                                                "The selected status deleted from the request list.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    // Showing error message if something goes wrong.
                                    Toast.makeText(MeetingRequestFragment.this.getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // Creating Map String Params.
                            Map<String, String> params = new HashMap<String, String>();
                            // Adding All values to Params.
                            params.put("id", "" + reqList.get(position).getId());
                            return params;
                        }

                    };
                    // Creating RequestQueue.
                    RequestQueue requestQueue = Volley.newRequestQueue(MeetingRequestFragment.this.getContext());

                    // Adding the StringRequest object into requestQueue.
                    requestQueue.add(stringRequest);

                }

            }

        });
//        mAdapter = new MeetingRequestAdapter(reqList, this.getContext(), new MeetingRequestAdapter.onClickListener() {
//            @Override
//            public void onClickRequest(int position) {
//                AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getContext());
//                confirmBuilder.setTitle("Meeting Request");
//                confirmBuilder.setMessage("Do you want to accept this meeting?");
//                confirmBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//                        progressDialog.setMessage("Loading...");
//                        progressDialog.show();
//
//                        idList = new ArrayList<>();
//                        statusList = new ArrayList<>();
//                        emailList = new ArrayList<>();
//
//                        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl,
//                                new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String ServerResponse) {
//
//                                        // Hiding the progress dialog after all task complete.
//                                        progressDialog.dismiss();
//                                        UpdateRequest(position, reqList.get(position));
//                                    }
//                                },
//                                new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError volleyError) {
//
//                                        // Hiding the progress dialog after all task complete.
//                                        progressDialog.dismiss();
//
//                                        // Showing error message if something goes wrong.
//                                        Toast.makeText(getContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                }) {
//                            @Override
//                            protected Map<String, String> getParams() {
//
//                                // Creating Map String Params.
//                                Map<String, String> params = new HashMap<String, String>();
//                                String s = ""+reqList.get(position).getId();
//                                // Adding All values to Params.
//                                params.put("id", s);
//                                params.put("status", "pending");
//
//                                return params;
//                            }
//                        };
//                        // Creating RequestQueue.
//                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//                        // Adding the StringRequest object into requestQueue.
//                        requestQueue.add(stringRequest);
//
//
//                    }
//                });
//                confirmBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//                        progressDialog.setMessage("Loading...");
//                        progressDialog.show();
//
//                        idList = new ArrayList<>();
//                        statusList = new ArrayList<>();
//                        emailList = new ArrayList<>();
//
//                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//                            @RequiresApi(api = Build.VERSION_CODES.O)
//                            @Override
//                            public void onResponse(JSONArray response) {
//
//                                for (int i = 0; i < response.length(); i++) {
//                                    try {
//                                        JSONObject jsonObject = response.getJSONObject(i);
////                                        req.setTitle(jsonObject.getString("title"));
//                                        // check to display the logged-in user's schedule only && display today's schedule only
//                                        if (jsonObject.getString("participant").equals(reqList.get(position).getParticipants())) {
//                                            idList.add(Integer.parseInt(jsonObject.getString("id")));
//                                            statusList.add(jsonObject.getString("status"));
//                                            if(!emailList.contains(jsonObject.getString("receiver_email"))) {
//                                                emailList.add(jsonObject.getString("receiver_email"));
//                                            }
//                                        }
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                        progressDialog.dismiss();
//                                    }
//                                }
//
//                                for(Integer id : idList) {
//                                    DeleteRequest(id);
//                                }
//                                reqList.remove(position);
//                                mAdapter.notifyDataSetChanged();
//                                progressDialog.dismiss();
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.e("Volley", error.toString());
//                                progressDialog.dismiss();
//                            }
//                        });
//                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//                        requestQueue.add(jsonArrayRequest);
//
//                    }
//                });
//                confirmBuilder.show();
//            }
//        });
        recyclerView.setAdapter(mAdapter);

        return root;
    }
    public void UpdateRequest(int position, MeetingRequest req) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        if (jsonObject.getString("participant").equals(reqList.get(position).getParticipants())) {
                            idList.add(Integer.parseInt(jsonObject.getString("id")));
                            statusList.add(jsonObject.getString("status"));
                            if(!emailList.contains(jsonObject.getString("receiver_email"))) {
                                emailList.add(jsonObject.getString("receiver_email"));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                boolean isComplete = true;
                for(String s : statusList) {
                    if(s.equals("Confirm")) {
                        isComplete = false;
                    }
                }
                if(isComplete) {
                    Toast.makeText(getContext(), "isComplete", Toast.LENGTH_SHORT).show();
                    //add to all schedules
                    for(String email : emailList) {
                        UpdateSchedule(email, req);
                    }

                    for(Integer id : idList) {
//                        DeleteRequest(id);
                    }
                    reqList.remove(position);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);

    }

    public void UpdateSchedule(String email, MeetingRequest req) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, insertUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
//                        Toast.makeText(getContext(), ServerResponse, Toast.LENGTH_LONG).show();
                        Toast.makeText(getContext(), "Meeting request accepted.", Toast.LENGTH_LONG).show();
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
                params.put("email", email);
                params.put("title", req.getTitle());
                params.put("start_date", req.getStart_date());
                params.put("start_time", req.getStart_time());
                params.put("end_date", req.getEnd_date());
                params.put("end_time", req.getEnd_time());
                params.put("description", req.getDescription());

                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }
    public void DeleteRequest(Integer id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        mAdapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), ServerResponse, Toast.LENGTH_LONG).show();
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
                String s = "" +id;
                // Adding All values to Params.
                params.put("id", s);

                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }


    public static MeetingRequestFragment newInstance(String text) {

        MeetingRequestFragment f = new MeetingRequestFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        pendingList = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                reqList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("sender_email").equals(User.getInstance().getEmail())) {

                            MeetingRequest req = new MeetingRequest();

                            req.setId(Integer.parseInt(jsonObject.getString("id")));
                            req.setSender_name(jsonObject.getString("sender_name"));
                            req.setSender_email(jsonObject.getString("sender_email"));
                            req.setReceiver_name(jsonObject.getString("receiver_name"));
                            req.setReceiver_email(jsonObject.getString("receiver_email"));
                            req.setStatus(jsonObject.getString("status"));
                            req.setTitle(jsonObject.getString("title"));
                            req.setStart_date(jsonObject.getString("start_date"));
                            req.setStart_time(jsonObject.getString("start_time"));
                            req.setEnd_date(jsonObject.getString("end_date"));
                            req.setEnd_time(jsonObject.getString("end_time"));
                            if (jsonObject.getString("description").equals("Exception: No Text Applied")) {
                                req.setDescription("");
                            } else {
                                req.setDescription(jsonObject.getString("description"));
                            }
                            req.setParticipants(jsonObject.getString("participant"));
                            reqList.add(req);

//                            if(!pendingList.contains(req.getParticipants()) && req.getStatus().equals("pending")) {
//                                pendingList.add(req.getParticipants());
//                                req.setTitle(req.getTitle() +" (Pending)");
//                                reqList.add(req);
//                            }
//                            else if(req.getStatus().equals("confirm")) {
//                                reqList.add(req);
//                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                // Sort by alphabetical order

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void search_pair (MeetingRequest request) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        if (jsonObject.getString("sender_email").equals(request.getReceiver_email()) &&
                                jsonObject.getString("receiver_email").equals(request.getSender_email()) &&
                                jsonObject.getString("status").equals("Confirm") &&
                                jsonObject.getString("start_date").equals(request.getStart_date()) &&
                                jsonObject.getString("start_time").equals(request.getStart_time()) &&
                                jsonObject.getString("end_date").equals(request.getEnd_date()) &&
                                jsonObject.getString("end_time").equals(request.getEnd_time()) &&
                                jsonObject.getString("title").equals(request.getTitle()) &&
                                jsonObject.getString("description").equals(request.getDescription())) {

//                            delete_request(jsonObject.getInt("id"));

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

    // num of participants except for you
    private int count_participant (String participants) {
        int number = 0;

        for (int i = 0; i < participants.length(); i++) {
            if (participants.charAt(i) == ',') {
                number++;
            }
        }

        return number;
    }

}
