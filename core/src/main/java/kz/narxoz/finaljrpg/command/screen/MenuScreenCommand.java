package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import kz.narxoz.finaljrpg.screen.MenuScreen;

public class MenuScreenCommand extends ScreenCommand{
    public MenuScreenCommand(Game game) {
        super(game);
    }

    @Override
    public void execute() {
        game.setScreen(new MenuScreen());
        history.push(this);
    }
}
