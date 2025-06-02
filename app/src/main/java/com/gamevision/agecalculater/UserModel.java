package com.gamevision.agecalculater;
public class UserModel {
    public int id;
    public String name;
    public String gender;
    public String birthdate;
    public String specialDate;

    public String birthtime;
    public String specialTime;
    public String category;;
    public UserModel(int id,String name,String gender,String birthdate,String specialDate, String birthtime,String specialTime,String category) {
        this.birthdate = birthdate;
        this.birthtime = birthtime;
        this.gender = gender;
        this.id = id;
        this.name = name;
        this.specialDate = specialDate;
        this.specialTime = specialTime;
        this.category = category;
    }

    // Getters (optional: add setters)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getBirthdate() { return birthdate; }
    public String getSpecialDate() { return specialDate; }
    public String getBirthtime() { return birthtime; }
    public String getSpecialTime() { return specialTime; }
    public String getCategory() { return category; }

}
