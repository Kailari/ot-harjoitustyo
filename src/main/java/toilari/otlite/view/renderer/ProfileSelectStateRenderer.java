package toilari.otlite.view.renderer;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.ProfileSelectState;
import toilari.otlite.game.event.ProfileMenuEvent;
import toilari.otlite.game.profile.Profile;
import toilari.otlite.game.profile.tracking.Statistics;
import toilari.otlite.view.Camera;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ProfileSelectStateRenderer implements IRenderer<ProfileSelectState, Camera> {
    private Scanner scanner;

    @Override
    public boolean init() {
        this.scanner = new Scanner(System.in);

        System.out.println("Welcome to OT-Lite");
        System.out.println("Please select/create a profile");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("\tquit\t\tquits the game");
        System.out.println("\tadd [name]\tadds a new profile");
        System.out.println("\tremove [id]\tremoves a profile");
        System.out.println("\tselect [id]\tselects a profile and starts the game");

        System.out.println();
        return false;
    }

    @Override
    public void draw(@NonNull Camera camera, @NonNull ProfileSelectState state) {
        System.out.println("Profiles available:");

        List<Profile> profiles;
        try {
            profiles = state.getGame().getProfileDao().findAll();
        } catch (SQLException e) {
            LOG.error("Database error occured while getting profiles: {}", e.getMessage());
            state.getGame().setRunning(false);
            return;
        }

        System.out.println("| id | name                           | has unfinished save | kills | tiles moved |");
        System.out.println("|----|--------------------------------|---------------------|-------|-------------|");
        for (val profile : profiles) {
            System.out.printf("| %2d | %-30s | %-19s | %5d | %11d |\n",
                profile.getId(),
                profile.getName(),
                (profile.hasUnfinishedSave() ? "Yes" : "No"),
                state.getGame().getStatistics().getLong(Statistics.KILLS, profile.getId()),
                state.getGame().getStatistics().getLong(Statistics.TILES_MOVED, profile.getId())
            );
        }
        System.out.println("|----|--------------------------------|---------------------|-------|-------------|");

        System.out.println();
        System.out.print(">");
        val cmd = this.scanner.nextLine();
        System.out.println();
        if (cmd.equals("quit")) {
            state.getEventSystem().fire(new ProfileMenuEvent.Quit());
        } else {
            val split = cmd.split(" ");
            if (split.length == 2) {
                switch (split[0]) {
                    case "add":
                        state.getEventSystem().fire(new ProfileMenuEvent.Add(split[1]));
                        break;
                    case "remove":
                        state.getEventSystem().fire(new ProfileMenuEvent.Remove(Integer.parseInt(split[1])));
                        break;
                    case "select":
                        state.getEventSystem().fire(new ProfileMenuEvent.Select(Integer.parseInt(split[1])));
                        break;
                }
            }
        }
    }

    @Override
    public void destroy() {
        this.scanner.close();
    }
}
