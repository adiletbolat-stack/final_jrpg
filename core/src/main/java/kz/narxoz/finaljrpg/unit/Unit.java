package kz.narxoz.finaljrpg.unit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public interface Unit {
    void update(float delta);
    void draw(SpriteBatch batch);
    String getName();
    Body getBody();
    Vector2 getPosition();
    float getWidth();
    float getHeight();
    Texture getTexture();
}
