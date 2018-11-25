package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

import java.util.Random;

public abstract class MoveControllerComponent extends AbstractControllerComponent<MoveAbility> {
    @Setter(AccessLevel.PROTECTED) private int inputX, inputY;

    public MoveControllerComponent(@NonNull AbstractCharacter character) {
        super(character);
    }

    @Override
    public boolean wants() {
        return getInputDirection() != Direction.NONE;
    }

    /**
     * Tarkistaa voiko hahmo liikkua annettuun suuntaan.
     *
     * @param direction suunta johon liikutaan
     * @param tiles     montako ruutua siirrytään
     * @return <code>true</code> jos liikkuminen on mahdollista
     */
    public boolean canMoveTo(Direction direction, int tiles) {
        if (tiles == 0) {
            return false;
        }

        int newX = getCharacter().getTileX() + direction.getDx();
        int newY = getCharacter().getTileY() + direction.getDy();

        val world = getCharacter().getWorld();
        if (!world.getCurrentLevel().isWithinBounds(newX, newY)) {
            return false;
        }

        val tileAtTarget = world.getCurrentLevel().getTileAt(newX, newY);
        val objectAtTarget = world.getObjectAt(newX, newY);

        val tileIsWalkable = !tileAtTarget.isWall();

        if (tileIsWalkable) {
            return objectAtTarget == null || objectAtTarget.isRemoved();
        }

        return false;
    }

    public Direction getInputDirection() {
        if (this.inputX != 0) {
            return this.inputX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (this.inputY != 0) {
            return this.inputY > 0 ? Direction.DOWN : Direction.UP;
        } else {
            return Direction.NONE;
        }
    }

    /**
     * Pelaajan ohjainkomponentti.
     */
    public static class Player extends MoveControllerComponent {
        private boolean isHolding;

        /**
         * Hakee raa'an liikesyötteen x-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa
         * onko kyseessä painallus vai jatkuva tila
         *
         * @return -1 vasemmalle, 1 oikealle, 0 paikallaan
         */
        private int getMoveInputX() {
            val right = Input.getHandler().isKeyDown(Key.RIGHT) ? 1 : 0;
            val left = Input.getHandler().isKeyDown(Key.LEFT) ? -1 : 0;
            return right + left;
        }

        /**
         * Hakee raa'an liikesyötteen y-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa
         * onko kyseessä painallus vai jatkuva tila
         *
         * @return -1 ylös, 1 alas, 0 paikallaan
         */
        private int getMoveInputY() {
            val down = Input.getHandler().isKeyDown(Key.DOWN) ? 1 : 0;
            val up = Input.getHandler().isKeyDown(Key.UP) ? -1 : 0;
            return down + up;
        }

        /**
         * Luo uuden pelaajan ohjainkomponentin. Komponentti käyttää syötteenä näppäimistöltä saatua pelaajan syötettä.
         *
         * @param character pelihahmo jonka toimintoja halutaan ohjata
         * @throws NullPointerException jos hahmo on <code>null</code>
         */
        public Player(@NonNull AbstractCharacter character) {
            super(character);
        }

        @Override
        public void updateInput() {
            int rawInputX = getMoveInputX();
            int rawInputY = getMoveInputY();

            if (this.isHolding) {
                setInputX(0);
                setInputY(0);
            } else {
                setInputX(rawInputX);
                setInputY(rawInputY);
            }

            this.isHolding = rawInputX != 0 || rawInputY != 0;
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
         * @param character pelihahmo jonka toimintoja halutaan ohjata
         * @throws NullPointerException jos hahmo on <code>null</code>
         */
        public AI(@NonNull AbstractCharacter character) {
            this(character, System.currentTimeMillis());
        }


        /**
         * Luo uuden tekoälyohjainkomponentin.
         *
         * @param character pelihahmo jonka toimintoja halutaan ohjata
         * @param seed      satunnaislukugeneraattorin siemenluku
         * @throws NullPointerException jos hahmo on <code>null</code>
         */
        public AI(@NonNull AbstractCharacter character, long seed) {
            super(character);
            this.random = new Random(seed);
        }

        @Override
        public void updateInput() {
            refreshMoveDirections();
            if (this.nDirections == 0) {
                setInputX(0);
                setInputY(0);
                return;
            }

            val direction = this.availableDirections[this.random.nextInt(this.nDirections)];
            setInputX(direction.getDx());
            setInputY(direction.getDy());
        }

        private void refreshMoveDirections() {
            val level = getCharacter().getWorld().getCurrentLevel();
            val x = getCharacter().getTileX();
            val y = getCharacter().getTileY();

            this.nDirections = 0;
            for (val direction : Direction.asIterable()) {
                if (canMoveTo(direction, 1) && !level.getTileAt(x + direction.getDx(), y + direction.getDy()).isDangerous()) {
                    this.availableDirections[this.nDirections++] = direction;
                }
            }
        }
    }
}
