package com.example.a368.ui.friends;

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

import java.io.Serializable;
import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<Friend> list;
    Context context;
    private onClickListener sOnClickListener;

    public FriendsListAdapter(Context context, List<Friend> friends, onClickListener onClickListener) {
        this.list = friends;
        this.context = context;
        this.sOnClickListener = onClickListener;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friends_list_item, parent, false);
        return new ViewHolder(v, sOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Friend friend = list.get(position);

        holder.tvName.setText(list.get(position).getName());
        holder.tvEmail.setText(list.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvEmail;
        LinearLayout layout;
        onClickListener onClickListener;

        public ViewHolder(@NonNull View itemView, onClickListener onClickListener) {
            super(itemView);

            tvName = itemView.findViewById(R.id.friend_name);
            tvEmail = itemView.findViewById(R.id.friend_email);
            layout = itemView.findViewById(R.id.friendsLayout);
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClickFriend(getAdapterPosition());
        }
    }

    public interface onClickListener {
        void onClickFriend (int position);
    }
}
