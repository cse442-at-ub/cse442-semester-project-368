package com.example.a368.ui.friends;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment implements FriendsListAdapter.onClickListener {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend/fetch_friend.php";
    private FriendsViewModel friendsViewModel;
    private RecyclerView fList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Friend> friendList;
    private RecyclerView.Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendsViewModel =
                ViewModelProviders.of(this).get(FriendsViewModel.class);
        View root = inflater.inflate(R.layout.friends_layout, container, false);

        FloatingActionButton fabAddFriend = root.findViewById(R.id.add_friend);
        fabAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddFriendActivity.class));
            }
        });

        fList = (RecyclerView)root.findViewById(R.id.friendListRecycler);
        friendList = new ArrayList<>();
        adapter = new FriendsListAdapter(getContext(), friendList, this);

        linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(fList.getContext(), linearLayoutManager.getOrientation());

        fList.setHasFixedSize(true);
        fList.setLayoutManager(linearLayoutManager);
        fList.addItemDecoration(dividerItemDecoration);
        fList.setAdapter(adapter);

        return root;
    }

    // On click schedule item
    @Override
    public void onClickFriend(int position) {
        Intent intent = new Intent(getContext(), FriendProfileActivity.class);
        intent.putExtra("name", friendList.get(position).getName());
        intent.putExtra("email", friendList.get(position).getEmail());
        startActivity(intent);
    }

    // Updates view friends list
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    // Fetch JSON data to display existing friends list
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

                adapter.notifyDataSetChanged();
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