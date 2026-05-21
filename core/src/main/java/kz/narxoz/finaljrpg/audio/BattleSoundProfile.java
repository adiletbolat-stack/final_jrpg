package kz.narxoz.finaljrpg.audio;

public record BattleSoundProfile(String walkSoundPath, String shootSoundPath, String jumpSoundPath) {

    public static final BattleSoundProfile EMPTY = new BattleSoundProfile(null, null, null);
}
