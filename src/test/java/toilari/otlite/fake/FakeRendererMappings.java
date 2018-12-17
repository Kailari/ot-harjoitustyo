package toilari.otlite.fake;

import lombok.NonNull;
import lombok.val;
import toilari.otlite.game.*;
import toilari.otlite.view.Camera;
import toilari.otlite.view.renderer.IGameStateRenderer;

import java.util.HashMap;
import java.util.Map;

public class FakeRendererMappings {
    public static Map<Class, IGameStateRenderer> create() {
        val mapping = new HashMap<Class, IGameStateRenderer>();
        mapping.put(ProfileSelectGameState.class, new NOPGameStateRenderer<ProfileSelectGameState>());
        mapping.put(MainMenuGameState.class, new NOPGameStateRenderer<MainMenuGameState>());
        mapping.put(BestiaryGameState.class, new NOPGameStateRenderer<BestiaryGameState>());
        mapping.put(PlayGameState.class, new NOPGameStateRenderer<PlayGameState>());
        return mapping;
    }

    private static class NOPGameStateRenderer<T extends GameState> implements IGameStateRenderer<T, Camera> {
        @Override
        public boolean init(@NonNull T state) {
            return false;
        }

        @Override
        public void draw(@NonNull Camera camera, @NonNull T state) {

        }

        @Override
        public void destroy(@NonNull T state) {

        }
    }
}
