package kz.narxoz.finaljrpg.battle.skill;

import com.badlogic.gdx.math.Vector2;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class PlayerThreeSkill implements PlayerSkill {
    private static final float MAX_FLIGHT_FUEL = 20f;

    @Override
    public void activate(BattleSession session, BattleUnit player, Vector2 targetPosition) {
        session.activatePlayerThreeHighJump(player, MAX_FLIGHT_FUEL);
    }
}
