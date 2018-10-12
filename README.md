# Geocodificación Inversa usando la API de Google

# ¿Qué es la Geocodificación inversa?

La geocodificación inversa es el proceso por el cual se obtiene una ubicación (País,Ciudad,etc) dada una coordenada GPS (latitud y longitud), más información en [Wikipedia](https://en.wikipedia.org/wiki/Reverse_geocoding).


# Comó integrarlo en un proyecto

Primero que se deben agregar las siguientes dependencias al archivo ``` build.gradle (Module : app ) ```

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'com.android.support:design:27.1.1'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'

    implementation 'com.google.android.gms:play-services:12.0.1'
}

