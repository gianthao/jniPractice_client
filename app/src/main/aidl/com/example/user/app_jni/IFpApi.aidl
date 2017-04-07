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
    void getCount(in int[] send_data);
    void getPerson(in Person person);
    void registerCallback(IMessageCallback cb);
    void unregisterCallback(IMessageCallback cb);
}
