package dz.esisba.a2cpi_project.interfaces;

import android.view.View;

public interface OnItemClickListner {
    void onAnswerClick(int position);
    void onShareClick(int position);
    void onMenuClick(int position, View v);
}
