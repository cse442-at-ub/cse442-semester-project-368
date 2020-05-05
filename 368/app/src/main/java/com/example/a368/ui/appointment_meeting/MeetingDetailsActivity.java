package com.example.a368.ui.appointment_meeting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.example.a368.User;
import com.example.a368.ui.friends.Friend;
import com.example.a368.ui.friends.FriendRequestFragment;
import com.example.a368.ui.monthly.AddMonthlySchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeetingDetailsActivity extends AppCompatActivity {
    private static String HttpUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/meeting/insert_meeting_request.php";
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    private TextView startTime, from_meeting_time, to_meeting_time;
    private EditText title, description;
    private int year, month, day, hour, minute;
    private Boolean startDateClicked = false, startTimeClicked = false, endDateClicked = false, endTimeClicked = false;
    private String strTitle, strStartDate, strStartTime, strEndDate, strEndTime, strDescription;
    private boolean edit_screen;
    private int length;
    private String start, tomorrow, timeSlot;
    private TimePair timePair;
    ArrayList<Friend> listParticipants;
    private String participants;
    private TextView tvParticipantDetails;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_menu_bar, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Meeting Details");
        actionBar.setDisplayHomeAsUpEnabled(false);
        progressDialog = new ProgressDialog(MeetingDetailsActivity.this);

        start = getIntent().getStringExtra("StartDate");
        tomorrow = getIntent().getStringExtra("tomorrow");
        timePair = getIntent().getParcelableExtra("time");
        timeSlot = getIntent().getStringExtra("TimeSlot");
        listParticipants = getIntent().getParcelableArrayListExtra("listParticipants");

        StringBuilder sb = new StringBuilder();
        sb.append("You, ");

        for (Friend f : listParticipants) {
            sb.append(f.getName());

            if (!f.getName().equals(listParticipants.get(listParticipants.size() - 1).getName())) {
                sb.append(", ");
            }
        }

        tvParticipantDetails = findViewById(R.id.participants_details);
        tvParticipantDetails.setText(sb.toString());
        participants = (sb.toString());
        title = findViewById(R.id.meeting_title);
        startTime = (TextView) findViewById(R.id.meeting_start_time);
        from_meeting_time = (TextView) findViewById(R.id.from_meeting_time);
        to_meeting_time = (TextView) findViewById(R.id.to_meeting_time);
        description = findViewById(R.id.meeting_description);
        startTime.setText(getIntent().getStringExtra("TimeSlot").substring(0,8));

        length = Integer.parseInt(getIntent().getStringExtra("length"));

        setEndTimes(timePair.getStartTime(), getIntent().getStringExtra("TimeSlot").substring(0,8));

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeClicked = true;
                TimePickerDialog picker = new TimePickerDialog(MeetingDetailsActivity.this, timeSetListener, hour, minute, false);
                hour = Integer.parseInt(timeSlot.substring(0,2));
                if(timePair.getStartTime() > 780) {
                    hour = hour + 12;
                }
                picker.updateTime(hour, Integer.parseInt(timeSlot.substring(3,5)));
                picker.show();
            }
        });

    }

    public void setEndTimes(int time, String msg) {
        time = time + length;
        int eHours, eMinutes;
        boolean isTomorrow = false;
        String eAM = "AM";

        if(time >= 1440) {
            time = time - 1440;
            isTomorrow = true;
        }

        eHours = time / 60;
        eMinutes = time % 60;
        if(eHours == 12) {
            eAM = "PM";
        }
        else if(eHours > 12) {
            eHours = eHours - 12;
            if(eHours == 12) {
                eAM = "AM";
            }
            else {
                eAM = "PM";
            }
        }
        if(eHours == 0) {
            eHours = 12;
        }

        if(isTomorrow) {
            strEndDate = tomorrow;
            strEndTime = String.format("%02d:%02d %s", eHours, eMinutes, eAM);
            from_meeting_time.setText(String.format("%s %s", start, msg));
            to_meeting_time.setText(String.format("%s %02d:%02d %s", tomorrow, eHours, eMinutes, eAM));
        }
        else {
            strEndDate = start;
            strEndTime = String.format("%02d:%02d %s", eHours, eMinutes, eAM);
            from_meeting_time.setText(String.format("%s %s", start, msg));
            to_meeting_time.setText(String.format("%s %02d:%02d %s", start, eHours, eMinutes, eAM));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // CANCEL: terminate add schedule activity
            case R.id.action_bar_cancel:
                finish();
                break;

            // SAVE
            case R.id.action_bar_save:
                /**
                 * Check whether the field is empty or not.
                 * If one of the field is empty, print out the message to guide the user to fill the blank in.
                 */
                if(title.getText().toString().length() == 0) {
                    Toast.makeText(MeetingDetailsActivity.this, "Please enter a title.", Toast.LENGTH_SHORT).show();
                }
                /**
                 * If the inputs are valid, save it to the local database,
                 * then print out the Toast message that it is successfully saved.
                 */
                else {
                    /**
                     * If the user did not fill in the "Description" field,
                     * then put random text to this field.
                     */


                    if (description.getText().length() == 0) {
                        description.setText("Exception: No Text Applied");
                    }

                    //Get the texts that the user put it in to the edittext field
                    strTitle = title.getText().toString();
                    strStartDate = start;
                    strStartTime = startTime.getText().toString();
                    strDescription = description.getText().toString();
                    for(Friend f : listParticipants) {
                            // Creating string request with post method.
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String ServerResponse) {

                                        // Hiding the progress dialog after all task complete.
                                        progressDialog.dismiss();

                                        Toast.makeText(MeetingDetailsActivity.this, "Meeting request sent.", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                        // Hiding the progress dialog after all task complete.
                                        progressDialog.dismiss();

                                        // Showing error message if something goes wrong.
                                        Toast.makeText(MeetingDetailsActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {

                                // Creating Map String Params.
                                Map<String, String> params = new HashMap<String, String>();

                                // Adding All values to Params.
                                params.put("sender_name", f.getName());
                                params.put("sender_email", f.getEmail());
                                params.put("receiver_name", User.getInstance().getName());
                                params.put("receiver_email", User.getInstance().getEmail());
                                params.put("status", "Confirm");
                                params.put("title", title.getText().toString());
                                params.put("start_date", strStartDate);
                                params.put("start_time", strStartTime);
                                params.put("end_date", strEndDate);
                                params.put("end_time", strEndTime);
                                params.put("description", strDescription);
                                params.put("participant", participants);

                                return params;
                            }
                        };
                        // Creating RequestQueue.
                        RequestQueue requestQueue = Volley.newRequestQueue(MeetingDetailsActivity.this);
                        // Adding the StringRequest object into requestQueue.
                        requestQueue.add(stringRequest);
                            stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String ServerResponse) {

                                            // Hiding the progress dialog after all task complete.
                                            progressDialog.dismiss();

                                            Toast.makeText(MeetingDetailsActivity.this, "Meeting request sent.", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {

                                            // Hiding the progress dialog after all task complete.
                                            progressDialog.dismiss();

                                            // Showing error message if something goes wrong.
                                            Toast.makeText(MeetingDetailsActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {

                                    // Creating Map String Params.
                                    Map<String, String> params = new HashMap<String, String>();

                                    // Adding All values to Params.
                                    params.put("sender_name", User.getInstance().getName());
                                    params.put("sender_email", User.getInstance().getEmail());
                                    params.put("receiver_name", f.getName());
                                    params.put("receiver_email", f.getEmail());
                                    params.put("status", "Pending");
                                    params.put("title", title.getText().toString());
                                    params.put("start_date", strStartDate);
                                    params.put("start_time", strStartTime);
                                    params.put("end_date", strEndDate);
                                    params.put("end_time", strEndTime);
                                    params.put("description", strDescription);
                                    params.put("participant", participants);
                                    return params;
                                }
                            };
                            // Creating RequestQueue.
                            requestQueue = Volley.newRequestQueue(MeetingDetailsActivity.this);
                            // Adding the StringRequest object into requestQueue.
                            requestQueue.add(stringRequest);

                            // To exit Create Appointment activity
                            setResult(Activity.RESULT_OK);
                            finish();

                        }
                    }
                }
        return super.onOptionsItemSelected(item);
    }


    private void hide_keyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

    // Check for passed date
    private boolean check_date(String start_date, String end_date, String pattern) {
        if (pattern.equals("HH:mm")) {
            start_date = date_parsing(start_date, "hh:mm aaa", "HH:mm");
            end_date = date_parsing(end_date, "hh:mm aaa", "HH:mm");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(start_date);
            Date date2 = sdf.parse(end_date);

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
    // Time Picker Dialog
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String AM_PM;
            int time = hourOfDay*60+minute;
            if (hourOfDay < 12) {
                AM_PM = "AM";
            } else {
                AM_PM = "PM";
                hourOfDay = hourOfDay - 12;
            }

            String msg = String.format("%d:%d %s", hourOfDay, minute, AM_PM);

            if (hourOfDay < 10 || minute < 10) {
                String hour = "", min = "";

                if (hourOfDay == 0) {
                    hour = String.valueOf(12);
                } else if (hourOfDay < 10 ) {
                    hour = "0" + String.valueOf(hourOfDay);
                } else {
                    hour = String.valueOf(hourOfDay);
                }

                if (minute < 10) {
                    min = "0" + String.valueOf(minute);
                } else {
                    min = String.valueOf(minute);
                }

                msg = String.format("%s:%s %s", hour, min, AM_PM);
            }

            if (startTimeClicked) {
                if(time < timePair.getStartTime() || time > timePair.getEndTime()) {
                    Toast.makeText(MeetingDetailsActivity.this, String.format("Start must be within %s", timeSlot), Toast.LENGTH_SHORT).show();
                }
                else {
                    startTime.setText(msg);
                    startTimeClicked = false;
                    setEndTimes(time, msg);
                }
            } else if (endTimeClicked) {
//                meetingTime.setText(msg);
                endTimeClicked = false;
            }

        }

    };
}
