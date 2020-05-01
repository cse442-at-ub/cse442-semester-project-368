package com.example.a368.ui.appointment_meeting;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.example.a368.Schedule;
import com.example.a368.ui.friends.Friend;
import com.example.a368.ui.today.ScheduleAdapter;

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
import java.util.GregorianCalendar;
import java.util.List;

public class createAppointment extends AppCompatActivity {

    RecyclerView availableTimes;
    RecyclerView.LayoutManager layoutManager;
    MeetingTimesAdapter mAdapter;
    ArrayList<Friend> list;
    TextView tvParticipants;
    Spinner spHours;
    Spinner spMinutes;
    private TextView startDate;
    private boolean startDateClicked = false;
    private int year, month, day, hour, minute;
    private boolean[] unFreeTime;
    ArrayList<Schedule> scheduleList;
    Button btAppointment;
    ArrayList<String> listEmail;
    ArrayList<TimePair> times;
    ArrayList<String> stringTimes;
    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/fetch_schedule.php";
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        availableTimes = findViewById(R.id.availableTimes);
        list = getIntent().getParcelableArrayListExtra("List");
        listEmail = new ArrayList<>();
        for(Friend f : list) {
            listEmail.add(""+f.getEmail());
            Log.d("id", ""+f.getEmail());
        }
        times = new ArrayList<>();
        scheduleList = new ArrayList<>();
        stringTimes = new ArrayList<>();
        mAdapter = new MeetingTimesAdapter(stringTimes, createAppointment.this);

        layoutManager = new LinearLayoutManager(this);

        linearLayoutManager = new LinearLayoutManager(createAppointment.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(availableTimes.getContext(), linearLayoutManager.getOrientation());
        availableTimes.addItemDecoration(dividerItemDecoration);
        unFreeTime = new boolean[1440];
        availableTimes.setLayoutManager(layoutManager);

        tvParticipants = findViewById(R.id.tvParticipants);
        StringBuilder sb = new StringBuilder();
        for (Friend f : list) {
            sb.append(f.getName());
            if (!f.getName().equals(list.get(list.size() - 1).getName())) {
                sb.append(", ");
            }
        }
        tvParticipants.setText(sb.toString());

        spMinutes = findViewById(R.id.spMinutes);
        ArrayAdapter<CharSequence> spMinutesAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_minutes, android.R.layout.simple_spinner_item);
        spMinutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMinutes.setAdapter(spMinutesAdapter);

        spHours = findViewById(R.id.spHours);
        ArrayAdapter<CharSequence> spHoursAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_hours, android.R.layout.simple_spinner_item);
        spHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spHours.setAdapter(spHoursAdapter);


        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        btAppointment = findViewById(R.id.btAppointment);
        btAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData(startDate.getText().toString());
            }
        });

        startDate = findViewById(R.id.appointmentDate);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard(v);
                startDateClicked = true;
                new DatePickerDialog(createAppointment.this, dateSetListener, year, month, day).show();
            }
        });


        availableTimes.setAdapter(mAdapter);
    }

    private void hide_keyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    // Date Picker Dialog
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg = String.format("%d/%d/%d", monthOfYear + 1, dayOfMonth, year);

            if (monthOfYear < 10 || dayOfMonth < 10) {
                String month = "", day = "";

                if (monthOfYear < 10) {
                    month = "0" + String.valueOf(monthOfYear + 1);
                } else {
                    month = String.valueOf(monthOfYear + 1);
                }

                if (dayOfMonth < 10) {
                    day = "0" + String.valueOf(dayOfMonth);
                } else {
                    day = String.valueOf(dayOfMonth);
                }

                msg = String.format("%s/%s/%d", month, day, year);
            }

            if (startDateClicked) {
                startDate.setText(msg);
                startDateClicked = false;
            }
        }
    };

    private void getSchedules(ArrayList<Friend> participants) {
    }

    private void getData(String appointmentDate) {
        final ProgressDialog progressDialog = new ProgressDialog(createAppointment.this);
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
                        String formatted_today = appointmentDate;

                        // Get current time
                        Date now = Calendar.getInstance().getTime();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String formatted_time = timeFormat.format(now);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (listEmail.contains(jsonObject.getString("email")) &&
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

                                parseTimeToMinute(schedule.getStart_time());

                                for(int j = parseTimeToMinute(schedule.getStart_time()); j < parseTimeToMinute(schedule.getEnd_time()); j++) {
                                    unFreeTime[j] = true;
                                }
                                scheduleList.add(schedule);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                findFreeTime();
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


                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(createAppointment.this);
        requestQueue.add(jsonArrayRequest);
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

    private void sortArray(List<Schedule> arrayList) {
        if (arrayList != null) {
            Collections.sort(arrayList, new Comparator<Schedule>() {
                @Override
                public int compare(Schedule o1, Schedule o2) {
                    return o1.getStart_time().compareTo(o2.getStart_time()); }
            });
        }
    }

    private int parseTimeToMinute(String time) {
        int hours = Integer.parseInt(time.substring(0,2));
        int minutes = Integer.parseInt(time.substring(3,5));
        return (hours*60)+minutes;
    }

    private void findFreeTime() {
        int length = (Integer.parseInt(spHours.getSelectedItem().toString())*60)+(Integer.parseInt(spMinutes.getSelectedItem().toString()));
        int start = 0;

        int r = 0;
        boolean add = false;

        for(int i = 0; i < unFreeTime.length; i++) {
            if(i > 1280) {

                Log.d("i", ""+r +" | " +unFreeTime[i] +" | " +i );
            }
            if(!unFreeTime[i] && i != 1439) {
                r++;
                if(r >= length) {
                    add = true;
                }
            }
            else {
                r = 0;
                if(add) {
                    times.add(new TimePair(start, i));
                    add = false;
                }
                start = i;
            }

        }

        //convert back to normal time string
        stringTimes.clear();
        int sHours, sMinutes, eHours, eMinutes = 0;
        String sAM = "AM", eAM = "AM";
        for(TimePair time : times) {
            sHours = time.getStartTime()/60;
            sMinutes = time.getStartTime()%60;
            if(sHours > 12) {
                sHours = sHours - 12;
                sAM = "PM";
            }
            else if (sHours == 0) {
                sHours = 12;
            }
            eHours = time.getEndTime()/60;
            eMinutes = time.getEndTime() % 60;
            if(eHours > 12) {
                eHours = eHours - 12;
                eAM = "PM";
            }
            else if (eHours == 0) {
                eHours = 12;
            }

            stringTimes.add(String.format("%02d:%02d %s- %02d:%02d %s", sHours, sMinutes, sAM, eHours, eMinutes, eAM));
        }
        mAdapter.notifyDataSetChanged();
    }
}

