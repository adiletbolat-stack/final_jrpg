package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Game;
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
import kz.narxoz.finaljrpg.command.CommandList;
import kz.narxoz.finaljrpg.ui.UIElement;
import kz.narxoz.finaljrpg.ui.UISettings;

public class SettingsScreen implements Screen {

    private UIElement settings;

    @Override
    public void show() {
        settings = new UISettings();
    }

    @Override
    public void render(float delta) {
        settings.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        settings.getStage().getViewport().update(width, height, true);
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
        settings.dispose();
    }
}
