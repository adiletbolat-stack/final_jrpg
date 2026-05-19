package kz.narxoz.finaljrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kz.narxoz.finaljrpg.command.CommandList;
import kz.narxoz.finaljrpg.screen.GameScreen;
import kz.narxoz.finaljrpg.screen.SettingsScreen;

public class UIMenu extends UIElement {
    public UIMenu(){
        super();
        TextButton playButton = new TextButton("PLAY", skin);
        TextButton settingsButton = new TextButton("SETTINGS", skin);
        TextButton exitButton = new TextButton("EXIT", skin);

        table.add(playButton).width(300).height(80).pad(20);
        table.row();

        table.add(settingsButton).width(300).height(80).pad(20);
        table.row();

        table.add(exitButton).width(300).height(80).pad(20);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CommandList.getInstance().toGame();
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CommandList.getInstance().toSettings();
                dispose();

            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

}
