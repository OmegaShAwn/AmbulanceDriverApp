package com.example.android.ambulancedriverapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private ImageView fire;
    private SeekBar s1;
    private TextView t1,t2;
    public int S,T,N;
    public boolean check = false;
    private ImageButton bg1;
    private ImageButton bg2;
    private ImageButton bg3;
    private ImageButton bg4;
    private ImageButton bg5;
    private ImageButton bg6;
    private Button b1,b2,b3,b4,b5,b6,b7;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("UserCategories/AmbulanceDrivers");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        String u=settings.getString("lusername","");
        final String username;

        if(!hasLoggedIn)
        {
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }

        if(!isNetworkAvailable())
            Toast.makeText(getApplicationContext(), "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();

        username=settings.getString("lusername","");


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(username)){
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                    SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0); // 0 - for private mode
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putBoolean("hasLoggedIn",false);
                    editor.putString("lusername","");
                    editor.commit();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(!u.equals("")) {
            Toast.makeText(MainActivity.this, username, Toast.LENGTH_SHORT).show();
        }



        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screen = displayMetrics.widthPixels*displayMetrics.heightPixels;
        if(screen>920000)
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_mainsmall);
//        s1 = (SeekBar)findViewById(R.id.seekBar);
          t1 = (TextView)findViewById(R.id.edit2);
          t1.setText(String.valueOf(" "));
        t2 = (TextView)findViewById(R.id.edit1);
        t2.setText(String.valueOf(" "));

        bg1 = (ImageButton) findViewById(R.id.fire);

        bg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                bg1.setBackgroundResource(R.drawable.my_button_bg2);
                bg2.setBackgroundResource(R.drawable.my_button_bg);
                bg3.setBackgroundResource(R.drawable.my_button_bg);
                bg4.setBackgroundResource(R.drawable.my_button_bg);
                bg5.setBackgroundResource(R.drawable.my_button_bg);
                bg6.setBackgroundResource(R.drawable.my_button_bg);

                T=1;
            }
        });

        bg2 = (ImageButton) findViewById(R.id.pregnancy);

        bg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                bg2.setBackgroundResource(R.drawable.my_button_bg2);
                bg1.setBackgroundResource(R.drawable.my_button_bg);
                bg3.setBackgroundResource(R.drawable.my_button_bg);
                bg4.setBackgroundResource(R.drawable.my_button_bg);
                bg5.setBackgroundResource(R.drawable.my_button_bg);
                bg6.setBackgroundResource(R.drawable.my_button_bg);
                T=2;
            }
        });

        bg3 = (ImageButton) findViewById(R.id.accident);

        bg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                bg3.setBackgroundResource(R.drawable.my_button_bg2);
                bg2.setBackgroundResource(R.drawable.my_button_bg);
                bg1.setBackgroundResource(R.drawable.my_button_bg);
                bg4.setBackgroundResource(R.drawable.my_button_bg);
                bg5.setBackgroundResource(R.drawable.my_button_bg);
                bg6.setBackgroundResource(R.drawable.my_button_bg);
                T=3;
            }
        });

        bg4 = (ImageButton) findViewById(R.id.heartattack);

        bg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                bg4.setBackgroundResource(R.drawable.my_button_bg2);
                bg2.setBackgroundResource(R.drawable.my_button_bg);
                bg3.setBackgroundResource(R.drawable.my_button_bg);
                bg1.setBackgroundResource(R.drawable.my_button_bg);
                bg5.setBackgroundResource(R.drawable.my_button_bg);
                bg6.setBackgroundResource(R.drawable.my_button_bg);
                T=4;
            }
        });

        bg5 = (ImageButton) findViewById(R.id.headinjury);

        bg5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                bg5.setBackgroundResource(R.drawable.my_button_bg2);
                bg2.setBackgroundResource(R.drawable.my_button_bg);
                bg3.setBackgroundResource(R.drawable.my_button_bg);
                bg1.setBackgroundResource(R.drawable.my_button_bg);
                bg4.setBackgroundResource(R.drawable.my_button_bg);
                bg6.setBackgroundResource(R.drawable.my_button_bg);
                T=5;
            }
        });


        bg6 = (ImageButton) findViewById(R.id.others);

        bg6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                bg6.setBackgroundResource(R.drawable.my_button_bg2);
                bg2.setBackgroundResource(R.drawable.my_button_bg);
                bg3.setBackgroundResource(R.drawable.my_button_bg);
                bg1.setBackgroundResource(R.drawable.my_button_bg);
                bg5.setBackgroundResource(R.drawable.my_button_bg);
                bg4.setBackgroundResource(R.drawable.my_button_bg);
                T=6;
            }
        });


        b1 = (Button) findViewById(R.id.low);

        b1.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View arg0) {
                                      check = true;
                                      t1.setText(String.valueOf("Low"));
                                      S = 1;
                                  }
                              });

        b2 = (Button) findViewById(R.id.med);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                t1.setText(String.valueOf("Medium"));
                S = 2;
            }
        });

        b3 = (Button) findViewById(R.id.high);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                t1.setText(String.valueOf("High"));
                S = 3;
            }
        });



        b4 = (Button) findViewById(R.id.one);

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                t2.setText(String.valueOf("1"));
                N = 1;
            }
        });
        b5 = (Button) findViewById(R.id.two);


        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                t2.setText(String.valueOf("2"));
                N = 2;
            }
        });
        b6 = (Button) findViewById(R.id.three);


        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                t2.setText(String.valueOf("3"));
                N = 3;
            }
        });
        b7 = (Button) findViewById(R.id.four);


        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                check = true;
                t2.setText(String.valueOf("4"));
                N = 4;
            }
        });



        /*s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                t1.setText(String.valueOf(progress/25+1));
                S=progress/25+1;
                //Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_SHORT).show();

            }
        });*/

        Button btn = (Button)findViewById(R.id.submit);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Bundle extras = getIntent().getExtras();
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                i.putExtra("SI",String.valueOf(S));
                i.putExtra("TI",String.valueOf(T));
                i.putExtra("NO",String.valueOf(N));
                i.putExtra("username",username);
                startActivity(i);
           // startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

    }


    int k=0;

    @Override
    public void onBackPressed()
    {
        if(k==0) {
            Toast.makeText(getApplicationContext(), "Press Back again to log out", Toast.LENGTH_SHORT).show();
            k++;
        }
        else{


            SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0); // 0 - for private mode
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean("hasLoggedIn",false);
            editor.putString("lusername","");
            editor.commit();
            Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);

        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                k=0;
            }
        }, 2000);

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

