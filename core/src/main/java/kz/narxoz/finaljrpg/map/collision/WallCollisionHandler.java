package kz.narxoz.finaljrpg.map.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Objects;

import static kz.narxoz.finaljrpg.Constants.*;
import static kz.narxoz.finaljrpg.Constants.ENEMY;

public class WallCollisionHandler extends AbstractCollisionHandler {
    @Override
    protected boolean canHandle(MapObject object) {
        return Objects.nonNull(object.getName()) && object.getName().equals("wall");
    }

    @Override
    protected void createCollision(MapObject object, World world) {
        Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
            (rectangle.getX() + rectangle.getWidth() / 2f) / PPM,
            (rectangle.getY() + rectangle.getHeight() / 2f) / PPM
        );

        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (rectangle.getWidth() / 2f) / PPM,
            (rectangle.getHeight() / 2f) / PPM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.filter.categoryBits = TERRAIN;
        fixtureDef.filter.maskBits = PLAYER | ENEMY;

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}
