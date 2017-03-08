// IMyAidlInterface.aidl
package com.example.user.app_jni;

// Declare any non-default types here with import statements
import com.example.user.app_jni.Person;
import com.example.user.app_jni.IMessageCallback;
interface IFpApi {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getCount();
    Person getPerson();
    void registerCallback(IMessageCallback cb);
    void unregisterCallback(IMessageCallback cb);
}
