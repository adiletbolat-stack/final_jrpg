package kz.narxoz.finaljrpg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import lombok.Getter;

public abstract class UIElement {
    @Getter
    protected Stage stage;
    protected Table table;
    protected Skin skin;
    protected Texture background;

    public UIElement(){
        stage = new Stage(new ScreenViewport());
        table = new Table();

        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        background = new Texture("menuBackground.png");

        table.setFillParent(true);

        stage.addActor(table);
    }

    public void render(float delta){
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
    };

    public void dispose(){
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}
