package com.example.a368.ui.friends;

import android.content.Context;
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

    public FriendRequestAdapter(Context context, List<FriendRequest> requests) {
        this.list = requests;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend_request_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position).getReceiver_name());
        holder.tvEmail.setText(list.get(position).getReceiver_email());
        holder.btnStatus.setText(list.get(position).getStatus());

        // Set button text differently based on the current request status
        if (list.get(position).getStatus().equals("Accepted")) {
            holder.btnAction.setText("Delete");
        } else if (list.get(position).getStatus().equals("Pending")) {
            holder.btnAction.setText("Cancel Request");
        } else if (list.get(position).getStatus().equals("Confirm")) {
            holder.btnAction.setText("Reject Request");
        } else if (list.get(position).getStatus().equals("Rejected")) {
            holder.btnAction.setText("Delete");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // implements onClick...
        TextView tvName;
        TextView tvEmail;
        Button btnStatus;
        Button btnAction;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.request_name);
            tvEmail = itemView.findViewById(R.id.request_email);
            btnStatus = itemView.findViewById(R.id.status);
            btnAction = itemView.findViewById(R.id.status_action);
            layout = itemView.findViewById(R.id.friend_request_item_layout);
        }

    }

}
