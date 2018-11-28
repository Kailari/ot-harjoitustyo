package toilari.otlite.game.input;

/**
 * Prosessoi käyttöjärjestelmän/alustan tarjoamia syötteen tilapäivityksiä helpommin käytettäväksi.
 */
public interface IInputHandler {
    /**
     * Onko näppäin painettuna.
     *
     * @param key näppäinkoodi jonka tila tarkistetaan
     * @return <code>true</code> jos näppäin on painettuna, muutoin <code>false</code>
     */
    boolean isKeyDown(Key key);

    /**
     * Painettiinko näppäin juuri pohjaan.
     *
     * @param key näppäinkoodi jonka tila tarkistetaan
     * @return <code>true</code> jos näppäin on painettuna eikä ollut painettuna viime päivityksen yhteydessä, muutoin <code>false</code>
     */
    boolean isKeyPressed(Key key);


    /**
     * Hakee hiiren x-koordinaatin suhteessa ikkunan vasempaan yläkulmaan.
     *
     * @return hiiren x-koordinaatti
     */
    int mouseX();

    /**
     * Hakee hiiren y-koordinaatin suhteessa ikkunan vasempaan yläkulmaan.
     *
     * @return hiiren y-koordinaatti
     */
    int mouseY();

    /**
     * Kertoo onko hiiren nappi pohjassa.
     *
     * @param button minkä napin tila tarkistetaan
     * @return <code>true</code> jos nappi on pohjassa
     */
    boolean isMouseDown(int button);

    /**
     * Päivittää syötekäsittelijän tilan.
     */
    void update();
}
