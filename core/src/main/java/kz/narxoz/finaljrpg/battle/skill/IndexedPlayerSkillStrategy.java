package kz.narxoz.finaljrpg.battle.skill;

public class IndexedPlayerSkillStrategy implements PlayerSkillCreationStrategy {
    @Override
    public PlayerSkill createSkill(int playerIndex) {
        return switch (playerIndex) {
            case 0 -> new PlayerOneSkill();
            case 1 -> new PlayerTwoSkill();
            default -> new PlayerThreeSkill();
        };
    }
}
