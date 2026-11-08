# Icy Punishing

English | [简体中文](README.md)

> A NeoForge combat mod inspired by *Punishing: Gray Raven*: awareness sets, weapons, signal-orb 3-matches and character-specific skill sets.

## Overview

**Icy Punishing** brings the core combat language of *Punishing: Gray Raven* into Minecraft: equip 6 **Awareness** pieces to complete set bonuses, switch combat stance, clear red/yellow/blue **Signal Orbs** to cast skills, fill the energy bar to unleash your **Signature**, and push weapons and characters to their limits through Ascension, Leap, Breakthrough and Enhancement.

Each tachi maps to a different **character skill set** — the weapon in your main hand decides which character you are playing.

Currently included:

- **30 awareness sets, 180 items in total**, each with 2/4/6-piece bonuses;
- **16 usable tachi** + 6 cosmetic coatings, **6 bows** + 7 cosmetic coatings;
- 3 complete character skill sets: **Honglian**, **Shadowblade - Inverse Severance**, **Hongying**;
- Blade-sweep attacks, Supercomputing Space, a floating-blade minion;
- Full weapon breakthrough / enhancement / leap and awareness enhancement / recycling systems.

### Glossary

| Term | Meaning |
| --- | --- |
| Awareness | This mod's accessories, equipped in 6 dedicated Curios slots |
| Crit | This mod's critical system; the crit stat affects trigger chance, base crit DMG is 1.5× |
| Signal Orb | Red/yellow/blue resource that determines which skills you can cast |
| Supercomputing Space | A damage window that greatly slows nearby enemies |

## Basic Information

| Item | Value |
| --- | --- |
| Name | Icy Punishing |
| Mod ID | `icy_punishing` |
| Version | 1.1.0 |
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.235 |
| Java | 21 |
| Author | icylemonheart |
| License | Code MIT / art and content assets CC BY-NC-SA 4.0 |
| Dependencies | [Icy Lib](https://github.com/icylemonheart/icy_lib) (required), [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) (required), More Upgrade Templates (optional, needed for weapon recipes) |
| Side | Required on both client and server |
| Category | Combat / Equipment / RPG |

## Installation

1. Install Java 21 and NeoForge 21.1.235 (or a compatible 1.21.1 build);
2. Download `icy_punishing-1.1.0.jar` from [Releases](https://github.com/icylemonheart/icy_punishing/releases), plus Icy Lib 1.1.2+ and Curios API;
3. Put the jars into your client `mods` folder; servers also need them for multiplayer;
4. Optional: install More Upgrade Templates — weapon recipes use its materials.

## Gameplay

1. Attack enemies to build up signal orbs and energy;
2. Press number keys `1`–`8` to clear an orb and cast a skill; a **3-match** casts the enhanced version;
3. When energy is full, press `X` to unleash your Signature;
4. Use the item **Humanity Stands Here** to open the enhancement UI for leveling, ascension, weapon breakthrough and awareness enhancement.

### Default Controls

| Key | Action |
| --- | --- |
| `Z` | Toggle combat / sheathed stance |
| `X` | Cast Signature |
| `1`–`8` | Clear the orb in that slot and cast its skill |

> All keys can be rebound in Options → Controls.

## Awareness System

### Rules

- Awareness pieces go into 6 dedicated Curios slots (`awareness_1`–`awareness_6`);
- Each piece rolls HP, Crit, ATK and DEF, each with its own random range;
- Higher quality means higher values (2★ around 50–80 HP, 6★ reaches 145+ HP and 130+ ATK);
- Equipping 2 / 4 / 6 pieces of the same set activates the matching bonuses;
- Awareness can be enhanced (consumes awareness materials) or recycled for materials.

### Set Bonuses

#### 2★

| Set | 2-piece | 4-piece |
| --- | --- | --- |
| Mernie | ATK +10 | HP +50 |

#### 3★

| Set | 2-piece | 4-piece |
| --- | --- | --- |
| Pavty | DMG +3% against mobs within 2 blocks | DMG taken -3% from mobs within 2 blocks |
| Pamelie | DMG +3% against mobs beyond 3 blocks | DMG taken -3% from mobs beyond 3 blocks |

#### 4★

| Set | 2-piece | 4-piece | 6-piece |
| --- | --- | --- | --- |
| Curie-Cohet | 20% chance to call a bombardment dealing 40% physical DMG (15s cooldown) | — | — |
| Cunningham | +5% physical and elemental DMG against super-armor enemies | Taking hits within 5s grants a damage-reduction shield; next hit taken -30% (5s cooldown) | — |
| Alice | Healing +15% | Healed targets gain +4% physical and elemental DMG for 4s | — |
| Edison | Crit DMG +5% | After a crit, own DMG +5% for 5s | — |
| Archimedes | Basic attack DMG +5% | Every 10s, next basic attack +10% DMG | — |
| Nimitz | Basic attack DMG +5% | Crit DMG +5% | Minion DMG +10% (permanent) |

#### 5★

| Set | 2-piece | 4-piece | 6-piece |
| --- | --- | --- | --- |
| Aifu | Each crit grants +2% all DMG for 3s, up to 5 stacks | Every 60s, the next crit grants 3s of Supercomputing Space | — |
| Aike | Extra DMG +2%, all elemental DMG +4% | 15% chance on elemental DMG to deal an extra hit of the same element (8s cooldown) | — |
| Voltaire | Attacks reduce target DEF by 10% for 5s (12s cooldown) | Each time you take DMG, reduce the attacker's generic RES by 0.15 for 7s, up to 2 stacks | — |
| Goroda | Attacks reduce target all-elemental RES by 0.06 for 5s (12s cooldown) | Each time you take DMG, reduce all-elemental RES of mobs within 5 blocks by 0.06 for 7s | — |
| Richelieu | Healing +5%; next heal grants the target +8% physical DMG for 4s (5s cooldown) | Every 8 crits, deal an extra crit instance of equal value (5s cooldown) | — |
| Samanda | Healing +5%; next heal grants +8% all elemental DMG for 4s (5s cooldown) | Healing leaves an elemental aura: allies inside gain +8% all elemental DMG and +0.05 all elemental RES (10s cooldown) | — |
| Mozart | +15% all DMG while in Supercomputing Space | Gaining Supercomputing Space triggers a 200% void DMG hit across the screen | — |
| Boudica | Basic attack DMG +7% | While in Supercomputing Space: all ATK +5%, movement speed +10% | Dealing DMG grants +3% extra DMG once per 2s, up to 5 stacks, lasts until the next hit taken |

#### 6★

| Set | 2-piece | 4-piece |
| --- | --- | --- |
| Einsteinna | Minion DMG reduces the target's matching elemental RES by 15% for 8s (10s cooldown) | When hit, deploy an elemental aura: +20% all elemental RES for 5s, elemental DMG inside +15% (8s cooldown) |
| Kantlina | All ATK +5% | 50% chance on attack to gain Fury; basic attack extra DMG +20% for 5s (8s cooldown) |
| Shakespeare | All ATK +3%, fire DMG +7% | Extra DMG +10%; attacks apply Burn (fire RES -15%) for 8s; attacking a burning target triggers a 600% fire explosion (6s cooldown) |
| Heisenberg | All ATK +3%, lightning DMG +7% | Extra DMG +10%; attacks apply Shock (lightning RES -15%) for 8s; attacking a shocked target triggers a 600% lightning storm (6s cooldown) |
| Darwin | Each crit grants +3% extra DMG for 4s, up to 5 stacks | After recording crit, jump-attack and normal DMG, release a 12-block shockwave dealing 600% physical DMG |
| Hanna | Crit extra DMG +25% | Crit stat ×1.1; every 30s, the next crit grants 3s of Supercomputing Space |
| Da Vinci | With a minion within an 11×11×11 volume: +10% ATK, +20% DEF | Minions focus the same target; with ≥3 minions nearby, all characters gain +20% ATK |
| Catherine | Minion attacks or your own crits reduce the target's physical RES by 20% for 8s (10s cooldown) | When hit, gain a 1000-point shield and +15% physical DMG for 5s (8s cooldown) |
| Feriet II | Healing +10%; healed allies gain +10% physical DMG and DEF for 5s (8s cooldown) | After 5 healing charges, summon an Allay: within 5 blocks, restore 2% max HP per second and +15% physical DMG for 5s (12s cooldown) |
| Guiney | Healing +10%; the target gains +10% all elemental DMG and RES for 5s (8s cooldown) | Same effect as Feriet II 4-piece |
| Friedrich | Each dodge grants +15% movement speed and +5% extra DMG (planned) | Supercomputing Space slows enemies and lowers their damage reduction (planned) |
| Patton | ATK +3%, physical DMG +7% | Extra DMG +10%; attacks apply Pierce (physical RES -7%) for 8s; attacking a pierced target triggers a 600% physical explosion (6s cooldown) |
| Basilone | ATK +3%, dark DMG +7% | Extra DMG +10%; attacks apply Erosion (dark RES -15%) for 8s; attacking an eroded target triggers a 600% dark explosion (6s cooldown) |

> Some 6★ sets are 2/4-piece only by design.

## Weapons

### Tachi

| Tachi | Quality | Trait |
| --- | --- | --- |
| Standard Tachi | 2★ | Starter blade |
| Hugotou | 3★ | Transition weapon |
| Mumei | 4★ | Crit DMG +0.15 |
| Kyuuji | 5★ | 20% chance on attack to call lightning for 50% physical DMG, 3s cooldown |
| Honglian | 6★ | Basic attack extra DMG +10%; hits stack Battle Will, +5% physical DMG per stack, up to 5 stacks, 2s |
| Hongying | 6★ | Weapon skill "Falling Sakura" |
| Sirius | 6★ | Off-hand floating blade attacks targets within 12 blocks once per second |
| Shadowblade - Inverse Severance | 6★ | Weapon skill "Shadow of Severance" |
| Zhu Hua (Stage I–IV) | 6★ | Four-stage growth tachi |
| Sarariel (Stage I–IV) | 6★ | Four-stage growth tachi |

**Cosmetic coatings**: Benizakura, Lupercalia, Canguang, Bonescale Blade, Red Sky Ritual, Floranum.

### Bows

Available: Ramiel, Tonitrus, Astraea, Falcon E-3320, Pulley Bow, Practice Bow. Coatings: Iris, White Acclaim, Nightflyer, Elpis Release!, Somber Arc, Eternal Nightfall: Dreamless Ode, Summer Rain-Bow.

> Bows currently have items and visuals only; their dedicated skill sets have not been started yet.

### Weapon Progression

- **Enhance**: consume Weapon Enhancement Material II / III / IV to raise level and stats;
- **Breakthrough**: consume low/high breakthrough alloys and low/high weapon breakthrough cores to raise the level cap;
- **Leap**: consume inverse fragments and leap chips to unlock leap passives in the character skill set;
- **Recycle**: iron / diamond / netherite-tier recipes return part of the materials;
- **Skill upgrade**: submit **Skill Points** into the center slot of the enhancement UI.

## Character Skill Sets

Each tachi maps to a skill set; the weapon in your main hand determines your current character.

### Honglian (Lucia: Lotus)

- **Red Orb**: 267% / 533% / 800% DMG
- **Yellow Orb**: 150% / 300% / 450% DMG
- **Blue Orb**: 100% / 200% / 300% DMG, plus 10% / 15% / 20% fire burn for 4 / 4 / 6s
- **Signature**: costs 100 energy; 5 slashes at 140% followed by a 1000% finisher
- **Core "Twin-Blade Burst"**: clear a red orb again within 4s after a 3-match to enter Burst, 1080% total DMG
- **Passive "Twin Blades"**: as leader, all-team ATK +10%; class bonus extra DMG +20%; Fury gives basic attacks +10% extra DMG and stacks Battle Will (+5% per stack, up to 5, 2s)
- **Dodge**: 30-tick cooldown, movement speed ×1.15, 12 ticks of invulnerability, 8-tick perfect-dodge window, triggers 60 ticks of Supercomputing Space and refunds 2 orbs
- **Ascension**: stage 1 air slash +20%; stage 2 finisher +15%; stage 3 core basic attack +15% and final liberation refunds 3 orbs
- **Leap**: +2% all skill DMG per level

### Shadowblade - Inverse Severance (Lucia: Dawn, lightning)

- **Red Orb**: 40% / 80% / 144%, 5 hits, grants 20 super armor
- **Yellow Orb**: 100% / 200% / 300%, leaves a thunder field dealing 20% / 30% / 40% per second, radius 4 blocks, lasts 60 / 80 / 100 ticks
- **Blue Orb**: thrust / pull, 80% / 160% / 240%
- **Signature**: costs 50 energy, summons the "Blade of Dawn" for 300 ticks at a 25% multiplier
- **Mark mechanic**: up to 3 stacks, 160 ticks, 25% / 50% / 75% lightning chance (50% / 75% / 100% after Leap), lightning deals 60%, 100-tick internal cooldown
- **SS / SSS**: thunder field DMG +20%; lightning DMG +20%; SSS+ grants an extra +15% for 200 ticks
- **Leap nodes**: 1 / 9 / 18 / 19 / 27 / 36 / 37 / 45 / 54
- **Ascension**: stage 1 superconductor field +20%; stage 2 flame lightning +20%; stage 3 dawn liberation +15% (10s) and refunds 3 orbs

### Hongying (Lucia: Crimson Abyss)

- **Red Orb**: 25% / 50% / 75% × 3 hits, 3-match adds +75%
- **Yellow Orb**: 250% / 500% / 750%
- **Blue Orb**: attack speed and movement speed +20% / 30% / 50% for 100 ticks (200 after Leap), empowers subsequent sword orbs
- **Sword Orb**: 340% on clear, stores up to 2 charges
- **Signature**: costs 85 energy, 2600% total (160% slash + 1000% finisher)
- **Core**: a blue 3-match opens a 160-tick empowerment window; Crimson Abyss grants +10% DMG; aura lasts 100 / 200 ticks
- **Orb buff**: up to 10 stacks, +2% each, 100 ticks
- **Leap**: dash 300%, Naito 600%, Signature sword aura 350% (extra +50% / +100%)
- **Ascension**: stage 1 +10% (4s); stage 2 +5% and refunds 2 orbs on exit; stage 3 blue-state physical DMG +20% and refunds 3 orbs

## Combat Mechanics

### Signal Orbs

- Hits restore signal orbs and energy; clearing orbs also restores energy;
- Orbs come in red, yellow and blue, randomly distributed across slots 1–8;
- Adjacent orbs of the same color can be cleared together; clearing 3 is a **3-match** and casts the enhanced skill;
- Each character has different 3-match benefits and conditions.

### Blade Sweep

Attacks hit through weapon-model collision: enemies within a 3-block blade length, 0.35-block radius and roughly 120° fan take damage.

### Supercomputing Space

Certain triggers (perfect dodge, set bonuses) enter Supercomputing Space: the screen gains a filter and nearby enemies are greatly slowed — the core window for damage and repositioning.

### Floating Blades

With **Sirius** equipped, a floating blade appears in the off-hand, attacking enemies within 12 blocks once per second and prioritizing the previous target.

## Drops and Acquisition

- Awareness drops from mobs by max-health tiers; chances are configurable;
- EXP containers, nut packs and skill points drop at milestones;
- Weapons and breakthrough materials come from recipes covering basic tachi, advanced tachi, breakthrough cores and recycling.

## Configuration

Config file: `config/icy/icy_punishing-common.toml`

| Setting | Description |
| --- | --- |
| Awareness drop toggle and chances | Controlled by mob max-health tiers |
| EXP container drops | Toggles EXP item drops |
| Nut pack drops | Toggles the upgrade currency drops |
| Skill point milestones | Sets the drop checkpoints for skill points |

## FAQ

**Q: Why does nothing happen when I press a skill key?**
A: The skill set requires the matching tachi in your main hand and combat stance (press `Z`). Empty hands or other weapons will not trigger it.

**Q: Signal orbs or skill icons are missing.**
A: Make sure Icy Lib and Curios are installed and the main-hand weapon belongs to this mod.

**Q: Why is my damage so low?**
A: This mod uses Icy Lib's "RES division + DEF subtraction" formula, and mobs carry defense based on max health by default. Lower the mob defense multiplier in Icy Lib's config, or raise ATK and penetration through awareness and enhancement.

**Q: Why are only some set bonuses active?**
A: Sets activate at 2 / 4 / 6 pieces, and some sets only have 2/4-piece bonuses. The Friedrich 2/4-piece effects are not implemented yet.

**Q: What are the colored / blinking texts?**
A: That is rich-text styling provided by Icy Lib and is normal.

**Q: Can I install it only on the server?**
A: No. The mod includes HUD, effects and keybinds, so both client and server must install it.

## Compatibility

- Requires NeoForge 21.1.235 (or a compatible 1.21.1 build) and Java 21;
- Curios and Icy Lib are required; the optional More Upgrade Templates is used by weapon recipes;
- When combined with other damage-reduction / armor mods, mind the damage calculation order;
- Weapons use OBJ models; on low-end shaders, try switching render settings if a model looks wrong.

## Building (Developers)

JDK 21 is required. The project resolves its library through `flatDir` against a sibling directory, so a fresh clone must build icy_lib first:

1. Place the icy_lib source in a sibling directory named `icy_lib-NeoForge-1.21.1` and run `gradlew build`;
2. Run `gradlew build` in this project;
3. The artifact is `build/libs/icy_punishing-1.1.0.jar`.

## License and Credits

- **Code**: MIT License, see [LICENSE](LICENSE);
- **Art and content assets**: CC BY-NC-SA 4.0;
- Awareness portraits, accessory and weapon textures are owned by **Kuro Games** and used non-commercially under CC BY-NC-SA 4.0;
- This is a fan-made, unofficial mod with no affiliation with Kuro Games;
- Inspired by *Punishing: Gray Raven*.

Please report issues via [Issues](https://github.com/icylemonheart/icy_punishing/issues).
