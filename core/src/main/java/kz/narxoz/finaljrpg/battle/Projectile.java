package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

public class Projectile {
    @Getter
    private final Team team;
    @Getter
    private final BattleUnit owner;
    @Getter
    private final Vector2 position;
    @Getter
    private final Vector2 previousPosition;
    private final Vector2 velocity;
    @Getter
    private final float damage;
    private final ProjectileHitEffect hitEffect;
    @Getter
    private final boolean collidesWithTerrain;
    @Getter
    private boolean alive = true;

    public Projectile(Team team, Vector2 position, Vector2 velocity, float damage) {
        this(team, null, position, velocity, damage, null, false);
    }

    public Projectile(
        Team team,
        BattleUnit owner,
        Vector2 position,
        Vector2 velocity,
        float damage,
        ProjectileHitEffect hitEffect,
        boolean collidesWithTerrain
    ) {
        this.team = team;
        this.owner = owner;
        this.position = new Vector2(position);
        this.previousPosition = new Vector2(position);
        this.velocity = new Vector2(velocity);
        this.damage = damage;
        this.hitEffect = hitEffect;
        this.collidesWithTerrain = collidesWithTerrain;
    }

    public void update(float delta) {
        previousPosition.set(position);
        position.mulAdd(velocity, delta);
    }

    public void hit(BattleSession session, BattleUnit target) {
        if (target != null) {
            target.damage(damage);
        }

        if (hitEffect != null) {
            hitEffect.onHit(session, this, target);
        }

        destroy();
    }

    public void destroy() {
        alive = false;
    }
}
