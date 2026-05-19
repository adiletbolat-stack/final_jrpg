package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import kz.narxoz.finaljrpg.command.screen.history.ScreenCommandHistory;

public abstract class ScreenCommand {
    protected Game game;
    protected ScreenCommandHistory history;

    public ScreenCommand(Game game){
        this.game = game;
        history = ScreenCommandHistory.getInstance();
    }

    public void undo(){
        if(history.getCommands().isEmpty()) return;
        history.pop().execute();
    }

    public abstract void execute();
}
