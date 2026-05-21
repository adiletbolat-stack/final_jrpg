package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static kz.narxoz.finaljrpg.Constants.PPM;

public class MapSpawnPoints {
    private static final Vector2 DEFAULT_PLAYER_SPAWN = new Vector2(0.7f, 1.6f);
    private static final Vector2 DEFAULT_ENEMY_SPAWN = new Vector2(15.5f, 1.3f);

    private final Vector2 playerSpawn;
    private final Vector2 enemySpawn;

    public MapSpawnPoints(TiledMap map) {
        playerSpawn = findSpawn(map, "players", DEFAULT_PLAYER_SPAWN);
        enemySpawn = findSpawn(map, "enemies", DEFAULT_ENEMY_SPAWN);
    }

    public Vector2 getPlayerSpawn() {
        return new Vector2(playerSpawn);
    }

    public Vector2 getEnemySpawn() {
        return new Vector2(enemySpawn);
    }

    private Vector2 findSpawn(TiledMap map, String name, Vector2 fallback) {
        MapLayer layer = map.getLayers().get("spawns");

        if (layer == null) {
            return new Vector2(fallback);
        }

        for (MapObject object : layer.getObjects()) {
            if (!name.equals(object.getName()) || !(object instanceof RectangleMapObject rectangleObject)) {
                continue;
            }

            Rectangle rectangle = rectangleObject.getRectangle();
            return new Vector2(
                (rectangle.x + rectangle.width / 2f) / PPM,
                (rectangle.y + rectangle.height / 2f) / PPM
            );
        }

        return new Vector2(fallback);
    }
}
