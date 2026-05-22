package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.math.Vector2;

public record BattleInput(
    boolean left,
    boolean right,
    boolean jump,
    boolean jumpHeld,
    boolean shoot,
    boolean shootHeld,
    boolean shootReleased,
    boolean skill,
    Vector2 aimPoint
) {
    public static final BattleInput EMPTY = new BattleInput(false, false, false, false, false, false, false, false, new Vector2());

    public BattleInput(
        boolean left,
        boolean right,
        boolean jump,
        boolean jumpHeld,
        boolean shoot,
        boolean shootHeld,
        boolean shootReleased,
        boolean skill,
        Vector2 aimPoint
    ) {
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.jumpHeld = jumpHeld;
        this.shoot = shoot;
        this.shootHeld = shootHeld;
        this.shootReleased = shootReleased;
        this.skill = skill;
        this.aimPoint = new Vector2(aimPoint);
    }

    @Override
    public Vector2 aimPoint() {
        return new Vector2(aimPoint);
    }
}
