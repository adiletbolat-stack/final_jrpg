package kz.narxoz.finaljrpg.unit.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import kz.narxoz.finaljrpg.control.Movement;
import kz.narxoz.finaljrpg.control.impl.DefaultMovement;
import kz.narxoz.finaljrpg.unit.Unit;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static kz.narxoz.finaljrpg.Constants.PPM;

@Getter
public class Kubik implements Unit {
    private String name;
    private Body body;
    private Vector2 position;
    private float width, height;
    private Texture texture;
    @Setter
    private Movement movement;

    @Builder
    public Kubik(World world, float startX, float startY, Texture texture,  String name) {
        this.name = name;
        this.texture = texture;
        this.width = texture.getWidth() / PPM;
        this.height = texture.getHeight() / PPM;
        position = new Vector2(startX, startY);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.5f;

        body.createFixture(fixtureDef);

        shape.dispose();

        movement = new DefaultMovement();
    }


    @Override
    public void update(float delta) {
        movement.move(this, delta);
        position.set(body.getPosition());
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x-(width/2), position.y-(height/2), width, height);
    }
}
