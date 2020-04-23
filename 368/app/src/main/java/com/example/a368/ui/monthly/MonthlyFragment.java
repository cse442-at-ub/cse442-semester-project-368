package com.example.a368.ui.monthly;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
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
import com.example.a368.Schedule;
import com.example.a368.ui.monthly.MonthlyAdapter;
import com.example.a368.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by: Jiwon Choi
 * This has the calendar and monthly schedule.
 */

public class MonthlyFragment extends Fragment implements MonthlyAdapter.onClickListener {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/fetch_schedule.php";
    private MonthlyViewModel monthlyViewModel;
    private TextView month;
    private static SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
    protected static Date passDate;

    private RecyclerView sList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Schedule> scheduleList;
    private RecyclerView.Adapter adapter;

    // Calendar Library
    private static CompactCalendarView compactCalendar;
    protected static List<Event> calendarList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        monthlyViewModel =
                ViewModelProviders.of(this).get(MonthlyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        long currentTime = System.currentTimeMillis();
        Date today = new Date(currentTime);
        passDate = today;

        // Add Schedule Floating Button
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fab_add_monthly);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthlyFragment.this.getContext(), AddMonthlySchedule.class);
                intent.putExtra("date", passDate);
                startActivity(intent);
            }
        });

        compactCalendar = (CompactCalendarView) root.findViewById(R.id.calendar_view);
        compactCalendar.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendar.shouldDrawIndicatorsBelowSelectedDays(true);
        month = (TextView) root.findViewById(R.id.calendar_month);
        month.setText(dateFormatMonth.format(compactCalendar.getFirstDayOfCurrentMonth()));

        sList = (RecyclerView)root.findViewById(R.id.monthly_schedule_list);
        scheduleList = new ArrayList<>();
        adapter = new MonthlyAdapter(getContext(), scheduleList, this);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(sList.getContext(), linearLayoutManager.getOrientation());

        sList.setHasFixedSize(true);
        sList.setLayoutManager(linearLayoutManager);
        sList.addItemDecoration(dividerItemDecoration);
        sList.setAdapter(adapter);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                passDate = dateClicked;
                refresh();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                month.setText(dateFormatMonth.format(firstDayOfNewMonth));

                passDate = firstDayOfNewMonth;
                refresh();
            }
        });

        return root;
    }

    // Updates view schedule list
    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {

        List<Date> dates = new ArrayList<Date>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {

                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

                calendarList.clear();
                compactCalendar.removeAllEvents();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if (jsonObject.getString("email").equals(User.getInstance().getEmail())) {
                            String str_date = jsonObject.getString("start_date");
                            String end_date = jsonObject.getString("end_date");

                            if (str_date.equals(end_date)) {
                                Date date = null;
                                try {
                                    date = formatter.parse(str_date);
                                    long milliTime = date.getTime();
                                    calendarList.add(new Event(Color.parseColor("#257a76"), milliTime));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                continue;
                            }

                            Date date = null;
                            try {
                                Date  startDate = (Date)formatter.parse(str_date);
                                Date  endDate = (Date)formatter.parse(end_date);

                                long interval = 24*1000 * 60 * 60; // 1 hour in millis
                                long endTime = endDate.getTime() ;
                                long curTime = startDate.getTime();

                                while (curTime <= endTime) {
                                    dates.add(new Date(curTime));
                                    curTime += interval;
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for(int j = 0; j < dates.size(); j++) {
                    Date lDate = (Date) dates.get(j);
                    long milliTime = lDate.getTime();
                    calendarList.add(new Event(Color.parseColor("#257a76"), milliTime));
                }

                for (int i = 0; i < calendarList.size(); i++) {
                    compactCalendar.addEvent(calendarList.get(i));
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
                        String formatted_today = dateFormat.format(passDate);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("email").equals(User.getInstance().getEmail())) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    // TODO
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

                /**
                 * Toast msg prints out option ID and SQL column id:
                Toast.makeText(getContext(), "MySQL ID: " + scheduleList.get(pos).getID() +
                        " | Menu ID:" + String.valueOf(menuID) , Toast.LENGTH_LONG).show();
                */

                switch (menuID) {
                    case 0:
                        Intent intent = new Intent(getContext(), AddMonthlySchedule.class);
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
                                                refresh();
                                                // Showing response message coming from server.
                                                Toast.makeText(getContext(), "Your schedule is successfully deleted.", Toast.LENGTH_LONG).show();
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
                                refresh();
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

    // Check for passed time
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

    private void sortArray(List<Schedule> arrayList) {
        if (arrayList != null) {
            Collections.sort(arrayList, new Comparator<Schedule>() {
                @Override
                public int compare(Schedule o1, Schedule o2) {
                    if (!(o1.getStart_date().equals(o2.getStart_date()))) {
                        return o1.getStart_date().compareTo(o2.getStart_date());
                    } else {
                        return o1.getStart_time().compareTo(o2.getStart_time());
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
}