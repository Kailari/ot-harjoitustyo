package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

import java.util.Random;

/**
 * Liikkumiskyvyn ohjainkomponentti.
 */
public abstract class MoveControllerComponent extends AbstractControllerComponent<MoveAbility> {
    @Setter(AccessLevel.PROTECTED) private int inputX, inputY;

    @Override
    public boolean wants(MoveAbility ability) {
        return getInputDirection() != Direction.NONE;
    }

    /**
     * Hakee suunnan johon hahmo haluaa liikkua.
     *
     * @return suunta johon hahmo haluaa liikkua
     */
    public Direction getInputDirection() {
        if (this.inputX != 0) {
            return this.inputX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (this.inputY != 0) {
            return this.inputY > 0 ? Direction.DOWN : Direction.UP;
        } else {
            return Direction.NONE;
        }
    }

    @Override
    public void abilityPerformed(MoveAbility ability) {
        this.inputX = this.inputY = 0;
    }

    /**
     * Pelaajan ohjainkomponentti.
     */
    @NoArgsConstructor
    public static class Player extends MoveControllerComponent {
        /**
         * Kopio komponentin toisesta kopmponentista.
         *
         * @param template komponentti josta kopioidaan
         */
        public Player(MoveControllerComponent template) {
        }

        /**
         * Hakee raa'an liikesyötteen x-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa
         * onko kyseessä painallus vai jatkuva tila
         *
         * @return -1 vasemmalle, 1 oikealle, 0 paikallaan
         */
        public int getMoveInputX() {
            val right = Input.getHandler().isKeyPressed(Key.RIGHT) ? 1 : 0;
            val left = Input.getHandler().isKeyPressed(Key.LEFT) ? -1 : 0;
            return right + left;
        }

        /**
         * Hakee raa'an liikesyötteen y-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa
         * onko kyseessä painallus vai jatkuva tila
         *
         * @return -1 ylös, 1 alas, 0 paikallaan
         */
        public int getMoveInputY() {
            val down = Input.getHandler().isKeyPressed(Key.DOWN) ? 1 : 0;
            val up = Input.getHandler().isKeyPressed(Key.UP) ? -1 : 0;
            return down + up;
        }

        @Override
        public void updateInput(MoveAbility ability) {
            setInputX(getMoveInputX());
            setInputY(getMoveInputY());
        }

        @Override
        public void abilityPerformed(MoveAbility ability) {
            super.abilityPerformed(ability);

            val state = getCharacter().getWorld().getObjectManager().getGameState();
            if (state != null) {
                state.getGame().getStatistics().increment(Statistics.TILES_MOVED, state.getGame().getActiveProfile().getId());
            }
        }
    }

    /**
     * Tekoälyn ohjainkomponentti.
     */
    public static class AI extends MoveControllerComponent {
        private final Random random;
        private Direction[] availableDirections = new Direction[4];
        int nDirections;


        /**
         * Luo uuden tekoälyohjainkomponentin ja asettaa automaattisesti sen satunnaislukugeneraattorille siemenluvun.
         *
         * @throws NullPointerException jos hahmo on <code>null</code>
         */
        public AI() {
            this(System.currentTimeMillis());
        }


        /**
         * Luo uuden tekoälyohjainkomponentin.
         *
         * @param seed satunnaislukugeneraattorin siemenluku
         * @throws NullPointerException jos hahmo on <code>null</code>
         */
        public AI(long seed) {
            this.random = new Random(seed);
        }

        /**
         * Kopio komponentin toisesta komponentista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AI(MoveControllerComponent template) {
            this.random = new Random();
        }

        @Override
        public void updateInput(MoveAbility ability) {
            refreshMoveDirections(ability);
            if (this.nDirections == 0) {
                setInputX(0);
                setInputY(0);
                return;
            }

            val direction = this.availableDirections[this.random.nextInt(this.nDirections)];
            setInputX(direction.getDx());
            setInputY(direction.getDy());
        }

        private void refreshMoveDirections(@NonNull MoveAbility ability) {
            val level = getCharacter().getWorld().getCurrentLevel();
            val x = getCharacter().getTileX();
            val y = getCharacter().getTileY();

            this.nDirections = 0;
            for (val direction : Direction.asIterable()) {
                if (ability.canMoveTo(direction, 1) && !level.getTileAt(x + direction.getDx(), y + direction.getDy()).isDangerous()) {
                    this.availableDirections[this.nDirections++] = direction;
                }
            }
        }
    }
}
