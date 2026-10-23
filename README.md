# Icy Punishing

A Minecraft NeoForge mod (1.21.1) inspired by Punishing: Gray Raven, adding consciousness accessories (意识) with set bonuses, katanas, mobs and materials.

## Features

- **Awareness Accessories**: Collectible consciousness items with different star ratings (2-6 stars), with quality-themed slot and tooltip backgrounds
- **Set Bonuses**: Equip matching sets for powerful bonuses
- **Upgrade System**: Level up and breakthrough awareness items
- **Katanas**: 3D-modelled katanas (制式刀, 护身刀, 无铭, 九字之定, 红莲狂士, 红樱, 天狼星, 影打-逆断, 朱桦, 沙利叶) rendered with a custom OBJ mesh pipeline
- **Mobs**: 修理机工 (Repair Mechanic) with GeckoLib animations
- **Materials**: Nuts, nut packs, alloys, circuits, upgrade materials and EXP containers

## Requirements

- Minecraft NeoForge 1.21.1 (21.1.x)
- [Icy Lib](https://github.com/icylemonheart/icy_lib) 1.1.1+
- [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) 9.5.1+1.21.1
- GeckoLib 4.9.3
- Java 21

## Building

1. First build icy_lib:
   ```bash
   cd ../icy_lib
   ./gradlew build
   cd ../icy_punishing
   ```

2. Then build icy_punishing:
   ```bash
   ./gradlew build
   ```

## Installation

1. Build both mods
2. Copy jars to your NeoForge mod folder:
   - `icy_lib/build/libs/icy_lib-1.1.1.jar`
   - `icy_punishing/build/libs/icy_punishing-1.1.0.jar`
3. Launch Minecraft with NeoForge, Curios API and GeckoLib

## Awareness Sets

- Einsteinna (爱因斯坦娜)
- Kantlina (康德丽娜)
- Shakespeare (莎士比亚)
- Heisenberg (海森)
- Darwin (达尔文)
- Hanna (汉娜)
- Da Vinci (达芬奇)
- Catherine (凯瑟琳)
- Feriet II (费列特二世)
- Guiney (桂尼)
- Friedrich (腓特烈)
- Patton (巴顿)
- Basilone (芭斯隆)
- And more...

## License

### Code License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Asset License

All texture files (PNG) including awareness portraits and accessories are subject to the **CC BY-NC-SA 4.0** license.

**Copyright Notice**: All awareness portrait and accessory texture files (PNG) are owned by **Kuro Games (库洛游戏)**. These assets are used with permission under the CC BY-NC-SA 4.0 license for non-commercial purposes.

[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)
