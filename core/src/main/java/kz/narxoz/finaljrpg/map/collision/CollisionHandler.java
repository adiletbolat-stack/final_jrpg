package kz.narxoz.finaljrpg.map.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;

public interface CollisionHandler {

    CollisionHandler setNext(CollisionHandler next);

    void handle(MapObject object, World world);
}
