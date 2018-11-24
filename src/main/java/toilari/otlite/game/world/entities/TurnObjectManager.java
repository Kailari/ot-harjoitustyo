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

    /**
     * Kuluttaa annetun määrän toimintopisteitä nykyiseltä vuorolta.
     *
     * @param amount kulutettujen toimintopisteiden määrä
     * @throws IllegalArgumentException jos määrä on negatiivinen
     * @throws IllegalArgumentException jos pisteiden määrä lopuksi olisi negatiivinen
     */
    public void spendActionPoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }

        if (this.remainingActionPoints < amount) {
            throw new IllegalArgumentException("Action points cannot go negative!");
        }

        this.remainingActionPoints -= amount;
    }

    /**
     * Päättää nykyisen akiivisen hahmon vuoron.
     */
    public void nextTurn() {
        if (getActiveCharacter() != null) {
            getActiveCharacter().updateAfterTurn();
        }

        this.turn++;
        this.totalTurn++;
        validateTurnIndex();

        if (this.characters.isEmpty()) {
            return;
        }

        this.remainingActionPoints = getActiveCharacter().getAttributes().getActionPoints(getActiveCharacter().getLevels());

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
        return this.turn < 0 || this.characters.isEmpty() ? null : this.characters.get(this.turn);
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

        getActiveCharacter().updateOnTurn(this);
    }

    @Override
    public void spawn(@NonNull GameObject object) {
        super.spawn(object);
        if (object instanceof AbstractCharacter) {
            this.characters.add(this.turn, (AbstractCharacter) object);
            if (this.characters.size() != 1) {
                this.turn++;
            } else {
                this.turn = -1;
                this.totalTurn = -1;
                nextTurn();
            }
        }
    }

    @Override
    protected void remove(GameObject object) {
        super.remove(object);
        if (object instanceof AbstractCharacter) {

            int index = this.characters.indexOf(object);
            if (index != -1) {
                if (index < this.turn) {
                    this.turn--;
                }
                this.characters.remove(index);
                validateTurnIndex();
            }
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

        if (this.characters.get(this.turn).isRemoved()) {
            this.characters.remove(this.turn);
            validateTurnIndex();
        }
    }
}
