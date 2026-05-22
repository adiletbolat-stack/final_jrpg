package kz.narxoz.finaljrpg.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import kz.narxoz.finaljrpg.battle.behavior.PlayerReplayBehavior;
import kz.narxoz.finaljrpg.battle.event.VictoryEvent;
import kz.narxoz.finaljrpg.battle.event.VictoryObserver;
import kz.narxoz.finaljrpg.battle.event.VictorySubject;
import kz.narxoz.finaljrpg.battle.render.CharacterAnimationSet;
import kz.narxoz.finaljrpg.battle.render.CharacterAnimationState;
import kz.narxoz.finaljrpg.battle.render.EnemyAnimationSet;
import kz.narxoz.finaljrpg.battle.render.EnemyAnimationState;
import kz.narxoz.finaljrpg.battle.render.ProjectileSpriteFlyweightFactory;
import kz.narxoz.finaljrpg.battle.unit.PlayerCharacter;
import kz.narxoz.finaljrpg.command.battle.ActivatePlayerSkillCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static kz.narxoz.finaljrpg.Constants.TERRAIN;
import static kz.narxoz.finaljrpg.Constants.MAP_HEIGHT;
import static kz.narxoz.finaljrpg.Constants.MAP_WIDTH;
import static kz.narxoz.finaljrpg.Constants.PPM;

public class BattleSession implements VictorySubject {
    private static final int PLAYER_COUNT = 3;
    private static final float PROJECTILE_SPEED = 4f;
    private static final float PROJECTILE_DRAW_SIZE = 8f / PPM;
    private static final float TELEPORT_PROJECTILE_SPEED = 12f;
    private static final float PROJECTILE_SPAWN_PADDING = 0.05f;
    private static final float PLAYER_THREE_HIGH_JUMP_IMPULSE = 0.35f;
    private static final float PLAYER_THREE_SLOW_FALL_SPEED = -0.675f;
    private static final float PLAYER_THREE_AIRBORNE_FUEL_USAGE = 2f;
    private static final int BASE_SCORE = 10000;
    private static final BattleUnitType[][] ENEMY_WAVES = {
        {BattleUnitType.NORMAL, BattleUnitType.HEAVY, BattleUnitType.FLYING},
        {BattleUnitType.NORMAL, BattleUnitType.NORMAL, BattleUnitType.HEAVY, BattleUnitType.FLYING},
        {BattleUnitType.HEAVY, BattleUnitType.FLYING, BattleUnitType.NORMAL, BattleUnitType.HEAVY, BattleUnitType.FLYING}
    };

    private final World world;
    private final BattleUnitFactory factory;
    private final Vector2 enemySpawn;
    private final BattleTimeline timeline = new BattleTimeline(PLAYER_COUNT);
    private final List<BattleUnit> players = new ArrayList<>();
    private final List<BattleUnit> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<VictoryObserver> victoryObservers = new ArrayList<>();
    private final Map<BattleUnit, CharacterAnimationState> playerAnimationStates = new IdentityHashMap<>();
    private final Map<String, CharacterAnimationSet> playerAnimationSets = new HashMap<>();
    private final Map<BattleUnit, EnemyAnimationState> enemyAnimationStates = new IdentityHashMap<>();
    private final Map<String, EnemyAnimationSet> enemyAnimationSets = new HashMap<>();
    private final ProjectileSpriteFlyweightFactory projectileSpriteFactory = new ProjectileSpriteFlyweightFactory();

    private int activePlayerIndex;
    private int currentWaveIndex;
    private int frame;
    private float battleTime;
    private boolean victoryNotified;
    private boolean previousShootPressed;
    private BattleInput activeInput = BattleInput.EMPTY;
    private final Vector2 mouseWorldPosition = new Vector2();

    public BattleSession(World world, MapSpawnPoints spawnPoints) {
        this.world = world;
        this.factory = new BattleUnitFactory(world);

        Vector2 playerSpawn = spawnPoints.getPlayerSpawn();
        this.enemySpawn = spawnPoints.getEnemySpawn();

        for (int i = 0; i < PLAYER_COUNT; i++) {
            players.add(factory.createPlayer(i, playerSpawn));
        }

        spawnWave(0);

        for (BattleUnit player : players) {
            PlayerCharacter character = (PlayerCharacter) player;
            playerAnimationStates.put(player, new CharacterAnimationState());
            playerAnimationSets.computeIfAbsent(character.getSpriteKey(), CharacterAnimationSet::new);
        }
    }

    public void update(float delta, OrthographicCamera camera) {
        if (victoryNotified) {
            return;
        }

        updateMouseWorldPosition(camera);
        int selectedPlayer = getSelectedPlayerIndex();

        if (selectedPlayer != activePlayerIndex) {
            switchActivePlayer(selectedPlayer);
        }

        activeInput = readCurrentInput();

        for (BattleUnit player : players) {
            player.update(this, delta);
        }

        for (BattleUnit enemy : enemies) {
            enemy.update(this, delta);
        }

        updatePlayerAnimations(delta);
        updateEnemyAnimations(delta);
        updateProjectiles(delta);
        battleTime += delta;
        checkVictory();
        frame++;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.begin();

        for (BattleUnit enemy : enemies) {
            drawEnemySprite(batch, enemy);
        }

        for (BattleUnit player : players) {
            drawPlayerSprite(batch, player);
        }

        for (Projectile projectile : projectiles) {
            drawProjectileSprite(batch, projectile);
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (BattleUnit unit : getAllUnits()) {
            drawUnitBars(shapeRenderer, unit);
        }

        shapeRenderer.end();
    }

    public BattleInput getInputForPlayer(int playerIndex) {
        if (playerIndex == activePlayerIndex) {
            timeline.recordInput(playerIndex, frame, activeInput);
            return activeInput;
        }

        return timeline.getInput(playerIndex, frame);
    }

    public void applyPlayerInput(BattleUnit unit, BattleInput input, float delta) {
        if (unit.isDead()) {
            stopHorizontal(unit);
            return;
        }

        float direction = 0f;

        if (input.left()) {
            direction -= 1f;
        }

        if (input.right()) {
            direction += 1f;
        }

        if (direction == 0f) {
            stopHorizontal(unit);
        } else {
            moveHorizontally(unit, direction);
        }

        if (input.jump() && unit.getType().getJumpImpulse() > 0f && isGrounded(unit.getBody())) {
            Body body = unit.getBody();
            body.applyLinearImpulse(0f, unit.getType().getJumpImpulse(), body.getWorldCenter().x, body.getWorldCenter().y, true);
            unit.playJumpSound();
            triggerJumpAnimation(unit);
        }

        if (isPlayerThree(unit)) {
            applyPlayerThreeSlowFall(unit, delta);
        }

        if (unit.getShootingMode() != null) {
            unit.getShootingMode().shoot(this, unit, input, delta);
        }

        if (input.skill()) {
            new ActivatePlayerSkillCommand(this, unit, input.aimPoint()).execute();
        }
    }

    public void moveHorizontally(BattleUnit unit, float direction) {
        unit.getMovement().moveHorizontally(unit, direction);
    }

    public void stopHorizontal(BattleUnit unit) {
        unit.getMovement().stopHorizontal(unit);
    }

    public void moveFlyingToward(BattleUnit unit, float targetY, float delta) {
        unit.getMovement().moveVerticallyToward(unit, targetY, delta);
    }

    public BattleUnit findNearestOpponent(BattleUnit unit) {
        List<BattleUnit> opponents = unit.getTeam() == Team.PLAYER ? enemies : players;
        BattleUnit nearest = null;
        float nearestDistance = Float.MAX_VALUE;

        for (BattleUnit opponent : opponents) {
            if (opponent.isDead()) {
                continue;
            }

            float distance = unit.getPosition().dst2(opponent.getPosition());

            if (distance < nearestDistance) {
                nearest = opponent;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    public boolean isTouchingOpponent(BattleUnit unit) {
        for (Contact contact : world.getContactList()) {
            if (!contact.isTouching()) {
                continue;
            }

            Body bodyA = contact.getFixtureA().getBody();
            Body bodyB = contact.getFixtureB().getBody();

            if (bodyA == unit.getBody() && isAliveOpponent(unit, bodyB)) {
                return true;
            }

            if (bodyB == unit.getBody() && isAliveOpponent(unit, bodyA)) {
                return true;
            }
        }

        return false;
    }

    public void shootAt(BattleUnit shooter, Vector2 targetPosition) {
        shootProjectile(shooter, targetPosition, PROJECTILE_SPEED, shooter.getType().getDamage());
        shooter.resetAttackTimer();
        shooter.playShootSound();
    }

    public void shootTeleportProjectile(BattleUnit shooter, Vector2 targetPosition) {
        shootProjectile(shooter, targetPosition, TELEPORT_PROJECTILE_SPEED, 0f, this::teleportOwnerToProjectile, true);
        shooter.playShootSound();
    }

    public void shootProjectile(BattleUnit shooter, Vector2 targetPosition, float speed, float damage) {
        shootProjectile(shooter, targetPosition, speed, damage, null);
    }

    public void shootProjectileRotated(BattleUnit shooter, Vector2 targetPosition, float speed, float damage, float angleOffsetDegrees) {
        shootProjectile(shooter, targetPosition, speed, damage, null, false, angleOffsetDegrees);
    }

    public void shootProjectile(
        BattleUnit shooter,
        Vector2 targetPosition,
        float speed,
        float damage,
        ProjectileHitEffect hitEffect
    ) {
        shootProjectile(shooter, targetPosition, speed, damage, hitEffect, false, 0f);
    }

    private void shootProjectile(
        BattleUnit shooter,
        Vector2 targetPosition,
        float speed,
        float damage,
        ProjectileHitEffect hitEffect,
        boolean collidesWithTerrain
    ) {
        shootProjectile(shooter, targetPosition, speed, damage, hitEffect, collidesWithTerrain, 0f);
    }

    private void shootProjectile(
        BattleUnit shooter,
        Vector2 targetPosition,
        float speed,
        float damage,
        ProjectileHitEffect hitEffect,
        boolean collidesWithTerrain,
        float angleOffsetDegrees
    ) {
        Vector2 direction = new Vector2(targetPosition).sub(shooter.getPosition());

        if (!isFinite(direction)) {
            return;
        }

        if (direction.isZero(0.001f)) {
            direction.set(shooter.getTeam() == Team.PLAYER ? 1f : -1f, 0f);
        }

        direction.nor();
        direction.rotateDeg(angleOffsetDegrees);
        Vector2 spawnPosition = new Vector2(shooter.getPosition())
            .mulAdd(direction, getProjectileSpawnDistance(shooter, direction));
        Vector2 velocity = new Vector2(direction).scl(speed);

        projectiles.add(new Projectile(
            shooter.getTeam(),
            shooter,
            spawnPosition,
            velocity,
            damage,
            hitEffect,
            collidesWithTerrain,
            projectileSpriteFactory.getForShooter(shooter)
        ));
        triggerShootAnimation(shooter, isFacingRightFromAim(direction));
    }

    private float getProjectileSpawnDistance(BattleUnit shooter, Vector2 direction) {
        float halfWidth = shooter.getWidth() / 2f;
        float halfHeight = shooter.getHeight() / 2f;
        float halfExtentInShotDirection = Math.abs(direction.x) * halfWidth + Math.abs(direction.y) * halfHeight;
        return halfExtentInShotDirection + PROJECTILE_SPAWN_PADDING;
    }

    public Vector2 getCameraTarget() {
        return players.get(activePlayerIndex).getPosition();
    }

    public float getBattleTime() {
        return battleTime;
    }

    public float getPlayerSkillCooldown(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= players.size()) {
            return 0f;
        }

        return players.get(playerIndex).getSkillCooldownTimer();
    }

    @Override
    public void addVictoryObserver(VictoryObserver observer) {
        victoryObservers.add(observer);
    }

    @Override
    public void removeVictoryObserver(VictoryObserver observer) {
        victoryObservers.remove(observer);
    }

    private int getSelectedPlayerIndex() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            return 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            return 1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            return 2;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            return (activePlayerIndex + 1) % PLAYER_COUNT;
        }

        return activePlayerIndex;
    }

    private void switchActivePlayer(int nextPlayerIndex) {
        activePlayerIndex = nextPlayerIndex;
        frame = 0;
        battleTime = 0f;
        victoryNotified = false;
        currentWaveIndex = 0;
        projectiles.clear();

        for (int i = 0; i < players.size(); i++) {
            BattleUnit player = players.get(i);
            player.reset();
            player.setBehavior(new PlayerReplayBehavior(i));
        }

        clearEnemies();
        spawnWave(currentWaveIndex);

        for (CharacterAnimationState state : playerAnimationStates.values()) {
            state.reset();
        }
    }

    public void activatePlayerThreeHighJump(BattleUnit unit, float maxFuel) {
        unit.fillFlightFuel(maxFuel);
        Body body = unit.getBody();
        body.setAwake(true);
        body.setLinearVelocity(body.getLinearVelocity().x, Math.max(0f, body.getLinearVelocity().y));
        body.applyLinearImpulse(0f, PLAYER_THREE_HIGH_JUMP_IMPULSE, body.getWorldCenter().x, body.getWorldCenter().y, true);
        unit.playJumpSound();
    }

    private void applyPlayerThreeSlowFall(BattleUnit unit, float delta) {
        if (unit.getFlightFuel() <= 0f || isGrounded(unit.getBody())) {
            return;
        }

        Body body = unit.getBody();
        float verticalVelocity = body.getLinearVelocity().y;

        if (verticalVelocity < PLAYER_THREE_SLOW_FALL_SPEED) {
            body.setLinearVelocity(body.getLinearVelocity().x, PLAYER_THREE_SLOW_FALL_SPEED);
        }

        unit.useFlightFuel(PLAYER_THREE_AIRBORNE_FUEL_USAGE * delta);
    }

    private BattleInput readCurrentInput() {
        boolean shootPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean shootReleased = previousShootPressed && !shootPressed;
        boolean jumpPressed = Gdx.input.isKeyPressed(Input.Keys.W);
        previousShootPressed = shootPressed;

        return new BattleInput(
            Gdx.input.isKeyPressed(Input.Keys.A),
            Gdx.input.isKeyPressed(Input.Keys.D),
            Gdx.input.isKeyJustPressed(Input.Keys.W),
            jumpPressed,
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT),
            shootPressed,
            shootReleased,
            Gdx.input.isKeyJustPressed(Input.Keys.F),
            mouseWorldPosition
        );
    }

    private void updateMouseWorldPosition(OrthographicCamera camera) {
        Vector3 cursor = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(cursor);
        mouseWorldPosition.set(cursor.x, cursor.y);
    }

    private void checkVictory() {
        for (BattleUnit enemy : enemies) {
            if (!enemy.isDead()) {
                return;
            }
        }

        if (currentWaveIndex < ENEMY_WAVES.length - 1) {
            currentWaveIndex++;
            projectiles.clear();
            clearEnemies();
            spawnWave(currentWaveIndex);
            return;
        }

        victoryNotified = true;
        VictoryEvent event = new VictoryEvent(battleTime, calculateScore());

        for (VictoryObserver observer : new ArrayList<>(victoryObservers)) {
            observer.onVictory(event);
        }
    }

    private void spawnWave(int waveIndex) {
        BattleUnitType[] wave = ENEMY_WAVES[waveIndex];

        for (int i = 0; i < wave.length; i++) {
            BattleUnitType type = wave[i];
            float rallyX = enemySpawn.x - 2.4f - i * 0.55f;
            float rallyY = enemySpawn.y + (type == BattleUnitType.FLYING ? 0.8f : 0f);
            BattleUnit enemy = factory.createEnemy(type, i, enemySpawn, new Vector2(rallyX, rallyY));
            enemies.add(enemy);
            registerEnemyAnimation(enemy);
        }
    }

    private void clearEnemies() {
        for (BattleUnit enemy : enemies) {
            enemyAnimationStates.remove(enemy);
            world.destroyBody(enemy.getBody());
        }

        enemies.clear();
    }

    private int calculateScore() {
        float remainingHealth = 0f;

        for (BattleUnit player : players) {
            remainingHealth += Math.max(0f, player.getHealth());
        }

        int timePenalty = Math.round(battleTime * 100f);
        int healthBonus = Math.round(remainingHealth * 5f);

        return Math.max(0, BASE_SCORE - timePenalty + healthBonus);
    }

    private void updateProjectiles(float delta) {
        Iterator<Projectile> iterator = projectiles.iterator();

        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.update(delta);

            if (!isFinite(projectile.getPosition())) {
                iterator.remove();
                continue;
            }

            hitTerrain(projectile);

            if (!projectile.isAlive()) {
                iterator.remove();
                continue;
            }

            hitOpponents(projectile);

            if (!projectile.isAlive() || isOutOfWorld(projectile)) {
                iterator.remove();
            }
        }
    }

    private void hitTerrain(Projectile projectile) {
        if (!projectile.isCollidesWithTerrain()) {
            return;
        }

        TerrainRayCastCallback callback = new TerrainRayCastCallback();
        world.rayCast(callback, projectile.getPreviousPosition(), projectile.getPosition());

        if (!callback.hitTerrain) {
            return;
        }

        projectile.getPosition().set(callback.hitPoint);
        projectile.hit(this, null);
    }

    private void hitOpponents(Projectile projectile) {
        List<BattleUnit> targets = projectile.getTeam() == Team.PLAYER ? enemies : players;

        for (BattleUnit target : targets) {
            if (target.isDead()) {
                continue;
            }

            Vector2 position = projectile.getPosition();
            Vector2 targetPosition = target.getPosition();
            boolean insideX = Math.abs(position.x - targetPosition.x) <= target.getWidth() / 2f;
            boolean insideY = Math.abs(position.y - targetPosition.y) <= target.getHeight() / 2f;

            if (insideX && insideY) {
                projectile.hit(this, target);
                return;
            }
        }
    }

    private void teleportOwnerToProjectile(BattleSession session, Projectile projectile, BattleUnit target) {
        BattleUnit owner = projectile.getOwner();

        if (owner == null || owner.isDead()) {
            return;
        }

        owner.getBody().setTransform(projectile.getPosition(), 0f);
        owner.getBody().setLinearVelocity(0f, 0f);
        owner.getBody().setAngularVelocity(0f);
        owner.getBody().setAwake(true);
    }

    private boolean isOutOfWorld(Projectile projectile) {
        Vector2 position = projectile.getPosition();
        return position.x < 0f || position.y < 0f || position.x > MAP_WIDTH / PPM || position.y > MAP_HEIGHT / PPM;
    }

    private boolean isFinite(Vector2 vector) {
        return Float.isFinite(vector.x) && Float.isFinite(vector.y);
    }

    private boolean isGrounded(Body body) {
        for (Contact contact : world.getContactList()) {
            if (!contact.isTouching()) {
                continue;
            }

            boolean isFixtureA = contact.getFixtureA().getBody() == body;
            boolean isFixtureB = contact.getFixtureB().getBody() == body;

            if (!isFixtureA && !isFixtureB) {
                continue;
            }

            WorldManifold manifold = contact.getWorldManifold();
            float normalY = manifold.getNormal().y;

            if (isFixtureA && normalY < -0.5f) {
                return true;
            }

            if (isFixtureB && normalY > 0.5f) {
                return true;
            }
        }

        return false;
    }

    private void updatePlayerAnimations(float delta) {
        for (BattleUnit player : players) {
            PlayerCharacter character = (PlayerCharacter) player;
            CharacterAnimationState state = playerAnimationStates.get(player);
            CharacterAnimationSet animationSet = playerAnimationSets.get(character.getSpriteKey());
            state.update(player, animationSet, delta, isGrounded(player.getBody()));
        }
    }

    private void updateEnemyAnimations(float delta) {
        for (BattleUnit enemy : enemies) {
            EnemyAnimationState state = enemyAnimationStates.get(enemy);
            EnemyAnimationSet animationSet = enemyAnimationSets.get(getEnemySpriteKey(enemy));

            if (state != null && animationSet != null) {
                state.update(enemy, animationSet, delta);
            }
        }
    }

    private void triggerJumpAnimation(BattleUnit unit) {
        CharacterAnimationState state = playerAnimationStates.get(unit);

        if (state != null) {
            state.triggerJump();
        }
    }

    private void triggerShootAnimation(BattleUnit unit, boolean facingRight) {
        CharacterAnimationState state = playerAnimationStates.get(unit);

        if (state != null) {
            state.triggerShoot(facingRight);
            return;
        }

        EnemyAnimationState enemyState = enemyAnimationStates.get(unit);

        if (enemyState != null) {
            enemyState.triggerShoot(facingRight);
        }
    }

    private void registerEnemyAnimation(BattleUnit enemy) {
        String spriteKey = getEnemySpriteKey(enemy);
        enemyAnimationStates.put(enemy, new EnemyAnimationState());
        enemyAnimationSets.computeIfAbsent(spriteKey, EnemyAnimationSet::new);
    }

    private boolean isAliveOpponent(BattleUnit unit, Body body) {
        BattleUnit other = findUnitByBody(body);
        return other != null && !other.isDead() && other.getTeam() != unit.getTeam();
    }

    private boolean isPlayerOne(BattleUnit unit) {
        return !players.isEmpty() && players.get(0) == unit;
    }

    private boolean isPlayerThree(BattleUnit unit) {
        return players.size() > 2 && players.get(2) == unit;
    }

    private String getEnemySpriteKey(BattleUnit unit) {
        return switch (unit.getType()) {
            case HEAVY -> "heavy";
            case FLYING -> "flying";
            case NORMAL -> "basic";
        };
    }

    private BattleUnit findUnitByBody(Body body) {
        for (BattleUnit unit : getAllUnits()) {
            if (unit.getBody() == body) {
                return unit;
            }
        }

        return null;
    }

    private List<BattleUnit> getAllUnits() {
        List<BattleUnit> units = new ArrayList<>(players.size() + enemies.size());
        units.addAll(players);
        units.addAll(enemies);
        return units;
    }

    private void drawPlayerSprite(SpriteBatch batch, BattleUnit unit) {
        if (unit.isDead()) {
            return;
        }

        PlayerCharacter character = (PlayerCharacter) unit;
        CharacterAnimationState state = playerAnimationStates.get(unit);
        CharacterAnimationSet animationSet = playerAnimationSets.get(character.getSpriteKey());
        TextureRegion frame = animationSet.getFrame(state.getMode(), state.getStateTime());
        Vector2 position = unit.getPosition();
        float drawWidth = frame.getRegionWidth() / PPM;
        float drawHeight = frame.getRegionHeight() / PPM;
        float x = position.x - drawWidth / 2f;
        float y = position.y - drawHeight / 2f;

        if (state.isFacingRight()) {
            batch.draw(frame, x, y, drawWidth, drawHeight);
        } else {
            batch.draw(frame, x + drawWidth, y, -drawWidth, drawHeight);
        }
    }

    private void drawEnemySprite(SpriteBatch batch, BattleUnit unit) {
        if (unit.isDead()) {
            return;
        }

        EnemyAnimationState state = enemyAnimationStates.get(unit);
        EnemyAnimationSet animationSet = enemyAnimationSets.get(getEnemySpriteKey(unit));
        TextureRegion frame = animationSet.getFrame(state.getMode(), state.getStateTime());
        Vector2 position = unit.getPosition();
        float drawWidth = frame.getRegionWidth() / PPM;
        float drawHeight = frame.getRegionHeight() / PPM;
        float x = position.x - drawWidth / 2f;
        float y = position.y - drawHeight / 2f;

        if (state.isFacingRight()) {
            batch.draw(frame, x, y, drawWidth, drawHeight);
        } else {
            batch.draw(frame, x + drawWidth, y, -drawWidth, drawHeight);
        }
    }

    private void drawProjectileSprite(SpriteBatch batch, Projectile projectile) {
        if (!projectile.isAlive() || projectile.getSprite() == null) {
            return;
        }

        TextureRegion frame = projectile.getSprite().getRegion();
        Vector2 position = projectile.getPosition();
        float x = position.x - PROJECTILE_DRAW_SIZE / 2f;
        float y = position.y - PROJECTILE_DRAW_SIZE / 2f;
        float angle = projectile.getVelocity().angleDeg();

        batch.draw(
            frame,
            x,
            y,
            PROJECTILE_DRAW_SIZE / 2f,
            PROJECTILE_DRAW_SIZE / 2f,
            PROJECTILE_DRAW_SIZE * projectile.getOwner().getScale().spriteScale(),
            PROJECTILE_DRAW_SIZE * projectile.getOwner().getScale().spriteScale(),
            1f,
            1f,
            angle
        );
    }

    private void drawUnitBars(ShapeRenderer shapeRenderer, BattleUnit unit) {
        if (unit.isDead()) {
            return;
        }

        Vector2 position = unit.getPosition();
        float x = position.x - unit.getWidth() / 2f;
        float y = position.y - unit.getHeight() / 2f;

        if (players.indexOf(unit) == activePlayerIndex) {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(x, y + unit.getHeight() + 0.05f, unit.getWidth(), 0.025f);
        }

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x, y + unit.getHeight() + 0.1f, unit.getWidth(), 0.025f);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(x, y + unit.getHeight() + 0.1f, unit.getWidth() * (unit.getHealth() / unit.getMaxHealth()), 0.025f);

        if (players.indexOf(unit) == 0) {
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(x, y + unit.getHeight() + 0.15f, unit.getWidth(), 0.025f);
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rect(x, y + unit.getHeight() + 0.15f, unit.getWidth() * unit.getSkillCharge(), 0.025f);
        }

        if (players.indexOf(unit) == 1 && unit.getShieldHealth() > 0f) {
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(x, y + unit.getHeight() + 0.15f, unit.getWidth(), 0.025f);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(x, y + unit.getHeight() + 0.15f, unit.getWidth() * unit.getShieldRatio(), 0.025f);
        }

        if (players.indexOf(unit) == 2 && unit.getMaxFlightFuel() > 0f) {
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(x, y + unit.getHeight() + 0.15f, unit.getWidth(), 0.025f);
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.rect(x, y + unit.getHeight() + 0.15f, unit.getWidth() * unit.getFlightFuelRatio(), 0.025f);
        }
    }

    private static class TerrainRayCastCallback implements RayCastCallback {
        private final Vector2 hitPoint = new Vector2();
        private boolean hitTerrain;

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if ((fixture.getFilterData().categoryBits & TERRAIN) == 0) {
                return -1f;
            }

            hitTerrain = true;
            hitPoint.set(point);
            return fraction;
        }
    }

    private boolean isFacingRightFromAim(Vector2 direction){
        return direction.x >= 0f;
    }

    public void dispose() {
        for (CharacterAnimationSet animationSet : playerAnimationSets.values()) {
            animationSet.dispose();
        }

        for (EnemyAnimationSet animationSet : enemyAnimationSets.values()) {
            animationSet.dispose();
        }

        projectileSpriteFactory.dispose();
    }
}
