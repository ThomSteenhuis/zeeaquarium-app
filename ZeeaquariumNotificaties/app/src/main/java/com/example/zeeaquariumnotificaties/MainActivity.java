package com.example.zeeaquariumnotificaties;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.microsoft.windowsazure.messaging.notificationhubs.NotificationHub;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHub.setListener(new CustomNotificationListener());
        NotificationHub.start(
            this.getApplication(),
            "zeeaquarium-notificatie-hub",
            "Endpoint=sb://zeeaquarium-notificaties.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=NGNesiYhIzvqortp3rIj5MyiY9h1Mc06qpC4Et5hlRk=");
    }
}