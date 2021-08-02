package com.hv.briskybake.Model;

public class User {
    public String name,email,phone,image;
    public String zlongitude;
    public String zlatitude;

    public User ()
    {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }


    public String getZlongitude() {
        return zlongitude;
    }

    public void setZlongitude(String zlongitude) {
        this.zlongitude = zlongitude;
    }

    public String getZlatitude() {
        return zlatitude;
    }

    public void setZlatitude(String zlatitude) {
        this.zlatitude = zlatitude;
    }

    public User(String name, String email, String phone, String zlongitude, String zlatitude) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.zlongitude = zlongitude;
        this.zlatitude = zlatitude;
    }

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(String name, String email, String phone, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
