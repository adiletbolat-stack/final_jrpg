package kz.narxoz.finaljrpg.battle;

import static kz.narxoz.finaljrpg.Constants.PPM;

public enum BattleUnitType {
    NORMAL(16f / PPM, 32f / PPM, 4f, 0.2f, 100f, 0.9f, 18f),
    HEAVY(32f / PPM, 32f / PPM, 2.2f, 0.2f, 220f, 1.35f, 32f),
    FLYING(16f / PPM, 16f / PPM, 3.2f, 0f, 70f, 0.65f, 14f);

    private final float width;
    private final float height;
    private final float speed;
    private final float jumpImpulse;
    private final float maxHealth;
    private final float attackCooldown;
    private final float damage;

    BattleUnitType(float width, float height, float speed, float jumpImpulse, float maxHealth, float attackCooldown, float damage) {
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.jumpImpulse = jumpImpulse;
        this.maxHealth = maxHealth;
        this.attackCooldown = attackCooldown;
        this.damage = damage;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public float getJumpImpulse() {
        return jumpImpulse;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public float getDamage() {
        return damage;
    }
}
