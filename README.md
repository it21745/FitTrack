# FitTrack
Για να τρεξει η εφαρμογη απαιτειται
* Java 21 ή νεοτερη
* Maven 3.8 ή νεοτερη

## Ρυθμιση τοπικων μεταβλητων
Η εφαρμογη απαιτει τις ακολουθες μεταβλητες περιβαλοντος, οι τιμες τους αφηνονται κενες στο συγκεκριμενο αρχειο και μπορουν να βρεθουν στην αναφορα


```
WINDOWS:
set DB_PASSWORD=
set JWT_SECRET_KEY=
set WEATHER_API_KEY=

LINUX/MACOS
export DB_PASSWORD=
export JWT_SECRET_KEY=
export WEATHER_API_KEY=
```

## Εκτελεση
Η εφαρμογη εκτελειται τρεχοντας την ακολουθη εντολη

```
WINDOWS:
mvnw spring-boot:run

LINUX/MACOS
./mvnw spring-boot:run
```