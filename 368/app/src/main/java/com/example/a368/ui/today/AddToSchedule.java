package com.example.a368.ui.today;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/*
Created by: Dave Rodrigues
Modified by: Jiwon Choi
Activity that allows users to add their daily schedule.
 */

public class AddToSchedule extends AppCompatActivity {
    private static String HttpUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/insert_schedule.php";
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    private TextView startDate, startTime, endDate, endTime;
    private int year, month, day, hour, minute;
    private Boolean startDateClicked = false, startTimeClicked = false, endDateClicked = false, endTimeClicked = false;
    private EditText title, description;
    private String strTitle, strStartDate, strStartTime, strEndDate, strEndTime, strDescription;

    // Add customized menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_menu_bar, menu);
        return true;
    }

    // press "CANCEL" or "SAVE" on top
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
                if (title.getText().length() == 0 ||
                        startDate.getText().toString().equals("MM/DD/YYYY") ||
                        endDate.getText().toString().equals("MM/DD/YYYY")) {
                    Toast.makeText(AddToSchedule.this, "Invalid input: Check your input field.", Toast.LENGTH_SHORT).show();
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
                    strStartDate = startDate.getText().toString();
                    strStartTime = startTime.getText().toString();
                    strEndDate = endDate.getText().toString();
                    strEndTime = endTime.getText().toString();
                    strDescription = description.getText().toString();

                    // Creating string request with post method.
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String ServerResponse) {

                                    // Hiding the progress dialog after all task complete.
                                    progressDialog.dismiss();

                                    // Showing response message coming from server.
                                    Toast.makeText(AddToSchedule.this, ServerResponse, Toast.LENGTH_LONG).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                    // Hiding the progress dialog after all task complete.
                                    progressDialog.dismiss();

                                    // Showing error message if something goes wrong.
                                    Toast.makeText(AddToSchedule.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {

                            // Creating Map String Params.
                            Map<String, String> params = new HashMap<String, String>();

                            // Adding All values to Params.
                            params.put("email", "cjwon531@gmail.com");
                            params.put("title", strTitle);
                            params.put("start_date", strStartDate);
                            params.put("start_time", strStartTime);
                            params.put("end_date", strEndDate);
                            params.put("end_time", strEndTime);
                            params.put("description", strDescription);

                            return params;
                        }

                    };

                    // Creating RequestQueue.
                    RequestQueue requestQueue = Volley.newRequestQueue(AddToSchedule.this);

                    // Adding the StringRequest object into requestQueue.
                    requestQueue.add(stringRequest);

                    finish();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_schedule);

        // Customize action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Daily Schedule");
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Attributes setup
        title = (EditText) findViewById(R.id.event_title);
        description = (EditText) findViewById(R.id.event_description);

        // Creating Volley newRequestQueue
        requestQueue = Volley.newRequestQueue(AddToSchedule.this);
        progressDialog = new ProgressDialog(AddToSchedule.this);

        // set up Date & Time Picker Dialog
        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        /**
         * startDateClicked, startTimeClicked, endDateClicked, endTimeClicked boolean variables:
         * Flag attribute since they are reusing the Date || Time picker dialog.
         */
        startDate = (TextView) findViewById(R.id.event_start_date);

        // Set up Start Date field (should be fixed for add daily schedule)
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        startDate.setText(dateFormat.format(today));

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard(v);
                startDateClicked = true;
                Toast.makeText(AddToSchedule.this,
                        "You can only add the schedule with the start date of today", Toast.LENGTH_LONG).show();
                // Disable for Add Daily Schedule
//                new DatePickerDialog(AddToSchedule.this, dateSetListener, year, month, day).show();
            }
        });

        startTime = (TextView) findViewById(R.id.event_start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeClicked = true;
                new TimePickerDialog(AddToSchedule.this, timeSetListener, hour, minute, false).show();
            }
        });

        endDate = (TextView) findViewById(R.id.event_end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard(v);
                endDateClicked = true;
                new DatePickerDialog(AddToSchedule.this, dateSetListener, year, month, day).show();
            }
        });

        endTime = (TextView) findViewById(R.id.event_end_time);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimeClicked = true;
                new TimePickerDialog(AddToSchedule.this, timeSetListener, hour, minute, false).show();
            }
        });

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
            } else if (endDateClicked) {
                endDate.setText(msg);
                endDateClicked = false;
            }
        }
    };

    // Time Picker Dialog
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String AM_PM;
            if (hourOfDay < 12) {
                AM_PM = "AM";
            } else {
                AM_PM = "PM";
                hourOfDay = hourOfDay - 12;
            }

            String msg = String.format("%d:%d %s", hourOfDay, minute, AM_PM);

            if (hourOfDay < 10 || minute < 10) {
                String hour = "", min = "";

                if (hourOfDay < 10) {
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
                startTime.setText(msg);
                startTimeClicked = false;
            } else if (endTimeClicked) {
                endTime.setText(msg);
                endTimeClicked = false;
            }
        }

    };

    // Disable keyboard pop-up
    private void hide_keyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
}
