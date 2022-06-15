package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;

import NotificationTest.FcmNotificationsSender;
import dz.esisba.a2cpi_project.models.PostModel;

public class SmartRoom extends AppCompatActivity {


    private FirebaseDatabase database;
    private FirebaseFirestore fstore;
    private DatabaseReference Temperatur;
    private DatabaseReference Sound;
    private DatabaseReference Movement;
    private TextView temperature,RoomState,mouvementState,noiseState;
    private ProgressBar mTemperatureProgressBar;
    NotificationBadge notificationBadge;
    private String downloadUrl;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DocumentReference userInfos ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_smart_room_statistics);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        userInfos = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());

        getStatisticsData();

    }

    public void getStatisticsData(){
        temperature = findViewById(R.id.temperatureText);
        mouvementState = findViewById(R.id.MouvementState);
        noiseState = findViewById(R.id.NoiseState);
        RoomState = findViewById(R.id.roomState);
        mTemperatureProgressBar = findViewById(R.id.temperatureProgressBar);
        notificationBadge = findViewById(R.id.badge);

        database = FirebaseDatabase.getInstance("https://hera-483e9-default-rtdb.europe-west1.firebasedatabase.app/");
        Temperatur = database.getReference("DHT_SENSOR");
        Sound = database.getReference("SOUND_SENSOR");
        Movement = database.getReference("MOVE_SENSOR");

        Toast.makeText(SmartRoom.this, "exist", Toast.LENGTH_SHORT).show();

        Temperatur.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue().toString();
                    temperature.setText(value+"°C");
                    mTemperatureProgressBar.setProgress(Integer.parseInt(value));
                    if (Integer.parseInt(value)<10){
                        RoomState.setText("Your room is freezing");
                        RoomState.setTextColor(R.color.hot);
                    }else if (Integer.parseInt(value)>=10 && Integer.parseInt(value)<20) {
                        RoomState.setText("Your room is cold");
                        RoomState.setTextColor(R.color.hot);
                    }else if (Integer.parseInt(value)>=20 && Integer.parseInt(value)<28){
                        RoomState.setText("Your room is stable");
                        RoomState.setTextColor(R.color.stable);
                    }else if (Integer.parseInt(value)>=28 && Integer.parseInt(value) < 38){
                        RoomState.setText("Your room is getting hot");
                        RoomState.setTextColor(R.color.hot);
                    }else if (Integer.parseInt(value)>=38){
                        RoomState.setText("Your room is so hot");
                        RoomState.setTextColor(R.color.so_hot);
                    }
                    boolean b = true;
                    if(Integer.parseInt(value) >= 28 && b){
                        Notify("CHECK THE ROOM TEMPERATURE",SmartRoom.this,4);
                        b=false;
                    }
                    if(Integer.parseInt(value)<=27){
                        b=true;
                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Tag","failed to read Temperature value",error.toException());
            }
        });

        Sound.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue().toString();
                    if(value.equals("1")){
                        noiseState.setText("Your child might be crying!");
                        Notify("THERE'S SOME NOISE IN THE ROOM",SmartRoom.this,5);
                    }else{
                        noiseState.setText("Your child is stable");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Tag","failed to read Sound value",error.toException());
            }
        });

        Movement.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue().toString();
                    if(value.equals("1")){
                        mouvementState.setText("Your child might be mouving!");
                        Notify("THERE'S SOME MOVEMENT IN THE ROOM",SmartRoom.this,6);
                    }else{
                        mouvementState.setText("Your child is stable");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Tag","failed to read Movement value",error.toException());
            }
        });
    }


    public void Notify(String title, Activity activity, int type) {


        DocumentReference gg = fstore.collection("Users").document(auth.getCurrentUser().getUid());


        gg.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    FcmNotificationsSender send = new FcmNotificationsSender(
                            task.getResult().getString("Token"),
                            title ,
                            "Click To See All Notifications",
                            activity);
                    send.SendNotifications();
                }
            }
        });


        userInfos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getLong("unseenNotifications") != null) {
//                    if(task.getResult().getLong("unseenNotifications").intValue() >99) {
//                        notificationBadge.setText("99+");
//                    }
//                    else{
//                        notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
//                    }
                        //                   notificationBadge.setNumber(task.getResult().getLong("unseenNotifications").intValue());
                    } else {
                        Log.d("____________", "onComplete: null value");
                    }
                }
            }
        });


    }
}