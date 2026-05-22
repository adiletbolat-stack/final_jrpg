package kz.narxoz.finaljrpg.battle.skill;

import com.badlogic.gdx.math.Vector2;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class PlayerTwoSkill implements PlayerSkill {
    private static final float COOLDOWN = 8f;
    private static final float SHIELD_HEALTH = 50f;

    @Override
    public void activate(BattleSession session, BattleUnit player, Vector2 targetPosition) {
        player.activateShield(SHIELD_HEALTH);
    }

    @Override
    public float getCooldown() {
        return COOLDOWN;
    }
}
