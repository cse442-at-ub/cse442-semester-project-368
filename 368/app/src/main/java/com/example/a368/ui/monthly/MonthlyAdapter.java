package com.example.a368.ui.monthly;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a368.R;
import com.example.a368.Schedule;

import java.util.List;

/*
Created by: Jiwon Choi
This is a schedule adapter to deal with the Schedule list view
 */
public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.ViewHolder> {

    private Context context;
    private List<Schedule> list;
    private onClickListener sOnClickListener;

    public MonthlyAdapter(Context context, List<Schedule> list, onClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.sOnClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.monthly_schedule_list, parent, false);
        return new ViewHolder(v, sOnClickListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView text_name, text_start, text_end, text_description;
        onClickListener onClickListener;

        public ViewHolder(View itemView, onClickListener onClickListener) {
            super(itemView);

            text_name = itemView.findViewById(R.id.text_name);
            text_start = itemView.findViewById(R.id.text_start);
            text_end = itemView.findViewById(R.id.text_end);
            text_description = itemView.findViewById(R.id.text_description);
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
