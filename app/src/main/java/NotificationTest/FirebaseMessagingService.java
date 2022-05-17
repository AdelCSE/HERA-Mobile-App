package NotificationTest;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.Objects;

import dz.esisba.a2cpi_project.BottomNavigationActivity;
import dz.esisba.a2cpi_project.ChangePasswordActivity;
import dz.esisba.a2cpi_project.LoginActivity;
import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.RegisterActivity;
import dz.esisba.a2cpi_project.SettingsActivity;
import dz.esisba.a2cpi_project.SplashScreenActivity;
import dz.esisba.a2cpi_project.UserProfileActivity;
import dz.esisba.a2cpi_project.navigation_fragments.NotificationsFragment;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {



    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        int resourceImage = getResources().getIdentifier(Objects.requireNonNull(remoteMessage.getNotification()).getIcon(), "drawable", getPackageName());

        Intent resultIntent = new Intent(this, BottomNavigationActivity.class);
        resultIntent.putExtra("menuFragment", NotificationsFragment.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_ID000");

        builder.setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSmallIcon(R.drawable.google);

//        Intent builder1 = new Intent(getApplicationContext(), UserProfileActivity.class);
//        builder1.putExtra("builder", (Serializable) builder);



        NotificationManager Manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Chak dir Hna !",
                    NotificationManager.IMPORTANCE_HIGH);
            Manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }



// notificationId is a unique int for each notification that you must define
        Manager.notify(100, builder.build());
    }

}

