package toilari.otlite.game.world.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.characters.AbstractCharacter;

import java.util.ArrayList;
import java.util.List;

/**
 * Objektimanageri, joka lisää vuoropohjaista toiminnallisuutta. Hallinnoi {@link AbstractCharacter pelihahmojen}
 * vuoroja ja tarjoaa metodit vuoron päättämiseen yms.
 */
public class TurnObjectManager extends ObjectManager {
    private final List<AbstractCharacter> characters = new ArrayList<>();
    @Getter private int totalTurn;
    @Getter private int remainingActionPoints;
    private int turn;

    public void spendActionPoints(int amount) {
        if (this.remainingActionPoints < amount) {
            throw new IllegalStateException("Action points cannot go negative!");
        }

        this.remainingActionPoints -= amount;
    }

    /**
     * Päättää nykyisen akiivisen hahmon vuoron.
     */
    public void nextTurn() {
        if (this.characters.isEmpty()) {
            return;
        }

        this.turn++;
        validateTurnIndex();

        this.totalTurn++;
        this.remainingActionPoints = getActiveCharacter().getAttributes().getActionPoints();

        val controller = getActiveCharacter().getController();
        if (controller != null) {
            controller.beginTurn();
        }
    }

    /**
     * Hakee hahmon jonka vuoro on tällä hetkellä.
     *
     * @return hahmo jonka vuoro nyt on, <code>null</code> jos hahmoja ei ole
     */
    public AbstractCharacter getActiveCharacter() {
        return this.characters.isEmpty() ? null : this.characters.get(this.turn);
    }

    /**
     * Tarkistaa onko annetun hahmon vuoro.
     *
     * @param character hahmo joka tarkistetaan
     * @return <code>true</code> jos on hahmon vuoro, muutoin <code>false</code>
     */
    public boolean isCharactersTurn(AbstractCharacter character) {
        return !this.characters.isEmpty() && getActiveCharacter().equals(character);
    }

    @Override
    public void init(@NonNull World world) {
        super.init(world);
        this.characters.clear();
        this.turn = 0;
    }

    @Override
    public void update() {
        super.update();

        if (this.characters.isEmpty()) {
            return;
        }

        getActiveCharacter().updateOnOwnTurn(this);
    }

    @Override
    public void spawn(@NonNull GameObject object) {
        super.spawn(object);
        if (object instanceof AbstractCharacter) {
            int index;
            if (this.turn == 0) {
                index = Math.max(0, this.characters.size() - 1);
            } else {
                index = this.turn - 1;
                this.turn++;
            }
            this.characters.add(index, (AbstractCharacter) object);
        }
    }

    @Override
    protected void remove(@NonNull GameObject object) {
        super.remove(object);
        if (object instanceof AbstractCharacter) {

            int index = this.characters.indexOf(object);
            if (index < this.turn) {
                this.turn--;
            }
            this.characters.remove(index);
            validateTurnIndex();
        }
    }

    private void validateTurnIndex() {
        if (this.characters.isEmpty()) {
            this.turn = 0;
            return;
        }

        if (this.turn == this.characters.size()) {
            this.turn = 0;
        }
    }
}
