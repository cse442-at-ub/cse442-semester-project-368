package com.example.a368.ui.today;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a368.R;

import java.util.List;

/*
Created by: Jiwon Choi
This is a schedule adapter to deal with the Schedule list view
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private Context context;
    private List<Schedule> list;

    public ScheduleAdapter(Context context, List<Schedule> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.schedule_list_item, parent, false);
        return new ViewHolder(v);
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text_name, text_start_date, text_start_time, text_end_date, text_end_time, text_description;

        public ViewHolder(View itemView) {
            super(itemView);

            text_name = itemView.findViewById(R.id.text_name);
            text_start_date = itemView.findViewById(R.id.text_start_date);
            text_start_time = itemView.findViewById(R.id.text_start_time);
            text_end_date = itemView.findViewById(R.id.text_end_date);
            text_end_time = itemView.findViewById(R.id.text_end_time);
            text_description = itemView.findViewById(R.id.text_description);
        }
    }

}