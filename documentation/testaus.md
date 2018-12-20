# Testaus

Pelin testaaminen on suoritettu JUnitilla toteutetuilla yksikkö- ja integraatiotesteillä ja manuaalisesti pelin testikarttoja pelaamalla. Johtuen esimerkiksi pelimaailman hallinnointiin liittyvistä eri abstraktiotasoista, eri tasojen testit ovat samalla yksikkötestejä testattavalle tasolle, mutta osin myös integraatiotestejä käytettäville alemmille tasoille.

Testikoodin osalta koodin toisteisuuden vähentäminen on jätetty toissijaiseksi yksittäisten testien koodin yksiselitteisyyden takaamiseksi. Kukin testi on mahdollisimman pitkälti pyritty pitämään "omana pakettinaan", jolloin tilanteissa joissa testin nimi ei ole kyllin kuvaava kertomaan mikä on testin epäonnistumisen syynä, on syy löydettävissä testimetodista itsestään, eikä yksityiskohtia tarvitse lähteä metsästämään setup/beforeAll/yms metodeista tai apuluokista.

DAO- ja tiedosto-I/O -luokkia testataan väliaikaisesti luotavilla tiedostoilla ja niiden rajapintojen keskusmuistitoteutuksilla.

Käyttöliittymää ei testata. 

## Testikattavuus

Sovelluslogiikan testien rivikattavuus on 80% ja haaraumakattavuus 70%. Testaamatta on jätetty tilanteita joissa luokan/metodin testaaminen ei ole relevanttia koska toiminnallisuus on delegoitu toiselle luokalle, jonka toimintaa testataan (esim. `CharacterDAO` sisältää hyvin vähän omaa logiikkaa, sillä `CharacterAdapter` tekee raskaan työn. `CharacterAdapter` testataan, mutta `CharacterDAO`:lle ei ole omia testejä). Lisäksi joitain tiedosto-oikeuksiin ym. tiedostojen avaamiseen liittyviä tapauksia on jätetty testaamatta.

## Manuaalinen testaus

Sovellus on asennettu käyttöohjeen mukaisesti ja testaus on suoritetu sekä Windows- että Linux-järjestelmissä.


## Toiminnallisuudet

Vaatimusmäärittelyn mukaiset toiminnallisuudet on testattu ja todettu toimiviksi.  Peliä on suoritettu myös virheellisesti laadittujen hahmojen ja ruutujen määritystieostojen kanssa. 


## Koodin rakenteeseen liittyvät ongelmat

### `init`-metodit ovat asia

Niiden ei pitäisi olla. Projektin alkuvaiheessa oliojen luonti eriytettiin alustuksesta johtuen siitä että useiden oliojen käyttöliittymäosien luominen vaatii että ikkunakonteksti on luotu (esim. OpenGL-kutsut ennen sitä kaatavat sovelluksen). Tästä syystä kaikki alustus siirrettiin `.init()`-metodeihin ongelman kiertämiseksi. Mitä _olisi pitänyt_ tehdä, olisi ollut muuttaa ohjelman käynnistyskoodia siten että käyttöliittymällä on mahdollisuus alustaa itsensä ennen kuin muuta sovellusta aletaan rakentaa.


### Hahmojen kyky-komponenttijärjestelmä on tarpeettoman monimutkainen

Hahmoille toteutettu kokeellinen kyky-komponenttijärjestelmä olisi ollut parempi korvata jo aikaisessa vaiheessa ylesiluontoisilla peliobjektien komponenttijärjestelmällä. Nykyisellään kyky-komponenttijärjestelmä on tarpeettoman monimutkainen siihen nähden mitä sillä saadaan aikaiseksi.


### Sovelluksesta yli 60% on testikoodia

Testikoodin määrä ei itsessään ole ongelma, mutta testikoodin osalta toisteisuutta olisi voinut vähentää enemmän ilman että luettavuus kärsii. Erilaisiin tilanteisiin luodut `Fake`-luokat (tällä hetkellä esim. `FakeCharacterObject` on hyvä esimerkki) vähentävät toisteista koodia, mutta kuvaavilla tehdasmetodinimillä varmistetaan ettei epäselvyyksiä jää. Toisaalta myös `@BeforeAll`, `@BeforeEach` laajamittaisempi käyttö joissain sopivissa tilanteissa auttaisi jonkin verran.


## Sovellukseen liittyvät ongelmat

### Profiilinvalinnassa ei voi syöttää profiilin nimeä

Toiminnallisuus profiilin nimen valintaan profiilia luodessa jäi toteuttamatta.


### Moottori toimii, mutta peli se ei ole

Kehityksen fokus oli rankasti toimivan ja helposti laajennettavan moottorin rakentamisessa ja esimerkkisisältöä on siksi melko vähän. Tämän vuoksi osa moottorin ominaisuuksista ei joko näy pelissä mitenkään tai niiden potentiaali jää muutoin hyödyntämättä.


### Virheenraportointi käyttäjälle on toteutettu heikosti

Pelin sisältö rakennetaan hyvin pitkälle _.json_-muotoisten määrittelyjen varaan, mutta virheenraportointi mahdollisesta virheellisestä muotoilusta/puuttuvista tageista käyttäjälle on melko vajavaista. Lisäksi olen melko varma että ohjelman saa joillain virheellisillä määrittelytiedostoilla kaatumaan, vaikkakaan en vielä ole löytänyt sellaista syötettä jolla se onnistuu.
