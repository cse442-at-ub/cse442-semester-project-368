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
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        TextView remaining_time = (TextView)root.findViewById(R.id.remaining_time_textview);
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fabAddAppointment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddToSchedule.class));
            }
        });

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        remaining_time.setText(currentDateTimeString);

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

        getData();

        return root;
    }

    // Fetch JSON data to display schedule
    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Date today = Calendar.getInstance().getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String formatted_today = dateFormat.format(today);

                        if (jsonObject.getString("start_date").equals(formatted_today)) {
                            Schedule schedule = new Schedule();

                            schedule.setID((jsonObject.getInt("id")));
                            schedule.setName(jsonObject.getString("title"));
                            schedule.setStart_date(jsonObject.getString("start_date"));
                            schedule.setStart_time(jsonObject.getString("start_time"));
                            schedule.setEnd_date(jsonObject.getString("end_date"));
                            schedule.setEnd_time(jsonObject.getString("end_time"));
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
}