package com.example.a368;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
Created by: Jiwon Choi
This is a schedule adapter to deal with the Schedule list view
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private Context context;
    private List<Schedule> list;
    private onClickListener sOnClickListener;

    public ScheduleAdapter(Context context, List<Schedule> list, onClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.sOnClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.schedule_list_item, parent, false);
        return new ViewHolder(v, sOnClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Schedule schedule = list.get(position);

        holder.text_name.setText(schedule.getName());
        holder.text_start_date.setText(String.valueOf(schedule.getStart_date()));
        holder.text_start_time.setText(String.valueOf(schedule.getStart_time()));
        holder.text_end_date.setText(schedule.getEnd_date());
        holder.text_end_time.setText(String.valueOf(schedule.getEnd_time()));
        holder.text_description.setText(String.valueOf(schedule.getDescription()));

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy'T'hh:mm aa");
        String s = schedule.getStart_date()+" "+Calendar.getInstance().get(Calendar.YEAR)+"T"+schedule.getStart_time();
        Date date = null;
        try {
            date = format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long remaining = date.getTime() - currentTime.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = remaining / daysInMilli;
        remaining = remaining % daysInMilli;

        long elapsedHours = remaining / hoursInMilli;
        remaining = remaining % hoursInMilli;

        long elapsedMinutes = remaining / minutesInMilli;
        remaining = remaining % minutesInMilli;

        long elapsedSeconds = remaining / secondsInMilli;

        String HR_str = String.format("%d", elapsedHours);
        String MIN_str = String.format("%02d", elapsedMinutes);
        String SEC_str = String.format("%02d", elapsedSeconds);
        String formatted = String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);

        if (formatted.indexOf("-") != -1) {
            formatted = "\nOngoing Now";
        } else {
            String msg = "";
            if (!(HR_str.equals("00")) && !(HR_str.equals("0"))) {
                msg += HR_str + " Hr\n";
                msg += MIN_str + " Min\n";
                msg += SEC_str + " Sec";
            } else if (!(MIN_str.equals("00")) && !(MIN_str.equals("0"))) {
                msg += "\n" + MIN_str + " Min\n";
                msg += SEC_str + " Sec";
            } else if (!(SEC_str.equals("00")) && !(SEC_str.equals("0"))) {
                msg += "\n" + SEC_str + " Sec";
            }
            formatted = "Remains\n" + msg;
        }
        holder.text_time_remaining.setText(formatted);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text_name, text_start_date, text_start_time, text_end_date, text_end_time, text_description, text_time_remaining;
        onClickListener onClickListener;

        public ViewHolder(View itemView, onClickListener onClickListener) {
            super(itemView);

            text_name = itemView.findViewById(R.id.text_name);
            text_start_date = itemView.findViewById(R.id.text_start_date);
            text_start_time = itemView.findViewById(R.id.text_start_time);
            text_end_date = itemView.findViewById(R.id.text_end_date);
            text_end_time = itemView.findViewById(R.id.text_end_time);
            text_description = itemView.findViewById(R.id.text_description);
            text_time_remaining = itemView.findViewById(R.id.time_remaining);
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickSchedule(getAdapterPosition());
        }
    }

    public interface onClickListener {
        void onClickSchedule (int position);
    }

}
