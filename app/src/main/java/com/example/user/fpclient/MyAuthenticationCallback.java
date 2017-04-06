package com.example.user.fpclient;

import android.hardware.fingerprint.FingerprintManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by user on 2017/3/28.
 */
public class MyAuthenticationCallback extends FingerprintManager.AuthenticationCallback{
    String tag="FpClient";
    private Handler handler;
    /**
     * Called when an unrecoverable error has been encountered and the operation is complete.
     * No further callbacks will be made on this object.
     * @param errorCode An integer identifying the error message
     * @param errString A human-readable error string that can be shown in UI
     */
    public MyAuthenticationCallback(Handler handler){
            this.handler = handler;
    }
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        Log.i(tag, "onAuthenticationError");
        handler.obtainMessage(MainActivity.MSG_AUTH_ERROR,errorCode,0,errString).sendToTarget();
    }

    /**
     * Called when a recoverable error has been encountered during authentication. The help
     * string is provided to give the user guidance for what went wrong, such as
     * "Sensor dirty, please clean it."
     * @param helpCode An integer identifying the error message
     * @param helpString A human-readable string that can be shown in UI
     */
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        Log.i(tag, "onAuthenticationHelp");
        handler.obtainMessage(MainActivity.MSG_AUTH_HELP,helpCode,0,helpString).sendToTarget();
    }

    /**
     * Called when a fingerprint is recognized.
     * @param result An object containing authentication-related data
     */
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Log.i(tag, "onAuthenticationSucceeded");
        Message msg =new Message();
        msg.what = MainActivity.MSG_AUTH_SUCCESSED;
        Log.i(tag,"successed");
        handler.sendMessage(msg);
    }

    /**
     * Called when a fingerprint is valid but not recognized.
     */
    public void onAuthenticationFailed() {
        Log.i(tag,"onAuthenticationFailed");
        super.onAuthenticationFailed();
        Message msg =new Message();
        msg.what = MainActivity.MSG_AUTH_FAILED;
        handler.sendMessage(msg);

    }

    /**
     * Called when a fingerprint image has been acquired, but wasn't processed yet.
     *
     * @param acquireInfo one of FINGERPRINT_ACQUIRED_* constants
     * @hide
     */
    public void onAuthenticationAcquired(int acquireInfo) {
        //super.onAuthenticationAcquired(acquireInfo);
        Log.i(tag,"onAuthenticationAcquired");
        handler.obtainMessage(MainActivity.MSG_AUTH_ACQUIRED,acquireInfo).sendToTarget();
    }
}
