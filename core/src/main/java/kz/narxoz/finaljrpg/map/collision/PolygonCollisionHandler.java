package kz.narxoz.finaljrpg.map.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static kz.narxoz.finaljrpg.Constants.*;

public class PolygonCollisionHandler extends AbstractCollisionHandler {

    @Override
    protected boolean canHandle(MapObject object) {
        return object instanceof PolygonMapObject;
    }

    @Override
    protected void createCollision(MapObject object, World world) {
        Polygon polygon = ((PolygonMapObject) object).getPolygon();
        float[] vertices = polygon.getTransformedVertices();
        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            worldVertices[i] = vertices[i] / PPM;
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.filter.categoryBits = PLATFORM;
        fixtureDef.filter.maskBits = PLAYER | ENEMY;

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}
