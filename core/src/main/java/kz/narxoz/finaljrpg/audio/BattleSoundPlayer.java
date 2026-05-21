package kz.narxoz.finaljrpg.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class BattleSoundPlayer {

    private static final float DEFAULT_VOLUME = 0.55f;
    private static final Map<String, Sound> sounds = new HashMap<>();

    private BattleSoundPlayer() {
    }

    public static void play(String soundPath) {
        if (soundPath == null || soundPath.isBlank()) {
            return;
        }

        Sound sound = getSound(soundPath);

        if (sound != null) {
            sound.play(DEFAULT_VOLUME);
        }
    }

    public static void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }

        sounds.clear();
    }

    private static Sound getSound(String soundPath) {
        if (sounds.containsKey(soundPath)) {
            return sounds.get(soundPath);
        }

        FileHandle file = Gdx.files.internal(soundPath);

        if (!file.exists()) {
            return null;
        }

        Sound sound = Gdx.audio.newSound(file);
        sounds.put(soundPath, sound);

        return sound;
    }
}
