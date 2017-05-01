package com.example.android.ambulancedriverapp;

/**
 * Created by RoshanJoy on 17-03-2017.
 */

public class EmergencyDetails {

        public String si;
        public String ti;
        public String username;
        public String no;

        public EmergencyDetails(){}

        public EmergencyDetails(String si,String ti,String username,String no){
            this.si=si;
            this.ti=ti;
            this.username=username;
            this.no=no;
        }

        public void setSi(String si){this.si=si;    }
        public void setTi(String ti){this.ti=ti;}
        public String getSi(){return si;}
        public void setNo(String no){this.no=no;}
         public String getNo(){return no;}
        public String getTi(){return ti;}
        public void setUsername(String username){this.username=username;}
        public String getUsername(){return username;}
    }


