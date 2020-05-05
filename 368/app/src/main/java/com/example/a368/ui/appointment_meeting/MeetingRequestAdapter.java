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

    public MeetingRequestAdapterListener onClickListener;

    public interface MeetingRequestAdapterListener {
        void statusOnClick(View v, int position);
        void actionOnClick(View v, int position);
    }

    public MeetingRequestAdapter(ArrayList<MeetingRequest> list, Context context, MeetingRequestAdapterListener listener) {
        this.list = list;
        mContext = context;
        onClickListener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_request_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvMeetingTitle.setText(list.get(position).getTitle());
        holder.tvMeetingTime.setText(String.format("%s %s - %s %s", list.get(position).getStart_date(), list.get(position).getStart_time(), list.get(position).getEnd_date(), list.get(position).getEnd_time()));
        holder.tvMeetingParticipants.setText(list.get(position).getParticipants());

        if (list.get(position).getDescription().equals("")) {
            holder.tvMeetingDescription.setVisibility(View.GONE);
        } else {
            holder.tvMeetingDescription.setText(list.get(position).getDescription());
        }

        holder.btnStatus.setText(list.get(position).getStatus());

        // Set button text differently based on the current request status
        if (list.get(position).getStatus().equals("Accepted")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusGreen), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
            holder.btnAction.setText("Delete");
        } else if (list.get(position).getStatus().equals("Pending")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusYellow), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
            holder.btnAction.setText("Cancel");
        } else if (list.get(position).getStatus().equals("Confirm")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusBlue), PorterDuff.Mode.MULTIPLY);
            holder.btnAction.setText("Reject");
        } else if (list.get(position).getStatus().equals("Rejected")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusRed), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
            holder.btnAction.setText("Delete");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMeetingTitle;
        TextView tvMeetingTime;
        TextView tvMeetingParticipants;
        TextView tvMeetingDescription;
        Button btnStatus;
        Button btnAction;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMeetingTitle = itemView.findViewById(R.id.tvMeetingReqTitle);
            tvMeetingTime = itemView.findViewById(R.id.tvMeetingReqTime);
            tvMeetingParticipants = itemView.findViewById(R.id.tvMeetingReqParticipants);
            tvMeetingDescription = itemView.findViewById(R.id.tvMeetingReqDescription);
            btnStatus = itemView.findViewById(R.id.status);
            btnAction = itemView.findViewById(R.id.status_action);
            layout = itemView.findViewById(R.id.meetingReqLayout);

            btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.statusOnClick(v, getAdapterPosition());
                }
            });

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.actionOnClick(v, getAdapterPosition());
                }
            });
        }

    }

}
