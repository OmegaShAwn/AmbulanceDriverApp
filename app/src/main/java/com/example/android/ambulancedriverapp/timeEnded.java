package com.example.android.ambulancedriverapp;

/**
 * Created by ShAwn on 20-06-2017.
 */

public class timeEnded {

    public int year,month,date,hour,minute;
    public void setyear(int year){
        this.year=year;
    }
    public void setmonth(int month){
        this.month=month;
    }
    public void setdate(int date){
        this.date=date;
    }
    public void sethour(int hour){
        this.hour=hour;
    }
    public void setminute(int minute){
        this.minute=minute;
    }
    public int getyear(){
        return year;
    }
    public int getmonth(){
        return month;
    }
    public int getdate(){
        return date;
    }
    public int gethour(){
        return hour;
    }
    public int getminute(){
        return minute;
    }
}
