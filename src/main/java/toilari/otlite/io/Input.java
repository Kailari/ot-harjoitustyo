package toilari.otlite.io;

/**
 * Apumetodeja käyttäjän syötteen käsittelyyn.
 */
public class Input {
    private static IInputHandler instance;

    public static void init(IInputHandler instance) {
        if (Input.instance != null) {
            throw new IllegalStateException("InputHandler instance initialized multiple times!");
        }
        Input.instance = instance;
    }

    public static IInputHandler getHandler() {
        return Input.instance;
    }
}
