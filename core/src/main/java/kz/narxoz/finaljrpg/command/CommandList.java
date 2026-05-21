package kz.narxoz.finaljrpg.command;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import kz.narxoz.finaljrpg.command.screen.GameScreenCommand;
import kz.narxoz.finaljrpg.command.screen.MenuScreenCommand;
import kz.narxoz.finaljrpg.command.screen.PauseScreenCommand;
import kz.narxoz.finaljrpg.command.screen.ResumeGameCommand;
import kz.narxoz.finaljrpg.command.screen.SettingsScreenCommand;
import kz.narxoz.finaljrpg.command.screen.history.ScreenCommandHistory;
import kz.narxoz.finaljrpg.command.victory.VictoryScreenCommand;
import kz.narxoz.finaljrpg.battle.event.VictoryEvent;

import java.util.Objects;

public class CommandList {
    private Game game;
    private static CommandList commandList;

    private ResumeGameCommand resumeGameCommand;

    private CommandList() {
        this.game = (Game) Gdx.app.getApplicationListener();
    }

    public static CommandList getInstance() {
        if (Objects.isNull(commandList))
            commandList = new CommandList();
        return commandList;
    }

    public void toGame() {
        new GameScreenCommand(game).execute();
    }

    public void toMenu() {
        new MenuScreenCommand(game).execute();
    }

    public void toSettings() {
        new SettingsScreenCommand(game).execute();
    }

    public void toVictory(VictoryEvent victoryEvent) {
        new VictoryScreenCommand(game, victoryEvent).execute();
    }

    public void toPause(Screen currentScreen) {

        resumeGameCommand = new ResumeGameCommand(game, currentScreen);

        new PauseScreenCommand(game).execute();
    }

    public void resumeGame() {

        if (resumeGameCommand != null) {

            resumeGameCommand.execute();
        }
    }

   public void screenBack(){

    if(ScreenCommandHistory
        .getInstance()
        .getCommands()
        .size() <= 1) return;

    ScreenCommandHistory history =
        ScreenCommandHistory.getInstance();

    history.pop();

    history.pop().execute();
}

}
