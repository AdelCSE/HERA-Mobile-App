package dz.esisba.a2cpi_project.interfaces;

import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import dz.esisba.a2cpi_project.models.PostModel;

public interface OnItemClickListner {
    void onAnswerClick(int position);
    void onShareClick(int position);
    void onLikeClick(int position, LottieAnimationView lottieAnimationView, TextView likesTxt, boolean isAnswer);
}
