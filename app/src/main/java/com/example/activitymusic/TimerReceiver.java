package com.example.activitymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TienNAb", "onReceive: ");
        Intent myIntent = new Intent(context, MediaPlaybackService.class);
        context.stopService(myIntent);
    }
}
