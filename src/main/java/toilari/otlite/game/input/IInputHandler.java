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
     * Onko näppäin vapautettuna.
     *
     * @param key näppäinkoodi jonka tila tarkistetaan
     * @return <code>true</code> jos näppäin on vapautettuna, muutoin <code>false</code>
     */
    boolean isKeyUp(Key key);
}
