package com.example.a368.ui.appointment_meeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.a368.R;

public class MeetingRequestFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.second_frag, container, false);

        TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static MeetingRequestFragment newInstance(String text) {

        MeetingRequestFragment f = new MeetingRequestFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
