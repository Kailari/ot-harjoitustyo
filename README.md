# ot-harjoitustyö - otlike

## Mikä?
Kurssin *"Ohjelmistotekniikka"* harjoitustyönä tuotettu yksinkertainen roguelite-henkinen luolaseikkailu, jossa lätkitään mörköjä turpaan ja otetaan turpaan kahta kovemmin.

## Apua! Koodissa on 9001 virhettä puuttuvista metodeista kun avaan tiedoston X editorissa Y!
Kehitysympäristösi ei todennäköisesti ymmärrä lombokilla ([*Project Lombok*](https://projectlombok.org)) generoituja gettreitä/settereitä/konstruktoreita ym. syntaksisokeria. Projekti käyttää lobokia generoimaan gettereitä yms. boilerplaten vähentämiseksi ja koodin luettavuuden parantamiseksi. Varjopuolena on kuitenkin etteivät editorit löydä generoituja metodeja, sillä ne eivät ole olemassa ennen kuin maven kääntämisen yhteydessä *delombokoi* koodin, generoiden edellämainitut metodit. (Generoitu koodi löytyy kääntämisen jälkeen polusta `target/generated-sources/delombok`).

Hätä ei suinkaan ole tämän näköinen sillä useimpiin kehitysympäristöihin löytyy lombok-liitännäinen, joka auttaa editoria havaitsemaan lombokilla generoidut metodit jo ennen kääntämistä.

*TL;DR: Puttuvat metodit generoidaan kääntämisen yhteydessä, jos haluat editorin sisällä virheistä eroon lataa lombok-liitännäinen.*

## Dokumentaatio
- [käyttöohje](documentation/kayttoohje.md)
- [vaatimusmäärittely](documentation/vaatimusmaarittely.md)
- [arkkitehtuuri](documentation/arkkitehtuuri.md)
- [tuntikirjanpito](documentation/tuntikirjanpito.md)

## Releaset
[viikko 5 (_RELEASE 1_)](https://github.com/Kailari/ot-harjoitustyo/releases/tag/viikko5)
[viikko 6 (_RELEASE 2_)](https://github.com/Kailari/ot-harjoitustyo/releases/tag/viikko6)

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
