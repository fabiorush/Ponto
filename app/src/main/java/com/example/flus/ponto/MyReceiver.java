package com.example.flus.ponto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.example.flus.ponto.PontoIntentService.ACTION_MARCAR;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        if (intent != null) {
            Intent intent1 = new Intent(intent);
            intent1.setClass(context, PontoIntentService.class);
            context.startService(intent1);
        }
    }
}
