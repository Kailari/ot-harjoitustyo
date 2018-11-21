package toilari.otlite.menu;

import lombok.NonNull;

/**
 * Kuuntelija käyttöliittymäviesteille.
 *
 * @param <T> Kuunnellun viestin tyyppi
 */
public interface IEventListener<T extends IEvent> {
    /**
     * Ajetaan kun kuuntelija kuulee vastaanotetusta viestistä.
     *
     * @param event viesti joka vastaanotettiin
     * @throws NullPointerException jos viesti on <code>null</code>
     */
    void onEvent(@NonNull T event);
}
