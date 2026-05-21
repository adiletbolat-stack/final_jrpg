package kz.narxoz.finaljrpg.battle.movement;

import kz.narxoz.finaljrpg.battle.BattleUnit;

public interface BattleMovement {
    void moveHorizontally(BattleUnit unit, float direction);

    void stopHorizontal(BattleUnit unit);

    default void moveVerticallyToward(BattleUnit unit, float targetY, float delta) {
    }
}
