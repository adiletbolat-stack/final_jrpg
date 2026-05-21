package kz.narxoz.finaljrpg.battle.movement;

import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class NormalEnemyMovement implements BattleMovement {
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
}
