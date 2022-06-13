package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class VerificationActivity extends AppCompatActivity {

    Button continueBtn, verify;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        user = FirebaseAuth.getInstance().getCurrentUser();
        continueBtn = findViewById(R.id.continueBtn);

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
        verify = findViewById(R.id.verify);

        user = FirebaseAuth.getInstance().getCurrentUser();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(VerificationActivity.this, "Verification email has been sent, check spam if you are unable to find it", Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}