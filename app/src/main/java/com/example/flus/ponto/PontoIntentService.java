package com.example.flus.ponto;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.script.Script;
import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;

import java.util.Collections;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PontoIntentService extends IntentService {
    private static final String TAG = "FLUSSS";

    public static final String ACTION_MARCAR = "com.example.flus.ponto.action.MARCAR";
//    private static final String ACTION_BAZ = "com.example.flus.ponto.action.BAZ";
//
//    // TODO: Rename parameters
//    private static final String EXTRA_PARAM1 = "com.example.flus.ponto.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "com.example.flus.ponto.extra.PARAM2";

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public PontoIntentService() {
        super("PontoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Notification notification = new NotificationCompat.Builder(this, "PONTOSERVICE")
                .setContentTitle("Ponto")
                .setContentText("Setando o ponto")
                .setSmallIcon(R.drawable.ic_alarm_yellow).build();
        startForeground(1, notification);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MARCAR.equals(action)) {
                int appWidgetId = intent.getIntExtra("appWidgetId", 65);
//                Log.d(TAG, "Chegou aqui! appWidgetId: " + appWidgetId);
                handleActionMarcar(appWidgetId);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
            }
        }
        stopForeground(true);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     * @param appWidgetId
     */
//    private void handleActionMarcar(String param1, String param2) {
    private void handleActionMarcar(int appWidgetId) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            boolean baterPonto = true;
            do {
                try {
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    getApplicationContext(),
                                    Collections.singleton(
                                            "https://www.googleapis.com/auth/spreadsheets")
                            );
                    credential.setSelectedAccount(account.getAccount());
                    Script mService = new com.google.api.services.script.Script.Builder(
                            HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(credential))
                            .setApplicationName("Ponto")
                            .build();

                    String scriptId = "15s77l1j3D0Y1GukPhkkMruNcpUIyHfeXoY178Wm0sfGVKqt3AhmtC0ND";

                    ExecutionRequest request = new ExecutionRequest()
                            .setFunction("marcarPonto");

                    Operation op =
                            mService.scripts().run(scriptId, request).execute();

                    if (op.getError() == null) {
                        String date = (String) op.getResponse().get("result");
                        Log.d(TAG, "Date: " + date);
                        baterPonto = false;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sp.edit().putString("LastPonto", date).apply();

                        Intent intentUpdate = new Intent(getApplicationContext(), PontoWidget.class);
                        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        int[] idArray = new int[]{appWidgetId};
                        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
                        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                                getApplicationContext(), appWidgetId, intentUpdate,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        pendingUpdate.send();
                    } else {
                        Log.d(TAG, "Deu erro!");
                    }

                } catch (Exception e) {
                    // Other non-recoverable exceptions.
                    Log.d(TAG, "Rolou excecao!" + e.toString());
                }
            } while (baterPonto);
        }
    }

//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    public HttpRequestInitializer setHttpTimeout(
            final HttpRequestInitializer requestInitializer) {
        return new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest)
                    throws java.io.IOException {
                requestInitializer.initialize(httpRequest);
                // This allows the API to call (and avoid timing out on)
                // functions that take up to 6 minutes to complete (the maximum
                // allowed script run time), plus a little overhead.
                httpRequest.setReadTimeout(380000);
            }
        };
    }
}
