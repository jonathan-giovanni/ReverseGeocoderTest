/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softlutions.hv12.reversegeocodertest;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale.Builder;
import java.util.List;
import java.util.Locale;

/**
 * Asincrónicamente maneja un intent usando un subproceso de trabajo.
 * Recibe un objeto ResultReceiver y una ubicación a través de un intent.
 * Intenta obtener la dirección de la ubicación mediante un PositionLatLng y envía el resultado al ResultReceiver.
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "INFO_GC";

    /**
     * El receptor donde se reenvían los resultados de este servicio.
     */
    private ResultReceiver mReceiver;

    /**
     * Este constructor es obligatorio y llama al super IntentService (String)
     * constructor con el nombre de un hilo de trabajo.
     */
    public FetchAddressIntentService() {
        super(TAG);
    }

    /**
     * Este servicio llama a este método desde el subproceso de trabajo predeterminado con la intención que se inició
     * el servicio. Cuando este método vuelve, el servicio se detiene automáticamente.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // si no se envia el recibidor entonces se detiene el proceso
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Obtengo la localizacion
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        // Si la ubicacion es nulla o no se ha enviado entonces se enviar un mensaje de error
        // Se detiene el proceso
        if (location == null) {
            errorMessage = "No location provider";
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            return;
        }

        // Para obtener los resultados en ingles
        Locale aLocale = new Builder().setLanguage("en").build();
        Geocoder geocoder = new Geocoder(this, aLocale);

        // Se guardan los resultados en una lisa de tipo Address
        List<Address> addresses = null;

        try {
            // Intenta obtener la ubicación con un maximo de 4 resultados
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    4);


        } catch (IOException ioException) {
            errorMessage = "Servicio no disponible";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Captura latitud y longitud invalidas
            errorMessage = "Coordenadas invalidas";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        // En caso de que no se hayan encontrado direcciones se envia un mensaje de error
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Direccion no encontrada";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Log.v(TAG,"Direccion encontrada 1");
            System.out.println("Resultados de busqueda");
            String city=null,state=null,country=null,result="";
            //para cada una de las ubicaciones encontradas

            /**
             * Se evaluan cada una de las direcciones y se obtiene la ciudad estado y pais
             * Se hace asi por que en algunos casos ya sea la ciudad o estado es nulo en una direccion
             * ademas se obtiene el nombre mas corto, esto es asi por que en algunos casos agrega texto adicional Ej.
             * Deparment of Sonsonate, es equivalente a solamente Sonsonate, por eso se busca el nombre mas corto
             * El resultado se concantena en un String result
             */
            for (Address addressItem:addresses) {
                System.out.println("Ciudad "+addressItem.getLocality());
                System.out.println("Estado "+addressItem.getAdminArea());
                System.out.println("Pais "+addressItem.getCountryName());
                System.out.println("Codigo del pais "+addressItem.getCountryCode());

                //TODO para mostrar la direccion a ese punto en especifico
                // for(int i=0;i<addressItem.getMaxAddressLineIndex();i++){
                //    System.out.println(addressItem.getAddressLine(i));
                // }


                String tempCity = addressItem.getLocality();
                String tempState = addressItem.getAdminArea();

                if(city==null){
                    city = addressItem.getLocality();
                }
                if(tempCity!=null){
                    if (city.length()>tempCity.length()){
                        city = tempCity;
                    }
                }
                if(state==null){
                    state = addressItem.getAdminArea();
                }
                if(tempState!=null){
                    if(state.length()>tempState.length()){
                        state = tempState;
                    }
                }
                if(country==null){
                    //country = addressItem.getCountryName();
                    country = addressItem.getCountryName();
                }
            }
            if(city!=null){
                city = city.replaceAll("Department","");
                result += city+" , ";
            }
            if(state!=null){
                state = state.replaceAll("Department","");
                result += state+" , ";
            }
            if(country!=null){
                result+=country;
            }
            //TODO enviar la direccion para ser procesada
            Address found_address = addresses.get(0);
            found_address.setLocality(city);
            found_address.setAdminArea(state);

            System.out.println(result);

            deliverResultToReceiver(Constants.SUCCESS_RESULT, result);
        }
    }

    /**
     * Envia un codigo de resultado al recibidor que obtiene el mensaje
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
