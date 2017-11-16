package com.example.nanden.awesomeusbmanager;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * this application is to find usb connected devices.
 * In order to communicate with the connected device, you need to implement the method using UsbInterface, UsbEndPoint,
 * UsbDeviceConnection, UsbRequest
 */

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // by giving a pending intent to another application, you are granting the right to perform
    // the operation you have specified as if the other application was yourself
    // PendingIntent is a token that you give to foreign application(3rd party application) which allow foreign application to use
    // your application's permission to execute a predefined piece of code. Give to driver board to execute the code inside the kiosk app..?
    // https://stackoverflow.com/questions/2808796/what-is-an-android-pendingintent
    PendingIntent mPermissionIntent;
    Button btnUsb;
    TextView tvInfo;
    Button btnInterface;
    TextView tvInterface;
    Button btnScan;
    // allows you to emulate and communicate with connected USB devices
    UsbManager usbManager;
    // Represents a connected USB devices. contains the method to access it's device information.
    // Also, devices's interface and endpoint information
    UsbDevice usbDevice;
    // save all the device interfaces
    Set<UsbInterface> interfaceSet;

    private static final int REQUEST_CODE = 0;
    // defining this application's permission
    private static final String ACTION_USB_PERMISSION = "com.example.nanden.awesomeusbmanager.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        interfaceSet = new HashSet<>();

        btnUsb = findViewById(R.id.btnUsb);
        tvInfo = findViewById(R.id.tvInfo);
        btnInterface = findViewById(R.id.btnInterface);
        tvInterface = findViewById(R.id.tvInterface);
        btnScan = findViewById(R.id.btnScan);

        btnUsb.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                tvInfo.setText("");
                checkInfo();
            }
        });

        btnInterface.setEnabled(false);
        btnInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayEndpointInfo();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ScanActivity.startIntent(MainActivity.this));
            }
        });
    }

    // listen for intents that gets broadcast when call requestPermission().
    // requestPermission() display a dialog to the user asking for permission to connect to the device.
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(LOG_TAG, "action: " + action);
            if (action.equals(ACTION_USB_PERMISSION)) {
                Log.d(LOG_TAG, "before synchronized: " + usbManager.getDeviceList().isEmpty());
                synchronized (this) {
                    // obtaining the attached device
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {
                            Log.d(LOG_TAG, "usbDevice.getDeviceProtocol(): " + usbDevice.getDeviceProtocol());
                        }
                    } else {
                        Log.d(LOG_TAG, "permission denied");
                    }
                }
                Log.d(LOG_TAG, "after synchronized: " + usbManager.getDeviceList().isEmpty());
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkInfo() {
        // get the system-level service by name
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // ask user for the USB permission
        mPermissionIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, new Intent(ACTION_USB_PERMISSION), 0);
        // you can use intentfilter to specify the type of intent you would like to receive.
        // in here we are specifying to receive ACTION_USB_PERMISSION
        // use intentfilter to discover the connected USB
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        Log.d(LOG_TAG, "registering the receiver");
        registerReceiver(mUsbReceiver, intentFilter);
        // what is the string value for this....?
        // if successfully obtain all the attached devices, get the devices list into hashmap
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        StringBuilder deviceInfo = new StringBuilder();
        if (!deviceList.isEmpty()) {
            for (Map.Entry<String, UsbDevice> device : deviceList.entrySet()) {
                usbManager.requestPermission(device.getValue(), mPermissionIntent);
                deviceInfo.append("device.getKey():\t" + device.getKey());
                deviceInfo.append("\nusbDevice.getDeviceName():\t" + device.getValue().getDeviceName());
                deviceInfo.append("\nusbDevice.getProductName():\t" + device.getValue().getProductName());
                deviceInfo.append("\nusbDevice.getVendorId():\t" + device.getValue().getVendorId());
                deviceInfo.append("\nusbDevice.getProductId():\t" + device.getValue().getProductId());
                deviceInfo.append("\ndevice.getValue().getInterfaceCount():\t" + device.getValue().getInterfaceCount());
                deviceInfo.append("\nusbDevice.getInterface(0).getName():\t" + device.getValue().getInterface(0).getName());
                deviceInfo.append("\ndevice.getValue().getInterface(0).getEndpointCount(): \t" + device.getValue().getInterface(0).getEndpointCount());
                // add all the interfaces into the HashSet
                if (device.getValue().getInterfaceCount() != 0) {
                    btnInterface.setEnabled(true);
                    for (int i = 0; i < device.getValue().getInterfaceCount(); i++) {
                        interfaceSet.add(device.getValue().getInterface(i));
                    }
                }
            }
        } else {
            Toast.makeText(this, "No USB device detected", Toast.LENGTH_LONG).show();
        }
        Log.d(LOG_TAG, deviceInfo.toString());
        tvInfo.setText(deviceInfo.toString());
    }

    @SuppressLint("WrongConstant")
    private void displayEndpointInfo() {
        StringBuilder endpointInfo = new StringBuilder();
        for (UsbInterface item: interfaceSet) {
            for (int i = 0; i < item.getEndpointCount(); i++) {
                UsbEndpoint endpoint = item.getEndpoint(i);
                endpointInfo.append("endpoint.getEndpointNumber():\t" + endpoint.getEndpointNumber() + "\n");
                endpointInfo.append("endpoint.getDirection():\t" + endpoint.getDirection() + "\n");
                endpointInfo.append("endpoint.getAddress():\t" + endpoint.getAddress() + "\n");
                endpointInfo.append("endpoint.describeContents():\t" + endpoint.describeContents() + "\n");
                endpointInfo.append("endpoint.getType():\t" + endpoint.getType() + "\n");
                endpointInfo.append("endpoint.getAttributes():\t" + endpoint.getAttributes() + "\n");
                endpointInfo.append("endpoint.getInterval():\t" + endpoint.getInterval() + "\n");
                endpointInfo.append("endpoint.getMaxPacketSize():\t" + endpoint.getMaxPacketSize() + "\n");
            }
        }
        if (!endpointInfo.toString().isEmpty()) {
            tvInterface.setText(endpointInfo.toString());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUsbReceiver != null) {
            Log.d(LOG_TAG, "unregister the receiver");
            unregisterReceiver(mUsbReceiver);
        }
    }

}

/**
 * References:
 * https://developer.android.com/guide/topics/connectivity/usb/host.html
 *
 */
