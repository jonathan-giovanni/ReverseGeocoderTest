# Geocodificación Inversa usando la API de Google


<img src="https://github.com/jonathancplusplus/ReverseGeocoderTest/blob/master/example_geocoder.png" width="480">

# ¿Qué es la Geocodificación inversa?

La geocodificación inversa es el proceso por el cual se obtiene una ubicación (País,Ciudad,etc) dada una coordenada GPS (latitud y longitud), más información en [Wikipedia](https://en.wikipedia.org/wiki/Reverse_geocoding).


# ¿Comó integrarlo en un proyecto?

* Primero se deben agregar las siguientes dependencias al archivo ``` build.gradle (Module : app ) ```

\tPara habilitar el uso de Lambda

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

* Segundo se deben agregar los siguientes cambios al archivo ``` AndroidManifest.xml```

Permisos necesarios para habilitar la geolocalización

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />


   


