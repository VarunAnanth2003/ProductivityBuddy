package com.example.productivitybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivityDebug";

    public int SMS_PERMISSION_CODE;
    public int SEND_SMS_PERMISSION_CODE;
    private NotificationManager nm;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        nm = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_CODE);
                }
            } else {
                //TODO: Handle Denial
            }
        }
        if (requestCode == SEND_SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                //TODO: Handle Denial
            }
        }
    }

    public void openLists(View view) {
        if (nm.isNotificationPolicyAccessGranted() &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(this, ListOfStatuses.class);
            startActivity(i);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Hey! you can't do that just yet...")
                    .setMessage("You need to allow certain permissions for this app to work.")
                    .setPositiveButton("Open Permissions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            askForPermission();
                        }
                    })
                    .setNegativeButton("Nevermind", null)
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .show();
        }
    }

    public void askForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
        }
        if (!nm.isNotificationPolicyAccessGranted()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Do Not Disturb Permissions")
                    .setMessage("Please enable Do Not Disturb Permissions for this app within your device's settings. I can redirect you there!")
                    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent openSettings = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(openSettings);
                        }
                    })
                    .setNegativeButton("No Thanks", null)
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .show();
        }

    }

    public void quitApp(View view) {
        finish();
    }
}
