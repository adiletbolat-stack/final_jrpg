package kz.narxoz.finaljrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kz.narxoz.finaljrpg.command.CommandList;

public class UIPause extends UIElement {

    public UIPause() {

        super();

        TextButton resumeButton = new TextButton("RESUME", skin);

        TextButton settingsButton = new TextButton("SETTINGS", skin);

        TextButton menuButton = new TextButton("MENU", skin);

        TextButton exitButton = new TextButton("EXIT", skin);

        table.add(resumeButton)
            .width(300)
            .height(80)
            .pad(20);

        table.row();

        table.add(settingsButton)
            .width(300)
            .height(80)
            .pad(20);

        table.row();
        table.add(menuButton)
            .width(300)
            .height(80)
            .pad(20);

        table.row();

        table.add(exitButton)
            .width(300)
            .height(80)
            .pad(20);

        resumeButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                CommandList.getInstance().resumeGame();
            }
        });

        settingsButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                CommandList.getInstance().toSettings();
            }
        });

        menuButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                CommandList.getInstance().toMenu();
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
