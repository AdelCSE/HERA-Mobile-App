package dz.esisba.a2cpi_project.navigation_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;

import java.io.Serializable;

import dz.esisba.a2cpi_project.R;
import dz.esisba.a2cpi_project.SmartRoom;

public class SmartRoomLink extends Fragment {

    private LottieAnimationView animation;
    private ImageButton backBtn;
    private AppCompatButton confirm;
    private View parentHolder;
    private EditText SmartRoomCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_smart_room_link, container, false);
        SmartRoomCode = parentHolder.findViewById(R.id.SRCodeEditTxt);
        confirm = parentHolder.findViewById(R.id.codeConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartRoomAuthentification();
            }
        });

        animation = parentHolder.findViewById(R.id.SmartRoomAnimation);
        animation.playAnimation();

        backBtn = parentHolder.findViewById(R.id.SmartRoomBtnReturn);

        return parentHolder;
    }

    public void SmartRoomAuthentification(){
        if(SmartRoomCode.getText().toString().equals("adel")){
            Intent intent = new Intent(getActivity(), SmartRoom.class);
            getActivity().startActivity(intent);
        }
    }
}