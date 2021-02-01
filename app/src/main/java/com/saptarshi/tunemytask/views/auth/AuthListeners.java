package com.saptarshi.tunemytask.views.auth;

import com.google.firebase.auth.FirebaseUser;

public class AuthListeners {

  /**
   * Callback to notify Activities about authentication related events. i.e Register, Login, Logout
   * ...
   */
  public interface AuthListener {

    /**
     * Callback to notify a Activity about Authentication success event.
     *
     * @param message Holds the success message .
     * @param user    Holds the currently authenticated user.
     */
    void onAuthSuccess(String message, FirebaseUser user);

    /**
     * Callback to notify a Activity about a Logout event .
     *
     * @param message Holds the success message.
     */
    void onAuthLogout(String message);

    /**
     * Callback to notify a Activity about a Authentication failure.
     *
     * @param error Holds the error that occurred during the Authentication.
     */
    void onAuthFailure(String error);
  }


}
