package com.saptarshi.tunemytask.models.repositories;

import android.app.Application;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.saptarshi.tunemytask.AppConfig;
import com.saptarshi.tunemytask.models.User;
import com.saptarshi.tunemytask.views.auth.AuthListeners;

public class AuthRepository extends AuthListeners {

  public static final String TAG = AuthRepository.class.getName();
  private DatabaseReference dbReferences;
  private final DatabaseReference dbRootRef;
  private FirebaseUser currentUser;
  private AuthListeners.AuthListener authListeners;
  private final FirebaseAuth userAuth;
  private UserProfileChangeRequest userProfileUpdate;

  public AuthRepository(Application application) {
    userAuth = AppConfig.FIREBASE_USER_AUTH;
    dbRootRef = AppConfig.FIREBASE_ROOT_REF;
  }


  /**
   * This method is called from AuthViewmodel to set a AuthListener in this Repo.
   *
   * @param authListeners Holds the listener which is passed from AuthViewmodel.
   */
  public void setAuthListeners(AuthListener authListeners) {
    this.authListeners = authListeners;
  }

  /**
   * This method is called from AuthViewModel to register the user in firebase . On successful
   * invoke this method registers the user in the Firebase
   *
   * @param userData Holds the user registration details eg: Name, email, password ..
   */
  public void registerUserInFirebase(User userData) {
    String _email = userData.getUserEmail();
    String _password = userData.getUserPassword();
    userAuth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(task -> {
      // OnCompleteListener attached
      if (task.isSuccessful()) {
        currentUser = userAuth.getCurrentUser();
        userData.setLoggedIn(true);
        dbReferences = dbRootRef.child(currentUser.getUid());
        dbReferences.child("Userinfo").setValue(userData);
        dbReferences.child("Taskdata").setValue("");
        Log.d(TAG + ":UserSignupSuccess: ", "Signup successful");
        //Setting user profile  eg. display name,  profile photo ..
        userProfileUpdate = new UserProfileChangeRequest.Builder()
            .setDisplayName(userData.getUserName())
            .build();
        currentUser.updateProfile(userProfileUpdate).addOnCompleteListener(update -> {
          if (update.isSuccessful()) {
            userAuth.getCurrentUser().reload();
            currentUser = userAuth.getCurrentUser();
            String _msg = "Signup successful, Logging you in";
            authListeners.onAuthSuccess(_msg, currentUser);
          }
        });
      } else {
        String error = task.getException().getMessage();
        authListeners.onAuthFailure(error);
        Log.d("onSignup: ", error);
      }
    });
  }

  /**
   * This method is called from AuthViewmodel in order to authenticate the user who is trying to
   * login in the app . This method will also fire a callback on successful or failed authentication
   * event to notify the LoginActivity.
   *
   * @param userData
   */
  public void authenticateUserInFirebase(User userData) {
    String _email = userData.getUserEmail();
    String _password = userData.getUserPassword();
    userAuth.signInWithEmailAndPassword(_email, _password).addOnCompleteListener(task -> {
      // OnCompleteListener attached
      if (task.isSuccessful()) {
        currentUser = userAuth.getCurrentUser(); // get curr user
        dbReferences = dbRootRef.child(currentUser.getUid()).child("Userinfo");
        dbReferences.child("loggedIn").setValue(true);
        String _msg = "User " + currentUser.getDisplayName() + " has authenticated";
        Log.d(TAG + ":UserLoginSuccess: ", _msg);
        authListeners.onAuthSuccess("Login Successful", currentUser);
      } else {
        String error = task.getException().getMessage();
        Log.d(TAG + ":UserLoginFailure: ", error);
        authListeners.onAuthFailure("Login failed :" + error);
      }
    });
  }

  /**
   * This method is called from AuthViewmodel to verify if the user is logged in or not. It fires a
   * callback in case the user is logged in currently.
   */
  public void getUserLoggedInStatus() {
    currentUser = userAuth.getCurrentUser();
    if (currentUser != null) {
      authListeners.onAuthSuccess("Logged in", currentUser);
    } else {
      authListeners.onAuthFailure("Please login to continue");
    }
  }

  /**
   * This method is called to when the user has requested logout from the application . It fires a
   * callback to notify the HomeActivity about the logout event.
   */
  public void makeUserLogout() {
    currentUser = userAuth.getCurrentUser();
    dbReferences = dbRootRef.child(currentUser.getUid()).child("Userinfo").child("loggedIn");
    dbReferences.setValue(false);
    userAuth.signOut();
    Log.d("onLogout: ", "Loggedout successfully");
    authListeners.onAuthLogout("Logged out");
  }

}
