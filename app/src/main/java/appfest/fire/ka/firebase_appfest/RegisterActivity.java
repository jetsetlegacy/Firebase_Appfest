
package appfest.fire.ka.firebase_appfest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private View btnLogin;
    private View btnSignUp;
    private ProgressDialog progressDialog;
    private TextInputLayout email;
    private TextInputLayout password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // Initialize Firebase Auth

        FirebaseUser mFirebaseUser = auth.getCurrentUser();

        if (mFirebaseUser != null) {
            // Not logged in, launch the Log In activity
            Intent i = new Intent(RegisterActivity.this,StartScreen.class);
            startActivity(i);
        }

        btnLogin = findViewById(R.id.login);
        btnSignUp = findViewById(R.id.sign_up);
        email = (TextInputLayout) findViewById(R.id.email_field);
        password = (TextInputLayout) findViewById(R.id.password_field);

        //go to Login Activity
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //sign up a new account
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.hasText(email)) {
                    Utils.showToast(RegisterActivity.this, "Please input your email");
                } else if (!Utils.hasText(password)) {
                    Utils.showToast(RegisterActivity.this, "Please input your password");
                } else {
                    //requesting Firebase server
                    showProcessDialog();
                    auth.createUserWithEmailAndPassword(Utils.getText(email), Utils.getText(password))
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Utils.showToast(RegisterActivity.this, "Register failed!");
                                    } else {
                                        Utils.showToast(RegisterActivity.this, "Register successful!");
                                        //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        progressDialog.dismiss();
                                        showProcessDialog();
                                        authenticateUser(Utils.getText(email), Utils.getText(password));
                                    }
                                }
                            });
                }
            }
        });
    }

    private void showProcessDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Register");
        progressDialog.setMessage("Register a new account...");
        progressDialog.show();
    }
    private void authenticateUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // When login failed
                        if (!task.isSuccessful()) {
                            Utils.showToast(RegisterActivity.this, "Login error!");
                        } else {
                            //When login successful, redirect user to main activity
                            Intent intent = new Intent(RegisterActivity.this, StartScreen.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                });
    }


}