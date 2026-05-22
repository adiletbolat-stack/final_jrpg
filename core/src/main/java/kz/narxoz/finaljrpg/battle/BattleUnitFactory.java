package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import kz.narxoz.finaljrpg.Constants;
import kz.narxoz.finaljrpg.audio.BattleSoundProfile;
import kz.narxoz.finaljrpg.battle.behavior.EnemyRallyBehavior;
import kz.narxoz.finaljrpg.battle.behavior.PlayerReplayBehavior;
import kz.narxoz.finaljrpg.battle.shooting.ChargedShotMode;
import kz.narxoz.finaljrpg.battle.shooting.FullAutoMode;
import kz.narxoz.finaljrpg.battle.shooting.ShootingMode;
import kz.narxoz.finaljrpg.battle.shooting.SpreadShotMode;
import kz.narxoz.finaljrpg.battle.skill.IndexedPlayerSkillStrategy;
import kz.narxoz.finaljrpg.battle.skill.PlayerSkillCreationStrategy;
import kz.narxoz.finaljrpg.battle.unit.FlyingEnemy;
import kz.narxoz.finaljrpg.battle.unit.HeavyEnemy;
import kz.narxoz.finaljrpg.battle.unit.NormalEnemy;
import kz.narxoz.finaljrpg.battle.unit.PlayerCharacter;

public class BattleUnitFactory {
    private static final BattleSoundProfile[] PLAYER_SOUND_PROFILES = {
        new BattleSoundProfile("audio/player_walk.mp3", "audio/plasma_gun.mp3", "audio/player_jump.mp3"),
        new BattleSoundProfile("audio/player_walk.mp3", "audio/shotgun_shoot.mp3", "audio/player_jump.mp3"),
        new BattleSoundProfile("audio/player_walk.mp3", "audio/machinegun_shoot.mp3", "audio/player_jump.mp3")
    };
    private static final BattleSoundProfile ENEMY_SOUND_PROFILE =
        new BattleSoundProfile(null, "audio/enemy_shoot.mp3", null);
    private static final String[] PLAYER_SPRITES = {"railgun", "shotgun", "gunner"};

    private final World world;
    private final PlayerSkillCreationStrategy skillCreationStrategy;

    public BattleUnitFactory(World world) {
        this(world, new IndexedPlayerSkillStrategy());
    }

    public BattleUnitFactory(World world, PlayerSkillCreationStrategy skillCreationStrategy) {
        this.world = world;
        this.skillCreationStrategy = skillCreationStrategy;
    }

    public BattleUnit createPlayer(int index, Vector2 spawn) {
        Vector2 unitSpawn = new Vector2(spawn.x + index * 0.35f, spawn.y);
        Body body = createBody(Team.PLAYER, BattleUnitType.NORMAL, unitSpawn);
        String spriteKey = PLAYER_SPRITES[index % PLAYER_SPRITES.length];
        ObjectScale scale = SpriteScales.getScale(spriteKey);

        BattleUnit unit = new PlayerCharacter(
            "Player " + (index + 1),
            body,
            unitSpawn,
            new Color(0.25f, 0.65f, 1f, 1f),
            getPlayerSoundProfile(index),
            skillCreationStrategy.createSkill(index),
            getPlayerShootingMode(index),
            spriteKey,
            scale
        );

        unit.setBehavior(new PlayerReplayBehavior(index));
        return unit;
    }

    public BattleUnit createEnemy(BattleUnitType type, int index, Vector2 spawn, Vector2 rallyPoint) {
        Vector2 unitSpawn = new Vector2(spawn.x - index * 0.35f, spawn.y + (type == BattleUnitType.FLYING ? 0.8f : 0f));
        Color color = type == BattleUnitType.HEAVY
            ? new Color(0.75f, 0.2f, 0.15f, 1f)
            : type == BattleUnitType.FLYING
                ? new Color(0.85f, 0.75f, 0.25f, 1f)
                : new Color(0.9f, 0.35f, 0.35f, 1f);
        Body body = createBody(Team.ENEMY, type, unitSpawn);
        String spriteKey = getEnemySpriteKey(type);
        ObjectScale scale = SpriteScales.getScale(spriteKey);
        BattleUnit unit = switch (type) {
            case HEAVY -> new HeavyEnemy("Heavy enemy", body, unitSpawn, rallyPoint, color, ENEMY_SOUND_PROFILE, spriteKey, scale);
            case FLYING -> new FlyingEnemy("Flying enemy", body, unitSpawn, rallyPoint, color, ENEMY_SOUND_PROFILE, spriteKey, scale);
            case NORMAL -> new NormalEnemy("Normal enemy", body, unitSpawn, rallyPoint, color, ENEMY_SOUND_PROFILE, spriteKey, scale);
        };
        unit.setBehavior(new EnemyRallyBehavior());
        return unit;
    }

    private BattleSoundProfile getPlayerSoundProfile(int index) {
        return PLAYER_SOUND_PROFILES[Math.min(index, PLAYER_SOUND_PROFILES.length - 1)];
    }

    private ShootingMode getPlayerShootingMode(int index) {
        return switch (index) {
            case 0 -> new ChargedShotMode();
            case 1 -> new SpreadShotMode();
            default -> new FullAutoMode();
        };
    }

    private String getEnemySpriteKey(BattleUnitType type) {
        return switch (type) {
            case HEAVY -> "heavy";
            case FLYING -> "flying";
            case NORMAL -> "basic";
        };
    }

    private Body createBody(Team team, BattleUnitType type, Vector2 spawn) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.allowSleep = false;
        bodyDef.position.set(spawn);

        Body body = world.createBody(bodyDef);
        body.setGravityScale(type == BattleUnitType.FLYING ? 0f : 1f);
        body.setAwake(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(type.getWidth() / 2f, type.getHeight() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = type == BattleUnitType.HEAVY ? 2f : 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = team == Team.PLAYER ? Constants.PLAYER : Constants.ENEMY;
        fixtureDef.filter.maskBits = Constants.PLATFORM | Constants.TERRAIN;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }
}
