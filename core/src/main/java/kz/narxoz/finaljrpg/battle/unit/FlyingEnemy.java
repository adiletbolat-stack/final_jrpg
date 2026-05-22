package kz.narxoz.finaljrpg.battle.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.audio.BattleSoundProfile;
import kz.narxoz.finaljrpg.battle.BattleUnit;
import kz.narxoz.finaljrpg.battle.BattleUnitType;
import kz.narxoz.finaljrpg.battle.ObjectScale;
import kz.narxoz.finaljrpg.battle.Team;
import kz.narxoz.finaljrpg.battle.movement.FlyingEnemyMovement;

public class FlyingEnemy extends BattleUnit {
    public FlyingEnemy(String name, Body body, Vector2 spawn, Vector2 rallyPoint, Color color, BattleSoundProfile soundProfile, String spriteKey, ObjectScale scale) {
        super(BattleUnit.builder()
            .name(name)
            .team(Team.ENEMY)
            .type(BattleUnitType.FLYING)
            .body(body)
            .spawn(spawn)
            .rallyPoint(rallyPoint)
            .color(color)
            .movement(new FlyingEnemyMovement())
            .soundProfile(soundProfile)
            .spriteKey(spriteKey)
            .scale(scale)
        );
    }
}
