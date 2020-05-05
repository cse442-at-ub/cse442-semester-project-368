package com.example.a368.ui.appointment_meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.media.RatingCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;
import com.example.a368.ui.friends.Friend;

import java.util.ArrayList;
import java.util.List;

public class MeetingFriendsAdapter extends RecyclerView.Adapter<MeetingFriendsAdapter.ViewHolder> {

    boolean[] selected;
    ArrayList<Friend> list;
    Context mContext;
    ArrayList<Friend> selectedList;
    ArrayList<Friend> listCopy;

    public MeetingFriendsAdapter(ArrayList<Friend> friends, Context context) {
        this.list = friends;
        this.listCopy = friends;
        Log.d("sizeIN: ", ""+list.size());
        this.mContext = context;
        this.selectedList = new ArrayList<>();
    }

    public void update() {
        selected = new boolean[list.size()];
    }

    public ArrayList<Friend> getSelectedList() {
        selectedList.clear();
        for(int i = 0; i < list.size(); i++) {
            if(selected[i]) {
                selectedList.add(list.get(i));
            }
        }
        return selectedList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    public void filter(String text) {
        ArrayList<Friend> temp_list = new ArrayList<>();
        if(text.isEmpty()) {
            temp_list.addAll(listCopy);
        } else {
            text = text.toLowerCase();
            for(Friend f : listCopy) {
                if(f.getName().toLowerCase().contains(text) || f.getEmail().toLowerCase().contains(text)) {
                    temp_list.add(f);
                }
            }
        }
        this.list = temp_list;
        notifyDataSetChanged();
    }

    int row_index = -1;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvEmail.setText(list.get(position).getEmail());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected[position] = !selected[position];
                notifyDataSetChanged();
            }
        });
        if(selected[position]) {
            holder.layout.setBackgroundColor(Color.parseColor("#BAECA63C"));
            holder.tvName.setTextColor(Color.parseColor("#ffffffff"));
            holder.tvEmail.setTextColor(Color.parseColor("#ffffffff"));
        }
        else {
            holder.layout.setBackgroundColor(Color.parseColor("#fafafafa"));
            holder.tvName.setTextColor(Color.parseColor("#8a000000"));
            holder.tvEmail.setTextColor(Color.parseColor("#8a000000"));
        }
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
