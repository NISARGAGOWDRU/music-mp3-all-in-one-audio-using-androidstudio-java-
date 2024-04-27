package com.example.dreamland;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity3 extends AppCompatActivity {
    MaterialButton resetButton;
    MaterialButton backButton;
    EditText emailEditText; // Changed the variable name to be more meaningful

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);
        emailEditText = findViewById(R.id.Email); // Corrected the variable name

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity3.this, "Directing to login page!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString(); // Corrected the variable name
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity3.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                } else {
                    forgotPassword(email);
                }
            }
        });
    }

    private void forgotPassword(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity3.this, "Email verification link is shared. Verify and reset password!!", Toast.LENGTH_SHORT).show(); // Added .show()
                            Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity3.this, "Something went wrong!!", Toast.LENGTH_SHORT).show(); // Added .show()
                        }
                    }
                });
    }
}
