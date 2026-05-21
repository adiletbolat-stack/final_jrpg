package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import kz.narxoz.finaljrpg.screen.PauseScreen;

public class PauseScreenCommand extends ScreenCommand {

    public PauseScreenCommand(Game game) {
        super(game);
    }

    @Override
    public void execute() {
        game.setScreen(new PauseScreen());
        history.push(this);
    }
}
