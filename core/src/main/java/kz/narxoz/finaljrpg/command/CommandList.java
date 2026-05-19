package kz.narxoz.finaljrpg.command;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import kz.narxoz.finaljrpg.command.screen.GameScreenCommand;
import kz.narxoz.finaljrpg.command.screen.MenuScreenCommand;
import kz.narxoz.finaljrpg.command.screen.ScreenCommand;
import kz.narxoz.finaljrpg.command.screen.SettingsScreenCommand;
import kz.narxoz.finaljrpg.command.screen.history.ScreenCommandHistory;

import java.util.Objects;

public class CommandList {
    private Game game;
    private static CommandList commandList;


    private CommandList(){
        this.game = (Game) Gdx.app.getApplicationListener();
    }

    public static CommandList getInstance(){
        if(Objects.isNull(commandList)) commandList = new CommandList();
        return commandList;
    }

    public void toGame(){
        new GameScreenCommand(game).execute();
    }

    public void toMenu(){
        new MenuScreenCommand(game).execute();
    }

    public void toSettings(){
        new SettingsScreenCommand(game).execute();
    }

    public void screenBack(){
        ScreenCommandHistory.getInstance().pop().undo();
    }
}
