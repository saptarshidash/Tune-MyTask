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
import com.saptarshi.tunemytask.views.home.HomeActivity;

public class LoginActivity extends AppCompatActivity implements AuthListeners.AuthListener,
    View.OnClickListener {

  private EditText userEmailEditText, userPasswordEditText;
  private Button loginButton;
  private TextView registerHereTextView;
  private AuthViewmodel authViewmodel;
  private ProgressDialog dialog;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    initViews();
    setEventListeners();
    authViewmodel.hasUserLoggedIn();
  }

  /**
   * This method sets all the listeners present in this Activity.
   */
  private void setEventListeners() {
    loginButton.setOnClickListener(this);
    registerHereTextView.setOnClickListener(this);
    authViewmodel.setAuthListeners(this);
  }

  /**
   * This method is invoked when the LoginActivity is created . It initializes all the view objects
   * present in this activity.
   */
  private void initViews() {
    // initialize views
    registerHereTextView = findViewById(R.id.textview_register_here);
    userEmailEditText = findViewById(R.id.edittext_email);
    userPasswordEditText = findViewById(R.id.edittext_password);
    userPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
    loginButton = findViewById(R.id.button_login);
    dialog = new ProgressDialog(this);
    //initialize viewmodels
    authViewmodel = new ViewModelProvider(this).get(AuthViewmodel.class);
  }

  /**
   * This method is called when the TextView {@link R.id#textview_register_here} is clicked. On
   * execution it redirects the user to the RegistrationActivity.
   */
  public void onRegisterHereTextViewClicked() {
    intent(LoginActivity.this, SignupActivity.class);
  }

  /**
   * This method is called when the Login button is clicked. On execution it collects the supplied
   * user email and password from the EditTexts and pass it to AuthViewmodel.
   */
  public void onLoginButtonClicked() {
    User user = new User();
    user.setUserEmail(userEmailEditText.getText().toString());
    user.setUserPassword(userPasswordEditText.getText().toString());
    authViewmodel.authenticateUser(user, LoginActivity.this);
    dialog.setTitle("Logging in");
    dialog.setMessage("Please wait ....");
    dialog.show();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_login:
        onLoginButtonClicked();
        break;
      case R.id.textview_register_here:
        onRegisterHereTextViewClicked();
        break;
    }
  }

  @Override
  public void onAuthSuccess(String message, FirebaseUser currentUser) {
    dialog.dismiss();
    toast(getApplicationContext(), message);
    intent(LoginActivity.this, HomeActivity.class);
    finish();
  }

  @Override
  public void onAuthLogout(String message) {
  }

  @Override
  public void onAuthFailure(String error) {
    dialog.dismiss();
    toast(getApplicationContext(), error);
  }


}