package kz.narxoz.finaljrpg.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kz.narxoz.finaljrpg.command.CommandList;

public class UISettings extends UIElement{

    public UISettings(){
        super();
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
                CommandList.getInstance().fullscreen(fullscreenBox.isChecked());
            }

        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CommandList.getInstance().screenBack();
            }

        });
    }
}
