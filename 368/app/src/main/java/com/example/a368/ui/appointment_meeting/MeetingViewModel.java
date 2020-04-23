package com.example.a368.ui.appointment_meeting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeetingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MeetingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is appointment_meeting fragment \nPlease add Doodle features to set up a group meeting schedule.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}