package com.example.a368.ui.friends;

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

public class FriendsViewPager extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.viewpager, container, false);
        ViewPager pager = (ViewPager)root.findViewById(R.id.view_pager);
        pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0) { // 0 = the first fragment in the ViewPager, in this case, the fragment i want to refresh its UI
                    FriendsFragment fragment = (FriendsFragment) pager.getAdapter().instantiateItem(pager, position);
                    fragment.onResume(); // here i call the onResume of the fragment, where i have the method updateUI() to update its UI
                    pager.getAdapter().notifyDataSetChanged();
                } else if (position == 1) {
                    FriendRequestFragment fragment = (FriendRequestFragment) pager.getAdapter().instantiateItem(pager, position);
                    fragment.onResume();
                    pager.getAdapter().notifyDataSetChanged();
                }
            }
        });

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
                case 0: return FriendsFragment.newInstance("FirstFragment: Friends List");
                case 1: return FriendRequestFragment.newInstance("SecondFragment: Friend Request");
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Friends";
                case 1:
                    return "Friend Request";
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

