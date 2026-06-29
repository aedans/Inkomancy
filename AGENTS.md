# AGENTS.md

Grounding for Inkomancy. Read this before diving into the code so you don't have to reverse-engineer the domain.

## What this is

A Minecraft mod. Players cast spells by painting patterns of **ink blocks** in the world. The mod scans those patterns, parses them into a spell tree, and executes it (break/grow blocks, summon mobs, transmute items, etc.).

- **Loader:** Architectury (multi-loader) — targets both **Fabric** and **NeoForge** from one codebase.
- **Minecraft:** 1.21.1 · **Java:** 21 · official Mojang mappings.
- **Build:** Gradle + Architectury Loom.

## Layout

```
common/    Shared game logic — almost all real code lives here.
           src/main/java/hans/inkomancy/{*.java, morphemes/, inks/, mixin/}
fabric/    Fabric entrypoints, mixins, datagen (recipes/models/lang/loot/tags).
neoforge/  NeoForge entrypoints + platform impls.
TODO/      Design docs (01-10-*.md) for a planned refactor — NOT implemented yet.
```

Generated assets live under `common/src/generated/` — don't hand-edit them; they come from the Fabric datagen classes.

## Commands

- `./gradlew build` — build all three modules.
- `./gradlew :fabric:runClient` / `./gradlew :neoforge:runClient` — launch in-game.
- `./gradlew :fabric:runDatagen` — regenerate assets/data after changing datagen generators.

## Core vocabulary

These are the terms that recur everywhere:

- **Morpheme** — the atomic unit of the spell language. Has a `name` and a set of supported output `Type`s: `SPELL`, `ITEMS`, `ENTITIES`, `POSITION`, `ACTION`. A morpheme knows how to interpret its child spells into one of those types (`interpretAsItems`, `interpretAsAction`, …). Subclasses live in `morphemes/` (e.g. `SummonMorpheme`, `BreakMorpheme`, `TransmuteMorpheme`, `DirectionMorpheme`). `Morpheme.java`

- **Glyph** — the 2D visual pattern that represents a morpheme in the world, drawn as a grid of `+` (filled), `_` (empty), `?` (optional). `Glyph.java` holds the static pattern definitions. Players "write" a morpheme by placing ink blocks in its glyph shape.

- **Spell** — the parsed, executable tree: a `Morpheme` plus a list of `connected` child `Spell`s. Serializable (Codec + StreamCodec) for NBT/networking. `Spell.java`

- **SpellParser** — reads ink blocks in the world, finds a START glyph, follows connectors, and builds the `Spell` tree. **SpellWriter** does the reverse: lays out glyphs in the world from a `Spell`. `SpellParser.java`, `SpellWriter.java`

- **Ink** — the medium/fuel. Three kinds: `ArdentInk`, `ConductiveInk`, `VoidInk` (`inks/`). Each is registered as a block + item in all 16 dye colors. `InkBlock` is the placed block players paint glyphs with.

- **SpellContext / ManaProvider** — execution context (world, caster, ink, mana, position/item inputs) threaded through interpretation. `SpellContext.java`

- **Interpretation** — executing a spell means calling `morpheme.interpretAsX(spell, context)` recursively; each morpheme consumes its children as the type it needs and produces items/entities/positions/an action.

## Conventions

- Almost all changes go in `common/`. Touch `fabric/` or `neoforge/` only for loader-specific entrypoints, mixins, or datagen.
- Registration is centralized in `Inkomancy.java` (the main class, with `MOD_ID`, color/dye/hex tables, and registrars).
- New morphemes: add the subclass in `morphemes/`, then register it in `Morpheme.getMorphemes()`.
