package com.example.productivitybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfStatuses extends AppCompatActivity {
    private String TAG = "ListOfStatusesDebug";
    public Constants c = new Constants();
    private Button enableButton;
    private StatusService ss = new StatusService();
    private Context mContext;
    private StatusBuilder currentStatus = new StatusBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_statuses);
        mContext = getApplicationContext();
        enableButton = findViewById(R.id.list_ActivateStatusButton);
        updateButtonText();
        Intent intent = getIntent();
        if(intent.hasExtra("name")) {
            getStatusData(intent);
        }
    }
    public void openStatusMaker(View view) {
        Intent i = new Intent(this, StatusBuilderActivity.class);
        startActivity(i);
        finish();
    }
    private void getStatusData(Intent i) {
        StatusBuilder sb = new StatusBuilder(i.getStringExtra("name"),
                i.getStringExtra("message"),
                i.getStringExtra("escapeMessage"),
                i.getStringArrayListExtra("nti"));
        setCurrentStatus(sb);
    }
    private void setCurrentStatus(StatusBuilder conformToStatus) { //Use this to set the currently clicked status from the list to the class variable
        currentStatus = conformToStatus;
    }

    public void testStatus(View view) {
        updateButtonText();
        if (ss.isActive(c.STATUS_SERVICE_NAME, mContext)) {
            Intent i = new Intent(mContext, StatusService.class);
            stopService(i);
        } else {
            startForeground();
        }
        updateButtonText();
    }

    public void startForeground() {
        Intent serviceIntent = new Intent(this, StatusService.class);
        serviceIntent.putExtra("name", currentStatus.name);
        serviceIntent.putExtra("message", currentStatus.message);
        serviceIntent.putExtra("escapeMessage", currentStatus.escapeMessage);
        serviceIntent.putStringArrayListExtra("numbersToIgnore", currentStatus.numbersToIgnore);
        startForegroundService(serviceIntent);
    }

    public void updateButtonText() {
        if (ss.isActive(c.STATUS_SERVICE_NAME, mContext)) {
            enableButton.setText("Disable Status");
        } else {
            enableButton.setText("Enable Status");
        }
    }

    //Use this for statuses
    public static class StatusBuilder {
        String name;
        String message;
        String escapeMessage;
        ArrayList<String> numbersToIgnore;

        StatusBuilder() {
            this.name = "Status: Default Do Not Disturb";
            this.message = "Hi there! Sorry, but this is an automated message. I can't reply at the moment. I'll get back to you soon!";
            this.escapeMessage = "urgent";
            this.numbersToIgnore = new ArrayList<>(Arrays.asList("+14256773306", "+14258940404", "+14258940401")); //So Sherisse/Mom/Dad won't get mad.
        }

        public StatusBuilder(String name, String message, String escapeMessage, ArrayList<String> numbersToIgnore) {
            this.name = name;
            this.message = message;
            this.escapeMessage = escapeMessage;
            this.numbersToIgnore = numbersToIgnore;
        }
    }
}
