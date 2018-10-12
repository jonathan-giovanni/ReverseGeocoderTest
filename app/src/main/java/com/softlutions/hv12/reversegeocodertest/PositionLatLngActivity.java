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
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.softlutions.hv12.reversegeocodertest.Utils.showSnackbar;

/**
 * Implementacion de servicios de localizacion basada en GPS
 * Se puede establecer que sea en diferentes intervalos
 * Se solicitan los permisos para la localizacion (esta en el manifest)
 */
public class PositionLatLngActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = "POSITION_LAT_LNG";
    protected LocationRequest mLocationRequest;
    private Location mLastKnownLocation;
    private GoogleApiClient mGoogleApiClient;

    final int MY_REQUEST_PERMISSION_CODE = 2323;
    private final int INTERVAL_MILISECOND = 3000;
    private boolean isUpdated = false;

    /**
     * @param savedInstanceState
     * se inicaliza el cliente de la API de Google
     * se inicializa el servicio de localización en intervalos
     */
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
     * Obtiene el resultado de la solicitud de permisos
     * @param requestCode codigo unico para llamada al permiso
     * @param permissions lista de permisos que se estan solicitando
     * @param grantResults resultados para los permisos solicitados
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

    /**
     * Funcion que llama al servicio de actualizacion y como resultado llama a la funcion handleNewLocation
     * Primero evalua los permisos y luego los solicita si no estan activados
     * Segundo si la localizacion esta desactivada se muestra un snackbar con funcion de ir a configuracion
     * Tercero si no se encuentra el servicio de localizacion se inicia un servicio de actualizacion
     * Cuarto si se encuentra la localizacion se envia a la funcion handleNewLocation
     * @return retorna el estado esto puede ser un Error Pendiente o OK
     */
    public int updateLatLng() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_REQUEST_PERMISSION_CODE);
            return Constants.STATE_ERROR;
        }else{
            if(!isLocationEnabled()){
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


    /**
     * funcion que evalua si la localizacion (GPS) esta activo
     * @return true si esta activado o false si esta desactivado
     */
    private boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * Funcion que maneja los cambios en la localizacion
     * Se llama cada vez que se realiza un cambio en la ubicacion
     * @param location
     */
    protected void handleNewLocation(Location location) {
        mLastKnownLocation = location;
    }

    /**
     * @return la ultima ubicacion conocida
     */
    protected Location getmLastKnownLocation(){
        return mLastKnownLocation;
    }

    /**
     * ------------------------------------------------------------------------------------
     * estados del activity manejan la conexion con la API de Google para la localización
     * Se modifica el estado de la conexión para ahorrar bateria
     * ------------------------------------------------------------------------------------
     */

    /**
     * Si el activity esta visible entonces se conecta con la API de Google
     */
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    /**
     * Si el activity no esta visible entonces se desconecta con la API de Google
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    /**
     * ------------------------------------------------------------------------------------
     * estados del la conexión con la API de Google
     * Se modifica el estado de la conexión para ahorrar bateria
     * ------------------------------------------------------------------------------------
     */

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG,"Se ha conectado a la API de Google");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"Se ha suspendido la conexión API de Google");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG,"Conexión fallida a la API de Google");
    }

    /**
     * Cuando se actualiza la ubicacion se guarda si la que se tiene como ultima conocida es nula
     * y se pregunta si se ha establecido como true la actualización en cada cambio
     * @param location localización
     */
    @Override
    public void onLocationChanged(Location location) {
        if(mLastKnownLocation==null){
            handleNewLocation(location);
        }else if(isUpdated){
            handleNewLocation(location);
        }
    }
}
