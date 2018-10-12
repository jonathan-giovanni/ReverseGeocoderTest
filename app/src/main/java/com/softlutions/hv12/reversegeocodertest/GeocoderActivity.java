package com.softlutions.hv12.reversegeocodertest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.softlutions.hv12.reversegeocodertest.Utils.showSnackbar;

public class GeocoderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private LocationRequest mLocationRequest;
    private Location mLastKnownLocation;
    private GoogleApiClient mGoogleApiClient;

    final int MY_REQUEST_PERMISSION_CODE = 2323;
    private final int INTERVAL_MILISECOND = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval( INTERVAL_MILISECOND)
                .setFastestInterval( INTERVAL_MILISECOND);
    }

    /**
     * Permiso
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLatLng();
                }
                break;
        }
    }


    public int updateLatLng() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_REQUEST_PERMISSION_CODE);
            return Constants.STATE_ERROR;
        }else{
            if(!isLocationEnabled(this)){
                showSnackbar(this,"GPS esta desactivado","Activalo",view -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                });
                mLastKnownLocation = null;
                return Constants.STATE_ERROR;
            }else{
                //TODO: actualizacion de la posicion
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location == null)
                {
                    mLastKnownLocation = null;
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    Toast.makeText(this,"Conectando...",Toast.LENGTH_SHORT).show();
                    return Constants.STATE_UPDATING;
                }
                else{
                    handleNewLocation(location);
                    return Constants.STATE_OK;
                }
            }
        }
    }

    private boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    protected void handleNewLocation(Location location) {
        mLastKnownLocation = location;
    }

    protected Location getmLastKnownLocation(){
        return mLastKnownLocation;
    }



    /**
     * estados del activity
     * para ahorrar bateria
     */

    //se conecta cuando esta el activity desplegado
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
    //se desconecta cuando no esta visible o esta en segundo plano
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLastKnownLocation==null){
            handleNewLocation(location);
        }
    }
}
