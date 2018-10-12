package com.softlutions.hv12.reversegeocodertest;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

public class MainActivity extends GeocoderActivity{

    Button btnRefreshLatLng;
    TextView txtLatLng;
    ProgressBar mProgressBarAddress;
    private AddressResultReceiver mResultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultReceiver  = new AddressResultReceiver(new Handler());

        btnRefreshLatLng = findViewById(R.id.btnRefreshLatLng);
        txtLatLng        = findViewById(R.id.txtLatLng);
        mProgressBarAddress = findViewById(R.id.progress_bar_address);

        btnRefreshLatLng.setOnClickListener(click -> {
            updateLatLng();
        });
    }

    private void setVisibleProgressBarAddress(boolean isVisible) {
        if(isVisible){
            mProgressBarAddress.setVisibility(ProgressBar.VISIBLE);
        } else {
            mProgressBarAddress.setVisibility(ProgressBar.GONE);
        }
    }

    @Override
    public int updateLatLng() {
        int r = super.updateLatLng();
        if(r!=Constants.STATE_ERROR){
            setVisibleProgressBarAddress(true);
        }
        return r;
    }


    @Override
    public void onConnectionSuspended(int i) {
        super.onConnectionSuspended(i);
        setVisibleProgressBarAddress(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        super.onConnectionFailed(connectionResult);
        setVisibleProgressBarAddress(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        setVisibleProgressBarAddress(false);
    }

    @Override
    protected void handleNewLocation(Location location) {
        super.handleNewLocation(location);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        //txtLatLng.setText("Lat : "+lat+"\nLng : "+lng);
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "No hay geocoder", Toast.LENGTH_SHORT).show();
            setVisibleProgressBarAddress(false);
            return;
        }

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String Address = resultData.getString(Constants.RESULT_DATA_KEY);
            txtLatLng.setText(Address);

            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(MainActivity.this, "Direccion "+Address, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "No se encontro direcion", Toast.LENGTH_SHORT).show();
            }
            setVisibleProgressBarAddress(false);

        }
    }
}
