package com.example.a368.ui.appointment_meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;
import com.example.a368.ui.monthly.MonthlyAdapter;

import java.util.ArrayList;

public class MeetingTimesAdapter extends RecyclerView.Adapter<MeetingTimesAdapter.ViewHolder> {
    ArrayList<String> list;
    private Context mContext;
    private onClickListener sOnClickListener;

    public MeetingTimesAdapter(ArrayList<String> times, Context context, onClickListener onClickListener) {
        list = times;
        mContext = context;
        this.sOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_list_item, parent, false);
        return new ViewHolder(view, sOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvAvailable.setText(""+list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAvailable;
        LinearLayout layout;
        onClickListener onClickListener;

        public ViewHolder(@NonNull View itemView,  onClickListener onClickListener) {
            super(itemView);

            tvAvailable = itemView.findViewById(R.id.tvAvailable);
            layout = itemView.findViewById(R.id.available_layout);
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
