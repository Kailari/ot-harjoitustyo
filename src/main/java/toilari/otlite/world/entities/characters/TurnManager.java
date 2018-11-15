package toilari.otlite.world.entities.characters;

import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class TurnManager {
    private static final int TURN_CHANGE_DELAY = 100;

    private final List<AbstractCharacter> characters = new ArrayList<>();
    private int turn;
    private long turnChangeTimer;

    public void init() {
        this.characters.clear();
        this.turn = 0;
    }

    public void update() {
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

    public void endTurn() {
        this.turnChangeTimer = System.currentTimeMillis();
        this.turn++;
        if (this.turn == this.characters.size()) {
            this.turn = 0;
        }
    }

    public void add(AbstractCharacter character) {
        if (this.characters.contains(character)) {
            throw new IllegalStateException("GameObject with the same ID is already tracked by the turn manager!");
        }
        this.characters.add(character);
    }
}
