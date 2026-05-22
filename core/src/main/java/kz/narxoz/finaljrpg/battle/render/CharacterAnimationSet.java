package kz.narxoz.finaljrpg.battle.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class CharacterAnimationSet implements Disposable {
    private static final int FRAME_COUNT = 4;
    private static final float IDLE_FRAME_DURATION = 0.18f;
    private static final float JUMP_FRAME_DURATION = 0.12f;
    private static final float SHOOT_FRAME_DURATION = 0.12f;

    private final Texture idleTexture;
    private final Texture jumpTexture;
    private final Texture shootTexture;
    private final Animation<TextureRegion> idle;
    private final Animation<TextureRegion> jump;
    private final Animation<TextureRegion> shoot;

    //sets Animation<TextureRegion> for all three states of any character
    //the path is created using label of a character and the file is received accordingly
    public CharacterAnimationSet(String characterKey) {
        String basePath = "sprites/characters/" + characterKey + "/" + characterKey;
        idleTexture = new Texture(basePath + "_idle.png");
        jumpTexture = new Texture(basePath + "_jump.png");
        shootTexture = new Texture(basePath + "_shoot.png");

        idle = new Animation<>(IDLE_FRAME_DURATION, splitFrames(idleTexture));
        jump = new Animation<>(JUMP_FRAME_DURATION, splitFrames(jumpTexture));
        shoot = new Animation<>(SHOOT_FRAME_DURATION, splitFrames(shootTexture));
    }

    //uses switch to decide which animation to use depending on the current state
    public TextureRegion getFrame(CharacterAnimationState.Mode mode, float stateTime) {
        return switch (mode) {
            case JUMP -> jump.getKeyFrame(stateTime, true);
            case SHOOT -> shoot.getKeyFrame(stateTime, false);
            case IDLE -> idle.getKeyFrame(stateTime, true);
        };
    }

    public boolean isShootFinished(float stateTime) {
        return shoot.isAnimationFinished(stateTime);
    }

    //splits the sprite sheet into frames of equal size depending on source width
    private TextureRegion[] splitFrames(Texture texture) {
        int frameWidth = texture.getWidth() / FRAME_COUNT;
        return TextureRegion.split(texture, frameWidth, texture.getHeight())[0];
    }

    @Override
    public void dispose() {
        idleTexture.dispose();
        jumpTexture.dispose();
        shootTexture.dispose();
    }
}
