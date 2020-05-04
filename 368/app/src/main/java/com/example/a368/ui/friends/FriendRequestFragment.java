package com.example.a368.ui.friends;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendRequestFragment extends Fragment {

    private static String url = "https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442w/friend_request/fetch_friend_request.php";
    private RecyclerView rList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private ArrayList<FriendRequest> requestList;
    private FriendRequestAdapter requestAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friend_request, container, false);

        rList = (RecyclerView)root.findViewById(R.id.friend_request_recycler);
        requestList = new ArrayList<>();
        requestAdapter = new FriendRequestAdapter(getContext(), requestList);

        linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(rList.getContext(), linearLayoutManager.getOrientation());

        rList.setLayoutManager(linearLayoutManager);
        rList.addItemDecoration(dividerItemDecoration);
        rList.setAdapter(requestAdapter);

        return root;
    }

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
                requestList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        // check to display the logged-in user's schedule only && display today's schedule only
                        if (jsonObject.getString("sender_email").equals(User.getInstance().getEmail())) {

                            FriendRequest request = new FriendRequest();

                            request.setID((jsonObject.getInt("id")));
                            request.setSender_name(User.getInstance().getName());
                            request.setSender_email(User.getInstance().getEmail());
                            request.setReceiver_name(jsonObject.getString("receiver_name"));
                            request.setReceiver_email(jsonObject.getString("receiver_email"));
                            request.setStatus(jsonObject.getString("status"));
                            requestList.add(request);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }

                // Sort by alphabetical order
                // TODO

                requestAdapter.notifyDataSetChanged();
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

    public static FriendRequestFragment newInstance(String text) {
        FriendRequestFragment f = new FriendRequestFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

}
