package com.saptarshi.tunemytask.views.auth;

import static com.saptarshi.tunemytask.utils.IntentUtil.intent;
import static com.saptarshi.tunemytask.utils.ViewUtilsKt.toast;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseUser;
import com.saptarshi.tunemytask.R;
import com.saptarshi.tunemytask.models.User;
import com.saptarshi.tunemytask.viewmodels.AuthViewmodel;
import org.apache.commons.lang3.StringUtils;

public class SignupActivity extends AppCompatActivity implements AuthListeners.AuthListener,
    View.OnClickListener {


  private EditText userNameEditText, userEmailEditText, userPasswordEditText, confirmPasswordEditText;
  private Button signupButton;
  private TextView loginHereTextView;
  private AuthViewmodel authViewmodel;
  private ProgressDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    initViews();
    setEventListeners();

  }

  private void setEventListeners() {
    signupButton.setOnClickListener(this::onClick);
    loginHereTextView.setOnClickListener(this);
    authViewmodel.setAuthListeners(this);
  }

  /**
   * Invoked when this Activity is created . It initializes all the view objects
   */
  private void initViews() {
    // init views
    userNameEditText = findViewById(R.id.edittext_signup_name);
    userEmailEditText = findViewById(R.id.edittext_signup_email);
    userPasswordEditText = findViewById(R.id.edittext_signup_password);
    userPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
    confirmPasswordEditText = findViewById(R.id.edittext_signup_confirm_password);
    confirmPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
    signupButton = findViewById(R.id.button_signup);
    loginHereTextView = findViewById(R.id.textview_login_here);
    dialog = new ProgressDialog(this);
    //init Viewmodels
    authViewmodel = new ViewModelProvider(this).get(AuthViewmodel.class);
  }

  /**
   * This method is invoked when the Button {@link R.id#button_signup} is clicked.
   */
  private void onSignupButtonClicked() {
    User user = new User();
    user.setUserName(StringUtils.capitalize(userNameEditText.getText().toString()));
    user.setUserEmail(userEmailEditText.getText().toString());
    user.setUserPassword(userPasswordEditText.getText().toString());
    authViewmodel.validateRegistrationData(user, SignupActivity.this);
    dialog.setTitle("Account Signup");
    dialog.setMessage("Creating your account ...");
    dialog.show();
  }

  /**
   * This method is invoked when the TextView {@link R.id#textview_login_here} is clicked. It
   * redirects the user to the LoginActivity again .
   */
  private void onLoginHereTextViewClicked() {
    intent(SignupActivity.this, LoginActivity.class);
  }

  @Override
  public void onAuthSuccess(String message, FirebaseUser user) {
    dialog.dismiss();
    toast(getApplicationContext(), message);
    intent(SignupActivity.this, LoginActivity.class);
    finish();
  }

  @Override
  public void onAuthLogout(String message) {
  }

  @Override
  public void onAuthFailure(String error) {
    dialog.dismiss();
    toast(getApplication(), error);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_signup:
        onSignupButtonClicked();
        break;
      case R.id.textview_login_here:
        onLoginHereTextViewClicked();


    }
  }
}