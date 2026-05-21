package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import kz.narxoz.finaljrpg.screen.GameScreen;
import kz.narxoz.finaljrpg.screen.MenuScreen;

public class GameScreenCommand extends ScreenCommand{

    public GameScreenCommand(Game game) {
        super(game);
    }

    @Override
    public void execute() {
        game.setScreen(new GameScreen());

        history.push(this);
    }
}
