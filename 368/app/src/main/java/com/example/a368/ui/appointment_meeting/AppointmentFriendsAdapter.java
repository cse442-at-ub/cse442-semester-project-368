package com.example.a368.ui.appointment_meeting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;
import com.example.a368.ui.friends.Friend;
import com.example.a368.ui.friends.FriendProfileActivity;

import java.util.ArrayList;

public class AppointmentFriendsAdapter extends RecyclerView.Adapter<AppointmentFriendsAdapter.ViewHolder> {

    ArrayList<Friend> list;
    Context mContext;
    public AppointmentFriendsAdapter(ArrayList<Friend> friends, Context context) {
        list = friends;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvEmail.setText(list.get(position).getEmail());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(new Intent(mContext, createAppointment.class));
                intent.putExtra("name", list.get(position).getName());
                intent.putExtra("email", list.get(position).getEmail());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvEmail;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.friend_name);
            tvEmail = itemView.findViewById(R.id.friend_email);
            layout = itemView.findViewById(R.id.friendsLayout);
        }
    }
}
