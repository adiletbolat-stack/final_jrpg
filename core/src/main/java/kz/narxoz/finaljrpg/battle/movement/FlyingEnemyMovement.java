package kz.narxoz.finaljrpg.battle.movement;

import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class FlyingEnemyMovement implements BattleMovement {
    @Override
    public void moveHorizontally(BattleUnit unit, float direction) {
        Body body = unit.getBody();
        body.setAwake(true);
        body.setLinearVelocity(Math.signum(direction) * unit.getType().getSpeed(), body.getLinearVelocity().y);
    }

    @Override
    public void stopHorizontal(BattleUnit unit) {
        Body body = unit.getBody();
        body.setAwake(true);
        body.setLinearVelocity(0f, body.getLinearVelocity().y);
    }

    @Override
    public void moveVerticallyToward(BattleUnit unit, float targetY, float delta) {
        Body body = unit.getBody();
        body.setAwake(true);
        float distanceY = targetY - body.getPosition().y;
        float velocityY = Math.max(-unit.getType().getSpeed(), Math.min(unit.getType().getSpeed(), distanceY * 3f));
        body.setLinearVelocity(body.getLinearVelocity().x, velocityY);
    }
}
