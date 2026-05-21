# final_jrpg

`final_jrpg` is a libGDX tactical action prototype about replaying a fight through several player characters. The player controls three characters one by one, records actions for each of them, and can switch back to another character with a time reset. Previously recorded characters replay their actions, while enemies react to the current battle state.

## Game Idea

The battle starts with three enemies and three player characters entering from spawn points on the Tiled map. Enemies first move toward their assigned points, then switch to attacking the nearest player character. The player wins by destroying all enemies.

Enemy types:

- Normal enemy: `16x32`
- Heavy enemy: `32x32`
- Flying enemy: `16x16`

Player characters currently use placeholder boxes and share the normal character size. Textures can be added later without changing the battle structure.

## Controls

- `A` / `D`: move left or right
- `W`: jump
- Left mouse button: shoot toward the cursor
- `1`, `2`, `3`: switch to a specific player character
- `TAB`: switch to the next player character
- `Z`: zoom camera out
- `ESC`: pause or resume from pause

## Battle Rules

- Player characters do not collide with each other.
- Player characters still collide with enemies and the map.
- Enemies collide with the map and player characters.
- Projectiles damage only the opposite team.
- The timer is shown at the top center of the game screen.
- When all enemies are defeated, the victory screen shows final time and score.

## Project Structure

- `core`: shared game logic, screens, battle systems, commands, and UI.
- `lwjgl3`: desktop launcher.
- `assets`: textures, UI skin, and Tiled map files.

Important battle packages:

- `battle`: battle session, units, projectiles, timeline, factories, and shared types.
- `battle.behavior`: behavior strategies for players and enemies.
- `battle.movement`: movement strategies for different unit types.
- `battle.unit`: concrete player and enemy classes.
- `battle.event`: victory observer event types.
- `command`: screen transition commands.

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
