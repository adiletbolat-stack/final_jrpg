package kz.narxoz.finaljrpg.battle.render;

import kz.narxoz.finaljrpg.battle.BattleUnit;

public class CharacterAnimationState {
    public enum Mode {
        IDLE,
        JUMP,
        SHOOT
    }

    //value that prevents changing direction of animation due to small bug of bodies having tiny amount of velocity
    private static final float FACING_VELOCITY_EPSILON = 0.02f;

    private Mode mode = Mode.IDLE;
    private float stateTime;
    private boolean facingRight = true;

    //runs every frame
    public void update(BattleUnit unit, CharacterAnimationSet animationSet, float delta, boolean grounded) {
        stateTime += delta;

        //shooting animation takes top priority (if shooting while jumping it will show shooting animation)
        if (mode == Mode.SHOOT) {
            if (animationSet.isShootFinished(stateTime)) {
                mode = grounded ? Mode.IDLE : Mode.JUMP;
                stateTime = 0f;
            }
            return;
        }

        updateFacingFromVelocity(unit);

        //changes into IDLE animation when landing
        if (grounded && mode == Mode.JUMP) {
            mode = Mode.IDLE;
            stateTime = 0f;
        }
    }

    public void triggerJump() {
        //again, shooting animation is top priority
        if (mode == Mode.SHOOT) {
            return;
        }

        //changes animation to jumping
        mode = Mode.JUMP;
        stateTime = 0f;
    }

    public void triggerShoot(boolean facingRight) {
        this.facingRight = facingRight;
        mode = Mode.SHOOT;
        stateTime = 0f;
    }

    public void reset() {
        mode = Mode.IDLE;
        stateTime = 0f;
        facingRight = true;
    }

    public Mode getMode() {
        return mode;
    }

    public float getStateTime() {
        return stateTime;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    private void updateFacingFromVelocity(BattleUnit unit) {
        float velocityX = unit.getBody().getLinearVelocity().x;

        if (Math.abs(velocityX) > FACING_VELOCITY_EPSILON) {
            facingRight = velocityX > 0f;
        }
    }
}
