package kz.narxoz.finaljrpg.battle.shooting;

import com.badlogic.gdx.math.MathUtils;
import kz.narxoz.finaljrpg.battle.BattleInput;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class FullAutoMode implements ShootingMode{
    private static final float SHOTS_PER_SECOND = 8f;
    private static final float PROJECTILE_SPEED = 6f;
    private static final float MAX_SPREAD_DEGREES = 5f;

    private float shotTimer;

    @Override
    public void shoot(BattleSession session, BattleUnit shooter, BattleInput input, float delta) {
        shotTimer = Math.max(0f, shotTimer - delta);

        if (!input.shootHeld() || shooter.isDead() || shotTimer > 0f) {
            return;
        }

        float spread = MathUtils.random(-MAX_SPREAD_DEGREES, MAX_SPREAD_DEGREES);
        session.shootProjectileRotated(shooter, input.aimPoint(), PROJECTILE_SPEED, shooter.getType().getDamage(), spread);
        shotTimer = 1f / SHOTS_PER_SECOND;
        shooter.playShootSound();
    }
}
