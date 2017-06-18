package com.example.android.ambulancedriverapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener ,OnMapReadyCallback{

    MapView mapView;
    GoogleMap googleMap;
    private final String LOG_TAG = "roshantest";
    private TextView txtOutput;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    int firstTime;
    String username;
    String SI;
    String TI;
    String NO;
    Marker marker;
    String distance=null;
    int h,m;
    LatLng destination = new LatLng(10.0876,76.3882);
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Emergencies");
    DatabaseReference locRef = database.getReference("Emergencies");
    public static final int perm=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


//        TextView S = (TextView) findViewById(R.id.sever);
//        TextView T = (TextView) findViewById(R.id.type);
//        TextView N = (TextView) findViewById(R.id.num);
        Button B = (Button) findViewById(R.id.buttonstop);
        Bundle extras = getIntent().getExtras();
        SI = extras.getString("SI");
        TI = extras.getString("TI");
        NO = extras.getString("NO");
        firstTime=0;
         username= extras.getString("username");
//        N.setText(NO);

//        S.setText(SI);
//        T.setText(TI);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }

        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, perm );

        }

        EmergencyDetails emg =new EmergencyDetails(SI,TI,username,NO);
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
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreateAccount = new Intent(Main2Activity.this, MainActivity.class);
//               LocationDetails k= new LocationDetails(1.0,1.0);
//                locRef.child(username).child("locationDetails").setValue(k);
                myRef.child(username).removeValue();
                intentCreateAccount.putExtra("username",username);
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
            if(firstTime==0){firstTime++;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate,17.0f);
                googleMap.moveCamera(cameraUpdate);}

            String url = getDirectionsUrl(coordinate, destination);

            DownloadTask downloadTask = new DownloadTask();

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
        myRef.child(username).removeValue();
//
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

        @Override
        public void onLocationChanged(Location location){
            LocationDetails loc = new LocationDetails(location.getLatitude(),location.getLongitude());
            locRef.child(username).child("locationDetails").setValue(loc);
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

            float f=googleMap.getCameraPosition().zoom;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinate, f);
            googleMap.moveCamera(cameraUpdate);

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
        super.onResume();
    }
    @Override
    public void onPause() {
        myRef.child(username).removeValue();
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

//            distanceTextView.setText("Estimated Time Taken:"+distance);

        }
    }


}

