package com.example.nanden.awesomeusbmanager;

import android.app.Application;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nanden on 11/15/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        // instantiate the locker into sqlite database
        LockerDatabaseHelper.getsInstance(this).addListOfLocker(createLockerList());
    }

    private List<Locker> createLockerList() {
        List<Locker> lockers = new ArrayList<>();
        String[] strings = getResources().getStringArray(R.array.locker_number);
        for (int i = 0; i < strings.length; i++) {
            lockers.add(new Locker(strings[i], String.valueOf(i+10)));
        }
        return lockers;
    }
}
