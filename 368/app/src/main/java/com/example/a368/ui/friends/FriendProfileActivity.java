package com.example.a368.ui.friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.Schedule;
import com.example.a368.User;
import com.example.a368.ui.friends.FriendsProfileAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a368.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendProfileActivity extends AppCompatActivity {

    private TextView name, email, instruction;
    private RecyclerView availableTimes;
    private LinearLayoutManager layoutManager;
    private FriendsProfileAdapter adapter;
    private Button removeFriend;
    private List<Schedule> scheduleList;
    private DividerItemDecoration dividerItemDecoration;
    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/fetch_schedule.php";
    private static String url_delete = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend/delete_friend.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        // Customize action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("View Friend's Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        instruction = findViewById(R.id.instruction);
        availableTimes = findViewById(R.id.availableTimes);
        removeFriend = findViewById(R.id.btRemoveFriend);

        name.setText(getIntent().getStringExtra("name"));
        email.setText(getIntent().getStringExtra("email"));
        instruction.setText(getIntent().getStringExtra("name") + "'s Daily Schedule:");
        scheduleList = new ArrayList<>();

        adapter = new FriendsProfileAdapter(this, scheduleList);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(availableTimes.getContext(), layoutManager.getOrientation());
        availableTimes.setLayoutManager(layoutManager);
        availableTimes.addItemDecoration(dividerItemDecoration);
        availableTimes.setAdapter(adapter);

        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(FriendProfileActivity.this);
                confirmBuilder.setTitle("Remove Friend");
                confirmBuilder.setMessage("Are you sure to delete:\n" + getIntent().getStringExtra("name") +
                        " (" + getIntent().getStringExtra("email") + ") ?\n\nYour friend will lose access to your profile as well.");
                confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String ServerResponse) {
                                        // Showing response message:
                                        Toast.makeText(FriendProfileActivity.this, getIntent().getStringExtra("name") +
                                                " is removed from your friend list.", Toast.LENGTH_LONG).show();
                                        delete_both(getIntent().getStringExtra("friend_id"));
                                        setResult(Activity.RESULT_OK);
                                        finish();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        // Showing error message if something goes wrong.
                                        Toast.makeText(FriendProfileActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                // Creating Map String Params.
                                Map<String, String> params = new HashMap<String, String>();
                                // Adding All values to Params.
                                params.put("id", "" + getIntent().getStringExtra("id"));
                                return params;
                            }

                        };
                        // Creating RequestQueue.
                        RequestQueue requestQueue = Volley.newRequestQueue(FriendProfileActivity.this);

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
//                finish();
            }});
    }

    // Go back to Friends Fragment
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                scheduleList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // Get today
                        Date today = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String formatted_today = dateFormat.format(today);


                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("email").equals(getIntent().getStringExtra("email"))) {

                            if (((jsonObject.getString("start_date").equals(formatted_today)) ||
                                    (jsonObject.getString("end_date").equals(formatted_today))) ||
                                    (check_timings(formatted_today, jsonObject.getString("end_date")) &&
                                            check_timings(jsonObject.getString("start_date"), formatted_today))) {

                                Schedule schedule = new Schedule();

                                schedule.setID((jsonObject.getInt("id")));
                                schedule.setName(jsonObject.getString("title"));
                                schedule.setStart_date(jsonObject.getString("start_date"));
                                schedule.setStart_date(date_parsing(schedule.getStart_date().substring(0, 5),
                                        "MM/dd", "MMM d"));

                                schedule.setStart_time(jsonObject.getString("start_time"));
                                schedule.setStart_time(date_parsing(schedule.getStart_time(),
                                        "hh:mm aaa", "HH:mm"));

                                schedule.setEnd_date(jsonObject.getString("end_date"));
                                schedule.setEnd_date(date_parsing(schedule.getEnd_date().substring(0, 5),
                                        "MM/dd", "MMM d"));

                                schedule.setEnd_time(jsonObject.getString("end_time"));
                                schedule.setEnd_time(date_parsing(schedule.getEnd_time(),
                                        "hh:mm aaa", "HH:mm"));

                                schedule.setDescription(jsonObject.getString("description"));

                                // empty description disregarded
                                if (schedule.getDescription().equals("Exception: No Text Applied")) {
                                    schedule.setDescription("");
                                }
                                scheduleList.add(schedule);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                // Sort by start time
                sortArray(scheduleList);

                // Re-format time
                for (int i = 0; i < scheduleList.size(); i++) {
                    // start time
                    scheduleList.get(i).setStart_time(date_parsing(scheduleList.get(i).getStart_time(),
                            "HH:mm", "hh:mm aaa"));

                    // end time
                    scheduleList.get(i).setEnd_time(date_parsing(scheduleList.get(i).getEnd_time(),
                            "HH:mm", "hh:mm aaa"));
                }

                adapter.notifyDataSetChanged();
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
    private void sortArray(List<Schedule> arrayList) {
        if (arrayList != null) {
            Collections.sort(arrayList, new Comparator<Schedule>() {
                @Override
                public int compare(Schedule o1, Schedule o2) {
                    if (o1.getStart_date().equals(o2.getStart_date())) {
                        return o1.getStart_time().compareTo(o2.getStart_time());
                    } else {
                        return o1.getStart_date().compareTo(o2.getStart_date());
                    }
                }
            });
        }
    }

    // Converts date format
    public String date_parsing (String old_date, String input_format, String output_format) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(input_format);
        SimpleDateFormat outputFormat = new SimpleDateFormat(output_format);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(old_date);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    // Check for passed date
    private boolean check_timings(String time, String endtime) {
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e){
            e.printStackTrace();
        }

        return false;
    }

    private void delete_both (String friend_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        // Showing response message coming from server.
//                        Toast.makeText(FriendProfileActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Showing error message if something goes wrong.
                        Toast.makeText(FriendProfileActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();
                // Adding All values to Params.
                params.put("id", friend_id);
                return params;
            }

        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(FriendProfileActivity.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }
}
