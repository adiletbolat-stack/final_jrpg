package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.narxoz.finaljrpg.Main;

public class MenuScreen implements Screen {

    private final Main game;

    private Stage stage;
    private Skin skin;

    private Texture background;

    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        background = new Texture("menuBackground.png");

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        TextButton playButton = new TextButton("PLAY", skin);
        TextButton exitButton = new TextButton("EXIT", skin);

        table.add(playButton).width(300).height(80).pad(20);
        table.row();
        table.add(exitButton).width(300).height(80).pad(20);

        playButton.addListener(event -> {
            game.setScreen(new GameScreen(game));
            dispose();
            return true;
        });

        exitButton.addListener(event -> {
            Gdx.app.exit();
            return true;
        });
    }

    @Override
    public void render(float delta) {

        stage.getBatch().begin();

        stage.getBatch().draw(
            background,
            0,
            0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}