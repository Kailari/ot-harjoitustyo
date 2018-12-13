package toilari.otlite.game.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import toilari.otlite.game.world.entities.GameObject;
import toilari.otlite.game.world.entities.characters.CharacterObject;

/**
 * Hahmojen toimintoihin liittyvät viestit.
 */
@RequiredArgsConstructor
public class CharacterEvent implements IEvent {
    @Getter @NonNull private final CharacterObject character;

    /**
     * Viesti joka lähetetään kun hahmo vahingoittaa jotakin peliobjektia.
     */
    public static class Damage extends CharacterEvent {
        @Getter @NonNull private final GameObject target;
        @Getter private final float amount;
        @Getter private final boolean critical;

        /**
         * Luo uuden viestin.
         *
         * @param character hyökkäävä hahmo
         * @param target    kohde jonka kimppuun hyökätään
         * @param amount    vahinkopisteiden määrä
         * @param critical  oliko hyökkäys kriittinen osuma
         */
        public Damage(@NonNull CharacterObject character, GameObject target, float amount, boolean critical) {
            super(character);
            this.target = target;
            this.amount = amount;
            this.critical = critical;
        }
    }

    /**
     * Viesti joka lähetetään kun hahmo kuolee.
     */
    public static class Death extends CharacterEvent {
        @Getter public final Cause cause;

        /**
         * Luo uuden viestin.
         *
         * @param character kuoleva peliobjekti
         * @param cause     kuolinsyy
         */
        public Death(@NonNull CharacterObject character, @NonNull Cause cause) {
            super(character);
            this.cause = cause;
        }

        /**
         * Kuolinsyy.
         */
        public enum Cause {
            /** Yleinen kuolinsyy, käytetään kun muut syyt eivät sovi. */
            GENERIC,

            /** Toisen hahmon tuottamat vahinkopisteet. */
            CHARACTER,

            /** Valitettava onnettomuus. */
            FALL,
        }
    }

    /**
     * Viesti joka lähetetään kun hahmon kokemustaso nousee.
     */
    public static class LevelUp extends CharacterEvent {
        @Getter private final int level;

        /**
         * Luo uuden viestin.
         *
         * @param character hahmo jonka kokemustaso nousee
         * @param level     hahmon uusi kokemustaso
         */
        public LevelUp(@NonNull CharacterObject character, int level) {
            super(character);
            this.level = level;
        }
    }

    /**
     * Viesti joka lähetetään kun hahmo saa lisää terveyspisteitä.
     */
    public static class Heal extends CharacterEvent {
        @Getter private final float amount;

        /**
         * Luo uuden viestin.
         *
         * @param character hahmo joka saa tervyspisteitä
         * @param amount    annettavien terveyspisteiden määrä
         */
        public Heal(@NonNull CharacterObject character, float amount) {
            super(character);
            this.amount = amount;
        }
    }

    /**
     * Viesti joka lähetetään kun hahmon hyökkäys osuu huti.
     */
    public static class MissedAttack extends CharacterEvent {
        @Getter @NonNull private final CharacterObject target;

        /**
         * Luo uuden viestin.
         *
         * @param character hyökkäävä hahmo
         * @param target    kohde
         */
        public MissedAttack(CharacterObject character, CharacterObject target) {
            super(character);
            this.target = target;
        }
    }
}
