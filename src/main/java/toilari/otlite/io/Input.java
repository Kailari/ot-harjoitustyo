package toilari.otlite.io;

import lombok.NonNull;

/**
 * Apumetodeja käyttäjän syötteen käsittelyyn.
 */
public class Input {
    private static IInputHandler instance;

    /**
     * Alustaa syötekäsittelijän/avustajan. Voidaan kutsua vain kerran ohjelman suorituksen aikana.
     *
     * @param instance syötekäsittelijän instanssi jonka toiminnallisuutta apumetodit toimivat
     * @throws NullPointerException jos instanssi on <code>null</code>
     */
    public static void init(@NonNull IInputHandler instance) {
        if (Input.instance != null) {
            throw new IllegalStateException("InputHandler instance initialized multiple times!");
        }
        Input.instance = instance;
    }

    /**
     * Hakee syötekäsittelijän instanssin.
     *
     * @return syötekäsittelijän instanssi
     */
    public static IInputHandler getHandler() {
        return Input.instance;
    }
}
