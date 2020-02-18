package com.example.a368.ui.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FriendsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FriendsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is friends fragment " +
                "\nPlease add features to add friends and list down current friends and see their schedules.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}