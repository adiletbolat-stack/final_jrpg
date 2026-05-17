package kz.narxoz.finaljrpg.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Objects;

import static kz.narxoz.finaljrpg.Constants.PPM;

public class MapCollision {
    public MapCollision(World world, TiledMap map){
        MapLayer collision = map.getLayers().get("collisions");

        if (Objects.isNull(collision)) throw new IllegalStateException("Map does not have any collisions");

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();

        
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        for (MapObject obj : collision.getObjects()){
            if (obj instanceof RectangleMapObject rectangleMapObject){
                Rectangle rect = rectangleMapObject.getRectangle();


                bodyDef.type = BodyDef.BodyType.StaticBody;

                float centerX = (rect.getX() + rect.getWidth() / 2f) / PPM;
                float centerY = (rect.getY() + rect.getHeight() / 2f) / PPM;
                bodyDef.position.set(centerX, centerY);

                body = world.createBody(bodyDef);

                float halfWidth = (rect.getWidth() / 2f) / PPM;
                float halfHeight = (rect.getHeight() / 2f) / PPM;
                shape.setAsBox(halfWidth, halfHeight);

                fixtureDef.shape = shape;
                fixtureDef.friction = 0f;

                body.createFixture(fixtureDef);
            }

            else if (obj instanceof PolygonMapObject polyObject) {
                Polygon polygon = polyObject.getPolygon();


                float[] vertices = polygon.getTransformedVertices();


                float[] worldVertices = new float[vertices.length];
                for (int i = 0; i < vertices.length; i++) {
                    worldVertices[i] = vertices[i] / PPM;
                }

                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;

                bdef.position.set(0, 0);
                body = world.createBody(bdef);

                shape.set(worldVertices);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                fdef.friction = 0f;
                body.createFixture(fdef);
                shape.dispose();
            }

        }

        shape.dispose();
    }
}
