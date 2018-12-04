package toilari.otlite.game.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import toilari.otlite.game.profile.Profile;

/**
 * Profiilivalikon viestit.
 */
public abstract class ProfileMenuEvent implements IEvent {
    /**
     * Viesti joka lähetetään kun uusi profiili halutaan lisätä.
     */
    @RequiredArgsConstructor
    public static class Add extends ProfileMenuEvent {
        @NonNull @Getter private final String name;
    }

    /**
     * Viesti joka lähetetään kun profiilin luonti epäonnistuu epäkelvollisen nimen vuoksi.
     */
    public static class InvalidName extends ProfileMenuEvent {
    }

    /**
     * Viesti joka lähetetään kun profiili halutaan poistaa.
     */
    @RequiredArgsConstructor
    public static class Remove extends ProfileMenuEvent {
        @Getter @NonNull private final Profile profile;
    }

    /**
     * Viesti joka lähetetään kun profiili halutaan valita.
     */
    @RequiredArgsConstructor
    public static class Select extends ProfileMenuEvent {
        @Getter @NonNull private final Profile profile;
    }

    /**
     * Viesti joka lähetetään kun uusi profiili on lisätty.
     */
    @RequiredArgsConstructor
    public static class Added extends ProfileMenuEvent {
        @NonNull @Getter private final Profile profile;
    }

    /**
     * Viesti joka lähetetään kun jokin profiili on poistettu.
     */
    @RequiredArgsConstructor
    public static class Removed extends ProfileMenuEvent {
        @Getter private final Profile profile;
    }
}
