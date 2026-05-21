package kz.narxoz.finaljrpg.battle.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.audio.BattleSoundProfile;
import kz.narxoz.finaljrpg.battle.BattleUnit;
import kz.narxoz.finaljrpg.battle.BattleUnitType;
import kz.narxoz.finaljrpg.battle.Team;
import kz.narxoz.finaljrpg.battle.movement.NormalEnemyMovement;

public class NormalEnemy extends BattleUnit {
    public NormalEnemy(String name, Body body, Vector2 spawn, Vector2 rallyPoint, Color color, BattleSoundProfile soundProfile) {
        super(name, Team.ENEMY, BattleUnitType.NORMAL, body, spawn, rallyPoint, color, new NormalEnemyMovement(), soundProfile);
    }
}
