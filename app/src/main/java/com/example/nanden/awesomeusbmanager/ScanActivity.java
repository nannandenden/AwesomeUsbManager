package com.example.nanden.awesomeusbmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by nanden on 11/14/17.
 */

public class ScanActivity extends AppCompatActivity {

    private static final String LOG_TAG = ScanActivity.class.getSimpleName();
    EditText etCode;
    Button btnFindLocker;
    Button btnScanAgain;
    TextView tvDescription;
    TextView tvLockerNumber;

    public static Intent startIntent(Context context) {
        return new Intent(context, ScanActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        etCode = findViewById(R.id.etScan);
        btnFindLocker = findViewById(R.id.btnFindLocker);
        btnScanAgain = findViewById(R.id.btnScanAgain);
        btnScanAgain.setEnabled(false);
        tvDescription = findViewById(R.id.tvDescription);
        tvLockerNumber = findViewById(R.id.tvLockerNumber);

        btnFindLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findLocker();
            }
        });

        btnScanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etCode.setText("");
                etCode.requestFocus();
                tvLockerNumber.setText("");
                tvDescription.setText("");
                findLocker();
                btnScanAgain.setEnabled(false);
            }
        });

    }

    private void findLocker() {
        if (TextUtils.isEmpty(etCode.getText())) {
            Toast.makeText(ScanActivity.this, "There is no input. The Package Code field cannot be empty", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "no input");
        } else {
            Log.d(LOG_TAG, "input is " + etCode.getText().toString());
            Locker locker = LockerDatabaseHelper.getsInstance(ScanActivity.this).getLockerNumber(etCode.getText().toString());
            if (locker != null) {
                tvDescription.setText("Your Locker Number is");
                tvLockerNumber.setText(locker.lockerNumber);
                btnScanAgain.setEnabled(true);
            } else {
                tvDescription.setText("There is no package for code for " + etCode.getText().toString());
                Toast.makeText(ScanActivity.this, "There is no package for code for " + etCode.getText().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
