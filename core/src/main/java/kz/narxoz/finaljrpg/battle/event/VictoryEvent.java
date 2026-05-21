package kz.narxoz.finaljrpg.battle.event;

public class VictoryEvent {
    private final float battleTime;
    private final int score;

    public VictoryEvent(float battleTime, int score) {
        this.battleTime = battleTime;
        this.score = score;
    }

    public float getBattleTime() {
        return battleTime;
    }

    public int getScore() {
        return score;
    }
}
