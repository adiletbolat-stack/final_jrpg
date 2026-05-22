package kz.narxoz.finaljrpg.battle.render;

import kz.narxoz.finaljrpg.battle.BattleUnit;

public class EnemyAnimationState {
    public enum Mode {
        IDLE,
        WALK,
        SHOOT
    }

    private static final float WALK_VELOCITY_EPSILON = 0.02f;

    private Mode mode = Mode.IDLE;
    private float stateTime;
    private boolean facingRight = false;

    public void update(BattleUnit unit, EnemyAnimationSet animationSet, float delta) {
        stateTime += delta;

        if (mode == Mode.SHOOT) {
            if (animationSet.isShootFinished(stateTime)) {
                mode = isWalking(unit) ? Mode.WALK : Mode.IDLE;
                stateTime = 0f;
            }
            return;
        }

        Mode nextMode = isWalking(unit) ? Mode.WALK : Mode.IDLE;

        if (mode != nextMode) {
            mode = nextMode;
            stateTime = 0f;
        }

        updateFacingFromVelocity(unit);
    }

    public void triggerShoot(boolean facingRight) {
        this.facingRight = !facingRight;
        mode = Mode.SHOOT;
        stateTime = 0f;
    }

    public void reset() {
        mode = Mode.IDLE;
        stateTime = 0f;
        facingRight = false;
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

    private boolean isWalking(BattleUnit unit) {
        return Math.abs(unit.getBody().getLinearVelocity().x) > WALK_VELOCITY_EPSILON;
    }

    private void updateFacingFromVelocity(BattleUnit unit) {
        float velocityX = unit.getBody().getLinearVelocity().x;

        if (Math.abs(velocityX) > WALK_VELOCITY_EPSILON) {
            facingRight = !(velocityX > 0f);
        }
    }
}
