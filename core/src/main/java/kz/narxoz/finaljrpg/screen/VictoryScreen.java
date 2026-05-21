package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.narxoz.finaljrpg.battle.event.VictoryEvent;
import kz.narxoz.finaljrpg.command.CommandList;

import java.util.Locale;

public class VictoryScreen implements Screen {
    private final VictoryEvent victoryEvent;
    private Stage stage;
    private Skin skin;
    private Texture background;

    public VictoryScreen(VictoryEvent victoryEvent) {
        this.victoryEvent = victoryEvent;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        background = new Texture("menuBackground.png");

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        Label titleLabel = new Label("VICTORY", skin);
        Label timeLabel = new Label("TIME: " + formatTime(victoryEvent.getBattleTime()), skin);
        Label scoreLabel = new Label("SCORE: " + victoryEvent.getScore(), skin);
        TextButton menuButton = new TextButton("MENU", skin);

        table.add(titleLabel).pad(20);
        table.row();
        table.add(timeLabel).pad(10);
        table.row();
        table.add(scoreLabel).pad(10);
        table.row();
        table.add(menuButton).width(300).height(80).pad(30);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CommandList.getInstance().toMenu();
            }
        });

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0.2f, 1f);
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

    private String formatTime(float seconds) {
        int minutes = (int) (seconds / 60f);
        float remainingSeconds = seconds - minutes * 60f;
        return String.format(Locale.US, "%02d:%05.2f", minutes, remainingSeconds);
    }
}
