package dz.esisba.a2cpi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton backBtn, confirmBtn;
    private TextInputEditText currentPassword,newPassword,confirmNewPassword;
    private ProgressBar progressBar;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        backBtn = findViewById(R.id.ChangePwBackBtn);
        progressBar = findViewById(R.id.changePwPB);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        changePassword();

    }

    private void changePassword(){
        currentPassword = findViewById(R.id.CurrentPasswordEditTxt);
        newPassword = findViewById(R.id.NewPasswordEditTxt);
        confirmNewPassword = findViewById(R.id.ConfirmNewPasswordEditTxt);
        confirmBtn = findViewById(R.id.ChangePwBtn);

        user = FirebaseAuth.getInstance().getCurrentUser();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String newpw = newPassword.getText().toString();
                String confirmpw = confirmNewPassword.getText().toString();

                if (currentPassword.getText().toString().isEmpty())
                {
                    Toast.makeText(ChangePasswordActivity.this, "Enter your current password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newpw.isEmpty())
                {
                    newPassword.setError("Enter new password");
                    return;
                }
                else if (newpw.length()<6){
                    newPassword.setError("Password must be more than 6 characters long");
                    return;
                }
                if (!confirmpw.equals(newpw))
                {
                    confirmNewPassword.setError("Confirm your password");
                    return;
                }
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword.getText().toString());

                user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(newpw).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ChangePasswordActivity.this, "Password Successfully Modified", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(ChangePasswordActivity.this, SettingsActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePasswordActivity.this, "Some error occurred please try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePasswordActivity.this, "Please enter the correct password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}