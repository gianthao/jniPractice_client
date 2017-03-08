// IMyAidlInterface.aidl
package com.example.user.app_jni;

// Declare any non-default types here with import statements
import com.example.user.app_jni.Person;
interface IFpApi {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getCount();
    Person getPerson();
}
