package com.example.user.app_jni;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2017/3/7.
 */
public class Person implements Parcelable{
    private String name;
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
    dest.writeString(name);
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            Person person = new Person();
            person.setName(source.readString());
            return person;
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
