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
- Profiilikohtainen "Bestiary" jossa pidetään  mm. lyötyjen mörköjen määrästä.

### Määritystiedostot
- Pelin sisältöä (möröt, kartat) voi muokata/lisätä määritystiedostoja käyttäen.
- Esim. mörköjen tiedot tallennetaan yksinkertaisiin .json-tiedostoihin, jotka ladataan automaagisesti määrätystä polusta pelin käynnistyessä.

### Valikot
- Profiilin valinnan jälkeen pelaajalla on saatavilla päävalikko, josta pääsee aloittamaan uuden pelin, lataamaan tallennustiedoston ja säätämään asetuksia.
- Lisäksi päävalikosta pääsee katsomaan tilastoja (bestiaari).

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
- Hahmojen attribuutit vaikuttavat käytössä oleviin toimintoihin ja tuotettuihin/vastaanotettuihin vahinkopisteisiin

#### Möröt
- Mörköjen vahvuus kasvaa joka kerroksessa.
- Mörköjä on erilaisia ja uusia mörkötyyppejä voi luoda määritystiedostoja käyttäen.
- Möröt lähtevät jahtaamaan pelaajaa tämän saapuessa liian lähelle.

## Lisäominaisuuksia / Jatkokehitysideoita

### Pelimekaniikat

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
- Peli käyttää melko naivia sarjapiirtäjän toteutusta, joka ei automaattisesti osaa optimoida piirtojärjestystä piirtokutsujen minimoimiseksi. Parempi järjestelmä olisi mahdollista toteuttaa melko pienellä vaivalla
    - Sen sijaan että piirettävät asiat viedään välittömästi VBO:hon ja VBO flushataan tekstuurin vaihtuessa, voitaisiin oliot asettaa jonoon ja järjestää "kerroksittain" jonkin prioriteetin mukaan.
    - Tällöin samaa tekstuuria käyttävät oliot joiden keskinäisellä järjestyksellä ei ole merkitystä voitaisiin yhdistää yhteen piirtokutsuun, vaikka tekstuuri vaihtuisikin älissä useita kertoja.

#### Statistiikka
- Statistiikkaa kerätään tällä hetkellä kirjoittaen jokainen päivitys suoraan kantaan. Tämä asettaa pelin suorituskyvylle ilmeisen I/O pullonkaulan mikäli statistiikkapäivityksiä tulee useita nopeaan tahtiin.
    - ongelmien minimoimiseksi päivitykset tulisi asettaa jonoon ja varsinainen kirjoittaminen suorittaa omassa säikeessään.
    - muutoksien nykytila voidaan päivittää jo muistiin, mutta levylle kirjoittamista ei tarvitse jäädä odottamaan, vaan se tapahtuu taustalla omia aikojaan.
