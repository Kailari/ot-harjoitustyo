package toilari.otlite.world.entities;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.world.World;
import toilari.otlite.world.entities.characters.AbstractCharacter;

import java.util.ArrayList;
import java.util.List;

/**
 * Objektimanageri, joka lisää vuoropohjaista toiminnallisuutta. Hallinnoi {@link AbstractCharacter pelihahmojen}
 * vuoroja ja tarjoaa metodit vuoron päättämiseen yms.
 */
public class TurnObjectManager extends ObjectManager {
    private static final int TURN_CHANGE_DELAY = 100;

    private final List<AbstractCharacter> characters = new ArrayList<>();
    private int turn;
    private long turnChangeTimer;

    /**
     * Päättää nykyisen akiivisen hahmon vuoron.
     */
    public void endTurn() {
        this.turnChangeTimer = System.currentTimeMillis();
        this.turn++;
        if (this.turn == this.characters.size()) {
            this.turn = 0;
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

        if (this.turnChangeTimer + TURN_CHANGE_DELAY > System.currentTimeMillis()) {
            return;
        }

        val character = findNextNotRemovedCharacter();
        character.updateOnOwnTurn(this);
    }

    @NonNull
    private AbstractCharacter findNextNotRemovedCharacter() {
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
            this.characters.remove(object);
        }
    }
}
