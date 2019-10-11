package com.example.activitymusic.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.activitymusic.Service.MediaPlaybackService;

public class TimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, MediaPlaybackService.class);
        context.stopService(myIntent);
    }
}
