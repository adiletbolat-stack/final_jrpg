package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.audio.BattleSoundPlayer;
import kz.narxoz.finaljrpg.audio.BattleSoundProfile;
import kz.narxoz.finaljrpg.battle.behavior.UnitBehavior;
import kz.narxoz.finaljrpg.battle.movement.BattleMovement;
import kz.narxoz.finaljrpg.battle.skill.PlayerSkill;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BattleUnit {
    private static final float WALK_SOUND_INTERVAL = 0.28f;
    private static final float FULL_SKILL_CHARGE = 1f;

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
    private final BattleSoundProfile soundProfile;
    private final PlayerSkill skill;

    @Setter
    private UnitBehavior behavior;
    private float health;
    private float attackTimer;
    private float walkSoundTimer;
    private float skillCharge;
    private float skillCooldownTimer;
    private float shieldHealth;
    private float maxShieldHealth;
    private float flightFuel;
    private float maxFlightFuel;

    public BattleUnit(String name, Team team, BattleUnitType type, Body body, Vector2 spawn, Vector2 rallyPoint, Color color, BattleMovement movement) {
        this(name, team, type, body, spawn, rallyPoint, color, movement, BattleSoundProfile.EMPTY);
    }

    public BattleUnit(
        String name,
        Team team,
        BattleUnitType type,
        Body body,
        Vector2 spawn,
        Vector2 rallyPoint,
        Color color,
        BattleMovement movement,
        BattleSoundProfile soundProfile
    ) {
        this(name, team, type, body, spawn, rallyPoint, color, movement, soundProfile, null);
    }

    public BattleUnit(
        String name,
        Team team,
        BattleUnitType type,
        Body body,
        Vector2 spawn,
        Vector2 rallyPoint,
        Color color,
        BattleMovement movement,
        BattleSoundProfile soundProfile,
        PlayerSkill skill
    ) {
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
        this.soundProfile = soundProfile;
        this.skill = skill;
        this.health = maxHealth;
    }

    public void update(BattleSession session, float delta) {
        walkSoundTimer = Math.max(0f, walkSoundTimer - delta);

        if (isDead()) {
            body.setLinearVelocity(0f, 0f);
            body.setActive(false);
            return;
        }

        body.setActive(true);
        body.setAwake(true);
        attackTimer = Math.max(0f, attackTimer - delta);
        skillCooldownTimer = Math.max(0f, skillCooldownTimer - delta);

        if (behavior != null) {
            behavior.update(session, this, delta);
        }
    }

    public void reset() {
        health = maxHealth;
        attackTimer = 0f;
        skillCharge = 0f;
        skillCooldownTimer = 0f;
        shieldHealth = 0f;
        maxShieldHealth = 0f;
        flightFuel = 0f;
        maxFlightFuel = 0f;
        body.setActive(true);
        body.setTransform(spawn, 0f);
        body.setLinearVelocity(0f, 0f);
        body.setAngularVelocity(0f);
        body.setAwake(true);
    }

    public boolean isDead() {
        return health <= 0f;
    }

    public void damage(float amount) {
        if (shieldHealth > 0f) {
            float absorbedDamage = Math.min(shieldHealth, amount);
            shieldHealth -= absorbedDamage;
            amount -= absorbedDamage;
        }

        health = Math.max(0f, health - amount);
    }

    public boolean canShoot() {
        return attackTimer <= 0f && !isDead();
    }

    public void resetAttackTimer() {
        attackTimer = type.getAttackCooldown();
    }

    public void activateSkill(BattleSession session, Vector2 targetPosition) {
        if (skill == null || isDead() || skillCooldownTimer > 0f) {
            return;
        }

        skill.activate(session, this, targetPosition);
        skillCooldownTimer = skill.getCooldown();
    }

    public void activateShield(float shieldHealth) {
        this.maxShieldHealth = shieldHealth;
        this.shieldHealth = shieldHealth;
    }

    public void fillFlightFuel(float maxFuel) {
        this.maxFlightFuel = maxFuel;
        this.flightFuel = maxFuel;
    }

    public void useFlightFuel(float amount) {
        flightFuel = Math.max(0f, flightFuel - amount);
    }

    public void chargeSkill(float delta, float chargeDuration) {
        if (chargeDuration <= 0f) {
            skillCharge = FULL_SKILL_CHARGE;
            return;
        }

        skillCharge = Math.min(FULL_SKILL_CHARGE, skillCharge + delta / chargeDuration);
    }

    public float getSkillChargeDamage(float minDamage, float maxDamage) {
        return minDamage + (maxDamage - minDamage) * skillCharge;
    }

    public void resetSkillCharge() {
        skillCharge = 0f;
    }

    public float getShieldRatio() {
        if (maxShieldHealth <= 0f) {
            return 0f;
        }

        return shieldHealth / maxShieldHealth;
    }

    public float getFlightFuelRatio() {
        if (maxFlightFuel <= 0f) {
            return 0f;
        }

        return flightFuel / maxFlightFuel;
    }

    public void playWalkSound() {
        if (walkSoundTimer > 0f) {
            return;
        }

        BattleSoundPlayer.play(soundProfile.walkSoundPath());
        walkSoundTimer = WALK_SOUND_INTERVAL;
    }

    public void playShootSound() {
        BattleSoundPlayer.play(soundProfile.shootSoundPath());
    }

    public void playJumpSound() {
        BattleSoundPlayer.play(soundProfile.jumpSoundPath());
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
}
