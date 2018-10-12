# Geocodificación Inversa usando la API de Google

# ¿Qué es la Geocodificación inversa?

La geocodificación inversa es el proceso por el cual se obtiene una ubicación (País,Ciudad,etc) dada una coordenada GPS (latitud y longitud), más información en [Wikipedia](https://en.wikipedia.org/wiki/Reverse_geocoding).


# Comó integrarlo en un proyecto

Primero que se deben agregar las siguientes dependencias al archivo ``` build.gradle (Module : app ) ```

    android {
        compileOptions {
            sourceCompatibility = '1.8'
            targetCompatibility = '1.8'
        }
    }


    dependencies {
        ...
        implementation 'com.google.android.gms:play-services:12.0.1'
    }

