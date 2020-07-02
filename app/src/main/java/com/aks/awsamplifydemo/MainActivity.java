package com.aks.awsamplifydemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String userName;
    private String userPassword;
    private EditText etUserName, etUserPassword, etVerificationCode;
    private Button btnAction, btnConfirmSignUp;
    private boolean signUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            e.printStackTrace();
        }
        Amplify.Auth.fetchAuthSession(
                new Consumer<AuthSession>() {
                    @Override
                    public void accept(@NonNull AuthSession result) {
                        Log.i(TAG, result.toString());
                    }
                },
                new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException error) {
                        Log.e(TAG, error.toString());
                    }
                }
        );

        etUserName = findViewById(R.id.etUserName);
        etUserPassword = findViewById(R.id.etUserPassword);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnAction = findViewById(R.id.btnAction);
        btnConfirmSignUp = findViewById(R.id.btnConfirmSignUp);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUp) {
                    signIn();
                } else {
                    signUp();
                }
            }
        });

        btnConfirmSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSignUp();
            }
        });
    }

    private void signUp() {
        userName = etUserName.getText().toString().trim();
        userPassword = etUserPassword.getText().toString().trim();
        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), "my@email.com"));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), "+15551234567"));
        Amplify.Auth.signUp(
                userName,
                userPassword,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                new Consumer<AuthSignUpResult>() {
                    @Override
                    public void accept(@NonNull AuthSignUpResult result) {
                        Log.e(TAG, result.toString());
                    }
                },
                new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException error) {
                        Log.e(TAG, error.toString());
                    }
                }
        );
    }

    private void confirmSignUp() {
        userName = etUserName.getText().toString().trim();
        String userVerificationCode = etVerificationCode.getText().toString().trim();
        Amplify.Auth.confirmSignUp(
                userName,
                userVerificationCode,
                new Consumer<AuthSignUpResult>() {
                    @Override
                    public void accept(@NonNull AuthSignUpResult result) {
                        Log.e(TAG, result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                        signUp = true;
                        btnAction.setText("Sign In");
                        etVerificationCode.setVisibility(View.GONE);
                        btnConfirmSignUp.setVisibility(View.GONE);
                    }
                },
                new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException error) {
                        Log.e(TAG, error.toString());
                    }
                }
        );
    }

    private void signIn() {
        userName = etUserName.getText().toString().trim();
        userPassword = etUserPassword.getText().toString().trim();
        Amplify.Auth.signIn(
                userName,
                userPassword,
                new Consumer<AuthSignInResult>() {
                    @Override
                    public void accept(@NonNull AuthSignInResult result) {
                        Log.e(TAG, result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    }
                },
                new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException error) {
                        Log.e(TAG, error.toString());
                    }
                }
        );
    }
}
