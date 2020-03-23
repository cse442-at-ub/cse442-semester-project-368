package com.example.a368.ui.today;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.example.a368.Schedule;
import com.example.a368.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/*
Created by: Jiwon Choi
Modified by: Dave Rodrigues
This has a daily schedule adding floating action button and displays schedule with their relevant remaining time.
 */
public class TodayFragment extends Fragment implements ScheduleAdapter.onClickListener {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/fetch_schedule.php";
    private TodayViewModel todayViewModel;
    private RecyclerView sList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Schedule> scheduleList;
    private RecyclerView.Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        todayViewModel =
                ViewModelProviders.of(this).get(TodayViewModel.class);
        View root = inflater.inflate(R.layout.today_schedule, container, false);

        TextView textView_current_time = (TextView)root.findViewById(R.id.current_time_textview);
        TextView textView_today = (TextView)root.findViewById(R.id.today_textview);
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fabAddAppointment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddToSchedule.class));
            }
        });

        Date curr_time = Calendar.getInstance().getTime();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aaa");
        String formatted_time = timeFormat.format(curr_time);
        if (formatted_time.indexOf("0") == 0) {
            formatted_time = formatted_time.replaceFirst("0", "");
        }
        textView_current_time.setText(formatted_time);

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        String formatted_today = dateFormat.format(today);
        textView_today.setText(formatted_today);

        sList = (RecyclerView)root.findViewById(R.id.schedule_list);

        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(getContext(), scheduleList, this);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(sList.getContext(), linearLayoutManager.getOrientation());

        sList.setHasFixedSize(true);
        sList.setLayoutManager(linearLayoutManager);
        sList.addItemDecoration(dividerItemDecoration);
        sList.setAdapter(adapter);

        return root;
    }

    // Updates view schedule list
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    // Fetch JSON data to display schedule
    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
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

                        // Get current time
                        Date now = Calendar.getInstance().getTime();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String formatted_time = timeFormat.format(now);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("email").equals(User.getInstance().getEmail()) &&
                                jsonObject.getString("start_date").equals(formatted_today)) {

                            if ((jsonObject.getString("end_date").equals(formatted_today) &&
                                    check_timings(formatted_time, jsonObject.getString("end_time"))) ||
                                (!(jsonObject.getString("end_date").equals(formatted_today)))) {

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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    // On click schedule item
    @Override
    public void onClickSchedule(int position) {
        final int pos = position;
        String[] colors = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int menuID) {
                // Edit / Delete Action goes here (menuID)
                // DB ID = scheduleID param

                // Temporary Printing for now
                Toast.makeText(getContext(), "MySQL ID: " + scheduleList.get(pos).getID() +
                        " | Menu ID:" + String.valueOf(menuID) , Toast.LENGTH_LONG).show();
                switch (menuID) {
                    case 0:
                        Intent intent = new Intent(getContext(), AddToSchedule.class);
                        intent.putExtra("id", ""+scheduleList.get(pos).getID());
                        intent.putExtra("name", scheduleList.get(pos).getName());
                        intent.putExtra("start_time", scheduleList.get(pos).getStart_time());
                        intent.putExtra("start_date", scheduleList.get(pos).getStart_date());
                        intent.putExtra("end_time", scheduleList.get(pos).getEnd_time());
                        intent.putExtra("end_date", scheduleList.get(pos).getEnd_date());
                        intent.putExtra("description", scheduleList.get(pos).getDescription());
                        startActivity(intent);

                        break;
                    case 1:
                        //code for delete
                        final String[] confirm = {"Yes", "No"};
                        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getContext());
                        confirmBuilder.setTitle("Delete Schedule");
                        confirmBuilder.setMessage("Are you sure you want to delete?");
                        confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/delete-schedule.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String ServerResponse) {
                                                getData();
                                                // Showing response message coming from server.
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
                                        // Adding All values to Params.
                                        params.put("id", ""+scheduleList.get(pos).getID());
                                        return params;
                                    }

                                };
                                // Creating RequestQueue.
                                RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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
                    break;
                }
            }
        });
        builder.show();
    }

    // Converts date format from MM/dd into MMM d
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

    private void sortArray(List<Schedule> arrayList) {
        if (arrayList != null) {
            Collections.sort(arrayList, new Comparator<Schedule>() {
                @Override
                public int compare(Schedule o1, Schedule o2) {
                    return o1.getStart_time().compareTo(o2.getStart_time()); }
            });
        }
    }

    // Check for passed time
    private boolean check_timings(String time, String endtime) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String time2 = date_parsing(endtime, "hh:mm aaa", "HH:mm");

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(time2);

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
}