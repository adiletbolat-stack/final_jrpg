package kz.narxoz.finaljrpg.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import kz.narxoz.finaljrpg.map.collision.CollisionHandler;
import kz.narxoz.finaljrpg.map.collision.PolygonCollisionHandler;
import kz.narxoz.finaljrpg.map.collision.RectangleCollisionHandler;
import kz.narxoz.finaljrpg.map.collision.WallCollisionHandler;

import java.util.Objects;

public class MapCollision {

    private static final String COLLISION_LAYER = "collisions";

    private final CollisionHandler collisionHandler;

    public MapCollision(World world, TiledMap map) {
        collisionHandler = buildCollisionHandler();
        MapLayer collision = map.getLayers().get(COLLISION_LAYER);

        if (Objects.isNull(collision)) throw new IllegalStateException("Map does not have any collisions");

        for (MapObject object : collision.getObjects()) {
            collisionHandler.handle(object, world);
        }
    }

    private CollisionHandler buildCollisionHandler() {
        CollisionHandler rectangleHandler = new RectangleCollisionHandler();
        CollisionHandler wallHandler = new WallCollisionHandler();
        CollisionHandler polyHandler = new PolygonCollisionHandler();
        wallHandler.setNext(polyHandler);
        rectangleHandler.setNext(wallHandler);

        return rectangleHandler;
    }
}
