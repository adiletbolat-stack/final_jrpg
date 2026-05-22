package kz.narxoz.finaljrpg.battle.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import kz.narxoz.finaljrpg.battle.BattleUnit;
import kz.narxoz.finaljrpg.battle.Team;
import kz.narxoz.finaljrpg.battle.unit.PlayerCharacter;

import java.util.HashMap;
import java.util.Map;

public class ProjectileSpriteFlyweightFactory implements Disposable {
    private static final String ENEMY_PROJECTILE_KEY = "enemy";

    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, ProjectileSprite> sprites = new HashMap<>();

    public ProjectileSprite getForShooter(BattleUnit shooter) {
        if (shooter.getTeam() == Team.ENEMY) {
            return get(ENEMY_PROJECTILE_KEY, "sprites/enemies/enemy-shot.png");
        }

        PlayerCharacter player = (PlayerCharacter) shooter;
        String spriteKey = player.getSpriteKey();
        return get(spriteKey, "sprites/characters/" + spriteKey + "/" + spriteKey + "-projectile.png");
    }

    private ProjectileSprite get(String key, String path) {
        ProjectileSprite existing = sprites.get(key);

        if (existing != null) {
            return existing;
        }

        Texture texture = new Texture(path);
        textures.put(key, texture);
        ProjectileSprite sprite = new ProjectileSprite(new TextureRegion(texture));
        sprites.put(key, sprite);
        return sprite;
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
    }
}
