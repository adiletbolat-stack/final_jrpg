package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ResumeGameCommand extends ScreenCommand {

    private final Screen gameScreen;

    public ResumeGameCommand(Game game, Screen gameScreen) {

        super(game);

        this.gameScreen = gameScreen;
    }

    @Override
    public void execute() {
        game.setScreen(gameScreen);
    }
}
