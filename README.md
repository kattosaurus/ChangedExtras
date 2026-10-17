# **Changed Extras**

![The icon of Changed Extras](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/src/main/resources/pack.png)

<p align="center">
    <a href="https://modrinth.com/mod/changed-extras" rel="Modrinth"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/available/modrinth_vector.svg"></a>
    <a href="https://www.curseforge.com/minecraft/mc-mods/changed-extras" rel="CurseForge"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/1aec26abb75544baec37249f42008b2fcc0e731f/assets/cozy/available/curseforge_vector.svg"></a>
    <a href="https://discord.gg/e8GxE4e4Bb" rel="Discord"><img src="https://raw.githubusercontent.com/LtxProgrammer/Changed-Minecraft-Mod/refs/heads/1.20.1-dev/images/discord-custom_vector.svg"></a>
    <a href="https://github.com/kattosaurus/ChangedExtras/tree/main/src/main/resources/assets/changedextras/lang" rel="Translate"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/translate/generic-plural_vector.svg"></a>
</p>

**The** Changed: Minecraft Mod Addon that adds **creatures, features and structures** that just won't be implemented by the other addons or the main mod.

Featuring new bosses, latex and organic variants, structures, items, blocks and other features.

Requires **Changed Mod 0.15.0 or higher** and the **Changed Addon Plus**.

---

Features currently in the mod, ordered from most important or experience changing to least experience changing.

## Improved latex intelligence

Toggled by the *gamerule* **`changedextras:smart_latex_ai`**, by default on **true**.

Makes the beasts **smarter**, giving them:

- **Memory**
  > The latex creatures can remember you, and hold a grudge against you. Lowered and raised by the server config **`latexAttackerMemoryTicks`**

- **Better pathfinding**
  > They won't be stuck on a 1 block wide jump now. They can jump and get you.

- **The ability to heal (via eating) and running**
  > Whenever the latex creature is in danger, they run to avoid the grasp of death, looking for food and using it to regenerate.

- **Shoot bows**
  > They can shoot bows like a skeleton now, they will get you, even if you bridge away.

- **Equip armor**
  > They can find, and equip armor and tools on the floor, your death will help them a lot.
  >
  > They can also spawn with armor, toggleable via the **`changedextras:latex_equipment_enabled`** gamerule. You can control the chances of armor and equipment via the other two gamerules, **`changedextras:latex_armor_chance`** and **`changedextras:latex_tool_chance`**.

## New transfur variants

This mod has multiple variants, here they are, ordered by origin:

### Kaiju paradise

- Latex Jammer
- Latex Hazzy
- Latex Catte

![Image of the Kaiju paradise variants](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/gallery/2026-10-06_19.19.09.png)

### SCP foundation or Contagious Revival

- SCP-009

![Image of the SCP-009 variant](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/gallery/2026-10-06_19.46.07.png)

### Resurgenced

- Cone Kat, in multiple ice cream flavors:
  - Vanilla (base)
  - Mint
  - Choco
  - Strawberry
  - Neapolitan

![Cone Kat, male and female variants](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/gallery/2026-10-06_19.20.41.png)

### Spirited Away

- Latex Haku

### Content creators

- Latex Thorniii (a squog, a squid dog, based on the YouTuber and streamer Thorniii)

### Original

- Latex White Cat
- Latex Artist
- Latex Katto
- Protogen bee

![Image of the original variants](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/gallery/2026-10-06_19.28.17.png)

All these variants were textured and modeled by me, except the Protogen (based on foxyas's model, used with permission).

## New structure and facility rooms

This mod adds **two** new structures, and two facility rooms for the main mod's Facility.

### Structures

- **Bunker**
  > A bunker, where Dr. [REDACTED] hid after TSC fell.

  ![Bunker entrance](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/gallery/2026-10-06_19.29.14.png)

- **Biological studies facility**
  > The Biological studies facility comes with rooms, the list of them is found next.

  ![Biological studies facility entrance](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/gallery/2026-10-06_19.29.57.png)

### Biological studies facility rooms

- White latex cafeteria / room
- Office
- EXP-012 containment chamber
- SCP-009 containment chamber
- Armory room

### Facility (main mod structure) rooms

- Cafeteria
- Recreation room

## A new boss: **An** artist or EXP-012

The artist, **an** artist, also known as EXP-012, is a boss specializing in "art".

There are multiple attacks she can perform: a swipe, a dash, etc. She has a vulnerability window where you can hit her. **Normal** attacks won't work unless she's reloading.

She has lore, but it's your job to find it.

The crafting recipe requires bio-mass, latex base, an orange, a totem of undying, **the palette** (craftable), a **flask of tears** (found in the EXP-012 room) and three paper.

![Image of the recipe](https://raw.githubusercontent.com/kattosaurus/ChangedExtras/refs/heads/main/gallery/IMG_20261006_173534.jpg)

The reward for winning against her is the next feature.

## Custom latex color and texture editing

Lets the player customize further custom latex, changing its color and texture.

To do this, you must have EXP-012's paint brush.

## New blocks, food and medicine

The blocks include the **ice cream** block, the **SCP-009 crystals**, an **orange bucket** and **headphones**.

The food is the ice cream cone, found in the cafeteria.

The medicine helps against pale, the crafting recipe must be found by you.

## Gamerules and server configuration

Gamerules:

- `changedextras:latex_spawn_in_day`. Default set to `false`
  > Lets latex creatures spawn more in the day and surface.

- `changedextras:latex_equipment_enabled`. Default set to `true`
  > Spawns latex creatures with armor and tools, configurable via the other gamerules.

- `changedextras:smart_latex_ai`. Default set to `true`
  > Enables the smart latex AI.

- `changedextras:latex_tool_chance`. Default set to `5`
  > Defines the percentage chance of latex creatures spawning with tools.

- `changedextras:latex_armor_chance`. Default set to `10`
  > Defines the percentage chance of latex creatures spawning with armor.

- `changedextras:give_guidebook`. Default set to `false`
  > Gives the Latex Guide book to players.

Server configuration:

- `latexAttackerMemoryTicks`. Default set to `160` (range: 0 - 36000)
  > How long smart latex creatures remember and pursue a player after being attacked or otherwise acquiring a target. Set to 0 to disable attacker memory.

- `useCustomDeathScreen`. Default set to `false`
  > Determines if the custom death screen with the death messages is used.

- `biologicalFacilityMaxRooms`. Default set to `50` (range: 10 - 100)
  > The maximum number of rooms that can generate in a Biological Studies Facility. A facility always aims for at least 10 rooms, so this cannot be set lower than that.

---

## FAQ (Frequently asked questions)

### How do I find the biological studies facility?

It's found the same way, and in the same places as the original facilities.

### Does this mod have a Discord server?

Yes! It does. You could help me a lot by joining, it's attached at the top.

### Can I put this mod in my modpack?

Yes, even if it's public.

### Does this mod work well in multiplayer?

Yes, the AI doesn't affect the TPS much, I've personally tested this.

### Is this compatible with the other addons?

It should be. It works with Changed Synergy, and as far as I know, with every other addon.

---

## Credits

**Music**

- 'Ashes of a Fallen Year' by Scott Buckley, used as the artist's theme. Released under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/). [www.scottbuckley.com.au](https://www.scottbuckley.com.au)
- 'Rush' by Of Far Different Nature (https://fardifferent.bandcamp.com/). Licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)

**Models and characters**

- Foxyas: original Protogen model, I have explicit permission to use it
- Kaiju paradise developer team: Hazzy, Jammer and Catte characters
- FoxxoTrystan: Conekat characters
- Thorniii: Latex Thorniii is based on them

Music was trimmed and converted to .ogg. Full credits: [MOD CREDITS](MOD%20CREDITS)

## Final words

Thank you for looking at my addon's page. It seriously means a lot to me. If you like my mod, you can **contribute** to it via this repository, **join my Discord** (linked at the top) or **commission me** (join my Discord for more info).

If you're visiting on behalf of a company, and you are interested in a partnership, or want me to take down a variant (for example, the KP ones), you can do so through my email (attached on my personal site) or my Discord (also linked at the top, and on my website).