package com.example.a368.ui.weekly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeeklyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WeeklyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is weekly fragment \nPlease add weekly schedule UI here.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}