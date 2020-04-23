package com.example.a368.ui.appointment_meeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.a368.R;
import com.google.android.material.tabs.TabLayout;

public class MeetingViewPager extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpager, container, false);
        ViewPager pager = (ViewPager)root.findViewById(R.id.view_pager);
        pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);

        return root;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return MeetingFragment.newInstance("FirstFragment: Create Meeting");
                case 1: return MeetingRequestFragment.newInstance("SecondFragment: Meeting Request");
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Create Meeting";
                case 1:
                    return "Meeting Request";
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
