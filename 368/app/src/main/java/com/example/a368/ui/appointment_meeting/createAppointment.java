package com.example.a368.ui.appointment_meeting;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
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
import com.example.a368.User;
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
    TextView tvAvailableLabel;
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
    String tomorrow = "";
    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/fetch_schedule.php";
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private int length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        availableTimes = findViewById(R.id.availableTimes);
        list = getIntent().getParcelableArrayListExtra("List");
        listEmail = new ArrayList<>();
        for(Friend f : list) {
            listEmail.add(""+f.getEmail());
            Log.d("id", ""+f.getEmail());
        }
        listEmail.add(User.getInstance().getEmail());
        times = new ArrayList<>();
        scheduleList = new ArrayList<>();
        stringTimes = new ArrayList<>();
        mAdapter = new MeetingTimesAdapter(stringTimes, createAppointment.this, new MeetingTimesAdapter.onClickListener() {
            @Override
            public void onClickSchedule(int position) {
                Intent intent = new Intent(createAppointment.this, MeetingDetailsActivity.class);
                intent.putExtra("StartDate", startDate.getText().toString());
                intent.putExtra("tomorrow", tomorrow);
                intent.putExtra("length", ""+(length-1));
                intent.putExtra("TimeSlot", stringTimes.get(position));
                intent.putExtra("time", times.get(position));
                intent.putParcelableArrayListExtra("listParticipants", list);
                startActivity(intent);
            }
        });


        layoutManager = new LinearLayoutManager(this);

        linearLayoutManager = new LinearLayoutManager(createAppointment.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(availableTimes.getContext(), linearLayoutManager.getOrientation());
        availableTimes.addItemDecoration(dividerItemDecoration);
        unFreeTime = new boolean[2880];
        availableTimes.setLayoutManager(layoutManager);

        tvParticipants = findViewById(R.id.tvParticipants);
        StringBuilder sb = new StringBuilder();
        sb.append("You, ");
        for (Friend f : list) {
            sb.append(f.getName());
            if (!f.getName().equals(list.get(list.size() - 1).getName())) {
                sb.append(", ");
            }
        }
        Log.d("LIST", ""+list.size() +" | " +sb.toString());
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

        tvAvailableLabel = findViewById(R.id.tvAvailableLabel);
        btAppointment = findViewById(R.id.btAppointment);
        btAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDate.getText().toString().equals("MM/DD/YYYY")) {
                    Toast.makeText(createAppointment.this, "Please select an appointment date.", Toast.LENGTH_SHORT).show();
                }
                else if(spHours.getSelectedItem().toString().equals("00") && spMinutes.getSelectedItem().toString().equals("00")) {
                    Toast.makeText(createAppointment.this, "Please select a meeting length.", Toast.LENGTH_SHORT).show();
                }
                else {
                    unFreeTime = new boolean[2880];
                    tvAvailableLabel.setVisibility(View.VISIBLE);
                    getData(startDate.getText().toString());
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                if((monthOfYear ==(Calendar.getInstance().get(Calendar.MONTH)) && year <= (Calendar.getInstance().get(Calendar.YEAR))) && dayOfMonth < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    Toast.makeText(createAppointment.this, "Date can not be earlier than today.", Toast.LENGTH_SHORT).show();
                }
                else {
                    startDate.setText(msg);
                    startDateClicked = false;
                }
            }
        }
    };


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
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dateFormat.parse(appointmentDate));
                        calendar.add(Calendar.DATE, 1);
                        String formatted_appointment = appointmentDate;
                        String formatted_tomorrow = dateFormat.format(calendar.getTime());
                        tomorrow = formatted_tomorrow;
                        Log.d("Dates", formatted_appointment +" | "+formatted_tomorrow);
                        // Get current time
                        Date now = Calendar.getInstance().getTime();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String formatted_time = timeFormat.format(now);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        Log.d("TEST", ""+jsonObject.getString("start_date").equals(formatted_appointment));
                        if (listEmail.contains(jsonObject.getString("email")) &&
                                (jsonObject.getString("start_date").equals(formatted_appointment) || jsonObject.getString("start_date").equals(formatted_tomorrow))) {
                            if (((jsonObject.getString("end_date").equals(formatted_appointment) || jsonObject.getString("end_date").equals(formatted_tomorrow))) ||
                                    (!(jsonObject.getString("end_date").equals(formatted_appointment)))) {

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

                                Log.d("INFO", ""+schedule.getStart_date() + " | " +date_parsing(formatted_tomorrow, "MM/dd", "MMM d") +" | " +schedule.getStart_date().equals(date_parsing(formatted_tomorrow, "MM/dd", "MMM d")));
                                Log.d("START", ""+parseTimeToMinute(schedule.getStart_time(), schedule.getStart_date().equals(date_parsing(formatted_tomorrow, "MM/dd", "MMM d"))));
                                Log.d("END", ""+parseTimeToMinute(schedule.getEnd_time(), schedule.getEnd_date().equals(date_parsing(formatted_tomorrow, "MM/dd", "MMM d"))));
                                for(int j = parseTimeToMinute(schedule.getStart_time(), schedule.getStart_date().equals(date_parsing(formatted_tomorrow, "MM/dd", "MMM d"))); j < parseTimeToMinute(schedule.getEnd_time(), schedule.getEnd_date().equals(date_parsing(formatted_tomorrow, "MM/dd", "MMM d"))); j++) {
                                    unFreeTime[j] = true;
                                }
                                scheduleList.add(schedule);
                            }
                        }

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                findFreeTime(appointmentDate);
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

    private int parseTimeToMinute(String time, boolean tomorrow) {
        int hours = Integer.parseInt(time.substring(0,2));
        int minutes = Integer.parseInt(time.substring(3,5));
        if(tomorrow) {
            return (hours*60)+minutes+1440;
        }
        else {
            return (hours*60)+minutes;
        }
    }

    private void findFreeTime(String date) {
        length = (Integer.parseInt(spHours.getSelectedItem().toString())*60)+(Integer.parseInt(spMinutes.getSelectedItem().toString()))+1;
        Log.d("LENGTH", ""+length);
        int start = 0;
        times.clear();
        String today = "";
        Date appointmentDate = Calendar.getInstance().getTime();
        int r = 0;
        int s = 0;
        boolean add = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            appointmentDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(DateUtils.isToday(appointmentDate.getTime())) {
            String time = String.format("%02d:%02d",Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));

            start = parseTimeToMinute(time, false)+1;
        }
        for(int i = start; i < (1440+length); i++) {
            if(!unFreeTime[i] && i != 1439+length) {
                r++;
                if(r >= length) {
                    add = true;
                }
                if(i < 1050) {
//                    Log.d("1050", "i: "+i +" | " +"r: " +r +" | " +"Length: " +length +" | " +"add: " +add);
                }
            }
            else {
                r = 0;
                if(add) {
                    if(i > 1439) {
                        if(start < i-length) {
                            times.add(new TimePair(start, i-length));
                        }
                    }
                    else {
                        if(start < i-length) {
                            times.add(new TimePair(start, i-length));
                        }
                    }
                    add = false;
                }
                start = i+2;
            }

        }
        for(TimePair t : times) {
            Log.d("Times" , ""+t.getStartTime() +" | " +t.getEndTime());
        }
        //convert back to normal time string
        stringTimes.clear();
        int sHours, sMinutes, eHours, eMinutes = 0;
        String sAM = "AM", eAM = "AM";
        for(TimePair time : times) {
            sHours = time.getStartTime()/60;
            sMinutes = time.getStartTime()%60;
            if(sHours == 12) {
                sAM = "PM";
            }
            else if(sHours > 12) {
                sHours = sHours - 12;
                sAM = "PM";
            }
            else if (sHours == 0) {
                sHours = 12;
            }
            eHours = time.getEndTime()/60;
            eMinutes = time.getEndTime() % 60;
            if(eHours >= 12) {
                eHours = eHours - 12;
                eAM = "PM";
            }
            else if (eHours == 0) {
                eHours = 12;
            }

            stringTimes.add(String.format("%02d:%02d %s- %02d:%02d %s", sHours, sMinutes, sAM, eHours, eMinutes, eAM));
        }
        if(stringTimes.size() == 0) {
            stringTimes.add("No Times Slots Available.");
        }
        mAdapter.notifyDataSetChanged();
    }
}

