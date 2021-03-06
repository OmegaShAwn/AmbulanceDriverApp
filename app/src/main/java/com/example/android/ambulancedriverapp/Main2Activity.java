package com.example.android.ambulancedriverapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener ,OnMapReadyCallback, SensorEventListener {

    MapView mapView;
    GoogleMap googleMap;
    private final String LOG_TAG = "roshantest";
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    int firstTime;
    String username;
    String SI;
    String TI;
    String NO;
    Marker marker;
    String distance=null;
    TextView distanceTextView;
    int h,m;
    LatLng destination = new LatLng(10.0876,76.3882);
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Emergencies");
    DatabaseReference locRef = database.getReference("Emergencies");
    DatabaseReference logRef = database.getReference("Log/Ambulance");
    public static final int perm=0;
    public static float swRoll;
    public static float swPitch;
    public float swAzimuth = 400;
    public float swAzimuthp = 300;


    public static SensorManager mSensorManager;
    public static Sensor accelerometer;
    public static Sensor magnetometer;

    public static float[] mAccelerometer = null;
    public static float[] mGeomagnetic = null;

//    public Location loca = new Location("loca");

    public Location loca = null;

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // onSensorChanged gets called for each sensor so we have to remember the values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometer = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success && loca != null) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth(direction), pitch and roll values.
                double azimuth = 180 * orientation[0] / Math.PI;
                swAzimuth = (float)azimuth;
                if((swAzimuthp-swAzimuth>7 || swAzimuthp-swAzimuth<-7)) {
                    swAzimuthp = swAzimuth;
                    double pitch = 180 * orientation[1] / Math.PI;
                    double roll = 180 * orientation[2] / Math.PI;

                    float bear = swAzimuth;
                    float zo = googleMap.getCameraPosition().zoom;
                    LatLng coordinate = new LatLng(loca.getLatitude(), loca.getLongitude());
                    CameraPosition currentPlace = new CameraPosition.Builder()
                            .target(coordinate)
                            .bearing(bear).tilt(65.5f).zoom(zo).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



//        TextView S = (TextView) findViewById(R.id.sever);
//        TextView T = (TextView) findViewById(R.id.type);
//        TextView N = (TextView) findViewById(R.id.num);
        Button B = (Button) findViewById(R.id.buttonstop);
        distanceTextView = (TextView)findViewById(R.id.textView3);
        Bundle extras = getIntent().getExtras();
        SI = extras.getString("SI");
        TI = extras.getString("TI");
        NO = extras.getString("NO");
        firstTime=0;
        final Calendar c= Calendar.getInstance();

        username= extras.getString("username");
        logRef=logRef.child(username);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }

        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, perm );

        }

        final EmergencyDetails emg =new EmergencyDetails(SI,TI,username,NO);
        myRef.child(username).child("emergencyDetails").setValue(emg);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

//        txtOutput = (TextView) findViewById(R.id.txtoutput);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        logRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                inc();
                if(count>49){
                    final DatabaseReference exceed = database.getReference("Log/Ambulance/"+username);
                    dec();

                    exceed.addChildEventListener(new ChildEventListener() {

                        int c=0;
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                            c++;
                            if (c != 1) {
                                timeEnded tE = dataSnapshot.getValue(timeEnded.class);
                                exceed.child(Integer.toString(c-1)).setValue(tE);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intentCreateAccount = new Intent(Main2Activity.this, MainActivity.class);
                myRef.child(username).removeValue();
                logRef = logRef.child(Integer.toString(count + 1));
                Calendar st = Calendar.getInstance();
                timeEnded tE = new timeEnded();
                tE.setyears(c.get(Calendar.YEAR));
                tE.setmonths(c.get(Calendar.MONTH));
                tE.setdates(c.get(Calendar.DAY_OF_MONTH));
                tE.sethours(c.get(Calendar.HOUR_OF_DAY));
                tE.setminutes(c.get(Calendar.MINUTE));
                tE.setyear(st.get(Calendar.YEAR));
                tE.setmonth(st.get(Calendar.MONTH));
                tE.setdate(st.get(Calendar.DAY_OF_MONTH));
                tE.sethour(st.get(Calendar.HOUR_OF_DAY));
                tE.setminute(st.get(Calendar.MINUTE));
                tE.setnumber(Integer.valueOf(emg.no));
                tE.setseverity(Integer.valueOf(emg.si));
                tE.settype(Integer.valueOf(emg.ti));
                if (loc != null) {
                    tE.setlatitudee(loc.getLatitude());
                    tE.setlongitudee(loc.getLongitude());
                } else {
                    tE.setlatitudee(0.0);
                    tE.setlongitudee(0.0);
                }
                if (logloc != null) {
                    tE.setlatitude(logloc.getLatitude());
                    tE.setlongitude(logloc.getLongitude());
                } else {
                    tE.setlatitude(0.0);
                    tE.setlongitude(0.0);
                }
                Location endloc = new Location("endloc");
                Location destloc = new Location("destloc");
                endloc.setLatitude(loc.getLatitude());
                endloc.setLongitude(loc.getLongitude());
                destloc.setLatitude(10.0876);
                destloc.setLongitude(76.3882);
//                destloc.setLatitude(10.504032);
//                destloc.setLongitude(76.234133);
                if(destloc.distanceTo(endloc)>1000)
                    tE.setdest(1);
                else
                    tE.setdest(0);
                logRef.setValue(tE);
                intentCreateAccount.putExtra("username", username);
                startActivity(intentCreateAccount);
                finish();
            }});



            if (ActivityCompat.checkSelfPermission(Main2Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main2Activity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ;

        if(googleMap!=null) {
            googleMap.setMyLocationEnabled(true);
            Log.v("main", "googlemap  not null");
            MapsInitializer.initialize(Main2Activity.this);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(55.854049, 13.661331));
            LatLngBounds bounds = builder.build();
            int padding = 0;
            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cameraUpdate);
        }



    }

    public int p=0;

    public int count=0;

    public void inc(){
        count++;
    }

    public void dec() {
        count--;
    }

    public void incc(){p++;}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case perm: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void setMap(Location location){
        if(googleMap!=null){
            MapsInitializer.initialize(Main2Activity.this);
            googleMap.clear();

            LatLng coordinate = new LatLng(location.getLatitude(),location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(coordinate));
            if(firstTime == 0) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate, 17.0f);
                googleMap.moveCamera(cameraUpdate);
                firstTime++;
            }
            String url = getDirectionsUrl(coordinate, destination);

            DownloadTask downloadTask = new DownloadTask();
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }



    @Override
    public void onMapReady(GoogleMap map) {
//DO WHATEVER YOU WANT WITH GOOGLEMAP
        googleMap=map;
        Log.v("main","googlemap set");
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);


    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
//        myRef.child(username).removeValue();
        mGoogleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Google api connected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        if (marker != null) {
            marker.remove();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        @Override
        public void onConnectionSuspended(int i){
            Log.i(LOG_TAG,"Google api connection has been suspended");

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult){
            Log.i(LOG_TAG,"Google api connection has been failed");
        }

        public LocationDetails logloc;
        public LocationDetails loc;
        int once=0;
        @Override
        public void onLocationChanged(Location location){
            loca=location;
            loc = new LocationDetails(location.getLatitude(),location.getLongitude());
            loc.active = 1;
            locRef.child(username).child("locationDetails").setValue(loc);
            if(once==0) {
                logloc=new LocationDetails(location.getLatitude(),location.getLongitude());
                once++;
            }

            /*if(distance!=null) {
                locRef.child(username).child("locationDetails").child("time").setValue(h*100+m);
            }
            else
                locRef.child(username).child("locationDetails").child("time").setValue("null");*/
            Log.i(LOG_TAG,location.toString());
//            txtOutput.setText("Latitude"+Double.toString(location.getLatitude())+"\nLongitude"+Double.toString(location.getLongitude()));
            if(googleMap!=null)
                googleMap.clear();
                if(loc.getLatitude()!=null) {

                    setMap(location);
                }
                MapsInitializer.initialize(Main2Activity.this);

                LatLng coordinate = new LatLng(location.getLatitude(),location.getLongitude());

                googleMap.addMarker(new MarkerOptions()
                        .position(coordinate));

            if(firstTime == 0) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate, 17.0f);
                googleMap.moveCamera(cameraUpdate);
                firstTime++;
            }
        }

    @Override
    public void onBackPressed()
    {
        Toast.makeText(Main2Activity.this,"Press STOP",Toast.LENGTH_LONG).show();
        // code here to show dialog
//        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    public void onResume() {
        mapView.onResume();
        EmergencyDetails emg =new EmergencyDetails(SI,TI,username,NO);
        myRef.child(username).child("emergencyDetails").setValue(emg);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }
    @Override
    public void onPause() {
//        myRef.child(username).removeValue();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        myRef.child(username).removeValue();
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();


}

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.v("url",url);
        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                String s =jObject.toString();
                String parts[] = s.split(",");
                String parts2[] = parts[13].split(":");
                distance = parts2[2];

                Log.v("distance",distance);


                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            if(result!=null)
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions!=null)
                googleMap.addPolyline(lineOptions);
            if(distance!=null)
                distanceTextView.setText("Estimated Time Taken :" + distance.substring(1, distance.length() - 1));


        }
    }


}

