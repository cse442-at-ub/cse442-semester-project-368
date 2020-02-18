package com.example.a368.ui.today;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TodayViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TodayViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Today fragment \nPlease add daily schedule UI here.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}