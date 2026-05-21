package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.battle.behavior.UnitBehavior;
import kz.narxoz.finaljrpg.battle.movement.BattleMovement;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BattleUnit {
    private final String name;
    private final Team team;
    private final BattleUnitType type;
    private final Body body;
    private final Vector2 spawn;
    private final Vector2 rallyPoint;
    private final Color color;
    private final float width;
    private final float height;
    private final float maxHealth;
    private final BattleMovement movement;

    @Setter
    private UnitBehavior behavior;
    private float health;
    private float attackTimer;

    public BattleUnit(String name, Team team, BattleUnitType type, Body body, Vector2 spawn, Vector2 rallyPoint, Color color, BattleMovement movement) {
        this.name = name;
        this.team = team;
        this.type = type;
        this.body = body;
        this.spawn = new Vector2(spawn);
        this.rallyPoint = new Vector2(rallyPoint);
        this.color = new Color(color);
        this.width = type.getWidth();
        this.height = type.getHeight();
        this.maxHealth = type.getMaxHealth();
        this.movement = movement;
        this.health = maxHealth;
    }

    public void update(BattleSession session, float delta) {
        if (isDead()) {
            body.setLinearVelocity(0f, 0f);
            return;
        }

        body.setActive(true);
        body.setAwake(true);
        attackTimer = Math.max(0f, attackTimer - delta);

        if (behavior != null) {
            behavior.update(session, this, delta);
        }
    }

    public void reset() {
        health = maxHealth;
        attackTimer = 0f;
        body.setTransform(spawn, 0f);
        body.setLinearVelocity(0f, 0f);
        body.setAngularVelocity(0f);
        body.setAwake(true);
    }

    public boolean isDead() {
        return health <= 0f;
    }

    public void damage(float amount) {
        health = Math.max(0f, health - amount);
    }

    public boolean canShoot() {
        return attackTimer <= 0f && !isDead();
    }

    public void resetAttackTimer() {
        attackTimer = type.getAttackCooldown();
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
}
