package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import kz.narxoz.finaljrpg.screen.SettingsScreen;

public class SettingsScreenCommand extends ScreenCommand{

    public SettingsScreenCommand(Game game) {
        super(game);
    }

    @Override
    public void execute() {
        game.setScreen(new SettingsScreen());
        history.push(this);
    }
}
