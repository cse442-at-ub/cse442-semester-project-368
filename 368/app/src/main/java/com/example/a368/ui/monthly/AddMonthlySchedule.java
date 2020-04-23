package com.example.a368.ui.monthly;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.example.a368.User;
import com.example.a368.ui.today.AddToSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class AddMonthlySchedule extends AppCompatActivity {
    private static String HttpUrl = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/insert_schedule.php";
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    private TextView startDate, startTime, endDate, endTime;
    private int year, month, day, hour, minute;
    private Boolean startDateClicked = false, startTimeClicked = false, endDateClicked = false, endTimeClicked = false;
    private EditText title, description;
    private String strTitle, strStartDate, strStartTime, strEndDate, strEndTime, strDescription;
    private boolean edit_screen = false;

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
                    Toast.makeText(AddMonthlySchedule.this, "Missing Information: You can only skip 'Description' field.", Toast.LENGTH_LONG).show();
                }

                /**
                 * Check whether the user inputted start date later than the end date (Invalid Schedule)
                 */
                else if (!(startDate.getText().toString().equals(endDate.getText().toString()))
                        && !(check_date(startDate.getText().toString(), endDate.getText().toString(), "MM/dd/yyyy"))) {
                    Toast.makeText(AddMonthlySchedule.this, "Your end date cannot be earlier than the start date.", Toast.LENGTH_LONG).show();
                }

                /**
                 * Check whether the user inputted end time earlier than the start time,
                 * when it occurs in the same day (Invalid Schedule)
                 */
                else if (startDate.getText().toString().equals(endDate.getText().toString()) &&
                        (!(check_date(startTime.getText().toString(), endTime.getText().toString(), "HH:mm")))) {
                    Toast.makeText(AddMonthlySchedule.this, "Your end time cannot be earlier than the start time.", Toast.LENGTH_LONG).show();
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
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String ServerResponse) {

                                                        // Hiding the progress dialog after all task complete.
                                                        progressDialog.dismiss();

                                                        // End Activity
                                                        finish();

                                                        // Showing response message coming from server.
                                                        if(edit_screen) {
                                                            Toast.makeText(AddMonthlySchedule.this, "Your schedule is successfully edited.", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(AddMonthlySchedule.this, "Your schedule is successfully added.", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError volleyError) {

                                                        // Hiding the progress dialog after all task complete.
                                                        progressDialog.dismiss();

                                                        // Showing error message if something goes wrong.
                                                        Toast.makeText(AddMonthlySchedule.this, volleyError.toString(), Toast.LENGTH_LONG).show();
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
                                        RequestQueue requestQueue = Volley.newRequestQueue(AddMonthlySchedule.this);

                                        // Adding the StringRequest object into requestQueue.
                                        requestQueue.add(stringRequest);
//                                        finish();

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
                        RequestQueue requestQueue = Volley.newRequestQueue(AddMonthlySchedule.this);

                        // Adding the StringRequest object into requestQueue.
                        requestQueue.add(stringRequest);
                    }
                    else {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String ServerResponse) {

                                        // Hiding the progress dialog after all task complete.
                                        progressDialog.dismiss();

                                        Toast.makeText(AddMonthlySchedule.this, ServerResponse, Toast.LENGTH_LONG).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                        // Hiding the progress dialog after all task complete.
                                        progressDialog.dismiss();

                                        // Showing error message if something goes wrong.
                                        Toast.makeText(AddMonthlySchedule.this, volleyError.toString(), Toast.LENGTH_LONG).show();
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
                        RequestQueue requestQueue = Volley.newRequestQueue(AddMonthlySchedule.this);
                        // Adding the StringRequest object into requestQueue.
                        requestQueue.add(stringRequest);
                        finish();
                    }
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
        actionBar.setTitle("Add Schedule");
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Attributes setup
        title = (EditText) findViewById(R.id.event_title);
        description = (EditText) findViewById(R.id.event_description);

        // Creating Volley newRequestQueue
        requestQueue = Volley.newRequestQueue(AddMonthlySchedule.this);
        progressDialog = new ProgressDialog(AddMonthlySchedule.this);

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
//        startDate.setText(getIntent().getStringExtra("date"));

        // Set up Start Date field (should be fixed for add daily schedule)
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard(v);
                startDateClicked = true;
                new DatePickerDialog(AddMonthlySchedule.this, dateSetListener, year, month, day).show();
            }
        });

        startTime = (TextView) findViewById(R.id.event_start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeClicked = true;
                new TimePickerDialog(AddMonthlySchedule.this, timeSetListener, hour, minute, false).show();
            }
        });

        endDate = (TextView) findViewById(R.id.event_end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard(v);
                endDateClicked = true;
                new DatePickerDialog(AddMonthlySchedule.this, dateSetListener, year, month, day).show();
            }

        });

        endTime = (TextView) findViewById(R.id.event_end_time);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimeClicked = true;
                new TimePickerDialog(AddMonthlySchedule.this, timeSetListener, hour, minute, false).show();
            }
        });

        if(getIntent().hasExtra("id")) {
            edit_screen = true;
            actionBar.setTitle("Edit Schedule");
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
}
