# Arkkitehtuurikuvaus

## Rakenne
Koodi on jaoteltu pakkausrakenteessa kokonaisuuksiin `game`, `dao` ja `view`, joista kukin vastaa selkeästi erillisestä osa-alueestaan.

- `view` sisältää käyttöliittymään ja pelin visuaaliseen osa-alueeseen liittyvät luokat. 
- `dao` sisältää tiedon tallentamiseen/lukemiseen tarvittavat luokat.
- `game` sisältää sovelluslogiikan

Pelin luokkarakenne yksinkertaisella tasolla (*esim. peliobjektin aliluokat ei listattuna*) kuvattuna luokkakaaviona:
![luokkakaavio](images/classes.png)

## Sovelluslogiikka
Sovelluslogiikka ajaa pelin simulaatiota [`GameRunner`](../src/main/java/toilari/otlite/game/AbstractGameRunner.java)-luokan avulla. `GameRunner` vastaa sovelluslogiikan ja käyttöliittymän ajamisesta. Tämä saavutetaan kutsumalla luokan [`Game`](../src/main/java/toilari/otlite/game/Game.java) (ja käyttöliittymälle spesifien luokkien) metodeja. Nykyisessä käyttöliittymätoteutuksessa `GameRunner` kutsuu pääloopissa päivitysrutiinien jälkeen [`IRenderer`](../src/main/java/toilari/otlite/view/renderer/IRenderer.java)-rajapinnan toteutuksien tarjoamia metodeja käyttöliittymän näyttämiseksi.

Päälaelleen käännettynä siis pelimoottorin korkean tason toiminta on seuraava:
- `Game` sisältää sovelluslogiikan
- `GameRunner` käärii luokan `Game` ja ajaa sen metodeilla sovelluslogiikkaa tarjoamassaan pääloopissa, kutsuen käyttöliittymälle tarvittavia metodeja.
- Ainoa yhteinen osa koodissa joka viittaa sekä sovelluslogiikkaan että käyttöliittymään on luokan `GameRunner` toteutuksessa.

### Game
Tarkemmin tarkasteltuna `Game` ei sisällä juuri lainkaan pelilogiikkaa. Pääloopin pitämiseksi siistinä, `Game` toimii vain eräänlaisena *State-ratkaisumalliin* perustuvana tilakoneena, jonka tarjoama varsinainen toiminnallisuus tulee abstraktin luokan [`GameState`](../src/main/java/toilari/otlite/game/GameState.java) konkreettisilta toteutuksilta.

Itsessään `Game` tarjoaa vain metodit tilakoneen tilan vaihtamiseen ja hakemiseen, sekä nykyisen tilan päivittämiseen.

Sekvenssikaavio nykyisen pelitilan vaihdosta uuteen:

![Pelitilan vaihto](images/Pelitilan%20vaihto.png)

### GameState
Varsinainen sovelluslogiikka on luokasta `GameState` perivissä konkreettisissa toteutuksissa. Esim. [`PlayGameState`](../src/main/java/toilari/otlite/game/PlayGameState.java) tarjoaa itse pelin toiminnallisuuden.

Pelissä on tällä hetkellä kolme eri tilaa:
- `SelectProfileGameState` eli profiilinvalinta
- `MenuGameState` eli päävalikko
- `PlayGameState` eli itse peli

## Käyttöliittymä
Korkeimmalla tasolla käyttöliittymästä vastaa `GameRunner`, joka suorittaa nykyistä pelitilaa vastaavan `IGameStateRenderer`-luokan ilmentymien tarjoamia metodeja. Käytettävät piirtäjien toteutukset injektoidaan konstruktorikutsun yhteydessä, jolloin käyttöliittymä on käytännössä täysin eriytetty sovelluslogiikasta.

## Tiedon tallennus
Sovellus käyttää tietoa levyltä nykyisestä suorituspolusta hakemistoista `content/` ja `data/`. Hakemiston `content/` tulee olla olemassa entuudestaan, sillä se sisältää pelin sisällön luomisen/visualisoinnin kannalta elintärkeitä tiedostoja. Hakemisto `data/` sen sijaan luodaan ensimmäisellä käynnistyskerralla ja pelin asetustiedostot sekä profiilitietokanta tallennetaan sinne.

Tiedon hakeminen ja tallentaminen tapahtuu *Data Access Object*-suunnittelumallia mukailevien DAO-luokkien avulla, jotka abstraktoivat varsinaisen I/O:n helppokäyttöisempien metodien taakse.
