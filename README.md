# SybSafetyFirst

Make Minecraft, just a little harder...

## Features

SybSafetyFirst is a Minecraft plugin that adds various modules to enhance the game's difficulty.
The following modules are currently available:

- **Fast Creepers**: Creepers move faster and can jump, making them more challenging to deal with. They also have a
  configurable fuse duration.
- **Water Current**: Simulates a water current that pushes players in a specific direction, with configurable speed and
  direction change intervals.
- **Intentional Game Design**: You'll figure this one out soon enough.
- **No F3**: Disables the F3 debug screen, including the visibility of the player's current coordinates.
- **Wildfire**: Fire spreads like wildfire.
- **Heavy Armor**: Players wearing heavy armor will experience reduced movement speed, making them more vulnerable in
  combat.
- **Hostile Reinforcement**: Hostile mobs will call for reinforcements when in combat with a player.
- **Broken Bones**: Players that take fall damage will be slowed down temporarily, simulating broken bones.
- **Player Zombies**: When a player dies, they will respawn as a zombie with the same name and equipment, the better
  your gear, the harder it is to get it back.
- **Hunger Delirium**: Players will experience nausea when their hunger bar is low, simulating the effects of
  starvation.
- **Limited Crafting**: Players can only craft items that they have unlocked the recipe for.
- **Lightning Fires**: Lightning strikes spread fire.
- **Wrong Tools Hurt**: Using the wrong tool on a block will cause damage to the player.
- **Nether Portals Destabilize**: Nether portals have a chance to destabilize, causing the portal destination to move.
- **Skill-based Inventory**: Inventory slots are locked until the player reaches a certain level, making early game
  inventory management more challenging.
- **Bad Air Caves**: Sometimes, poisonous gas will fill caves, causing damage to players.
- **Baby Creatures**: Any LivingEntity can spawn as a baby, babies are faster and smaller than their adult counterparts.
- **Expensive Trades**: Villager trades are more expensive, making it harder to obtain valuable items.
- **Smaller Stack Sizes**: Item stack sizes are reduced, making inventory management more challenging.
- **Inconsistent Redstone**: Redstone components may not work as expected, adding unpredictability to redstone
  contraptions.
- **No F5**: Disables the F5 third-person view.
- **No Sweeping Damage**: Disables sweeping damage, making combat with multiple mobs more challenging.
- **More to come**: This plugin is a work in progress, with more modules planned for future releases.

## Configuration

Each module can be toggled on or off and be configured to your liking the configuration files,
allowing server administrators to customize the gameplay experience.
Documentation for each module's configuration can be found
[here](https://sybsuper.github.io/SybSafetyFirst/-syb-safety-first/com.sybsuper.sybsafetyfirst.modules/-module-options/index.html).

## Installation

1. Download the plugin JAR file.
2. Place the JAR file in your server's `plugins` directory.
3. Start or restart your server.
4. Configure the modules as needed in the `plugins/SybSafetyFirst/modules` directory.

## Commands

The following commands are available to server administrators:

- `/sf reload <module>` - Reloads the module, useful for configuration changes.
- `/sf enable/disable <module>` - Enables or disables the module on or off.
- `/sf modules` - Lists all available modules and their current status.

## Development

### Requirements

- Kotlin
- Java
- Gradle
- Minecraft server (for testing)

### Building the Project

Run the following command to build the project:

```bash
./gradlew build
```

Or run a test server with:

```bash
./gradlew runServer
```

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
