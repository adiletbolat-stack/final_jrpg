package kz.narxoz.finaljrpg.battle.behavior;

import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class AttackNearestBehavior implements UnitBehavior {
    private static final float SHOOT_RANGE = 3.8f;
    private static final float KEEP_DISTANCE = 1.2f;

    @Override
    public void update(BattleSession session, BattleUnit unit, float delta) {
        BattleUnit target = session.findNearestOpponent(unit);

        if (target == null) {
            session.stopHorizontal(unit);
            return;
        }

        float dx = target.getPosition().x - unit.getPosition().x;
        float distance = unit.getPosition().dst(target.getPosition());

        if (Math.abs(dx) > KEEP_DISTANCE) {
            session.moveHorizontally(unit, Math.signum(dx));
        } else {
            session.stopHorizontal(unit);
        }

        session.moveFlyingToward(unit, target.getPosition().y, delta);

        if (distance <= SHOOT_RANGE && unit.canShoot()) {
            session.shootAt(unit, target.getPosition());
        }
    }
}
