package kz.narxoz.finaljrpg.battle.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.battle.BattleUnit;
import kz.narxoz.finaljrpg.battle.BattleUnitType;
import kz.narxoz.finaljrpg.battle.Team;
import kz.narxoz.finaljrpg.battle.movement.HeavyEnemyMovement;

public class HeavyEnemy extends BattleUnit {
    public HeavyEnemy(String name, Body body, Vector2 spawn, Vector2 rallyPoint, Color color) {
        super(name, Team.ENEMY, BattleUnitType.HEAVY, body, spawn, rallyPoint, color, new HeavyEnemyMovement());
    }
}
