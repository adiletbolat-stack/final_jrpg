package kz.narxoz.finaljrpg.battle.render;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ProjectileSprite {
    private final TextureRegion region;

    public ProjectileSprite(TextureRegion region) {
        this.region = region;
    }

    public TextureRegion getRegion() {
        return region;
    }
}
