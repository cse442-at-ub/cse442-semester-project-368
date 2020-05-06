package com.example.a368.ui.appointment_meeting;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;

import java.util.ArrayList;

public class MeetingRequestAdapter extends RecyclerView.Adapter<MeetingRequestAdapter.ViewHolder> {
    ArrayList<MeetingRequest> list;
    Context mContext;
    private onClickListener sOnClickListener;

    public MeetingRequestAdapter(ArrayList<MeetingRequest> list, Context context, onClickListener onClickListener) {
        this.list = list;
        mContext = context;
        this.sOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_request_item, parent, false);
        ViewHolder holder = new ViewHolder(view, sOnClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMeetingTitle.setText(list.get(position).getTitle());
        holder.tvMeetingTime.setText(String.format("%s %s - %s %s", list.get(position).getStart_date(), list.get(position).getStart_time(), list.get(position).getEnd_date(), list.get(position).getEnd_time()));
        holder.btnStatus.setText(list.get(position).getStatus());

        if (list.get(position).getDescription().equals("")) {
            holder.tvMeetingReqDescription.setVisibility(View.GONE);
        } else {
            holder.tvMeetingReqDescription.setText(list.get(position).getDescription());
        }

        // Set button text differently based on the current request status
        if (list.get(position).getStatus().equals("accepted")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusGreen), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
        } else if (list.get(position).getStatus().equals("pending")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusYellow), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
        } else if (list.get(position).getStatus().equals("confirm")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusBlue), PorterDuff.Mode.MULTIPLY);
        } else if (list.get(position).getStatus().equals("rejected")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusRed), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMeetingTitle;
        TextView tvMeetingTime;
        TextView tvMeetingReqDescription;
        Button btnStatus;
        LinearLayout layout;
        onClickListener onClickListener;

        public ViewHolder(@NonNull View itemView, onClickListener onClickListener) {
            super(itemView);

            tvMeetingTitle = itemView.findViewById(R.id.tvMeetingReqTitle);
            tvMeetingTime = itemView.findViewById(R.id.tvMeetingReqTime);
            tvMeetingReqDescription = itemView.findViewById(R.id.tvMeetingReqDescription);
            btnStatus = itemView.findViewById(R.id.meeting_status);
            layout = itemView.findViewById(R.id.meetingReqLayout);
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickRequest(getAdapterPosition());
        }
    }
    public interface onClickListener {
        void onClickRequest (int position);
    }
}
