package toilari.otlite;

/**
 * Sovelluksen pääluokka. Vastaa sovelluksen suorittamisesta.
 */
public class OTLite {
    /**
     * Aloittaa sovelluksen suorituksen. Aloittaa {@link #init() alustamalla}
     * sovelluksen tarvitsemat resurssit ja siirtyy {@link #loop() päälooppiin} sen
     * jälkeen. Kun päälooppi viimein valmistuu, viimeistellään suoritus metodissa
     * {@link #destroy()}
     */
    public void run() {
        init();
        loop();
        destroy();
    }

    /**
     * Kutsutaan kerran ennen päälooppiin siirtymistä, kun sovelluksen suoritus
     * alkaa.
     */
    private void init() {
    }

    /**
     * Kutsutaan kerran kun sovellus siirtyy päälooppiin. Vastaa pääloopin
     * suorittamisesta. Metodi palaa vasta kun pääloopin suoritus on valmis.
     */
    private void loop() {
    }

    /**
     * Kutsutaan kerran pääloopin jälkeen, kun ohjelman suoritus on loppumassa.
     */
    private void destroy() {
    }
}
