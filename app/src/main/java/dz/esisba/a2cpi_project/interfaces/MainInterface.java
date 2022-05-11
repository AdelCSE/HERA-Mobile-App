package dz.esisba.a2cpi_project.interfaces;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public interface MainInterface {
    void onShareClick(int position);
    void onLikeClick(int position, LottieAnimationView lottieAnimationView, TextView likesTxt, boolean isAnswer);
}
