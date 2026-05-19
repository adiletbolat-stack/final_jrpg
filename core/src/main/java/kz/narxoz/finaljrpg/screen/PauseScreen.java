package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Screen;
import kz.narxoz.finaljrpg.ui.UIPause;

public class PauseScreen implements Screen {

    private UIPause pauseMenu;

    @Override
    public void show() {
        pauseMenu = new UIPause();
    }

    @Override
    public void render(float delta) {
        pauseMenu.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        pauseMenu.getStage().getViewport().update(width, height, true);
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
        pauseMenu.dispose();
    }
}