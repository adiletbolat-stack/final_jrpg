package kz.narxoz.finaljrpg.battle.movement;

import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class HeavyEnemyMovement implements BattleMovement {
    private static final float ACCELERATION = 7f;

    @Override
    public void moveHorizontally(BattleUnit unit, float direction) {
        Body body = unit.getBody();
        body.setAwake(true);
        float targetVelocity = Math.signum(direction) * unit.getType().getSpeed();
        float velocityDelta = targetVelocity - body.getLinearVelocity().x;
        float impulse = Math.max(-ACCELERATION, Math.min(ACCELERATION, velocityDelta)) * body.getMass();
        body.applyLinearImpulse(impulse, 0f, body.getWorldCenter().x, body.getWorldCenter().y, true);
    }

    @Override
    public void stopHorizontal(BattleUnit unit) {
        Body body = unit.getBody();
        body.setAwake(true);
        body.setLinearVelocity(body.getLinearVelocity().x * 0.85f, body.getLinearVelocity().y);
    }
}
