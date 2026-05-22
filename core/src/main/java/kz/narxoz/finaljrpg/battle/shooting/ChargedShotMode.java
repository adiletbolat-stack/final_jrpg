package kz.narxoz.finaljrpg.battle.shooting;

import kz.narxoz.finaljrpg.battle.BattleInput;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class ChargedShotMode implements ShootingMode{
    private static final float CHARGED_SHOT_SPEED = 8f;
    private static final float CHARGE_DURATION = 4f;
    private static final float MIN_DAMAGE = 5f;
    private static final float MAX_DAMAGE = 100f;

    @Override
    public void shoot(BattleSession session, BattleUnit shooter, BattleInput input, float delta) {
        if (input.shootHeld()) {
            shooter.chargeSkill(delta, CHARGE_DURATION);
        }

        if (!input.shootReleased() || !shooter.canShoot()) {
            return;
        }

        float damage = shooter.getSkillChargeDamage(MIN_DAMAGE, MAX_DAMAGE);
        shooter.resetSkillCharge();
        session.shootProjectile(shooter, input.aimPoint(), CHARGED_SHOT_SPEED, damage);
        shooter.resetAttackTimer();
        shooter.playShootSound();
    }
}
