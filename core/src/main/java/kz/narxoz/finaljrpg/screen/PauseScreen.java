package kz.narxoz.finaljrpg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import kz.narxoz.finaljrpg.command.CommandList;
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
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) CommandList.getInstance().resumeGame();
    }

    @Override
    public void resize(int width, int height) {
        pauseMenu.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

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
