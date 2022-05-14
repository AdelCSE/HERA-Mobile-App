package dz.esisba.a2cpi_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    SharedPreferences sharedPreferences;
    static int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Display splash screen for a few seconds before starting Login Activity
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (firstRun()){
                    Intent mainIntent = new Intent(SplashScreenActivity.this,LoginActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
                    finish();
                }else{
                    Intent mainIntent = new Intent(SplashScreenActivity.this,OnboardingScreensActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
                    finish();
                    i++;
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    //Test if this is the first application run
    public boolean firstRun() {
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        if(i==1){
            return sharedPreferences.getBoolean("finished",false);
        }else{
            return sharedPreferences.getBoolean("finished",true);
        }
    }
}