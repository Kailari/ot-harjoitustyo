package toilari.otlite.game.event;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Viestinvälitysjärjestelmä jolla käyttöliittymä ja logiikka voivat vuorovaikuttaa keskenään.
 */
@Slf4j
public class EventSystem {
    private final Map<Class<? extends IEvent>, List<IEventListener>> listeners = new HashMap<>();

    /**
     * Asettaa annetun viestikuuntelijan tilaamaan annetuntyyppisiä viestejä.
     *
     * @param eventClass kuunnellun viestin tyyppi
     * @param listener   kuuntelija joka reagoi viestiin
     * @param <T>        kuunneltavan viestin tyyppi
     * @throws NullPointerException jos viestin tyyppi tai kuuntelija on <code>null</code>
     */
    public <T extends IEvent> void subscribeTo(@NonNull Class<T> eventClass, @NonNull IEventListener<T> listener) {
        if (!this.listeners.containsKey(eventClass)) {
            this.listeners.put(eventClass, new ArrayList<>());
        }

        this.listeners.get(eventClass).add(listener);
    }

    /**
     * Lähettää viestin kuuntelijoille jotka ovat tilanneet sen.
     *
     * @param event lähetettävä viesti
     */
    @SuppressWarnings("unchecked") // subscribeTo ensures correct type
    public void fire(@NonNull IEvent event) {
        if (!this.listeners.containsKey(event.getClass())) {
            return;
        }

        for (val listener : this.listeners.get(event.getClass())) {
            listener.onEvent(event);
        }
    }
}
