package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import kz.narxoz.finaljrpg.map.MapBound;
import kz.narxoz.finaljrpg.unit.Unit;
import kz.narxoz.finaljrpg.unit.impl.Kubik;

import static kz.narxoz.finaljrpg.Constants.*;

public class GameScreen implements Screen {
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private Texture background;
    private Texture some;


    private Body groundBody;
    private OrthographicCamera camera;

    private Unit unit;

    @Override
    public void show() {
        world = new World(new Vector2(0,-9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.setToOrtho(false, SCREEN_WIDTH/PPM, SCREEN_HEIGHT/PPM);
        batch = new SpriteBatch();
        background = new Texture("gameBackground.png");

        some = new Texture("check.png");
        unit = Kubik.builder().world(world).name("some").texture(some).startX(1).startY(1).build();

        MapBound.createBound(world, (camera.viewportWidth/2), 0, (SCREEN_WIDTH/2f)/PPM, 1/PPM);
        MapBound.createBound(world, (camera.viewportWidth/2), camera.viewportHeight, (SCREEN_WIDTH/2f)/PPM, 1/PPM);
        MapBound.createBound(world, camera.viewportWidth, camera.viewportHeight/2, 1/PPM, (SCREEN_HEIGHT/2f)/PPM);
        MapBound.createBound(world, 0, camera.viewportHeight/2, 1/PPM, (SCREEN_HEIGHT/2f)/PPM);


    }

    @Override
    public void render(float delta) {
        unit.update(delta);
//        camera.position.set(unit.getPosition().x, unit.getPosition().y, 10);
//        camera.update();

        batch.begin();
        batch.draw(background, 0, 0, SCREEN_WIDTH/PPM, SCREEN_HEIGHT/PPM);
        unit.draw(batch);
        batch.end();


        world.step(1/60f, 6, 2);
//        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);

        if(Gdx.input.isKeyPressed(Input.Keys.Z)) camera.zoom = 100;
        else camera.zoom = 1;

        System.out.println(unit.getPosition());

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
        batch.dispose();
        background.dispose();
        some.dispose();
    }
}
