package com.saptarshi.tunemytask.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.saptarshi.tunemytask.R;
import com.saptarshi.tunemytask.models.User;
import com.saptarshi.tunemytask.models.repositories.AuthRepository;
import com.saptarshi.tunemytask.views.auth.AuthListeners;

public class AuthViewmodel extends AndroidViewModel {

  private final AuthRepository authRepository;
  private AwesomeValidation formValidation;

  /**
   * The Constructor is invoked when this viewmodel is instantiated.
   *
   * @param application Holds the application state
   */
  public AuthViewmodel(@NonNull Application application) {
    super(application);
    authRepository = new AuthRepository(application);

  }

  /**
   * This method is called to set a listener which can listen to various Authentication related
   * events such as Register, Login, Logout.
   *
   * @param authListener Holds the listener which is  used to notify about various auth events. This
   *                     listener is then passed to AuthRepository to set the listener in the
   *                     AuthRepository class to fire the callbacks.
   */
  public void setAuthListeners(AuthListeners.AuthListener authListener) {
    authRepository.setAuthListeners(authListener);
  }

  /**
   * This method is called from the SignUpActivity to validate the user data before make a call to
   * the AuthRepository method to register the user in firebase.
   *
   * @param user    This object holds the user details , eg: name, email, password ...
   * @param context Holds the context of the caller activity
   */

  public void validateRegistrationData(User user, Context context) {
    formValidation = new AwesomeValidation(ValidationStyle.BASIC);
    formValidation.addValidation(
        (Activity) context,
        R.id.edittext_signup_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$",
        R.string.nameerror);

    formValidation.addValidation(
        (Activity) context,
        R.id.edittext_signup_email,
        Patterns.EMAIL_ADDRESS, R.string.emailerror);
    String regexPassword = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    formValidation.addValidation(
        (Activity) context,
        R.id.edittext_signup_password, regexPassword,
        R.string.passerror);

    formValidation.addValidation(
        (Activity) context,
        R.id.edittext_signup_confirm_password,
        R.id.edittext_signup_password, R.string.confpasserror);

    if (formValidation.validate()) {
      authRepository.registerUserInFirebase(user);
    }
  }

  /**
   * This method is called from the LoginActivity to authenticate(login) the user in the
   * application. It validates the user data and then calls another method present in
   * AuthRepository.
   *
   * @param user    Holds the user email and password (other data members are set to their default
   *                values)
   * @param context Holds the context of the caller activity
   */
  public void authenticateUser(User user, Context context) {
    formValidation = new AwesomeValidation(ValidationStyle.BASIC);
    formValidation.addValidation((Activity) context, R.id.edittext_email, Patterns.EMAIL_ADDRESS,
        R.string.emailerror);
    formValidation.addValidation((Activity) context, R.id.edittext_password, "^(?=\\s*\\S).*$",
        R.string.emptypasserr);
    if (formValidation.validate()) {
      authRepository.authenticateUserInFirebase(user);
    }
  }

  /**
   * This method is called from LoginActivity and HomeActivity to check if the user is currently
   * logged in or not.
   */
  public void hasUserLoggedIn() {
    authRepository.getUserLoggedInStatus();
  }

  public void logoutUser() {
    authRepository.makeUserLogout();
  }

}
