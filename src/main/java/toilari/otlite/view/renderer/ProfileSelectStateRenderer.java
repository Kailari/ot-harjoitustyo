package toilari.otlite.view.renderer;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import toilari.otlite.game.ProfileSelectState;
import toilari.otlite.game.profile.Profile;
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
        System.out.println("\tselect [id]\t\t");

        System.out.println();
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

        System.out.println("| id | name                           | save state                   |");
        System.out.println("|----|--------------------------------|------------------------------|");
        for (val profile : profiles) {
            System.out.printf("| %d  | %-30s | (%s unfinished saved game)\n", profile.getId(), profile.getName(), (profile.hasUnfinishedSave() ? "Has" : "No"));
        }
        System.out.println("|----|--------------------------------|------------------------------|");

        System.out.println();
        System.out.print(">");
        val cmd = this.scanner.nextLine();
        System.out.println();
        if (cmd.equals("quit")) {
            state.getEventSystem().fire(new ProfileSelectState.QuitEvent());
        } else {
            val split = cmd.split(" ");
            if (split.length == 2) {
                switch (split[0]) {
                    case "add":
                        state.getEventSystem().fire(new ProfileSelectState.AddEvent(split[1]));
                        break;
                    case "remove":
                        state.getEventSystem().fire(new ProfileSelectState.RemoveEvent(Integer.parseInt(split[1])));
                        break;
                    case "select":
                        state.getEventSystem().fire(new ProfileSelectState.SelectEvent(Integer.parseInt(split[1])));
                        break;
                }
            }
        }
    }

    @Override
    public void destroy(@NonNull ProfileSelectState profileSelectState) {
        this.scanner.close();
    }
}
