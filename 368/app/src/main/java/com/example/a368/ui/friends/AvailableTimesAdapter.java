package com.example.a368.ui.friends;

import android.content.Context;
import android.text.Layout;
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

public class AvailableTimesAdapter extends RecyclerView.Adapter<AvailableTimesAdapter.ViewHolder> {
    ArrayList<String> list = new ArrayList<String>();
    private Context mContext;

    public AvailableTimesAdapter(ArrayList<String> times, Context context) {
        list = times;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvAvailable.setText(list.get(position));
        holder.layout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Appointment added: " +list.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAvailable;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAvailable = itemView.findViewById(R.id.tvAvailable);
            layout = itemView.findViewById(R.id.available_layout);

        }
    }
}
