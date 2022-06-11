package dz.esisba.a2cpi_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class SecurityVerification extends AppCompatActivity {
    private EditText verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_verification);

        verificationCode = findViewById(R.id.verificationCode);
        Button verify = findViewById(R.id.Verifybtn);

        verify.setOnClickListener(v -> {
            String code = verificationCode.getText().toString();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("verificationCode");
                if(value.equals(code)){
                    //open home
                    //login directly
                    retrieveRestoreToken();
                    Toast.makeText(SecurityVerification.this, "Log-in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SecurityVerification.this, BottomNavigationActivity.class));
                    finish();
                }else{
                    Toast.makeText(SecurityVerification.this, "Incorrect Code , Try Again", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(SecurityVerification.this, "Email was not sent ,Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveRestoreToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            final Map<String, Object> userToken=new HashMap<>();
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    userToken.put("Token", token);
                    FirebaseFirestore.getInstance().collection("Users").document(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userToken).addOnSuccessListener(unused -> Toast.makeText(SecurityVerification.this,"Success",Toast.LENGTH_LONG));
                }
            }
        });
    }
}