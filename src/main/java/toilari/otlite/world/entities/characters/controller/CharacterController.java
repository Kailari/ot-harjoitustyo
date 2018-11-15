package toilari.otlite.world.entities.characters.controller;

import lombok.Getter;
import lombok.NonNull;
import toilari.otlite.world.entities.characters.AbstractCharacter;

/**
 * Ohjaa hahmoja pelimaailmassa. Voi ottaa syötteen esim. pelaajalta tai "tekoälyltä"
 */
public abstract class CharacterController {
    @Getter private AbstractCharacter controlledCharacter;

    public void takeControl(@NonNull AbstractCharacter character) {
        this.controlledCharacter = character;
    }

    public abstract int getInputX();

    public abstract int getInputY();
}
