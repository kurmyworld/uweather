package com.uweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.uweather.app.service.AutoUpdateService;

/**
 * Created by ringr on 2016/2/8.
 */
public class AutoUpdateReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
