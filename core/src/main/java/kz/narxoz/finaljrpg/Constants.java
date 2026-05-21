package kz.narxoz.finaljrpg;

import com.badlogic.gdx.Gdx;

public class Constants {

    public static final float PPM = 100f;

    public static final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static final int SCREEN_HEIGHT = Gdx.graphics.getHeight();

    public static final float MAP_WIDTH = 100 * 16f;
    public static final float MAP_HEIGHT = 15 * 16f;

//  bit values
    public static final short TERRAIN = 0x0001;
    public static final short PLAYER = 0x0002;
    public static final short ENEMY = 0x0004;
}
