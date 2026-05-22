package kz.narxoz.finaljrpg.battle.shooting;

import com.badlogic.gdx.math.Vector2;
import kz.narxoz.finaljrpg.battle.BattleInput;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public interface ShootingMode {
    default void shoot(BattleSession session, BattleUnit shooter, Vector2 target) {
        shoot(session, shooter, BattleInput.EMPTY, 0f);
    }

    void shoot(BattleSession session, BattleUnit shooter, BattleInput input, float delta);
}
