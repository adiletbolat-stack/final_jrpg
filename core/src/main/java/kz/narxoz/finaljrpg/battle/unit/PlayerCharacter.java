package kz.narxoz.finaljrpg.battle.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.audio.BattleSoundProfile;
import kz.narxoz.finaljrpg.battle.BattleUnit;
import kz.narxoz.finaljrpg.battle.BattleUnitType;
import kz.narxoz.finaljrpg.battle.ObjectScale;
import kz.narxoz.finaljrpg.battle.Team;
import kz.narxoz.finaljrpg.battle.movement.PlayerMovement;
import kz.narxoz.finaljrpg.battle.shooting.ShootingMode;
import kz.narxoz.finaljrpg.battle.skill.PlayerSkill;

public class PlayerCharacter extends BattleUnit {
    public PlayerCharacter(String name, Body body, Vector2 spawn, Color color, BattleSoundProfile soundProfile, PlayerSkill skill, ShootingMode shootingMode, String spriteKey, ObjectScale scale) {
        super(BattleUnit.builder()
            .name(name)
            .team(Team.PLAYER)
            .type(BattleUnitType.NORMAL)
            .body(body)
            .spawn(spawn)
            .rallyPoint(spawn)
            .color(color)
            .movement(new PlayerMovement())
            .soundProfile(soundProfile)
            .skill(skill)
            .shootingMode(shootingMode)
            .spriteKey(spriteKey)
            .scale(scale)
        );
    }
}
