package com.example.productivitybuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiverDebug";
    public Constants c = new Constants();
    private StatusService statusService = new StatusService();
    private String message = "";
    private String number = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        message = "";
        if (statusService.isActive(c.STATUS_SERVICE_NAME, context)) {
            Bundle bundle = intent.getExtras();
            String format = bundle.getString("format");
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            getMessages(messages, pdus, format, context);
        }
    }
    public void getMessages(SmsMessage[] messages, Object[] pdus, String format, Context context) {
        for (int i = 0; i < messages.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            message += messages[i].getMessageBody();
        }
        number = messages[0].getOriginatingAddress();
        if (number.length() >= 10) {
            statusService.putDataHere(message, number, context);
        }
    }
}
