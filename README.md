# Stages

This project is yet another attempt at creating a "Stage" mod which enabled modpack creators to lock specific content behind conditions.
In this case, the content is defined via JSON files inside a datapack and can be granted/revoked using commands.

### Definition of Stages

Stage definitions can be placed in datapacks using the following name convention:

```
data/[namespace]/stage/[id].json
```

```json5
{
  // Optional, defines if the stage should be enabled/disabled by default
  "defaultState": false,
  // Removed Items
  "items": [
    { "item": "minecraft:enchanting_table" },
    { "item": "minecraft:obsidian" },
    { "item": "minecraft:diamond_pickaxe" }
  ],
  // Removed Fluids
  "fluids": [
    "thermal:lava"
  ],
  // Hidden JEI categories
  "categories": [
    "minecraft:brewing"
  ],
  // Removed Recipes by their ID
  "recipes": [
    "minecraft:cut_copper_stairs_from_cut_copper_stonecutting.json"
  ],
  "disguisedBlocks": {
    "minecraft:diamond_ore": "minecraft:stone",
    "minecraft:deepslate_diamond_ore": "minecraft:deepslate"
  }
}

```

### Granting/Revoking Stages

Stages can be modified on a server-level using the following command syntax:
```
/stages enable namespace:id
/stages disable namespace:id
/stages reset namespace:id
```

Stages can also be granted/revoked per player, in which case the priority overrides the server-defined value:
```
/stages enable namespace:id [player]
/stages disable namespace:id [player]
/stages reset namespace:id [player]
```