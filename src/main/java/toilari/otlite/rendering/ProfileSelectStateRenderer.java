package toilari.otlite.rendering;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.ProfileSelectState;
import toilari.otlite.menu.Profile;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ProfileSelectStateRenderer implements IRenderer<ProfileSelectState> {
    private Scanner scanner;

    @Override
    public boolean init() {
        this.scanner = new Scanner(System.in);
        return false;
    }

    @Override
    public void draw(@NonNull Camera camera, @NonNull ProfileSelectState state) {
        System.out.println("Profiles available:");

        List<Profile> profiles;
        try {
            profiles = state.getProfiles();
        } catch (SQLException e) {
            LOG.error("Database error occured while getting profiles: {}", e.getMessage());
            state.getGame().setRunning(false);
            return;
        }

        for (val profile : profiles) {
            System.out.printf("#%d - %-30s (%s unfinished saved game)\n", profile.getId(), profile.getName(), (profile.hasUnfinishedSave() ? "Has" : "No"));
        }

        val cmd = this.scanner.nextLine();
        if (cmd.equals("quit")) {
            state.getEventSystem().fire(new ProfileSelectState.QuitEvent());
        }
    }

    @Override
    public void destroy(@NonNull ProfileSelectState profileSelectState) {
        this.scanner.close();
    }
}
