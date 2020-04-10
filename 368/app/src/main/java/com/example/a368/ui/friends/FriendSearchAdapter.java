package com.example.a368.ui.friends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder> {

    private List<Friend> listCopy;
    private List<Friend> list;
    private Context context;
    private onClickListener sOnClickListener;

    public FriendSearchAdapter(Context context, ArrayList<Friend> friends, onClickListener onClickListener) {
        this.list = friends;
        this.listCopy = friends;
        this.context = context;
        this.sOnClickListener = onClickListener;
        notifyDataSetChanged();
    }

    public void filter(String text) {
        List<Friend> temp_list = new ArrayList<>();
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
