package com.example.piinn.projectapp;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    TextView text;
    TextView statusText;
    Timer timer = new Timer();
    TimerTask timerTask;
    //int id = 01;
    //Intent intent = new Intent(this, MainActivity.class);
    //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.speed);
        statusText = (TextView) findViewById(R.id.status);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



    }


    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();

    }


    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        // Do something when connected with Google API Client


        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(1000);
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
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            statusText.setText("Connect");
        } else {
            statusText.setText("can not Connect");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do something when Google API Client connection was suspended
        statusText.setText("suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Do something when Google API Client connection failed
        statusText.setText("failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        float accuracy = location.getAccuracy();
        float bearing = location.getBearing();
        float speed = location.getSpeed();
        long time = location.getTime()/1000;
        boolean stopApp = true;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat secondFormat = new SimpleDateFormat("ss");
        int timeSS=0;
        final String strDate = simpleDateFormat.format(calendar.getTime());
        //long realTime = location.getElapsedRealtimeNanos();
        int direction ;
        long Rt = (((time/1000)/60)/60)/24;

        long startTime = 0L,timeInMilliseconds = 0L,timeSwapBuff=0L,updateTime = 0L;
        timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
        updateTime = timeSwapBuff+timeInMilliseconds;
        int secs = (int)(updateTime/1000);
        int mins = secs/60;
        secs %= 60;
        int millisec = (int)(updateTime%1000);





        text.setText("\n\nSpeed : " + speed +
                "\nLatitude : " + latitude +
                "\nLongitude : " + longitude +
                "\nTime : " + strDate +
                "\nTimer : " + mins + ":" + String.format("%2d", secs) + ":" + String.format("%3d",millisec)+
                "\nProvider : "+provider+
                "\nAltitude : "+altitude+
                "\nAccuracy : "+accuracy+
                "\nBearing : "+bearing);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Telematics For Car Insurance")
                        .setAutoCancel(false)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Speed : " + speed + "\nLongitude : "+longitude+"\nLatitude: "+latitude))
                        .setContentText("Speed : " + speed + "\nLongitude : "+longitude+"\nLatitude: "+latitude)
                        .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(01, mBuilder.build());



    }


}