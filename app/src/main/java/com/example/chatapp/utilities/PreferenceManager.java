package com.example.chatapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.chatapp.models.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        } else {
            throw new IllegalArgumentException("Context cannot be null");
        }
    }

    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
    public void putString(String key, String value ){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public  String getString(String key){
        return sharedPreferences.getString(key,null);
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void putFriendArrayList(String key, ArrayList<String> arrayList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, serializeFriendArrayList(arrayList));
        editor.apply();
    }

    public ArrayList<String> getFriendArrayList(String key) {
        String serializedData = sharedPreferences.getString(key, null);
        return deserializeFriendArrayList(serializedData);
    }

    private String serializeFriendArrayList(ArrayList<String> arrayList) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(arrayList);
            objectOutputStream.close();
            return byteArrayOutputStream.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addFriend(String key, String user) {
        ArrayList<String> friendList = getFriendArrayList(key);
        if (friendList == null) {
            friendList = new ArrayList<>();
        } else if (!friendList.contains(user)) {
            friendList.add(user);
            putFriendArrayList(key, friendList);
        }

    }

    private ArrayList<String> deserializeFriendArrayList(String serializedData) {
        try {
            if (serializedData == null) {
                return new ArrayList<>();  // or handle it according to your logic
            }
            byte[] bytes = serializedData.getBytes("ISO-8859-1");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            ArrayList<String> arrayList = (ArrayList<String>) objectInputStream.readObject();
            objectInputStream.close();
            return arrayList;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
