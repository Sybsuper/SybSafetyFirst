# SybSafetyFirst

Make Minecraft, just a little harder...

## Features
- **Fast Creepers**: Creepers move faster and can jump, making them more challenging to deal with. They also have a configurable fuse duration.
- **Water Current**: Simulates a water current that pushes players in a specific direction, with configurable speed and direction change intervals.
- **Intentional Game Design**: You'll figure this one out soon enough.
- **No F3**: Disables the F3 debug screen, including the visibility of the player's current coordinates.
- **Wildfire**: Fire spreads like wildfire.
- **Heavy Armor**: Players wearing heavy armor will experience reduced movement speed, making them more vulnerable in
  combat.
- **Hostile Reinforcement**: Hostile mobs will call for reinforcements when in combat with a player.
- **Broken Bones**: Players that take fall damage will be slowed down temporarily, simulating broken bones.
- **More to come**: This plugin is a work in progress, with more modules planned for future releases.

## Installation

1. Download the plugin JAR file.
2. Place the JAR file in your server's `plugins` directory.
3. Start or restart your server.
4. Configure the modules as needed in the `plugins/SybSafetyFirst/modules` directory.

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
This project is licensed under the MIT License. See the `LICENSE` file for details.
