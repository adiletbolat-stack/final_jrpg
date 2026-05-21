package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import kz.narxoz.finaljrpg.battle.BattleSession;
import kz.narxoz.finaljrpg.battle.MapSpawnPoints;
import kz.narxoz.finaljrpg.command.CommandList;
import kz.narxoz.finaljrpg.map.MapCollision;

import static kz.narxoz.finaljrpg.Constants.*;

public class GameScreen implements Screen {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont hudFont;
    private GlyphLayout hudLayout;
    private Matrix4 hudProjection;
    private Texture background;

    private OrthographicCamera camera;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private BattleSession battleSession;
    private boolean initialized;

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);

        if (initialized) {
            return;
        }

        initialized = true;
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.setToOrtho(false, (SCREEN_WIDTH / 4f) / PPM, (SCREEN_HEIGHT / 4f) / PPM);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        hudFont = new BitmapFont();
        hudLayout = new GlyphLayout();
        hudProjection = new Matrix4();
        background = new Texture("gameBackground.png");

        map = new TmxMapLoader().load("map/map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / PPM);

        new MapCollision(world, map);
        battleSession = new BattleSession(world, new MapSpawnPoints(map));
        battleSession.addVictoryObserver(event ->
            Gdx.app.postRunnable(() -> CommandList.getInstance().toVictory(event))
        );

    }

    @Override
    public void render(float delta) {
        updateCamera();
        camera.update();

        battleSession.update(delta, camera);
        world.step(1 / 60f, 6, 2);

        updateCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        mapRenderer.setView(camera);

        batch.begin();
        batch.draw(background, 0, 0, MAP_WIDTH / PPM, MAP_HEIGHT / PPM);
        batch.end();

        mapRenderer.render();

        battleSession.render(shapeRenderer);

        debugRenderer.render(world, camera.combined);
        renderHud();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            CommandList.getInstance().toPause(this);
        }

    }

    private void updateCamera() {
        float mapWidth = MAP_WIDTH / PPM;
        float mapHeight = MAP_HEIGHT / PPM;
        float requestedZoom = Gdx.input.isKeyPressed(Input.Keys.Z) ? 4f : 1f;
        float maxZoom = Math.min(mapWidth / camera.viewportWidth, mapHeight / camera.viewportHeight);

        camera.zoom = Math.min(requestedZoom, maxZoom);

        float halfViewportWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfViewportHeight = camera.viewportHeight * camera.zoom / 2f;
        Vector2 cameraTarget = battleSession.getCameraTarget();
        float cameraX = cameraTarget.x;
        float cameraY = cameraTarget.y;

        if (halfViewportWidth * 2f >= mapWidth) {
            cameraX = mapWidth / 2f;
        } else {
            cameraX = Math.max(halfViewportWidth, Math.min(cameraX, mapWidth - halfViewportWidth));
        }

        if (halfViewportHeight * 2f >= mapHeight) {
            cameraY = mapHeight / 2f;
        } else {
            cameraY = Math.max(halfViewportHeight, Math.min(cameraY, mapHeight - halfViewportHeight));
        }

        camera.position.set(cameraX, cameraY, 10);
    }

    private void renderHud() {
        String timeText = formatTime(battleSession.getBattleTime());
        hudLayout.setText(hudFont, timeText);
        hudProjection.setToOrtho2D(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(hudProjection);

        batch.begin();
        hudFont.draw(
            batch,
            hudLayout,
            (Gdx.graphics.getWidth() - hudLayout.width) / 2f,
            Gdx.graphics.getHeight() - 20f
        );
        batch.end();
    }

    private String formatTime(float seconds) {
        int minutes = (int) (seconds / 60f);
        float remainingSeconds = seconds - minutes * 60f;
        return String.format(java.util.Locale.US, "%02d:%05.2f", minutes, remainingSeconds);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (!initialized) {
            return;
        }

        world.dispose();
        debugRenderer.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        hudFont.dispose();
        background.dispose();
        mapRenderer.dispose();
        map.dispose();
        initialized = false;
    }
}
