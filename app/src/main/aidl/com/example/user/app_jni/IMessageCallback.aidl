// IMessageCallback.aidl
package com.example.user.app_jni;

// Declare any non-default types here with import statements

interface IMessageCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void getMsg(in int[] msg);
}
