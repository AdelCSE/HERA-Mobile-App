package NotificationTest;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import java.util.Objects;

import dz.esisba.a2cpi_project.NotificationsActivity;
import dz.esisba.a2cpi_project.R;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {



    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        CreatPushNotification(remoteMessage);
//        int resourceImage = getResources().getIdentifier(Objects.requireNonNull(remoteMessage.getNotification()).getIcon(), "drawable", getPackageName());


    }

    private void CreatPushNotification(RemoteMessage remoteMessage){
        Intent resultIntent = new Intent(getApplicationContext(), NotificationsActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stakBuilder = TaskStackBuilder.create(this);
        stakBuilder.addNextIntentWithParentStack(resultIntent);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = stakBuilder.getPendingIntent( 10,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_ID000");

        builder.setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setContentIntent(pendingIntent);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSmallIcon(R.drawable.google);


        NotificationManager Manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            Manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }



// notificationId is a unique int for each notification that you must define
        Manager.notify(100, builder.build());
    }

}

