package com.example.a368.ui.appointment_meeting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a368.R;
import com.example.a368.User;
import com.example.a368.ui.friends.Friend;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MeetingFragment extends Fragment {

    private SearchView searchView;
    private LinearLayoutManager linearLayoutManager;
    private MeetingViewModel meetingViewModel;
    RecyclerView.LayoutManager layoutManager;
    MeetingFriendsAdapter mAdapter;
    private DividerItemDecoration dividerItemDecoration;
    ArrayList<Friend> friendList = new ArrayList<Friend>();
    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend/fetch_friend.php";

    public static MeetingFragment newInstance(String text) {

        MeetingFragment f = new MeetingFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meetingViewModel =
                ViewModelProviders.of(this).get(MeetingViewModel.class);
        View root = inflater.inflate(R.layout.appointment_layout, container, false);

        FloatingActionButton fabAddFriend = root.findViewById(R.id.create_appointment);
        fabAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.getSelectedList().size() == 0) {
                    Toast.makeText(getContext(), "You must select at least one friend.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getContext(), CreateAppointmentActivity.class);
                    intent.putParcelableArrayListExtra("List", mAdapter.getSelectedList());
                    startActivity(intent);
                }
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.friendListRecycler);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        getData();
        linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new MeetingFriendsAdapter(friendList, this.getContext());
        recyclerView.setAdapter(mAdapter);

        searchView = (SearchView) root.findViewById(R.id.meeting_friend_search);
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

        return root;
    }

    // Update
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONArray response) {
                friendList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("email_a").equals(User.getInstance().getEmail())) {

                            Friend friend = new Friend();

                            friend.setID((jsonObject.getInt("id")));
                            friend.setName(jsonObject.getString("name_b"));
                            friend.setEmail(jsonObject.getString("email_b"));
                            friendList.add(friend);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                // Sort by alphabetical order
                // TODO

                mAdapter.notifyDataSetChanged();
                mAdapter.update();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }
}