package kz.narxoz.finaljrpg.battle.shooting;

import kz.narxoz.finaljrpg.battle.BattleInput;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class SpreadShotMode implements ShootingMode{
    private static final int PROJECTILE_COUNT = 5;
    private static final float CONE_DEGREES = 28f;
    private static final float PROJECTILE_SPEED = 4f;

    @Override
    public void shoot(BattleSession session, BattleUnit shooter, BattleInput input, float delta) {
        if (!input.shoot() || !shooter.canShoot()) {
            return;
        }

        float startAngle = -CONE_DEGREES / 2f;
        float angleStep = CONE_DEGREES / (PROJECTILE_COUNT - 1);

        for (int i = 0; i < PROJECTILE_COUNT; i++) {
            session.shootProjectileRotated(
                shooter,
                input.aimPoint(),
                PROJECTILE_SPEED,
                shooter.getType().getDamage(),
                startAngle + angleStep * i
            );
        }

        shooter.resetAttackTimer();
        shooter.playShootSound();
    }
}
