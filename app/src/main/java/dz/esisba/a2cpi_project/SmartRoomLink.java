package dz.esisba.a2cpi_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;

public class SmartRoomLink extends Fragment {

    private LottieAnimationView animation;
    private ImageButton backBtn;
    private View parentHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentHolder = inflater.inflate(R.layout.fragment_smart_room_link, container, false);

        animation = parentHolder.findViewById(R.id.SmartRoomAnimation);
        animation.playAnimation();

        backBtn = parentHolder.findViewById(R.id.SmartRoomBtnReturn);

        return parentHolder;
    }
}