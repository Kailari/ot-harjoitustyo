# Vaatimusmäärittely

## Sovelluksen tarkoitus
Sovellus on yksinkertainen hieman roguelike-henkinen luolaseikkailu, jossa pelaaja siivoaa luolaa möröistä kerros kerrallaan kohdaten jatkuvasti vahvempia mörköjä. Useat käyttäjät voivat pelata peliä samalla laitteella erillisiä pelaajaprofiileja käyttäen ilman huolta että he vahingossa sotkevat toistensa tallennuksia tai asetuksia.

## Käyttäjät
Sovelluksen luonteen vuoksi sovellukseen kuuluu vain yhden tyyppisiä käyttäjiä, joita kutsutaan tässä yhteydessä *"pelaajaprofiileiksi"*. Mikäli pelin statistiikkakanta siirettäisiin keskitetyksi verkkopalvelimelle, tarvittaisiin myös kannan ylläpidolle erillisiä pääkäyttäjiä, mutta paikallisen kannan tapauksessa on asioiden yksinkertaistamiseksi annettu kaikille sovelluksen käyttäjille pelin toimintojen puitteissa "vapaa pääsy" kantaan.

### Pelaajaprofiilit
Pelillä voi olla samalla laitteella useille pelaajille profiileja, joihin tallennetaan yksilöidyt tallennus- ja asetustiedostot. Lisäksi sovellus ylläpitää tietokantaa johon tallennetaan eri pelaajaprofiilien piste-ennätykset ja lyötyjen mörköjen määrä ym.

Pelin käynnistyttyä, joko valitaan profiili tai luodaan uusi. Profiileja ei ole suojattu sen ihmeemmin, joten niiden tarkoituksena on vain sallia samalla laitteella pelaavien käyttäjien käyttää sujuvasti erilaisia asetuksia ja välttää sekaannuksia tallennustiedostojen kanssa.

## Perusominaisuudet
### Profiilit
- Peliin voi luoda useita profiileja, jotka tallennetaan tietokantaan.
- Jokaisella profiililla on omat asetuksensa, jotka ladataan profiilin valinnan jälkeen.
- Profiilikohtainen "Bestiary" jossa pidetään kirjaa lyötyjen mörköjen määrästä.

### Määritystiedostot
- Pelin sisältöä (möröt, esineet) voi muokata/lisätä määritystiedostoja käyttäen.
- Esim. mörköjen tiedot tallennetaan yksinkertaisiin .json-tiedostoihin, jotka ladataan automaagisesti määrätystä polusta pelin käynnistyessä.

### Valikot
- Profiilin valinnan jälkeen pelaajalla on saatavilla päävalikko, josta pääsee aloittamaan uuden pelin, lataamaan tallennustiedoston ja säätämään asetuksia.
- Lisäksi päävalikosta pääsee katsomaan tilastoja.

### Pelimekaniikat
#### Yleisesti
- Peli etenee vuoropohjaisesti, siten että jokainen pelimaailman hahmo saa vuorollaan attribuuttiensa mukaisen määrän toimintopisteitä joita se voi käyttää erilaisiin toimintoihin.
- Eri toiminnot maksavat eri määrän toimintopisteitä.
- Pelimaailma rakentuu kerroksista, jotka pelaajan tulee siivota möröistä ennen kuin seuraavaan kerrokseen pääsee.
- Mikäli pelaajan terveyspisteet loppuvat joko mörköjen iskuista tai valitettavan onnettomuuden vuoksi (rotko), peli päättyy ja tallennustiedosto poistetaan automaattisesti.

#### Pelimaailma
- Pelimaailma on yksinkertainen ruudukko, jossa on seiniä, lattiaruutuja ja rotkoja.
- Rotkoon putoaminen tappaa sinne pudonneen hahmon välittömästi.

#### Taistelu
- Kahden hahmon ollessa vierekkäin, ne saavat vuorollaan käytettäväksi taistelutoimintoja, joilla ne voivat tuottaa vahinkopisteitä toisilleen.
- Hahmoilla on myös attribuutteja, jotka vaikuttavat niihin kohdistuviin vahinkopisteisiin.

#### Möröt
- Mörköjen vahvuus kasvaa joka kerroksessa.
- Mörköjä on erilaisia ja uusia mörkötyyppejä voi luoda määritystiedostoja käyttäen.
- Möröt lähtevät jahtaamaan pelaajaa tämän saapuessa liian lähelle.

## Lisäominaisuuksia / Jatkokehitysideoita

### Pelimekaniikat
#### Taistelu
- Laajemmat vaihtoehdot taisteluun. Korkeammat attribuutit --> enemmän erilaisia iskuvaihtoehtoja
- Rotkon reunalla olevan mörön voi yrittää potkaista alas

#### Esineet
- Pelissä on esineitä jotka vaikuttavat hahmojen attribuutteihin.
- Esineitä voi luoda määritystiedostoja käyttäen.
- Hahmot jättävät kuollessaan jälkeensä kantamansa esineet.

### Mukauttaminen
- Pelin sisältö kulkee tällä hetkellä erillisessä _content/_ kansiopolussa. Paketoinnin helpottamiseksi, pelin oletussisältö tulisi pakata mukaan ajettavaan _.jar_-tiedostoon itseensä.
    - ja I/O-luokkia tulisi muuttaa siten että ne hakevat oletuksena paketista, mutta jos kansiosta löytyy samanniminen tiedosto, käytetään paketoidun sijaan sitä.
    - Tämä pitää muokattavuuden edelleen samalla tasolla (oletussisältöä voi muokata koskematta paketin sisältöön), mutta helpottaa pelin siirtämistä/asentamista (_.jar kulkee sellaisenaan_)

### Suorituskyky
#### Grafiikka
- Peli käyttää tällä hetkellä erittäin naiivia lähestymistapaa piirtämiseen, jossa lähes jokainen piirrettävä objekti piirretään omana piirtokäskynään. Tämä on luonnollisesti kohtuuttoman hidasta.
    - Piirtämisen voisi toteuttaa tehokkaammin prioriteettijonolla optimoidulla sarjapiirtäjällä ("Batched renderer")
    - Tekstuurit tulee pakata suurempiin "atlaksiin" joissa on usean eri objektin tekstuurit yhdessä kuvassa.
    - Spritejä ei piirretä suoraan, vaan kukin sprite asetetaan vuorollaan "jonoon" piirtämistä varten.
    - Jonona toimii prioriteettijono, joka järjestää ensisijaisesti z-akselin mukaan, ja toissijaisesti käytetyn tekstuurin mukaan.
    - Varsinainen piirtäminen tapahtuu käymällä prioriteettijonoa läpi alusta loppuun ja lisäämällä uusien spritejen geometria lennossa luotavaan VAO/VBO:iin. Tätä jatketaan kunnes tekstuuri vaihtuu, jolloin nykyinen piirtäjä "flushataan" eli tähänasti luotu VBO piirretään ja aletaan luoda uutta uudella tekstuurilla.
    - Optimitilanteessa tämä tarkoittaa että piirtokomentojen määrä on objektien määrän sijaan käytettyjen tekstuurien määrä

#### Statistiikka
- Statistiikkaa kerätään tällä hetkellä kirjoittaen jokainen päivitys suoraan kantaan. Tämä asettaa pelin suorituskyvylle ilmeisen I/O pullonkaulan mikäli statistiikkapäivityksiä tulee useita nopeaan tahtiin.
    - ongelmien minimoimiseksi päivitykset tulisi asettaa jonoon ja varsinainen kirjoittaminen suorittaa omassa säikeessään.
    - muutoksien nykytila voidaan päivittää jo muistiin, mutta levylle kirjoittamista ei tarvitse jäädä odottamaan, vaan se tapahtuu taustalla omia aikojaan.
