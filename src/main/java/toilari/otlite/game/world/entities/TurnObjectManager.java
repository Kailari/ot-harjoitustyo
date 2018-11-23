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
    private AbstractCharacter activeCharacter;

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
        this.totalTurn++;
        this.turn++;
        this.activeCharacter = findNextNotRemovedCharacter();
        this.remainingActionPoints = this.activeCharacter.getAttributes().getActionPoints();

        val controller = this.activeCharacter.getController();
        if (controller != null) {
            controller.beginTurn();
        }
    }

    /**
     * Tarkistaa onko annetun hahmon vuoro.
     *
     * @param character hahmo joka tarkistetaan
     * @return <code>true</code> jos on hahmon vuoro, muutoin <code>false</code>
     */
    public boolean isCharactersTurn(AbstractCharacter character) {
        return this.characters.get(this.turn).equals(character);
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

        if (this.activeCharacter == null) {
            nextTurn();
        }

        this.activeCharacter.updateOnOwnTurn(this);
    }

    @NonNull
    private AbstractCharacter findNextNotRemovedCharacter() {
        while (this.turn >= this.characters.size()) {
            this.turn -= this.characters.size();
        }

        while (this.characters.get(this.turn).isRemoved()) {
            this.characters.remove(this.turn);

            if (this.characters.size() == 0) {
                throw new IllegalStateException("There should always be at least one character present when turn manager is enabled!");
            } else if (this.turn == this.characters.size()) {
                this.turn = 0;
            }
        }
        return this.characters.get(this.turn);
    }

    @Override
    public void spawn(@NonNull GameObject object) {
        super.spawn(object);
        if (object instanceof AbstractCharacter) {
            this.characters.add((AbstractCharacter) object);
        }
    }

    @Override
    protected void remove(@NonNull GameObject object) {
        super.remove(object);
        if (object instanceof AbstractCharacter) {
            if (object.equals(this.activeCharacter)) {
                nextTurn();
            }

            this.characters.remove(object);
        }
    }
}
