package kz.narxoz.finaljrpg.battle.event;

public interface VictorySubject {
    void addVictoryObserver(VictoryObserver observer);

    void removeVictoryObserver(VictoryObserver observer);
}
