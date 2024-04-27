package com.example.dreamland;


import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        EditText username = findViewById(R.id.username);
        EditText email = findViewById(R.id.Email);
        EditText phno = findViewById(R.id.phno);
        EditText password = findViewById(R.id.password);
        EditText password2 = findViewById(R.id.password2);
        MaterialButton register = findViewById(R.id.register);

        register.setOnClickListener(v -> {
            String textUsername = username.getText().toString();
            String textEmail = email.getText().toString();
            String textPhno = phno.getText().toString();
            String textPassword = password.getText().toString();
            String textPassword2 = password2.getText().toString();

            if (TextUtils.isEmpty(textUsername)) {
                showErrorAndFocus(username, "Please enter a username");
            } else if (TextUtils.isEmpty(textEmail)) {
                showErrorAndFocus(email, "Please enter an email");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                showErrorAndFocus(email, "Please enter a valid email");
            } else if (TextUtils.isEmpty(textPhno) || textPhno.length() != 10) {
                showErrorAndFocus(phno, "Please enter a valid 10-digit phone number");
            } else if (TextUtils.isEmpty(textPassword) || textPassword.length() < 6) {
                showErrorAndFocus(password, "Please enter a password of at least 6 characters");
            } else if (!textPassword.equals(textPassword2)) {
                showErrorAndFocus(password2, "Passwords do not match");
            } else {
                registerUser(textEmail, textPassword);
            }
        });
    }

    private void showErrorAndFocus(EditText editText, String errorMessage) {
        Toast.makeText(MainActivity2.this, errorMessage, Toast.LENGTH_SHORT).show();
        editText.setError(errorMessage);
        editText.requestFocus();
    }

    private void registerUser(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity2.this, task -> {
                    if (task.isSuccessful()) {
                        sendVerificationEmail(Objects.requireNonNull(auth.getCurrentUser()));
                    } else {
                        Toast.makeText(MainActivity2.this, "Registration failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity2.this, "Registration successful. Verify your email.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity2.this, "Verification email sending failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


