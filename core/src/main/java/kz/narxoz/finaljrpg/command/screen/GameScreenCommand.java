package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import kz.narxoz.finaljrpg.screen.GameScreen;
import kz.narxoz.finaljrpg.screen.MenuScreen;

public class GameScreenCommand extends ScreenCommand{

    private Screen gameScreen;
    public GameScreenCommand(Game game) {
        super(game);
    }

    @Override
    public void execute() {
        if(game.getScreen() instanceof MenuScreen){
            gameScreen = new GameScreen();
            game.setScreen(gameScreen);
        } else game.setScreen(gameScreen);

        history.push(this);
    }
}
