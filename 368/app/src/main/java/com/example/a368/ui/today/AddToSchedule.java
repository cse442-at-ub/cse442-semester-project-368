package com.example.a368.ui.today;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.example.a368.User;

import java.text.ParseException;
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
                    if(getIntent().hasExtra("id")) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/delete-schedule.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String ServerResponse) {
                                        // Showing response message coming from server.

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        // Showing error message if something goes wrong.

                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                // Creating Map String Params.
                                Map<String, String> params = new HashMap<String, String>();
                                // Adding All values to Params.

                                params.put("id", ""+getIntent().getStringExtra("id"));
                                return params;
                            }

                        };
                        // Creating RequestQueue.
                        RequestQueue requestQueue = Volley.newRequestQueue(AddToSchedule.this);

                        // Adding the StringRequest object into requestQueue.
                        requestQueue.add(stringRequest);
                    }
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
                            params.put("email", User.getInstance().getEmail());
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
//                if(getIntent().hasExtra("end_date")) {
//                    SimpleDateFormat format = new SimpleDateFormat("mmm dd yyyy");
//                    SimpleDateFormat outForm = new SimpleDateFormat("MM/dd/yyyy");
//
//                    try {
//                        Date date = new SimpleDateFormat("MMM").parse(getIntent().getStringExtra("end_date")+" "+Calendar.getInstance().get(Calendar.YEAR));
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(date);
////                        date = outForm.parse(date.toString());
//                        Log.d("mdate", ""+cal.getInstance().get(Calendar.MONTH));
//                        new DatePickerDialog(AddToSchedule.this, dateSetListener, cal.getInstance().get(Calendar.YEAR), cal.getInstance().get(Calendar.MONTH), cal.getInstance().get(Calendar.DATE)).show();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else {
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
        if(getIntent().hasExtra("id")) {
            actionBar.setTitle("Edit Daily Schedule");
            title.setText(getIntent().getStringExtra("name"));
            startTime.setText(getIntent().getStringExtra("start_time"));
            startDate.setText(getIntent().getStringExtra("start_date"));
            endTime.setText(getIntent().getStringExtra("end_time"));
            Date eDate = null;
            Date sDate = null;
            try {
                eDate = new SimpleDateFormat("MMM dd yyyy").parse(getIntent().getStringExtra("end_date")+" "+ Calendar.getInstance().get(Calendar.YEAR));
                sDate = new SimpleDateFormat("MMM dd yyyy").parse(getIntent().getStringExtra("start_date")+" "+ Calendar.getInstance().get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar eCal = Calendar.getInstance();
            Calendar sCal = Calendar.getInstance();
            eCal.setTime(eDate);
            sCal.setTime(sDate);
            String strEDate = String.format("%02d/%02d/%04d", eCal.get(eCal.MONTH)+1, eCal.get(eCal.DATE), eCal.get(eCal.YEAR));
            String strSDate = String.format("%02d/%02d/%04d", sCal.get(sCal.MONTH)+1, sCal.get(sCal.DATE), sCal.get(sCal.YEAR));
            endDate.setText(strEDate);
            startDate.setText(strSDate);

            description.setText(getIntent().getStringExtra("description"));
        }
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
                if (msg.indexOf("0") == 0) {
                    msg = msg.replaceFirst("0", "");
                }
                startTime.setText(msg);
                startTimeClicked = false;
            } else if (endTimeClicked) {
                if (msg.indexOf("0") == 0) {
                    msg = msg.replaceFirst("0", "");
                }
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
