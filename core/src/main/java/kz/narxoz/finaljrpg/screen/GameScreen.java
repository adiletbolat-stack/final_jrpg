package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class GameScreen implements Screen {
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body playerBody;
    private Body groundBody;
    private static final Float PPM = 100f;
    private OrthographicCamera camera;

    @Override
    public void show() {
        world = new World(new Vector2(0,-9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(10, 10);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(200/PPM, 300/PPM);

        playerBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(100/PPM, 100/PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.2f;

        playerBody.createFixture(fixtureDef);
        shape.dispose();

        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        groundDef.position.set((Gdx.graphics.getWidth()/2f)/PPM, 50/PPM);

        groundBody = world.createBody(groundDef);

        PolygonShape groundShape = new PolygonShape();

        groundShape.setAsBox((Gdx.graphics.getWidth()/2f)/PPM, 20/PPM);

        groundBody.createFixture(groundShape, 0.0f);

        groundShape.dispose();
    }

    @Override
    public void render(float delta) {
        world.step(1/60f, 6, 2);
        debugRenderer.render(world, camera.combined);
        camera.position.set(playerBody.getPosition().x, playerBody.getPosition().y, 10);
        camera.update();
        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) playerBody.applyLinearImpulse(0, 10f, playerBody.getWorldCenter().x, playerBody.getWorldCenter().y, true);
        if(Gdx.input.isKeyPressed(Input.Keys.D)) playerBody.setLinearVelocity(4f, playerBody.getLinearVelocity().y);
        else if(Gdx.input.isKeyPressed(Input.Keys.A)) playerBody.setLinearVelocity(-4f, playerBody.getLinearVelocity().y);
        else playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }
}
