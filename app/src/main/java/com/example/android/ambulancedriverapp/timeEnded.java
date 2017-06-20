package com.example.android.ambulancedriverapp;

/**
 * Created by ShAwn on 20-06-2017.
 */

public class timeEnded {
    public String time;
    String year, month, date, hour, minute;

    public timeEnded(String year, String month, String date, String hour, String minute){

        time=year+" | "+month+" | "+date+" | "+hour+" | "+minute;
    }
}
