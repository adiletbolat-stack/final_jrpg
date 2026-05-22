package kz.narxoz.finaljrpg.battle;

import java.util.Map;

public class SpriteScales {
    public static final Map<String, ObjectScale> SCALES = Map.of(
        "railgun", new ObjectScale(3.5f, 0f, 0.5f),
        "heavy", new ObjectScale(2.0f, 0f, 0f)
    );

    public static ObjectScale getScale(String spriteKey) {
        return SCALES.getOrDefault(spriteKey, defaultScale(spriteKey));
    }
    public static ObjectScale defaultScale(String spriteKey) {
        return new ObjectScale(1f, 0f, 0f);
    }
}
