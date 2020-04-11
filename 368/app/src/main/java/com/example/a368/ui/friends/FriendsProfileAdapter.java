package com.example.a368.ui.friends;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a368.R;
import com.example.a368.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
Created by: Jiwon Choi
This is a schedule adapter to deal with the Schedule list view
 */
public class FriendsProfileAdapter extends RecyclerView.Adapter<FriendsProfileAdapter.ViewHolder> {

    private Context context;
    private List<Schedule> list;

    public FriendsProfileAdapter(Context context, List<Schedule> list) {
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.monthly_schedule_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Schedule schedule = list.get(position);

        holder.text_name.setText(schedule.getName());
        holder.text_start.setText(String.valueOf(schedule.getStart_date()) + " - " + String.valueOf(schedule.getStart_time()));
        holder.text_end.setText(String.valueOf(schedule.getEnd_date()) + " - " + String.valueOf(schedule.getEnd_time()));
        holder.text_description.setText(String.valueOf(schedule.getDescription()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text_name, text_start, text_end, text_description;

        public ViewHolder(View itemView) {
            super(itemView);

            text_name = itemView.findViewById(R.id.text_name);
            text_start = itemView.findViewById(R.id.text_start);
            text_end = itemView.findViewById(R.id.text_end);
            text_description = itemView.findViewById(R.id.text_description);
        }

    }

}
