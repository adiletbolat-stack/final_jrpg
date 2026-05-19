package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kz.narxoz.finaljrpg.Main;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kz.narxoz.finaljrpg.ui.UIMenu;

public class MenuScreen implements Screen {

    private UIMenu menu;

    @Override
    public void show() {
        menu = new UIMenu();
    }

    @Override
    public void render(float delta) {
        menu.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        menu.getStage().getViewport().update(width, height, true);
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
        menu.dispose();
    }
}
