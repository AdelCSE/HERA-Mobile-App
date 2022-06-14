package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class VerificationActivity extends AppCompatActivity {

    ImageView backBtn;
    LottieAnimationView verificationAnimation;
    Button continueBtn;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        backBtn = findViewById(R.id.verificationBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        verificationAnimation = findViewById(R.id.verificationAnimation);
        user = FirebaseAuth.getInstance().getCurrentUser();
        continueBtn = findViewById(R.id.continueBtn);

        verificationAnimation.playAnimation();
        Verification();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (user.isEmailVerified()) {
                            startActivity(new Intent(getApplicationContext(), OnboardingScreensActivity.class));
                            finish();
                        }
                        else Toast.makeText(VerificationActivity.this, "Please verify your account and try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void Verification(){

        user = FirebaseAuth.getInstance().getCurrentUser();
        boolean b = false;
        while(!b){
            if(user!=null){
                user.sendEmailVerification();
            }
        }
    }
}