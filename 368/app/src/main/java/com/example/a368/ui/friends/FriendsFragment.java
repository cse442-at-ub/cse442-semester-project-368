package com.example.a368.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a368.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        Friend f1 = new Friend("Mario Speedwagon", "mario@speedwagon.com");
        Friend f2 = new Friend("Petey Cruiser", "petey@cruiser.com");
        Friend f3 = new Friend("Anna Sthesia", "anna@sthesia.com");
        Friend f4 = new Friend("Paul Molive", "paul@molive.com");
        Friend f5 = new Friend("Anna Mull", "anna@mull.com");
        Friend f6 = new Friend("Gail Forcewind", "gail@forewind.com");
        Friend f7 = new Friend("Paige Turner", "paige@turner.com");
        Friend f8 = new Friend("Walter Melon", "walter@melon.com");

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

    }
}