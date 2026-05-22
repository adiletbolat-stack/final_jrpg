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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import kz.narxoz.finaljrpg.battle.behavior.EnemyRallyBehavior;
import kz.narxoz.finaljrpg.battle.behavior.PlayerReplayBehavior;
import kz.narxoz.finaljrpg.battle.event.VictoryEvent;
import kz.narxoz.finaljrpg.battle.event.VictoryObserver;
import kz.narxoz.finaljrpg.battle.event.VictorySubject;
import kz.narxoz.finaljrpg.battle.render.CharacterAnimationSet;
import kz.narxoz.finaljrpg.battle.render.CharacterAnimationState;
import kz.narxoz.finaljrpg.battle.unit.PlayerCharacter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static kz.narxoz.finaljrpg.Constants.MAP_HEIGHT;
import static kz.narxoz.finaljrpg.Constants.MAP_WIDTH;
import static kz.narxoz.finaljrpg.Constants.PPM;

public class BattleSession implements VictorySubject {
    private static final int PLAYER_COUNT = 3;
    private static final float PROJECTILE_SPEED = 6f;
    private static final float PROJECTILE_SPAWN_PADDING = 0.07f;
    private static final int BASE_SCORE = 10000;

    private final World world;
    private final BattleTimeline timeline = new BattleTimeline(PLAYER_COUNT);
    private final List<BattleUnit> players = new ArrayList<>();
    private final List<BattleUnit> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<VictoryObserver> victoryObservers = new ArrayList<>();
    private final Map<BattleUnit, CharacterAnimationState> playerAnimationStates = new IdentityHashMap<>();
    private final Map<String, CharacterAnimationSet> playerAnimationSets = new HashMap<>();

    private int activePlayerIndex;
    private int frame;
    private float battleTime;
    private boolean victoryNotified;
    private BattleInput activeInput = BattleInput.EMPTY;
    private final Vector2 mouseWorldPosition = new Vector2();

    public BattleSession(World world, MapSpawnPoints spawnPoints) {
        this.world = world;

        BattleUnitFactory factory = new BattleUnitFactory(world);
        Vector2 playerSpawn = spawnPoints.getPlayerSpawn();
        Vector2 enemySpawn = spawnPoints.getEnemySpawn();

        for (int i = 0; i < PLAYER_COUNT; i++) {
            players.add(factory.createPlayer(i, playerSpawn));
        }

        enemies.add(factory.createEnemy(BattleUnitType.NORMAL, 0, enemySpawn, new Vector2(enemySpawn.x - 3f, enemySpawn.y)));
        enemies.add(factory.createEnemy(BattleUnitType.HEAVY, 1, enemySpawn, new Vector2(enemySpawn.x - 2.4f, enemySpawn.y)));
        enemies.add(factory.createEnemy(BattleUnitType.FLYING, 2, enemySpawn, new Vector2(enemySpawn.x - 3.6f, enemySpawn.y + 0.8f)));

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
        updateProjectiles(delta);
        battleTime += delta;
        checkVictory();
        frame++;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (BattleUnit enemy : enemies) {
            drawEnemyUnit(shapeRenderer, enemy);
        }

        shapeRenderer.end();

        batch.begin();

        for (BattleUnit player : players) {
            drawPlayerSprite(batch, player);
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (BattleUnit unit : getAllUnits()) {
            drawUnitBars(shapeRenderer, unit);
        }

        shapeRenderer.setColor(Color.WHITE);
        for (Projectile projectile : projectiles) {
            if (projectile.isAlive()) {
                shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 0.04f, 10);
            }
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

    public void applyPlayerInput(BattleUnit unit, BattleInput input) {
        if (unit.isDead()) {
            stopHorizontal(unit);
            return;
        }

        float direction = 0f;

        if (input.isLeft()) {
            direction -= 1f;
        }

        if (input.isRight()) {
            direction += 1f;
        }

        if (direction == 0f) {
            stopHorizontal(unit);
        } else {
            moveHorizontally(unit, direction);
        }

        if (input.isJump() && unit.getType().getJumpImpulse() > 0f && isGrounded(unit.getBody())) {
            Body body = unit.getBody();
            body.applyLinearImpulse(0f, unit.getType().getJumpImpulse(), body.getWorldCenter().x, body.getWorldCenter().y, true);
            triggerJumpAnimation(unit);
        }

        if (input.isShoot() && unit.canShoot()) {
            shootAt(unit, input.getAimPoint());
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
        Vector2 direction = new Vector2(targetPosition).sub(shooter.getPosition());

        if (!isFinite(direction)) {
            return;
        }

        if (direction.isZero(0.001f)) {
            direction.set(shooter.getTeam() == Team.PLAYER ? 1f : -1f, 0f);
        }

        direction.nor();
        Vector2 spawnPosition = new Vector2(shooter.getPosition())
            .mulAdd(direction, Math.max(shooter.getWidth(), shooter.getHeight()) / 2f + PROJECTILE_SPAWN_PADDING);
        Vector2 velocity = new Vector2(direction).scl(PROJECTILE_SPEED);

        projectiles.add(new Projectile(shooter.getTeam(), spawnPosition, velocity, shooter.getType().getDamage()));
        triggerShootAnimation(shooter, isFacingRightFromAim(direction));
        shooter.resetAttackTimer();
    }

    public Vector2 getCameraTarget() {
        return players.get(activePlayerIndex).getPosition();
    }

    public float getBattleTime() {
        return battleTime;
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
        projectiles.clear();

        for (int i = 0; i < players.size(); i++) {
            BattleUnit player = players.get(i);
            player.reset();
            player.setBehavior(new PlayerReplayBehavior(i));
        }

        for (BattleUnit enemy : enemies) {
            enemy.reset();
            enemy.setBehavior(new EnemyRallyBehavior());
        }

        for (CharacterAnimationState state : playerAnimationStates.values()) {
            state.reset();
        }
    }

    private BattleInput readCurrentInput() {
        return new BattleInput(
            Gdx.input.isKeyPressed(Input.Keys.A),
            Gdx.input.isKeyPressed(Input.Keys.D),
            Gdx.input.isKeyJustPressed(Input.Keys.W),
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT),
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

        victoryNotified = true;
        VictoryEvent event = new VictoryEvent(battleTime, calculateScore());

        for (VictoryObserver observer : new ArrayList<>(victoryObservers)) {
            observer.onVictory(event);
        }
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

            hitOpponents(projectile);

            if (!projectile.isAlive() || isOutOfWorld(projectile)) {
                iterator.remove();
            }
        }
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
                target.damage(projectile.getDamage());
                projectile.destroy();
                return;
            }
        }
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
        }
    }

    private boolean isAliveOpponent(BattleUnit unit, Body body) {
        BattleUnit other = findUnitByBody(body);
        return other != null && !other.isDead() && other.getTeam() != unit.getTeam();
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

    private void drawEnemyUnit(ShapeRenderer shapeRenderer, BattleUnit unit) {
        if (unit.isDead()) {
            return;
        }

        Vector2 position = unit.getPosition();
        float x = position.x - unit.getWidth() / 2f;
        float y = position.y - unit.getHeight() / 2f;

        shapeRenderer.setColor(unit.getColor());
        shapeRenderer.rect(x, y, unit.getWidth(), unit.getHeight());
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
    }

    private boolean isFacingRightFromAim(Vector2 direction){
        return direction.x >= 0f;
    }

    public void dispose() {
        for (CharacterAnimationSet animationSet : playerAnimationSets.values()) {
            animationSet.dispose();
        }
    }
}
