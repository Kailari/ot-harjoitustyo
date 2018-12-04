package toilari.otlite.game.event;

public abstract class MainMenuEvent implements IEvent {
    public static class Continue extends MainMenuEvent {
    }

    public static class NewGame extends MainMenuEvent {
    }

    public static class Bestiary extends MainMenuEvent {
    }

    public static class Settings extends MainMenuEvent {
    }
}
