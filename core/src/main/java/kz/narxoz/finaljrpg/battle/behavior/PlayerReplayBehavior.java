package kz.narxoz.finaljrpg.battle.behavior;

import kz.narxoz.finaljrpg.battle.BattleInput;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class PlayerReplayBehavior implements UnitBehavior {
    private final int playerIndex;

    public PlayerReplayBehavior(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    @Override
    public void update(BattleSession session, BattleUnit unit, float delta) {
        BattleInput input = session.getInputForPlayer(playerIndex);
        session.applyPlayerInput(unit, input, delta);
    }
}
