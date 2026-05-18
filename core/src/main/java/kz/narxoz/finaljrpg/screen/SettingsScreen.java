package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kz.narxoz.finaljrpg.Main;

public class SettingsScreen implements Screen {

    private final Main game;

    private Stage stage;
    private Skin skin;

    private Texture background;

    public SettingsScreen(Main game) {
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

        Label title = new Label("SETTINGS", skin);

        CheckBox fullscreenBox = new CheckBox(" Fullscreen", skin);

        Slider volumeSlider = new Slider(0, 100, 1, false, skin);

        volumeSlider.setValue(50);

        TextButton backButton = new TextButton("BACK", skin);

        table.add(title).padBottom(50);
        table.row();

        table.add(fullscreenBox).pad(20);
        table.row();

        table.add(volumeSlider)
                .width(300)
                .pad(20);

        table.row();

        table.add(backButton)
                .width(250)
                .height(70)
                .padTop(40);

        fullscreenBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (fullscreenBox.isChecked()) {

                    Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();

                    Gdx.graphics.setFullscreenMode(mode);

                } else {

                    Gdx.graphics.setWindowedMode(1920, 1080);
                }

            }

        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }

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
                Gdx.graphics.getHeight());

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