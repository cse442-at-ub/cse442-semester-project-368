package com.example.a368.ui.appointment_meeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;
import com.example.a368.ui.friends.Friend;
import com.example.a368.ui.friends.FriendsListAdapter;

import java.util.ArrayList;

public class AppointmentMeetingFragment extends Fragment {

    private AppointmentMeetingViewModel appointmentMeetingViewModel;
    RecyclerView.LayoutManager layoutManager;
    AppointmentFriendsAdapter mAdapter;
    ArrayList<Friend> list = new ArrayList<Friend>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appointmentMeetingViewModel =
                ViewModelProviders.of(this).get(AppointmentMeetingViewModel.class);
        View root = inflater.inflate(R.layout.appointment_layout, container, false);

        Friend f1 = new Friend("Mario Speedwagon", "mario@speedwagon.com");
        Friend f2 = new Friend("Petey Cruiser", "petey@cruiser.com");
        Friend f3 = new Friend("Anna Sthesia", "anna@sthesia.com");
        Friend f4 = new Friend("Paul Molive", "paul@molive.com");
        Friend f5 = new Friend("Anna Mull", "anna@mull.com");
        Friend f6 = new Friend("Gail Forcewind", "gail@forewind.com");
        Friend f7 = new Friend("Paige Turner", "paige@turner.com");
        Friend f8 = new Friend("Walter Melon", "walter@melon.com");

        list.add(f1);
        list.add(f2);
        list.add(f3);
        list.add(f4);
        list.add(f5);
        list.add(f6);
        list.add(f7);
        list.add(f8);

        RecyclerView recyclerView = root.findViewById(R.id.friendListRecycler);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new AppointmentFriendsAdapter(list, this.getContext());
        recyclerView.setAdapter(mAdapter);



        return root;
    }
}