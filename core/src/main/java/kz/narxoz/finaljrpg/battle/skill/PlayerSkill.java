package kz.narxoz.finaljrpg.battle.skill;

import com.badlogic.gdx.math.Vector2;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public interface PlayerSkill {
    void activate(BattleSession session, BattleUnit player, Vector2 targetPosition);
}
