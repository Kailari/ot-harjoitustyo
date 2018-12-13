package toilari.otlite.game.world.entities.characters.abilities.components;

import lombok.*;
import toilari.otlite.game.input.Input;
import toilari.otlite.game.input.Key;
import toilari.otlite.game.profile.statistics.Statistics;
import toilari.otlite.game.util.Direction;
import toilari.otlite.game.world.entities.characters.abilities.MoveAbility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Liikkumiskyvyn ohjainkomponentti.
 */
public abstract class MoveControllerComponent extends AbstractControllerComponent<MoveAbility> {
    @Setter(AccessLevel.PROTECTED) private transient int inputX, inputY;
    @Getter(AccessLevel.PROTECTED) private transient final Random random;
    @Getter private transient List<Direction> availableDirections = new ArrayList<>();

    protected MoveControllerComponent() {
        this.random = new Random();
    }

    protected MoveControllerComponent(AbstractControllerComponent<MoveAbility> template) {
        super(template);
        this.random = new Random();
    }

    @Override
    public boolean wants(MoveAbility ability) {
        return getCharacter().isPanicking() || getInputDirection() != Direction.NONE;
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

    protected abstract void doUpdateInput(@NonNull MoveAbility ability);

    @Override
    public final void updateInput(@NonNull MoveAbility ability) {
        if (getCharacter().getWorld().getCurrentLevel() != null) {
            refreshMoveDirections(ability);

            if (getAvailableDirections().isEmpty()) {
                setInputX(0);
                setInputY(0);
                return;
            }
        }

        if (!getCharacter().isPanicking()) {
            doUpdateInput(ability);
        } else {
            this.availableDirections.sort(Comparator.comparingInt(this::distanceToPanicSource));
            val direction = this.availableDirections.get(this.availableDirections.size() - 1);
            setInputX(direction.getDx());
            setInputY(direction.getDy());
        }
    }

    private int distanceToPanicSource(Direction direction) {
        val dx = (getCharacter().getTileX() + direction.getDx()) - getCharacter().getPanicSourceX();
        val dy = (getCharacter().getTileY() + direction.getDy()) - getCharacter().getPanicSourceY();
        return dx * dx + dy * dy;
    }

    private void refreshMoveDirections(@NonNull MoveAbility ability) {
        this.availableDirections.clear();

        val world = getCharacter().getWorld();
        val x = getCharacter().getTileX();
        val y = getCharacter().getTileY();

        for (val direction : Direction.asIterable()) {
            val tileAtTarget = world.getTileAt(x + direction.getDx(), y + direction.getDy());
            val panickingOrNotDangerous = !tileAtTarget.isDangerous() || getCharacter().isPanicking();

            if (ability.canMoveTo(direction, 1) && panickingOrNotDangerous) {
                this.availableDirections.add(direction);
            }
        }
    }


    @Override
    public void abilityPerformed(MoveAbility ability) {
        this.inputX = this.inputY = 0;
    }

    @Override
    public void reset() {
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
            super(template);
        }

        /**
         * Hakee raa'an liikesyötteen x-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa onko kyseessä
         * painallus vai jatkuva tila
         *
         * @return -1 vasemmalle, 1 oikealle, 0 paikallaan
         */
        public int getMoveInputX() {
            val right = Input.getHandler().isKeyPressed(Key.RIGHT) ? 1 : 0;
            val left = Input.getHandler().isKeyPressed(Key.LEFT) ? -1 : 0;
            return right + left;
        }

        /**
         * Hakee raa'an liikesyötteen y-aksellilla. Arvo on parsimaton näppäimistösyöte eikä ota kantaa onko kyseessä
         * painallus vai jatkuva tila
         *
         * @return -1 ylös, 1 alas, 0 paikallaan
         */
        public int getMoveInputY() {
            val down = Input.getHandler().isKeyPressed(Key.DOWN) ? 1 : 0;
            val up = Input.getHandler().isKeyPressed(Key.UP) ? -1 : 0;
            return down + up;
        }

        @Override
        public void doUpdateInput(MoveAbility ability) {
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
    @NoArgsConstructor
    public static class AI extends MoveControllerComponent {
        /**
         * Kopio komponentin toisesta komponentista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AI(MoveControllerComponent template) {
            super(template);
        }

        @Override
        public void doUpdateInput(MoveAbility ability) {
            val direction = getAvailableDirections().get(getRandom().nextInt(getAvailableDirections().size()));
            moveToDirection(direction);
        }

        protected void moveToDirection(Direction direction) {
            setInputX(direction.getDx());
            setInputY(direction.getDy());
        }
    }

    /**
     * Tekoälyn ohjainkomponentti, joka pyrkii liikkumaan kohti pelaajaa pelaajan ollessa tarpeeksi lähellä.
     */
    @RequiredArgsConstructor
    public static class AIMoveTowardsPlayer extends AI {
        private final int searchRange;

        /**
         * Kopio komponentin tiedot toisesta komponentista.
         *
         * @param template komponentti josta kopioidaan
         */
        public AIMoveTowardsPlayer(MoveControllerComponent template) {
            this.searchRange = ((AIMoveTowardsPlayer) template).searchRange;
        }

        @Override
        public void doUpdateInput(MoveAbility ability) {
            if (targetIsNear()) {
                moveTowardsTarget();
            } else {
                super.doUpdateInput(ability);
            }
        }

        private void moveTowardsTarget() {
            getAvailableDirections().stream()
                .min(Comparator.comparingInt((d) -> {
                    val x = getCharacter().getTileX();
                    val y = getCharacter().getTileY();
                    return squaredDistanceToPlayerFrom(x + d.getDx(), y + d.getDy());
                })).ifPresent(this::moveToDirection);
        }

        private int squaredDistanceToPlayerFrom(int x, int y) {
            val player = getCharacter().getWorld().getObjectManager().getPlayer();
            int dx = x - player.getTileX();
            int dy = y - player.getTileY();
            return dx * dx + dy * dy;
        }

        private boolean targetIsNear() {
            val player = getCharacter().getWorld().getObjectManager().getPlayer();
            val dx = Math.abs(player.getTileX() - getCharacter().getTileX());
            val dy = Math.abs(player.getTileY() - getCharacter().getTileY());
            return dx <= this.searchRange && dy <= this.searchRange;
        }
    }
}
