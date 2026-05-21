package kz.narxoz.finaljrpg.command.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

public class FullscreenCommand {

    private final boolean fullscreen;

    public FullscreenCommand(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void execute() {
        if (fullscreen) {
            Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(mode);
        } else {
            Gdx.graphics.setWindowedMode(1920, 1080);
        }
    }
}
