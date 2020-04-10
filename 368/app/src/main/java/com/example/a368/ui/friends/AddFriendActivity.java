package com.example.a368.ui.friends;

import android.os.Bundle;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.SearchManager;
import android.view.Menu;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import com.example.a368.R;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {

    RecyclerView.LayoutManager layoutManager;
    ArrayList<String> list = new ArrayList<String>();
    FriendSearchAdapter mAdapter;

    // Add customized menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Customize action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add New Friend");
        actionBar.setDisplayHomeAsUpEnabled(true);

        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);
//
//        list.add("Frank");
//        list.add("John");
//        list.add("Paul");
//        list.add("Garth");
//        list.add("Jack");
//        list.add("Robert");
//        list.add("Jeff");
//        list.add("Bill");
//        list.add("Cody");
//        list.add("Brett");
//        list.add("Brooke");
//
//
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        mAdapter = new FriendSearchAdapter(list,this);
//        recyclerView.setAdapter(mAdapter);
//
//


    }
}
