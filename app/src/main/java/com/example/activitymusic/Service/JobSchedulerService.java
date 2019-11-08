package com.example.activitymusic.Service;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.activitymusic.Activity.MainActivityMusic;
import com.example.activitymusic.Adapter.BannerAdapter;
import com.example.activitymusic.Model.Notification;
import com.example.activitymusic.Model.SongOnline;
import com.example.activitymusic.R;
import com.example.activitymusic.Server.APIServer;
import com.example.activitymusic.Server.DataServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JobSchedulerService extends JobService {
    private static final String TAG = "JobService";
    private boolean jobCancelled = false;

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;
    public static final String Notification_ID = "NotificationServer";
    private SharedPreferences mSharedPreferences;
    private String sharePrefFile = "SongSharedPreferences";

    public JobSchedulerService() {
    }


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mSharedPreferences = getSharedPreferences(sharePrefFile, MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    Notification_ID,
                    "Notification Server",
                    NotificationManager.IMPORTANCE_LOW
            );
            musicServiceChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            musicServiceChannel.enableVibration(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(musicServiceChannel);
        }
        doBackground(jobParameters);
        return true;
    }

    private void doBackground(final JobParameters jobParameters) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i > 0; i++) {
                    if (jobCancelled) {
                        return;
                    }

                    DataServer dataServer = APIServer.getServer();
                    Call<List<Notification>> callback = dataServer.getDataNotification();
                    callback.enqueue(new Callback<List<Notification>>() {
                        @Override
                        public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                            ArrayList<Notification> notifications = (ArrayList<Notification>) response.body();
                            String id_notification = mSharedPreferences.getString("IDNotification", "0");
                            Log.d(TAG, "onResponse: " + id_notification);
                            if(notifications!=null)
                            for (int i = 0; i < notifications.size(); i++) {
                                if (Integer.parseInt(id_notification) < Integer.parseInt(notifications.get(i).getID())) {
                                    onShowNotification(notifications.get(i));

                                }

                            }

                            Log.d(TAG, "onResponse: " + notifications.get(0).getDATE());
                        }

                        @Override
                        public void onFailure(Call<List<Notification>> call, Throwable t) {

                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                jobFinished(jobParameters, false);
            }
        }).start();

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancelled = true;
        return true;
    }

    void onShowNotification(Notification notification) {
        Intent intent = new Intent(this, MainActivityMusic.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notificationManager = NotificationManagerCompat.from(getApplication());
        builder = new NotificationCompat.Builder(getApplication(), Notification_ID);
        builder.setContentTitle("Music Online")
                .setContentText(notification.getTITLE())
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getCONTENT())
                        .setBigContentTitle("News").setSummaryText("Notification Music Online")
                )
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManager.notify(Integer.parseInt(notification.getID()), builder.build());

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("IDNotification", notification.getID());
        editor.apply();
    }
}
