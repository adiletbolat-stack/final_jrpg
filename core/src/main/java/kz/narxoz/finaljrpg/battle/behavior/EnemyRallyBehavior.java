package kz.narxoz.finaljrpg.battle.behavior;

import com.badlogic.gdx.math.Vector2;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class EnemyRallyBehavior implements UnitBehavior {
    private static final float RALLY_EPSILON = 0.12f;

    @Override
    public void update(BattleSession session, BattleUnit unit, float delta) {
        BattleUnit nearestOpponent = session.findNearestOpponent(unit);

        if (nearestOpponent != null && session.isTouchingOpponent(unit)) {
            unit.setBehavior(new AttackNearestBehavior());
            return;
        }

        Vector2 position = unit.getPosition();
        Vector2 rallyPoint = unit.getRallyPoint();
        float distanceX = rallyPoint.x - position.x;

        if (Math.abs(distanceX) <= RALLY_EPSILON) {
            unit.setBehavior(new AttackNearestBehavior());
            return;
        }

        session.moveHorizontally(unit, Math.signum(distanceX));
        session.moveFlyingToward(unit, rallyPoint.y, delta);
    }
}
