package toilari.otlite.game.world.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.var;
import toilari.otlite.game.world.World;
import toilari.otlite.game.world.entities.characters.CharacterObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Objektimanageri, joka lisää vuoropohjaista toiminnallisuutta. Hallinnoi {@link CharacterObject pelihahmojen}
 * vuoroja ja tarjoaa metodit vuoron päättämiseen yms.
 */
public class TurnObjectManager extends ObjectManager {
    private final List<CharacterObject> characters = new ArrayList<>();
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
            getActiveCharacter().endTurn();
        }

        this.turn++;
        this.totalTurn++;
        validateTurnIndex();

        if (this.characters.isEmpty()) {
            return;
        }

        this.remainingActionPoints = getActiveCharacter().getAttributes().getActionPoints(getActiveCharacter().getLevels());

        getActiveCharacter().beginTurn();
    }

    /**
     * Hakee hahmon jonka vuoro on tällä hetkellä.
     *
     * @return hahmo jonka vuoro nyt on, <code>null</code> jos hahmoja ei ole
     */
    public CharacterObject getActiveCharacter() {
        return this.turn < 0 || this.characters.isEmpty() ? null : this.characters.get(this.turn);
    }

    /**
     * Tarkistaa onko annetun hahmon vuoro.
     *
     * @param character hahmo joka tarkistetaan
     * @return <code>true</code> jos on hahmon vuoro, muutoin <code>false</code>
     */
    public boolean isCharactersTurn(CharacterObject character) {
        return !this.characters.isEmpty() && getActiveCharacter().equals(character);
    }

    @Override
    public void init(@NonNull World world) {
        super.init(world);
        this.characters.clear();
        this.turn = 0;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (this.characters.isEmpty()) {
            return;
        }

        getActiveCharacter().updateOnTurn(this);
    }

    @Override
    public void spawn(@NonNull GameObject object) {
        super.spawn(object);
        if (object instanceof CharacterObject) {
            var wasEmpty = this.characters.isEmpty();

            this.characters.add(this.turn, (CharacterObject) object);
            if (!wasEmpty) {
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
        if (object instanceof CharacterObject) {

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

    @Override
    public void clearAllNonPlayerObjects() {
        super.clearAllNonPlayerObjects();
        this.characters.clear();
        if (getPlayer() != null) {
            this.characters.add(getPlayer());
            this.turn = -1;
            nextTurn();
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
