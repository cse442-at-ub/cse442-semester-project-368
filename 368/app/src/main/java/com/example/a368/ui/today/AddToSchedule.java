package com.example.a368.ui.today;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.a368.R;
import com.example.a368.ui.ScheduleData;

import java.util.Calendar;
import java.util.GregorianCalendar;

/*
Created by: Dave Rodrigues
Modified by: Jiwon Choi
Activity that allows users to add their daily schedule.
 */

public class AddToSchedule extends AppCompatActivity {

    private ScheduleData scheduleData;
    private TextView startDate, startTime, endDate, endTime;
    private int year, month, day, hour, minute;
    private Boolean startDateClicked = false, startTimeClicked = false, endDateClicked = false, endTimeClicked = false;
    private EditText title, location, description;

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
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDateClicked = true;
                new DatePickerDialog(AddToSchedule.this, dateSetListener, year, month, day).show();
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

//        Button btCreate = findViewById(R.id.btCreateAppointment);
//
//        btCreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(AddToSchedule.this, "Appointment Created", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });

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
}
