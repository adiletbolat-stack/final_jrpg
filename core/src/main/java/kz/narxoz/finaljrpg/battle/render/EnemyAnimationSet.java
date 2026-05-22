package kz.narxoz.finaljrpg.battle.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class EnemyAnimationSet implements Disposable {
    private static final int FRAME_COUNT = 4;
    private static final float IDLE_FRAME_DURATION = 0.18f;
    private static final float WALK_FRAME_DURATION = 0.12f;
    private static final float SHOOT_FRAME_DURATION = 0.12f;

    private final Texture idleTexture;
    private final Texture walkTexture;
    private final Texture shootTexture;
    private final Animation<TextureRegion> idle;
    private final Animation<TextureRegion> walk;
    private final Animation<TextureRegion> shoot;

    public EnemyAnimationSet(String enemyKey) {
        String basePath = "sprites/enemies/" + enemyKey + "/" + enemyKey + "-enemy";
        idleTexture = new Texture(basePath + "-idle.png");
        walkTexture = new Texture(basePath + "-walk.png");
        shootTexture = new Texture(basePath + "-shoot.png");

        idle = new Animation<>(IDLE_FRAME_DURATION, splitFrames(idleTexture));
        walk = new Animation<>(WALK_FRAME_DURATION, splitFrames(walkTexture));
        shoot = new Animation<>(SHOOT_FRAME_DURATION, splitFrames(shootTexture));
    }

    public TextureRegion getFrame(EnemyAnimationState.Mode mode, float stateTime) {
        return switch (mode) {
            case WALK -> walk.getKeyFrame(stateTime, true);
            case SHOOT -> shoot.getKeyFrame(stateTime, false);
            case IDLE -> idle.getKeyFrame(stateTime, true);
        };
    }

    public boolean isShootFinished(float stateTime) {
        return shoot.isAnimationFinished(stateTime);
    }

    private TextureRegion[] splitFrames(Texture texture) {
        int frameWidth = texture.getWidth() / FRAME_COUNT;
        return TextureRegion.split(texture, frameWidth, texture.getHeight())[0];
    }

    @Override
    public void dispose() {
        idleTexture.dispose();
        walkTexture.dispose();
        shootTexture.dispose();
    }
}
