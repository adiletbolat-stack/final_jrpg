package kz.narxoz.finaljrpg.map;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MapBound {

    public static void createBound(World world, float x, float y, float hx, float hy){
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        groundDef.position.set(x,y);

        Body groundBody = world.createBody(groundDef);

        PolygonShape groundShape = new PolygonShape();

        groundShape.setAsBox(hx, hy);

        groundBody.createFixture(groundShape, 0.0f);

        groundShape.dispose();
    }
}
