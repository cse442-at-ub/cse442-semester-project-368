package com.example.a368.ui.today;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
            @Override
            public void onResponse(JSONArray response) {
                scheduleList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Date today = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String formatted_today = dateFormat.format(today);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("email").equals(User.getInstance().getEmail()) &&
                                jsonObject.getString("start_date").equals(formatted_today)) {
                            Schedule schedule = new Schedule();

                            schedule.setID((jsonObject.getInt("id")));
                            schedule.setName(jsonObject.getString("title"));
                            schedule.setStart_date(jsonObject.getString("start_date"));
                            schedule.setStart_date(date_parsing(schedule.getStart_date().substring(0, 5)));

                            schedule.setStart_time(jsonObject.getString("start_time"));
                            if (schedule.getStart_time().indexOf("0") == 0) {
                                schedule.setStart_time(schedule.getStart_time().replaceFirst("0", " "));
                            }

                            schedule.setEnd_date(jsonObject.getString("end_date"));
                            schedule.setEnd_date(date_parsing(schedule.getEnd_date().substring(0, 5)));

                            schedule.setEnd_time(jsonObject.getString("end_time"));
                            if (schedule.getEnd_time().indexOf("0") == 0) {
                                schedule.setEnd_time(schedule.getEnd_time().replaceFirst("0", " "));
                            }
                            schedule.setDescription(jsonObject.getString("description"));

                            // empty description disregarded
                            if (schedule.getDescription().equals("Exception: No Text Applied")) {
                                schedule.setDescription("");
                            }

                            scheduleList.add(schedule);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
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
            }
        });
        builder.show();
    }

    // Converts date format from MM/dd into MMM d
    public String date_parsing (String old_date) {
        String inputPattern = "MM/dd";
        String outputPattern = "MMM d";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

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
}