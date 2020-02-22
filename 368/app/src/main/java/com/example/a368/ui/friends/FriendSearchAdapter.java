package com.example.a368.ui.friends;

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

import java.util.ArrayList;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder> {

    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> listCopy = new ArrayList<String>();

    private Context mContext;

    public FriendSearchAdapter(ArrayList<String> friends, Context context) {
        list = friends;
        listCopy.addAll(friends);
        mContext = context;
    }

    public void filter(String text) {
        list.clear();
        if(text.isEmpty()) {
            list.addAll(listCopy);
        } else {
            text = text.toLowerCase();
            for(String s : listCopy) {
                if(s.toLowerCase().contains(text)) {
                    list.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position));
        holder.layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, list.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            layout = itemView.findViewById(R.id.searchLayout);

        }

    }
}
