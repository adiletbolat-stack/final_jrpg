package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

public class Projectile {
    @Getter
    private final Team team;
    @Getter
    private final Vector2 position;
    private final Vector2 velocity;
    @Getter
    private final float damage;
    @Getter
    private boolean alive = true;

    public Projectile(Team team, Vector2 position, Vector2 velocity, float damage) {
        this.team = team;
        this.position = new Vector2(position);
        this.velocity = new Vector2(velocity);
        this.damage = damage;
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
    }

    public void destroy() {
        alive = false;
    }
}
