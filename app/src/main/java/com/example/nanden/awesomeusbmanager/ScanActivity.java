package com.example.nanden.awesomeusbmanager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by nanden on 11/14/17.
 */

public class ScanActivity extends AppCompatActivity {

    public static Intent startIntent(Context context) {
        return new Intent(context, ScanActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
    }

}
