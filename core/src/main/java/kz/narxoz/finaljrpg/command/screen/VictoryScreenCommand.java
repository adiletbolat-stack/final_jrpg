package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Game;
import kz.narxoz.finaljrpg.battle.event.VictoryEvent;
import kz.narxoz.finaljrpg.screen.VictoryScreen;

public class VictoryScreenCommand extends ScreenCommand {
    private final VictoryEvent victoryEvent;

    public VictoryScreenCommand(Game game, VictoryEvent victoryEvent) {
        super(game);
        this.victoryEvent = victoryEvent;
    }

    @Override
    public void execute() {
        game.setScreen(new VictoryScreen(victoryEvent));
        history.push(this);
    }
}
