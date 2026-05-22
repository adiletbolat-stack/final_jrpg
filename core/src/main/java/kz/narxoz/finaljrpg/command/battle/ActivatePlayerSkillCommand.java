package kz.narxoz.finaljrpg.command.battle;

import com.badlogic.gdx.math.Vector2;
import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.BattleUnit;

public class ActivatePlayerSkillCommand {
    private final BattleSession session;
    private final BattleUnit player;
    private final Vector2 targetPosition;

    public ActivatePlayerSkillCommand(BattleSession session, BattleUnit player, Vector2 targetPosition) {
        this.session = session;
        this.player = player;
        this.targetPosition = new Vector2(targetPosition);
    }

    public void execute() {
        player.activateSkill(session, targetPosition);
    }
}
