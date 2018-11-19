# ot-harjoitustyö - otlike

## Mikä?
Kurssin *"Ohjelmistotekniikka"* harjoitustyönä tuotettu yksinkertainen roguelite-henkinen luolaseikkailu, jossa lätkitään mörköjä turpaan ja otetaan turpaan kahta kovemmin.

## Dokumentaatio
- [vaatimusmäärittely](documentation/vaatimusmaarittely.md)
- [tuntikirjanpito](documentation/tuntikirjanpito.md)

## Releaset
_Coming soon(ish)_

## Komentorivitoiminnot
### Testaus
Testien ajaminen tapahtuu komennolla
```
mvn test
```

Testikattavuusraportin generointi tapahtuu Mavenin [JaCoCo](https://www.eclemma.org/jacoco/)-lisäosalla
```
mvn jacoco:report
```
jonka jälkeen selaimella tarkasteltava raportti löytyy polusta `target/site/jacoco/index.html`.

### Suorittaminen
Pelin voi käynnistää suoraan kutsumalla
```
mvn compile exec:java
```
tai
```
mvn compile exec:java -Dexec.mainClass=toilari.otlite.Launcher
```

### Pakkaus
Suoritettavan _.jar_-tiedoston saa generoitua komennolla
```
mvn package
```
Generoitu _OTLite-1.0-SNAPSHOT.jar_ löytyy polusta `target/`.

### JavaDoc
JavaDoc generoidaan komennolla
```
mvn javadoc:javadoc
```
jonka jälkeen selaimella tarkasteltavissa oleva dokumentaatio löytyy polusta `target/site/apidocs/index.html`.

### Checkstyle
Käytössä oleva _checkstyle_ on määritelty projektin juuressa löytyvässä [checkstyle.xml](checkstyle.xml)-tidostossa. Checkstylen tarkistus tapahtuu komennolla
```
mvn jxr:jxr checkstyle:checkstyle
```
jonka jälkeen selaimella tarkasteltavissa oleva raportti löytyy polusta `target/site/checkstyle.html`.
