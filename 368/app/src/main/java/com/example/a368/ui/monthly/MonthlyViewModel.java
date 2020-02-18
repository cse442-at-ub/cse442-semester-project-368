package com.example.a368.ui.monthly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MonthlyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MonthlyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is monthly fragment \nPlease add monthly calendar API UI here.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}