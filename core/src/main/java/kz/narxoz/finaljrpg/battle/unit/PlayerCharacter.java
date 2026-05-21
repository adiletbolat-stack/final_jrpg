package kz.narxoz.finaljrpg.battle.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.battle.BattleUnit;
import kz.narxoz.finaljrpg.battle.BattleUnitType;
import kz.narxoz.finaljrpg.battle.Team;
import kz.narxoz.finaljrpg.battle.movement.PlayerMovement;

public class PlayerCharacter extends BattleUnit {
    public PlayerCharacter(String name, Body body, Vector2 spawn, Color color) {
        super(name, Team.PLAYER, BattleUnitType.NORMAL, body, spawn, spawn, color, new PlayerMovement());
    }
}
