package dz.esisba.a2cpi_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextInputEditText currentPassword,newPassword,confirmNewPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        backBtn = findViewById(R.id.ChangePwBackBtn);
        currentPassword = findViewById(R.id.CurrentPasswordEditTxt);
        newPassword = findViewById(R.id.NewPasswordEditTxt);
        confirmNewPassword = findViewById(R.id.ConfirmNewPasswordEditTxt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}