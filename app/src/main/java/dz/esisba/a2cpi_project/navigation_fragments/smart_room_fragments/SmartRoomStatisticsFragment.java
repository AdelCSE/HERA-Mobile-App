package dz.esisba.a2cpi_project.navigation_fragments.smart_room_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dz.esisba.a2cpi_project.R;

public class SmartRoomStatisticsFragment extends Fragment {

    private View parentHolder;
    private FirebaseDatabase database;
    private DatabaseReference MyRef;
    private TextView temperature,RoomState;
    private ProgressBar mTemperatureProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_smart_room_statistics,container,false);

        getTemperatureData();

        return parentHolder;
    }

    public void getTemperatureData(){
        temperature = parentHolder.findViewById(R.id.temperatureText);
        RoomState = parentHolder.findViewById(R.id.roomState);
        mTemperatureProgressBar = parentHolder.findViewById(R.id.temperatureProgressBar);

        database = FirebaseDatabase.getInstance();
        MyRef = database.getReference("DHT_SENSOR");

        MyRef.addValueEventListener(new ValueEventListener() {
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Tag","failed to read value",error.toException());
            }
        });
    }
}