package com.example.a368.ui.monthly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.a368.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MonthlyFragment extends Fragment {

    private MonthlyViewModel monthlyViewModel;
    private TextView month;
    private static SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM (yyyy)", Locale.ENGLISH);
    protected static Date passDate;

    // Calendar Library
    protected static CompactCalendarView compactCalendar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        monthlyViewModel =
                ViewModelProviders.of(this).get(MonthlyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        compactCalendar = (CompactCalendarView) root.findViewById(R.id.calendar_view);
        compactCalendar.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendar.shouldDrawIndicatorsBelowSelectedDays(true);
        month = (TextView) root.findViewById(R.id.calendar_month);
        month.setText(dateFormatMonth.format(compactCalendar.getFirstDayOfCurrentMonth()));

//        cDBHelper = new CalendarDBHelper(this.getActivity(), null, null, 1);
//        listView = (ListView) view.findViewById(R.id.event_list);
//        eventAdapter = new EventAdapter(getContext(), scheduleList);
//        listView.setAdapter(eventAdapter);

        long currentTime = System.currentTimeMillis();
        Date today = new Date(currentTime);
        passDate = today;

        return root;
    }
}