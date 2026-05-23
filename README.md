# Super Time Bros

`Super Time Bros` is a libGDX tactical action game about winning one battle by cooperating with your own past runs. You control three heroes one at a time. When you switch heroes, the fight restarts from the beginning, the newly selected hero becomes playable, and the other heroes replay the inputs you recorded for them on previous timelines.

The goal is to clear every enemy wave as quickly as possible while keeping your team alive. Victory shows your final time and score.

## Controls

- `A` / `D`: move left or right
- `W`: jump
- Mouse cursor: aim
- Left mouse button: use the active character's weapon
- `F`: use the active character's special ability
- `1`, `2`, `3`: switch directly to Player 1, Player 2, or Player 3
- `TAB`: switch to the next player
- `Z`: hold to zoom the camera out
- `F3`: toggle debug rendering
- `ESC`: pause or resume the game

## Game Mechanics

### Time Replay

The game records the active player's movement, aiming, shooting, and skill inputs each frame. When you switch to another player, the battle timer resets, enemies and projectiles are respawned, and all non-active players replay the actions recorded for their timelines.

Use this to build coordinated attacks: for example, record one hero drawing fire, switch timelines, then use another hero to attack while the first one repeats the earlier route.

### Player Characters

Each hero has a different weapon and special ability:

- Player 1: charged railgun shot. Hold the left mouse button to charge, then release to fire. Press `F` to fire a teleport projectile that moves the player to its impact point.
- Player 2: shotgun spread shot. Click to fire five projectiles in a cone. Press `F` to activate a temporary shield.
- Player 3: full-auto gun. Hold the left mouse button to fire rapid shots with light spread. Press `F` to perform a high jump and gain limited slow-fall fuel.

### Enemies and Waves

Enemies spawn in waves and first move toward their rally points. After reaching position, or after touching a player, they attack the nearest living player. Flying enemies can move vertically, while normal and heavy enemies stay grounded.

Enemy types:

- Normal enemy: balanced health, speed, and damage
- Heavy enemy: slower, larger, higher health, and higher damage
- Flying enemy: fast, low health, and able to track targets vertically

Projectiles damage only the opposite team. Player characters do not collide with each other, but they still collide with enemies and the map.

### Scoring

The score starts from a base value, loses points as the battle timer increases, and gains a bonus from surviving player health. Faster clears with healthier heroes score higher.

## Project Structure

- `core`: shared game logic, screens, battle systems, commands, and UI
- `lwjgl3`: desktop launcher
- `assets`: textures, audio, UI skin, and Tiled map files

Important battle packages:

- `battle`: battle session, units, projectiles, timeline, factories, and shared types
- `battle.behavior`: behavior strategies for players and enemies
- `battle.movement`: movement strategies for different unit types
- `battle.shooting`: weapon firing modes
- `battle.skill`: player special abilities
- `battle.unit`: concrete player and enemy classes
- `battle.event`: victory observer event types
- `command`: screen transition commands

## Running

Use the Gradle wrapper:

```bash
./gradlew lwjgl3:run
```

On Windows:

```bat
gradlew.bat lwjgl3:run
```

Build the project:

```bash
./gradlew build
```

The repository also includes a packaged desktop jar:

```bash
java -jar final_jrpg-1.0.0.jar
```

### ITCH
https://ushgames.itch.io/time-force-bros
