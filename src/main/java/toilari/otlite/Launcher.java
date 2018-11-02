package toilari.otlite;

/**
 * Vastaa sovelluksen käynnistämisestä ja komentoriviparametrien parsimisesta.
 */
public class Launcher {
    public static void main(String[] args) {
        OTLite app = new OTLite();
        app.run();
    }
}
