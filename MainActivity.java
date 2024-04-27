package com.example.dreamland;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authprofile;
    private EditText email;
    private EditText password;
    private TextView forgotPassword;
    private MaterialButton signup;
    private MaterialButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        authprofile = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        if (authprofile.getCurrentUser() != null) {
            // User is already logged in, redirect to home page
            redirectToHomePage();
        } else {
            // No user is logged in, show the login page
            setContentView(R.layout.activity_main);
            setupUI();
        }
    }

    private void setupUI() {
        // Initialize UI elements
        email = findViewById(R.id.email);
        password = findViewById(R.id.password01);
        forgotPassword = findViewById(R.id.Forgotpassword);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        // Set up click listeners for buttons
        setupClickListeners();
    }

    private void setupClickListeners() {
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct to signup page
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct to reset password page
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        String textEmail = email.getText().toString();
        String textPassword = password.getText().toString();

        if (validateForm(textEmail, textPassword)) {
            loginuser(textEmail, textPassword);
        }
    }

    private boolean validateForm(String email, String password) {
        // Validate the login form
        if (TextUtils.isEmpty(email)) {
            this.email.setError("Email is required");
            this.email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Valid email is required");
            this.email.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            this.password.setError("Password is required");
            this.password.requestFocus();
            return false;
        }
        return true;
    }

    private void loginuser(String email, String password) {
        // Perform login with Firebase authentication
        authprofile.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    redirectToHomePage();
                } else {
                    Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void redirectToHomePage() {
        // Redirect to home page
        Intent intent = new Intent(MainActivity.this, MainActivity4.class);
        startActivity(intent);
        finish();
    }
}
