package kz.narxoz.finaljrpg.battle.behavior;

import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public interface UnitBehavior {
    void update(BattleSession session, BattleUnit unit, float delta);
}
