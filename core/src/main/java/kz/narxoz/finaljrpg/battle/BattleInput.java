package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.math.Vector2;

public class BattleInput {
    public static final BattleInput EMPTY = new BattleInput(false, false, false, false, new Vector2());

    private final boolean left;
    private final boolean right;
    private final boolean jump;
    private final boolean shoot;
    private final Vector2 aimPoint;

    public BattleInput(boolean left, boolean right, boolean jump, boolean shoot, Vector2 aimPoint) {
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.shoot = shoot;
        this.aimPoint = new Vector2(aimPoint);
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isJump() {
        return jump;
    }

    public boolean isShoot() {
        return shoot;
    }

    public Vector2 getAimPoint() {
        return new Vector2(aimPoint);
    }
}
