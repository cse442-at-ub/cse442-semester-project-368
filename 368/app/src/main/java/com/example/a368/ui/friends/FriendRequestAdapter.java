package com.example.a368.ui.friends;

import android.content.Context;
import android.graphics.Color;
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

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private List<FriendRequest> list;
    Context context;
    private onClickListener sOnClickListener;

    public FriendRequestAdapter(Context context, List<FriendRequest> requests, onClickListener onClickListener) {
        this.list = requests;
        this.context = context;
        this.sOnClickListener = onClickListener;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend_request_item, parent, false);
        ViewHolder holder = new ViewHolder(v, sOnClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position).getReceiver_name());
        holder.tvEmail.setText(list.get(position).getReceiver_email());
        holder.btnStatus.setText(list.get(position).getStatus());

        // Set button text differently based on the current request status
        if (list.get(position).getStatus().equals("Accepted")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusGreen), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
//            holder.btnAction.setText("Delete");
        } else if (list.get(position).getStatus().equals("Pending")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusYellow), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
//            holder.btnAction.setText("Cancel");
        } else if (list.get(position).getStatus().equals("Confirm")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusBlue), PorterDuff.Mode.MULTIPLY);
//            holder.btnAction.setText("Reject");
        } else if (list.get(position).getStatus().equals("Rejected")) {
            holder.btnStatus.getBackground().setColorFilter(holder.btnStatus.getContext().getResources().
                    getColor(R.color.colorStatusRed), PorterDuff.Mode.MULTIPLY);
            holder.btnStatus.setEnabled(false);
//            holder.btnAction.setText("Delete");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // implements onClick...
        TextView tvName;
        TextView tvEmail;
        Button btnStatus;
//        Button btnAction;
        LinearLayout layout;
        onClickListener onClickListener;

        public ViewHolder(@NonNull View itemView, onClickListener onClickListener) {
            super(itemView);

            tvName = itemView.findViewById(R.id.request_name);
            tvEmail = itemView.findViewById(R.id.request_email);
            btnStatus = itemView.findViewById(R.id.status);
//            btnAction = itemView.findViewById(R.id.status_action);
            layout = itemView.findViewById(R.id.friend_request_item_layout);

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
