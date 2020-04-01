package com.example.a368.ui.monthly;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.example.a368.ScheduleAdapter;
import com.example.a368.User;
import com.example.a368.ui.today.AddToSchedule;
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

public class MonthlyFragment extends Fragment implements ScheduleAdapter.onClickListener {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/fetch_schedule.php";
    private MonthlyViewModel monthlyViewModel;
    private TextView month;
    private static SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM (yyyy)", Locale.ENGLISH);
    protected static Date passDate;

    private RecyclerView sList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Schedule> scheduleList;
    private RecyclerView.Adapter adapter;

    // Calendar Library
    protected static CompactCalendarView compactCalendar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        monthlyViewModel =
                ViewModelProviders.of(this).get(MonthlyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Add Schedule Floating Button
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fab_add_monthly);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddMonthlySchedule.class));
            }
        });

        compactCalendar = (CompactCalendarView) root.findViewById(R.id.calendar_view);
        compactCalendar.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendar.shouldDrawIndicatorsBelowSelectedDays(true);
        month = (TextView) root.findViewById(R.id.calendar_month);
        month.setText(dateFormatMonth.format(compactCalendar.getFirstDayOfCurrentMonth()));

        long currentTime = System.currentTimeMillis();
        Date today = new Date(currentTime);
        passDate = today;

//        sList = (RecyclerView)root.findViewById(R.id.schedule_list);
//        scheduleList = new ArrayList<>();
//        adapter = new ScheduleAdapter(getContext(), scheduleList, this);
//
//        linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        dividerItemDecoration = new DividerItemDecoration(sList.getContext(), linearLayoutManager.getOrientation());
//
//        sList.setHasFixedSize(true);
//        sList.setLayoutManager(linearLayoutManager);
//        sList.addItemDecoration(dividerItemDecoration);
//        sList.setAdapter(adapter);

        return root;
    }

    // TODO
    @Override
    public void onClickSchedule(int position) {

    }
}