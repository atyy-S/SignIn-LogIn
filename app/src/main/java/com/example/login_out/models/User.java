package com.example.login_out.models;

import java.io.Serializable;
/*
keep track of the user data (First & last name, image, email, token, and Id)
 */
public class User implements Serializable {
    public String Firstname,lastname, image,email,token, id;
}
