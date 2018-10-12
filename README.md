# Geocodificación Inversa en Android


<img src="https://github.com/jonathancplusplus/ReverseGeocoderTest/blob/master/example_geocoder.png" width="480">

Este proyecto tiene las siguientes caracteristicas:

* Obtención de latitud y longitud de manera precisa
* Se puede establecer en modo automático para obtener cambios en la ubicación
* Implementación sencilla a traves de herencia al Activity que deseas aplicar
* Manejo de permisos necesarios para obtener la ubicación
* Manejo de error en caso de que el GPS este desactivado
* Geocodificación inversa por medio de llamada a un servicio
* No necesita <b> Google API Key</b> para ser usado

# ¿Qué es la Geocodificación inversa?

La geocodificación inversa es el proceso por el cual se obtiene una ubicación (País,Ciudad,etc) dada una coordenada GPS (latitud y longitud), más información en [Wikipedia](https://en.wikipedia.org/wiki/Reverse_geocoding).


# ¿Comó integrarlo en un proyecto?

<b>Primero se deben agregar las siguientes dependencias al archivo </b> ``` build.gradle (Module : app ) ```

Para habilitar el uso de Lambda

    android {
        ...
        compileOptions {
            sourceCompatibility = '1.8'
            targetCompatibility = '1.8'
        }
    }
Para integrar los servicios de la API de Google

    dependencies {
        ...
        implementation 'com.google.android.gms:play-services:12.0.1'
    }

<b> Segundo se deben agregar los siguientes cambios al archivo </b> ``` AndroidManifest.xml```

Permisos necesarios para habilitar la geolocalización

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

Siempre en manifest dentro de las etiquetas ``` <application> ... </application> ``` se agrega el siguiente servicio

    <application ...>
        ... 
        <service
            android:name=".FetchAddressIntentService"
            android:exported="false"/>
    </application>
  
<b> Finalmente copia los archivos </b>  ``` Constants.java , FetchAddressIntentService.java , PositionLatLngActivity.java , Utils.java ``` <b> que estan en [ReverseGeocoderTest/app/src/main/java/com/softlutions/hv12/reversegeocodertest/](https://github.com/jonathancplusplus/ReverseGeocoderTest/tree/master/app/src/main/java/com/softlutions/hv12/reversegeocodertest). a tu proyecto </b>

# ¿Comó usar los servicios de geolocalización?

<b> Aplica herencia de la clase </b> ``` PositionLatLngActivity ``` <b> en el Activity que deseas agregar la funcionalidad de geolocalización. </b>

    public class MainActivity extends PositionLatLngActivity {
      ...
    }
<b> Conocer la coordenada GPS </b>

Si deseas saber Latitud y Longitud de la posición en ese instante llama a la función ``` updateLatLng(); ``` y captura el resultado llamando el siguiente método
    
    
    @Override
    protected void handleNewLocation(Location location) {
        super.handleNewLocation(location);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
    }
    
Por defecto las actualizaciones automáticas estan desactivadas pero si deseas cambiar esto llama al método ``` isAutomaticUpdates( true o false ) ``` y asignale true o false dependiendo que desees, y si quieres establecer cada cuanto tiempo se haran las actualizaciones entonces llama al mismo método pero envia como segundo parametro el tiempo en milisegundos.

<b> Conocer la ubicación (País,Estado o Departamento y Ciudad ) </b>

Aquí se aplica la geocodificación inversa de manera que conociendo las coordenadas del GPS como se puede observar en la función ``` protected void handleNewLocation(Location location) { ... } ``` se envian como parametros a la API de Google Places y se obtiene las variables de ubicación mencionadas, para esto se agrega los siguientes fragmentos de código:

Para obtener el resultado del servicio de ubicación creamos la siguiente clase dentro de nuestro activity:

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
        
            String r_address = resultData.getString(Constants.RESULT_DATA_KEY);
            
            if (resultCode == Constants.SUCCESS_RESULT) {
                //en caso de que se encuentra la dirección
                Toast.makeText(MainActivity.this, "Dirección encontrada "+r_address, Toast.LENGTH_SHORT).show();
            }else{
                //en caso de que no se encontro la dirección
                Toast.makeText(MainActivity.this, "No se encontro direcion", Toast.LENGTH_SHORT).show();
            }
        }
    }

Creamos el siguiente objeto en nuestra activity que manejara los resultados:

    private AddressResultReceiver mResultReceiver;
    
Se inicializa el objeto ``` mResultReceiver ``` que acabamos de crear,esto dentro de la función ``` onCreate ```

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ...
        
        // se crea un manejador para recibir los resultados
        mResultReceiver  = new AddressResultReceiver(new Handler());   
    }

Iniciando el servicio de ubicación dada una coordenada GPS:

    @Override
    protected void handleNewLocation(Location location) {
        super.handleNewLocation(location);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        /**obtengo la latitud y longitud*/
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "No hay geocoder ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


El proyecto muestra un ejemplo de la aplicación de las clases mencionadas utilizando botones,barras de progreso,etc.


# Licencia

    Copyright 2015-2018 Jonathan Hernández

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
