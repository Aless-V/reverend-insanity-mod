This is a fork of the [reverend-insanity-mod](https://github.com/dkjsiogu/reverend-insanity-mod) mod but then fully transalted into english, the original had support for english but almost all of the language was hard coded in mandarin, so the english language file didn't change much. Besides fully translating, I am also going to change a lot of things concerning game logic to make it follow the novel more and make changes to block and entity textures. Suggestions and/or issues are welcomed!

### (below is the original readme but translated into english)

---

# Reverend Insanity — Minecraft Mod

> A Minecraft mod fully developed by AI, based on the world of the novel *Reverend Insanity*.

**Minecraft NeoForge 1.21.1 | Java 21**

---

## How This Mod Was Made

Not a single line of human-written code.

The entire project was completed fully autonomously by **Claude Code** (Anthropic Claude Opus) — from world research and mechanics design to pixel art generation, Java coding, compilation, debugging, and code review. Humans were only responsible for providing direction and final approval.

### Development Workflow

```
┌─────────────────────────────────────────────────────┐
│  Phase 1: Original Novel Research + Concept Design  │
│  ├─ Research Reverend Insanity lore, Gu insects,    │
│  │  and Killer Move descriptions                    │
│  ├─ Design game mechanics (faithful to the novel,   │
│  │  no potion-effect shortcuts)                     │
│  ├─ Generate pixel art textures using Python+Pillow │
│  └─ AI verifies texture quality, regenerates if     │
│     unsatisfactory                                  │
├─────────────────────────────────────────────────────┤
│  Phase 2: Parallel Coding with an Agent Team        │
│  ├─ Split tasks, create 3-4 AI agents for parallel  │
│  │  development                                     │
│  ├─ Each agent works on independent modules         │
│  │  (items/combat/visuals/network)                  │
│  └─ Integrate and compile                           │
├─────────────────────────────────────────────────────┤
│  Phase 3: Agent Team Code Review                    │
│  ├─ Create an independent review agent team         │
│  ├─ Review code quality, performance, faithfulness  │
│  │  to the novel, and security                      │
│  └─ Fix all identified issues and recompile         │
├─────────────────────────────────────────────────────┤
│  Phase 4: Loop → Return to Phase 1 to develop next  │
│  content                                            │
└─────────────────────────────────────────────────────┘
```

### Technical Details

- **Codebase**: 73,000+ lines of Java code, 1,300+ files
- **Textures**: All procedurally generated via Python scripts in pixel art style
- **AI Coding Principle**: No MobEffect shortcuts — all mechanics must be implemented through attribute modifiers, event interception, custom damage calculations, projectile systems, etc.
- **Parallel Development**: Multiple AI agents code different modules simultaneously, coordinated by a main agent
- **Quality Assurance**: Every change must pass `./gradlew build` — no compilation, no next phase

---

## Gameplay

You play as a Gu Master, cultivating, collecting Gu insects, combining Killer Moves, and battling enemies in a ruthless world of survival of the fittest.

### Cultivation System

- **Opening the Aperture**: Consume Hope Gu to open your aperture, determining how many Gu insects you can equip
- **Five Ranks**: Rank 1 → 2 → 3 → 4 → 5 (Gu Immortal), each with its own breakthrough requirements
- **Breakthroughs**: Meditate for minor sub-rank breakthroughs; consume Breakthrough Stones for major rank advancements
- **Primeval Essence + Thought Power**: Dual-resource system; recovery accelerates at night; talent affects efficiency
- **Lifespan System**: Cultivation rank determines maximum lifespan; breakthroughs extend it; death when exhausted

### Gu Insect System

- **177 Gu insects**, covering all **48 Paths** (Moon Path, Strength Path, Sword Path, Soul Path, etc.)
- Each Gu insect has unique skills — no simple buffs/debuffs
- **Rank 1–5 tier system**; higher-rank Gu insects are more powerful
- **Feeding**: Gu insects starve and die if not fed
- **Refining**: Upgrade Gu insects in the Gu Refining Furnace
- **Damage**: Gu insects can be damaged in combat — effectiveness halved, requires Primeval Essence to repair
- **Devouring**: Sacrifice lower-rank Gu insects to empower higher-rank Gu of the same Path
- **Wild Gu Capture**: Naturally spawning Wild Gu entities can be captured nearby

### Killer Move System

- **61 Preset Killer Moves**: Powerful techniques combining multiple Gu insects
- **Create Your Own**: Freely combine up to 5 auxiliary Gu insects, then deduce new Killer Moves
- **Path Combination Engine**:
  - **Path Reactions** — Combining Gu insects from different Paths produces fusion effects (e.g., Ice + Wind → Ice Wind Tornado)
  - **Path Stacking** — Stacking Gu insects from the same Path triggers qualitative transformations (e.g., Strength × 3 → Beast Shadow)
  - No recipes required — players can freely experiment, and canonical Killer Move effects emerge naturally

### Combat Content

**Bosses:**
- **Ten Venerables** — Each Venerable has a unique AI, signature Killer Move sequences, and 3-phase battles:
  - Fang Yuan (Thieving Path + Refining Path), Star Constellation Venerable (Star Path + Luck Path), Giant Sun Venerable (Luck Path + Blood Path)
  - Red Lotus Venerable (Flame Path + Fire Path, summons historical Gu Immortal phantoms from the river of time)
  - Heavenly Court Earth Spirit (Earth Path + Heaven Path), Spectral Soul Venerable (Soul Path + Shadow Path)
  - Thieving Heaven Venerable (Thieving Path + Time Path), Primordial Venerable (Qi Path + Yin-Yang Path)
  - Limitless Demon Venerable (Law Path + Forbidden Path), Sword Slayer Venerable (Sword Path + Killing Path)
- **Ancient Gu Immortal Remnant Souls** — 5 attack patterns + Boss health bar
- **NPC Gu Masters** — 5 combat archetypes (melee/ranged/control/support/assassin), skills assigned by Path
- **The Faceless Hand** — A tracking entity with 5 independently attacking fingers

**Mobs:**
- Electric Wolf, Thunder Crown Alpha Wolf, Wild Boar, Jade-Eyed Stone Monkey, Scarecrow Puppet, Mountain Spider (rideable)

### World Content

**Generated Structures:**
- Gu Caverns (underground Gu insect nests)
- Clan Settlements (NPC Gu Master villages)
- Inheritance Sites (wave-based combat trials → Dao Mark rewards)
- Wine Traveler's Tomb (hidden underground ruins)
- Moon Orchid Caves (rare resource caverns)

**Blocks:**
- Essence Stone Ore / Deepslate Essence Stone Ore, Spirit Spring, Moon Orchid Clusters, Crystal Stalactites
- Gu Refining Furnace (refining recipes), Gu Rack (display), Wine Jar, Formation Stone
- Blessed Land Core (creates a personal dimension)

### Feature Systems

| System | Description |
|--------|-------------|
| **Immortal Aperture** | Personal pocket dimension; terrain/biomes/particles match Path affinity; can be invaded |
| **Heaven's Will** | Higher cultivation draws more attention, triggering lightning strikes, suppression, or divine punishment |
| **Luck System** | Defeating enemies steals luck; influences Gu refining success rate and loot drops |
| **Bloodline System** | Randomly assigned one of 6 bloodlines upon aperture opening — Dragon's Might is the rarest |
| **Dream Exploration** | Enter dreams to gain Dao Marks, recipes, or Primeval Essence; 5 types of dream events |
| **Transformation System** | 3 forms: Wolf Form, Bear Form, and Earth Shrink |
| **Formation System** | Cross-shaped placement; Trapping Formation, Shield Formation, Heaven and Earth Grand Formation |
| **Poison Oath System** | Short-term buffs + penalties for breaking oaths; affects luck and lifespan |
| **Gate of Life and Death** | 50% gamble; luck influences the outcome |
| **Avatar System** | Phantom avatar grants 20% dodge chance + 40% bonus damage |
| **Deduction System** | Research new Killer Moves; consumes time and resources |
| **Tribulation System** | 4 types of Earthly Tribulations + 3 types of Heavenly Tribulations; defend your Immortal Aperture |
| **Faction Reputation** | Family/faction reputation affects trade discounts |

### Keybinds

| Key | Function |
|-----|----------|
| **G** | Open Aperture Management |
| **H** | Open Immortal Aperture interface |
| **J** | Open Deduction interface |
| **K** | Open Gu Insect Codex |

---

## Installation

1. Install [Minecraft 1.21.1](https://www.minecraft.net/)
2. Install [NeoForge 1.21.1](https://neoforged.net/)
3. Place the mod jar file in `.minecraft/mods/`
4. Launch the game

## Build

```bash
# Requires Java 21
./gradlew build
```

Build output is located in `build/libs/`.

---

## Acknowledgments

- *Reverend Insanity* — Gu Zhen Ren
- This project is a fan-made derivative work for learning and archival purposes only.

## License

MIT
