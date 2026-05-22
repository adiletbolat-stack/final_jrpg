package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.audio.BattleSoundPlayer;
import kz.narxoz.finaljrpg.audio.BattleSoundProfile;
import kz.narxoz.finaljrpg.battle.behavior.UnitBehavior;
import kz.narxoz.finaljrpg.battle.movement.BattleMovement;
import kz.narxoz.finaljrpg.battle.shooting.ShootingMode;
import kz.narxoz.finaljrpg.battle.skill.PlayerSkill;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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
    private final ShootingMode shootingMode;
    private String spriteKey;
    private ObjectScale scale;

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

    protected BattleUnit(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.team = Objects.requireNonNull(builder.team, "team");
        this.type = Objects.requireNonNull(builder.type, "type");
        this.body = Objects.requireNonNull(builder.body, "body");
        this.spawn = new Vector2(Objects.requireNonNull(builder.spawn, "spawn"));
        this.rallyPoint = new Vector2(builder.rallyPoint == null ? builder.spawn : builder.rallyPoint);
        this.color = new Color(Objects.requireNonNull(builder.color, "color"));
        this.width = type.getWidth();
        this.height = type.getHeight();
        this.maxHealth = type.getMaxHealth();
        this.movement = Objects.requireNonNull(builder.movement, "movement");
        this.soundProfile = builder.soundProfile == null ? BattleSoundProfile.EMPTY : builder.soundProfile;
        this.skill = builder.skill;
        this.shootingMode = builder.shootingMode;
        this.spriteKey = builder.spriteKey;
        this.scale = builder.scale == null ? SpriteScales.defaultScale(spriteKey) : builder.scale;
        this.health = maxHealth;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private String name;
        private Team team;
        private BattleUnitType type;
        private Body body;
        private Vector2 spawn;
        private Vector2 rallyPoint;
        private Color color;
        private BattleMovement movement;
        private BattleSoundProfile soundProfile = BattleSoundProfile.EMPTY;
        private PlayerSkill skill;
        private ShootingMode shootingMode;
        private String spriteKey;
        private ObjectScale scale;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder team(Team team) {
            this.team = team;
            return this;
        }

        public Builder type(BattleUnitType type) {
            this.type = type;
            return this;
        }

        public Builder body(Body body) {
            this.body = body;
            return this;
        }

        public Builder spawn(Vector2 spawn) {
            this.spawn = spawn;
            return this;
        }

        public Builder rallyPoint(Vector2 rallyPoint) {
            this.rallyPoint = rallyPoint;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder movement(BattleMovement movement) {
            this.movement = movement;
            return this;
        }

        public Builder soundProfile(BattleSoundProfile soundProfile) {
            this.soundProfile = soundProfile;
            return this;
        }

        public Builder skill(PlayerSkill skill) {
            this.skill = skill;
            return this;
        }

        public Builder shootingMode(ShootingMode shootingMode) {
            this.shootingMode = shootingMode;
            return this;
        }

        public Builder spriteKey(String spriteKey) {
            this.spriteKey = spriteKey;
            return this;
        }

        public Builder scale(ObjectScale scale) {
            this.scale = scale;
            return this;
        }
    }
}
