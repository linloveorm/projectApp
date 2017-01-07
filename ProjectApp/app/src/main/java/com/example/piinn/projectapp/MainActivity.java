package com.example.piinn.projectapp;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DigitalClock;
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
    TextView test;
    TextView distanceTxt;
    TextView timerTxt;

    boolean locationChange = true;

    //initial count time
    int countTime = 0 ;
    int countCheck = 0 ;
    int timerStart = 0;
    int timerNext = 0;

    //Initial variable for calculate distance
    double latStr = 0;
    double longStr = 0;
    double latNext = 0 ;
    double longNext = 0 ;
    double distance = 0;

    //test if
    int i = 0,j=0;

    //Initial variable for get speed
    float speedStart = 0;
    float speedNext = 0;

    //Initial varialble for calculate accelerate
    double accStart = 0;
    double accNext = 0;
    double accChange = 0;


    //initial Timestamp Start Time
    String mytime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());

    //Set Clock
    DigitalClock digitalClock ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initial TextView
        text = (TextView) findViewById(R.id.speed);
        statusText = (TextView) findViewById(R.id.status);
        test = (TextView)findViewById(R.id.test);
        distanceTxt = (TextView)findViewById(R.id.distance);
        timerTxt = (TextView)findViewById(R.id.timerTxt);

        //Initial DigitalClock
        digitalClock = (DigitalClock)findViewById(R.id.digitalClk);

        //Initial googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //counting time and time stamp start time
        final Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        test.setText("\ncount = "+countTime+"\ntimestamp : "+mytime);
                        countTime++;
                    }
                });
            }
        }, 1000, 1000);

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
                    .setInterval(5000);
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

    /* onLocationChange Real
    @Override
    public void onLocationChanged(Location location) {
        countCheck++;
        Log.i("test",""+countCheck);
        Log.i("Test",""+countTime);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //time stamp when get location
        String timeStampLoc ;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //initial variable that need to use << comment
        //String provider = location.getProvider();
        //long time = location.getTime()/1000;
        //double altitude = location.getAltitude();
        //float accuracy = location.getAccuracy();
        //float bearing = location.getBearing();
        float speed = location.getSpeed();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //show Current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        final String strDate = simpleDateFormat.format(calendar.getTime());



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Initial lat and long in Start
        if(locationChange&&distance==0&&latStr ==0&&longStr==0)
        {
            i++;
            //calculate Distance
            latStr = latitude;
            longStr =longitude;
            speedStart = speed;
            timerStart = countTime;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            locationChange = false;
        }

        //calculate distance every 1 minute
        if(((countTime-((countCheck+1)/5))%60) == 0 || (latStr != latitude || longStr != longitude))
        {
            latNext = latitude;
            longNext = longitude;
            speedNext = speed;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        //show distance
        distanceTxt.setText("\nDistance : " + distance +
                "\nStart : " + latStr + " , " + longStr +
                "\nNext : " + latNext + " , " + longNext+
                "\nSpeed Start : "+speedStart+" , "+speedNext+
                "\nTimer : "+timerStart+" , "+timerNext+
                "\nAccelerate : "+accStart+" , "+accNext+
                "\nAcChange : "+accChange);

        //When location change
        if(!locationChange&&distance != 0)
        {
            i++;
            latStr = latNext;
            longStr = longNext;
            speedStart = speedNext;
            timerStart = timerNext;
            if(accStart != 0 && accNext!=0)
            {
                accStart = accNext;
            }

            locationChange = true;
        }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Show Location Speed and time
        text.setText("\n\nSpeed : " + speed +
                "\nLatitude : " + latitude +
                "\nLongitude : " + longitude +
                "\nTime : " + strDate +
                "\nCheck : "+countCheck);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Notification application
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Telematics For Car Insurance")
                        .setAutoCancel(false)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Speed : " + speed +
                                "\nLongitude : "+longitude+
                                "\nLatitude: "+latitude))
                        .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(01, mBuilder.build());
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }*/


    //onLocationChange for test
    @Override
    public void onLocationChanged(Location location) {
        countCheck++;
        Log.i("test",""+countCheck);
        Log.i("Test",""+countTime);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //time stamp when get location
        String timeStampLoc ;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //initial variable that need to use comment
        float speed = location.getSpeed();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //show Current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        final String strDate = simpleDateFormat.format(calendar.getTime());



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Initial lat and long in Start
        if(countCheck==1)//1st
        {
            i++;
            //calculate Distance
            latStr = 13.721940;
            longStr =100.776215;
            speedStart = 60;
            timerStart = countTime;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            locationChange = false;
        }

        //calculate distance every 1 minute
        if(countCheck==2)//2nd
        {
            latNext = 13.708242;
            longNext = 100.774757;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==3)//3rd
        {
            latNext = 13.721940;
            longNext = 100.776215;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==4)//4th
        {
            latNext = 13.713290;
            longNext = 100.754467;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==5)//5th
        {
            latNext = 13.714660;
            longNext = 100.748944;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==6)//6th
        {
            latNext = 13.717076;
            longNext = 100.744101;
            speedNext = 60;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==7)//7th
        {
            latNext = 13.718226;
            longNext = 100.740614;
            speedNext = 60;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }


        if(countCheck==8)//8th
        {
            latNext = 13.722311;
            longNext = 100.739973;
            speedNext = 65;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==9)//9th
        {
            latNext = 13.722650;
            longNext = 100.746145;
            speedNext = 65;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==10)//10th
        {
            latNext = 13.722762;
            longNext = 100.749373;
            speedNext = 70;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==11)//11th
        {
            latNext = 13.723171;
            longNext = 100.751351;
            speedNext = 65;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==12)//12th
        {
            latNext = 13.722415;
            longNext = 100.752858;
            speedNext = 70;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==13)//13th
        {
            latNext = 13.721660;
            longNext = 100.752858;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==14)//14th
        {
            latNext = 13.721798;
            longNext = 100.766236;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==15)//13th
        {
            latNext = 13.721940;
            longNext = 100.776215;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        if(countCheck==16)//16th
        {
            latNext = 13.721900;
            longNext = 100.774693;
            speedNext = 80;
            timerNext = countTime ;

            distance = getDistance(latStr,longStr,latNext,longNext);
            accStart = calAccelerate(speedStart,speedNext,timerStart,timerNext);

            accChange = accStart-accNext;

            timeStampLoc = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
            timerTxt.setText("Test : "+i+" , "+j+"\nLocation Time Stamp : "+timeStampLoc+"\nBoolean : "+locationChange);

            j++;
        }

        //show distance
        distanceTxt.setText("\nDistance : " + distance +
                "\nStart : " + latStr + " , " + longStr +
                "\nNext : " + latNext + " , " + longNext+
                "\nSpeed Start : "+speedStart+" , "+speedNext+
                "\nTimer : "+timerStart+" , "+timerNext+
                "\nAccelerate : "+accStart+" , "+accNext+
                "\nAcChange : "+accChange);

        //When location change
        if(!locationChange&&distance != 0)
        {
            i++;
            latStr = latNext;
            longStr = longNext;
            speedStart = speedNext;
            timerStart = timerNext;
            if(accStart != 0 && accNext!=0)
            {
                accStart = accNext;
            }

            locationChange = true;
        }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Show Location Speed and time
        text.setText("\n\nSpeed : " + speed +
                "\nLatitude : " + latitude +
                "\nLongitude : " + longitude +
                "\nTime : " + strDate +
                "\nCheck : "+countCheck);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Notification application
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Telematics For Car Insurance")
                        .setAutoCancel(false)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Speed : " + speed +
                                "\nLongitude : "+longitude+
                                "\nLatitude: "+latitude))
                        .setOngoing(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(01, mBuilder.build());
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    //calculate distance in meters from lat and long
    private double getDistance (double latStart,double  longStart,double  latNext,double  longNext)
    {
        double latS = latStart;
        double longS = longStart;
        double latN = latNext;
        double longN = longNext;

        double  calLong = longN-longS;
        double dist = Math.sin(deg2rad(latS))
                * Math.sin(deg2rad(latN))
                + Math.cos(deg2rad(latS))
                * Math.cos(deg2rad(latN))
                * Math.cos(deg2rad(calLong));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist*1000;
        return (dist);


    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private double calAccelerate(float speedS,float speedN,int timerS,int timerN)
    {
        double accelerateM = (speedS-speedN) / (timerS - timerN) ;

        return accelerateM;
    }





}