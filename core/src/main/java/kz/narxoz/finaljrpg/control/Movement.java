package kz.narxoz.finaljrpg.control;

import kz.narxoz.finaljrpg.unit.Unit;

public interface Movement {
    void move(Unit unit, float delta);
}
