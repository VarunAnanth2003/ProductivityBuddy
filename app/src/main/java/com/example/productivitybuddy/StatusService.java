package com.example.productivitybuddy;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.productivitybuddy.ListOfStatuses.StatusBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatusService extends Service {
    private String CHANNEL_ID = "ForegroundServiceChannel_StatusService";
    private static final String TAG = "StatusServiceDebug";
    public Constants c = new Constants();

    AudioManager audioManager;
    private static int prevRingerMode;

    private String message;
    private String number;

    private static ArrayList<String> sentNumbers = new ArrayList<>();

    private static StatusBuilder CurrentStatus;

    private Context mContext;
    Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        timer.purge();
        timer.scheduleAtFixedRate(new RefreshList(), 0, c.REFRESH_SENT_LIST_TIME);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            prevRingerMode = audioManager.getRingerMode();
        } else {
            prevRingerMode = AudioManager.RINGER_MODE_VIBRATE;
        }
    }

    class RefreshList extends TimerTask {
        @Override
        public void run() {
            sentNumbers.clear();
        }
    }

    class WaitToSilence extends TimerTask {
        @Override
        public void run() {
            silencePhone();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        CurrentStatus = new StatusBuilder(
                intent.getStringExtra("name"),
                intent.getStringExtra("message"),
                intent.getStringExtra("escapeMessage"),
                intent.getStringArrayListExtra("numbersToIgnore")
        );
        makeForegroundNotification();
        silencePhone();
        return START_NOT_STICKY;
    }

    public void putDataHere(String m, String n, Context c) {
        message = m;
        number = n;
        this.mContext = c;
        sendMessages();
    }

    public void sendMessages() {
        SmsManager smsManager = SmsManager.getDefault();
        String checkMessage = message;
        String escapeSeq = CurrentStatus.escapeMessage;
        ArrayList<String> numbersToIgnore = CurrentStatus.numbersToIgnore;
        if (checkMessage.trim().equalsIgnoreCase(escapeSeq.trim())) {
            makePhoneBuzz();
            createUrgentNotification((int) (Math.random() * 2976));
            smsManager.sendTextMessage(number, null, "User notified! They should respond soon.", null, null);
        } else if (!sentNumbers.contains(number)) {
            if (!numbersToIgnore.contains(number)) {
                sentNumbers.add(number);
                smsManager.sendTextMessage(number, null, CurrentStatus.message + "\nPlease type \"" + CurrentStatus.escapeMessage + "\" to get your message through.", null, null);
            } else {
                makePhoneBuzz();
            }
        }
    }

    private void makePhoneBuzz() {
        audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        audioManager.setRingerMode(prevRingerMode);
        timer.schedule(new WaitToSilence(), c.WAIT_TO_SILENCE_PHONE_TIME);
    }

    public void silencePhone() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void makeForegroundNotification() {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        createNotificationChannel();
        Notification foregroundNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(CurrentStatus.name)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Message: " + CurrentStatus.message + "\n\n"
                                + "Exceptions: " + CurrentStatus.numbersToIgnore.toString() + "\n\n"
                                + "Escape Word: " + "\"" + CurrentStatus.escapeMessage + "\""))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pi)
                .build();
        startForeground(99, foregroundNotification);
    }

    private void createUrgentNotification(int notificationID) {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, openAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification urgentNotification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("URGENT MESSAGE")
                .setContentText(number + " sent you an urgent message!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(notificationID, urgentNotification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onDestroy() {
        audioManager.setRingerMode(prevRingerMode);
        super.onDestroy();
    }

    public boolean isActive(String name, Context context) {
        boolean active = false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> listOfServices = am.getRunningServices(10);
        for (int i = 0; i < listOfServices.size(); i++) {
            if (listOfServices.get(i).service.getClassName().equals(name)) {
                active = true;
            } else {
                active = false;
            }
        }
        return active;
    }
}
