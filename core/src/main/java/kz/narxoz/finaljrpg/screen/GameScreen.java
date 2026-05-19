package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;



import kz.narxoz.finaljrpg.map.MapCollision;
import kz.narxoz.finaljrpg.unit.Unit;
import kz.narxoz.finaljrpg.unit.impl.Kubik;

import static kz.narxoz.finaljrpg.Constants.*;

public class GameScreen implements Screen {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private Texture background;
    private Texture some;

    private OrthographicCamera camera;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Unit unit;



    @Override
    public void show() {
        world = new World(new Vector2(0,-9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.setToOrtho(false, (SCREEN_WIDTH/4f)/PPM, (SCREEN_HEIGHT/4f)/PPM);
        batch = new SpriteBatch();
        background = new Texture("gameBackground.png");

        map = new TmxMapLoader().load("map/map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f/PPM);

        some = new Texture("check.png");
        unit = Kubik.builder().world(world).name("some").texture(some).startX(1).startY(1).build();

        new MapCollision(world, map);


    }


    @Override
    public void render(float delta) {
        unit.update(delta);
        camera.position.set(unit.getPosition().x, unit.getPosition().y, 10);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        mapRenderer.setView(camera);

        batch.begin();
        batch.draw(background, 0, 0, MAP_WIDTH/PPM, MAP_HEIGHT/PPM);
        batch.end();

        mapRenderer.render();

        batch.begin();
        unit.draw(batch);
        batch.end();


        world.step(1/60f, 6, 2);
        debugRenderer.render(world, camera.combined);

        if(Gdx.input.isKeyPressed(Input.Keys.Z)) camera.zoom = 4;
        else camera.zoom = 1;

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
        mapRenderer.dispose();
        map.dispose();
    }
}
