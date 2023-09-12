package ru.mirea.gribinichenko.mireaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.gribinichenko.mireaproject.databinding.ActivityRegistrationBinding;

public class Registration extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityRegistrationBinding binding;
    // START declare_auth
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization views
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // [START initialize_auth] Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        binding.verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(binding.editTextEmail.getText().toString(), binding.editTextPassword.getText().toString());
            }
        });
        binding.verifyButton.setVisibility(View.GONE);
        // [END initialize_auth]
    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Если пользователь уже авторизован, запросите повторную авторизацию
            String email = currentUser.getEmail();
            String password = "текущийПароль";

            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(currentUser);
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Registration.this, "Please enter authentication",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
        else {
            Toast.makeText(Registration.this, "Please enter authentication",
                    Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }
    // [END on_start_check_user]
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            binding.textView2.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            binding.textView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            binding.signInButton.setText("Sign out");
            binding.signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
            binding.registerButton.setVisibility(View.GONE);
            binding.verifyButton.setEnabled(!user.isEmailVerified());
            Intent intent = new Intent(Registration.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            binding.textView2.setText(R.string.signed_out);
            binding.textView.setText(null);
            binding.signInButton.setText("Sign in");
            binding.signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn(binding.editTextEmail.getText().toString(), binding.editTextPassword.getText().toString());
                }
            });
            binding.registerButton.setVisibility(View.VISIBLE);
        }
    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure",
                                    task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            binding.verifyButton.setVisibility(View.VISIBLE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            binding.textView2.setText(R.string.auth_failed);
                        }
                        // [END_EXCLUDE]
                    }
                });
        //[END sign_in_with_email]
    }
    private void signOut() {
        mAuth.signOut();
        binding.verifyButton.setVisibility(View.GONE);
        updateUI(null);
    }
    private void sendEmailVerification() {
        // Disable button
        binding.verifyButton.setEnabled(false);
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        binding.verifyButton.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Registration.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }
    private boolean validateForm() {
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();

        if (email.isEmpty()) {
            binding.editTextEmail.setError("Required.");
            return false;
        }

        if (password.isEmpty()) {
            binding.editTextPassword.setError("Required.");
            return false;
        }

        if (password.length() < 6) {
            binding.editTextPassword.setError("Password should be at least 6 characters.");
            return false;
        }

        binding.editTextEmail.setError(null);
        binding.editTextPassword.setError(null);

        return true;
    }
}
