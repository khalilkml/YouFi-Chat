package com.example.chatapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{
    public String name, image, email, token, id;
    public ArrayList<User> Friendslist = new ArrayList<>();
}
