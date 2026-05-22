package kz.narxoz.finaljrpg.battle.skill;

import com.badlogic.gdx.math.Vector2;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class PlayerOneSkill implements PlayerSkill {
    private static final float COOLDOWN = 4f;

    @Override
    public void activate(BattleSession session, BattleUnit player, Vector2 targetPosition) {
        session.shootTeleportProjectile(player, targetPosition);
    }

    @Override
    public float getCooldown() {
        return COOLDOWN;
    }
}
