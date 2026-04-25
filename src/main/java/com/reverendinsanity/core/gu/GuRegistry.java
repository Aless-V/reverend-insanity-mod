package com.reverendinsanity.core.gu;

import com.reverendinsanity.core.path.DaoPath;
import com.reverendinsanity.ReverendInsanity;
import net.minecraft.resources.ResourceLocation;
import java.util.*;
import java.util.stream.Collectors;

// 蛊虫注册表
public class GuRegistry {

    private static final Map<ResourceLocation, GuType> REGISTRY = new LinkedHashMap<>();

    public static void register(GuType type) {
        REGISTRY.put(type.id(), type);
    }

    public static GuType get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<GuType> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static List<GuType> getByPath(DaoPath path) {
        return REGISTRY.values().stream()
            .filter(g -> g.path() == path)
            .collect(Collectors.toList());
    }

    public static List<GuType> getByRank(int rank) {
        return REGISTRY.values().stream()
            .filter(g -> g.rank() == rank)
            .collect(Collectors.toList());
    }

    public static List<GuType> getByCategory(GuType.GuCategory category) {
        return REGISTRY.values().stream()
            .filter(g -> g.category() == category)
            .collect(Collectors.toList());
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, name);
    }

    public static void registerDefaults() {
        register(new GuType(id("hope_gu"), "Hope Gu", 1, DaoPath.HUMAN, GuType.GuCategory.SPECIAL, 0, 0, ""));
        register(new GuType(id("moonlight_gu"), "Moonlight Gu", 1, DaoPath.MOON, GuType.GuCategory.ATTACK, 10, 60, "reverend_insanity:moon_orchid_petal"));
        register(new GuType(id("liquor_worm"), "Liquor Worm", 1, DaoPath.FOOD, GuType.GuCategory.SUPPORT, 5, 120, "minecraft:potion"));
        register(new GuType(id("bear_strength_gu"), "Bear Strength Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.ATTACK, 8, 90, "minecraft:honeycomb"));
        register(new GuType(id("jade_skin_gu"), "Jade Skin Gu", 1, DaoPath.METAL, GuType.GuCategory.DEFENSE, 8, 90, "minecraft:copper_ingot"));
        register(new GuType(id("white_boar_gu"), "White Boar Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.ATTACK, 12, 90, "minecraft:porkchop"));
        register(new GuType(id("stealth_scales_gu"), "Stealth Scales Gu", 1, DaoPath.WOOD, GuType.GuCategory.DEFENSE, 15, 90, "minecraft:string"));
        register(new GuType(id("four_flavors_liquor_worm"), "Four Flavors Liquor Worm", 2, DaoPath.FOOD, GuType.GuCategory.SUPPORT, 8, 150, "minecraft:honey_bottle"));
        register(new GuType(id("gold_light_worm"), "Gold Light Worm", 2, DaoPath.METAL, GuType.GuCategory.ATTACK, 20, 90, "minecraft:gold_ingot"));
        register(new GuType(id("iron_bone_gu"), "Iron Bone Gu", 2, DaoPath.STRENGTH, GuType.GuCategory.DEFENSE, 16, 120, "minecraft:iron_ingot"));
        register(new GuType(id("enslave_snake_gu"), "Enslave Snake Gu", 2, DaoPath.ENSLAVE, GuType.GuCategory.ENSLAVE, 25, 120, "minecraft:spider_eye"));
        register(new GuType(id("moonscar_gu"), "Moonscar Gu", 2, DaoPath.MOON, GuType.GuCategory.ATTACK, 18, 90, "reverend_insanity:moon_orchid_petal"));
        register(new GuType(id("silver_moon_gu"), "Silver Moon Gu", 3, DaoPath.MOON, GuType.GuCategory.ATTACK, 30, 120, "reverend_insanity:moon_orchid_petal"));
        register(new GuType(id("white_jade_gu"), "White Jade Gu", 3, DaoPath.METAL, GuType.GuCategory.DEFENSE, 25, 150, "minecraft:emerald"));
        register(new GuType(id("heavens_eye_gu"), "Heavens Eye Gu", 3, DaoPath.WISDOM, GuType.GuCategory.DETECTION, 20, 150, "minecraft:ender_eye"));

        register(new GuType(id("flesh_bone_gu"), "Flesh Bone Gu", 1, DaoPath.BLOOD, GuType.GuCategory.HEALING, 15, 90, "minecraft:rotten_flesh"));
        register(new GuType(id("displacement_gu"), "Displacement Gu", 2, DaoPath.SPACE, GuType.GuCategory.MOVEMENT, 20, 120, "minecraft:chorus_fruit"));

        register(new GuType(id("spring_autumn_cicada"), "Spring Autumn Cicada", 6, DaoPath.TIME, GuType.GuCategory.SPECIAL, 5000, 0, ""));

        register(new GuType(id("blood_gu"), "Blood Gu", 1, DaoPath.BLOOD, GuType.GuCategory.ATTACK, 8, 90, "minecraft:rotten_flesh"));
        register(new GuType(id("self_heal_gu"), "Self Heal Gu", 1, DaoPath.BLOOD, GuType.GuCategory.HEALING, 10, 90, "minecraft:rotten_flesh"));
        register(new GuType(id("solidify_origin_gu"), "Solidify Origin Gu", 1, DaoPath.BLOOD, GuType.GuCategory.SUPPORT, 12, 90, "minecraft:rotten_flesh"));
        register(new GuType(id("blood_wing_gu"), "Blood Wing Gu", 2, DaoPath.BLOOD, GuType.GuCategory.MOVEMENT, 20, 120, "minecraft:spider_eye"));
        register(new GuType(id("poison_bee_gu"), "Poison Bee Gu", 1, DaoPath.POISON, GuType.GuCategory.ATTACK, 10, 90, "minecraft:spider_eye"));
        register(new GuType(id("gold_silkworm_gu"), "Gold Silkworm Gu", 2, DaoPath.POISON, GuType.GuCategory.ENSLAVE, 25, 120, "minecraft:gold_ingot"));

        register(new GuType(id("savage_bull_gu"), "Savage Bull Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.ATTACK, 5, 90, "minecraft:honeycomb"));
        register(new GuType(id("taishan_gu"), "Taishan Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.ATTACK, 12, 90, "minecraft:cobblestone"));
        register(new GuType(id("giant_strength_gu"), "Giant Strength Gu", 2, DaoPath.STRENGTH, GuType.GuCategory.SUPPORT, 18, 120, "minecraft:raw_iron"));
        register(new GuType(id("cold_ice_gu"), "Cold Ice Gu", 1, DaoPath.ICE, GuType.GuCategory.ATTACK, 8, 90, "minecraft:snowball"));
        register(new GuType(id("frost_armor_gu"), "Frost Armor Gu", 1, DaoPath.ICE, GuType.GuCategory.DEFENSE, 12, 90, "minecraft:packed_ice"));
        register(new GuType(id("ice_seal_gu"), "Ice Seal Gu", 2, DaoPath.ICE, GuType.GuCategory.ENSLAVE, 25, 120, "minecraft:blue_ice"));

        register(new GuType(id("fire_seed_gu"), "Fire Seed Gu", 1, DaoPath.FIRE, GuType.GuCategory.ATTACK, 8, 90, "minecraft:blaze_powder"));
        register(new GuType(id("flame_armor_gu"), "Flame Armor Gu", 1, DaoPath.FIRE, GuType.GuCategory.DEFENSE, 12, 90, "minecraft:magma_cream"));
        register(new GuType(id("blazing_flame_gu"), "Blazing Flame Gu", 2, DaoPath.FIRE, GuType.GuCategory.ATTACK, 20, 120, "minecraft:blaze_rod"));
        register(new GuType(id("earth_wall_gu"), "Earth Wall Gu", 1, DaoPath.EARTH, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:clay_ball"));
        register(new GuType(id("earth_split_gu"), "Earth Split Gu", 1, DaoPath.EARTH, GuType.GuCategory.ATTACK, 10, 90, "minecraft:gravel"));
        register(new GuType(id("petrify_gu"), "Petrify Gu", 2, DaoPath.EARTH, GuType.GuCategory.ENSLAVE, 20, 120, "minecraft:deepslate"));

        register(new GuType(id("breeze_gu"), "Breeze Gu", 1, DaoPath.WIND, GuType.GuCategory.MOVEMENT, 8, 90, "minecraft:feather"));
        register(new GuType(id("wind_blade_gu"), "Wind Blade Gu", 1, DaoPath.WIND, GuType.GuCategory.ATTACK, 10, 90, "minecraft:phantom_membrane"));
        register(new GuType(id("gale_gu"), "Gale Gu", 2, DaoPath.WIND, GuType.GuCategory.MOVEMENT, 18, 120, "minecraft:wind_charge"));
        register(new GuType(id("lightning_gu"), "Lightning Gu", 1, DaoPath.LIGHTNING, GuType.GuCategory.ATTACK, 12, 90, "minecraft:glowstone_dust"));
        register(new GuType(id("thunder_shield_gu"), "Thunder Shield Gu", 1, DaoPath.LIGHTNING, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:prismarine_shard"));
        register(new GuType(id("thunderstorm_gu"), "Thunderstorm Gu", 2, DaoPath.LIGHTNING, GuType.GuCategory.ATTACK, 25, 120, "minecraft:heart_of_the_sea"));

        register(new GuType(id("tide_gu"), "Tide Gu", 1, DaoPath.WATER, GuType.GuCategory.ATTACK, 10, 90, "minecraft:kelp"));
        register(new GuType(id("water_shield_gu"), "Water Shield Gu", 1, DaoPath.WATER, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:sea_pickle"));
        register(new GuType(id("torrent_gu"), "Torrent Gu", 2, DaoPath.WATER, GuType.GuCategory.ATTACK, 22, 120, "minecraft:nautilus_shell"));
        register(new GuType(id("soul_search_gu"), "Soul Search Gu", 1, DaoPath.SOUL, GuType.GuCategory.ATTACK, 12, 90, "minecraft:echo_shard"));
        register(new GuType(id("soul_shield_gu"), "Soul Shield Gu", 1, DaoPath.SOUL, GuType.GuCategory.DEFENSE, 8, 90, "minecraft:amethyst_shard"));
        register(new GuType(id("soul_crush_gu"), "Soul Crush Gu", 2, DaoPath.SOUL, GuType.GuCategory.ATTACK, 25, 120, "minecraft:sculk_catalyst"));

        register(new GuType(id("light_beam_gu"), "Light Beam Gu", 1, DaoPath.LIGHT, GuType.GuCategory.ATTACK, 8, 90, "minecraft:glowstone"));
        register(new GuType(id("radiance_gu"), "Radiance Gu", 1, DaoPath.LIGHT, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:glow_berries"));
        register(new GuType(id("blazing_light_gu"), "Blazing Light Gu", 2, DaoPath.LIGHT, GuType.GuCategory.ATTACK, 20, 120, "minecraft:sea_lantern"));
        register(new GuType(id("dark_bolt_gu"), "Dark Bolt Gu", 1, DaoPath.DARK, GuType.GuCategory.ATTACK, 10, 90, "minecraft:ink_sac"));
        register(new GuType(id("shadow_cloak_gu"), "Shadow Cloak Gu", 1, DaoPath.DARK, GuType.GuCategory.DEFENSE, 8, 90, "minecraft:coal"));
        register(new GuType(id("abyss_devour_gu"), "Abbys Devour Gu", 2, DaoPath.DARK, GuType.GuCategory.ATTACK, 22, 120, "minecraft:obsidian"));

        register(new GuType(id("dream_gu"), "Dream Gu", 1, DaoPath.DREAM, GuType.GuCategory.ATTACK, 10, 90, "minecraft:chorus_fruit"));
        register(new GuType(id("lucid_dream_gu"), "Lucid Dream Gu", 1, DaoPath.DREAM, GuType.GuCategory.DEFENSE, 8, 90, "minecraft:spider_eye"));
        register(new GuType(id("nightmare_gu"), "Nightmare Gu", 2, DaoPath.DREAM, GuType.GuCategory.ATTACK, 22, 120, "minecraft:fermented_spider_eye"));
        register(new GuType(id("phantom_gu"), "Phantom Gu", 1, DaoPath.ILLUSION, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:ender_pearl"));
        register(new GuType(id("mirage_gu"), "Mirage Gu", 1, DaoPath.ILLUSION, GuType.GuCategory.ATTACK, 8, 90, "minecraft:prismarine_shard"));
        register(new GuType(id("grand_illusion_gu"), "Grand Illusion Gu", 2, DaoPath.ILLUSION, GuType.GuCategory.ATTACK, 20, 120, "minecraft:ender_eye"));

        register(new GuType(id("flying_sword_gu"), "Flying Sword Gu", 1, DaoPath.SWORD, GuType.GuCategory.ATTACK, 10, 90, "minecraft:iron_sword"));
        register(new GuType(id("sword_shield_gu"), "Sword Shield Gu", 1, DaoPath.SWORD, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:iron_nugget"));
        register(new GuType(id("myriad_sword_gu"), "Myriad Sword Gu", 2, DaoPath.SWORD, GuType.GuCategory.ATTACK, 22, 120, "minecraft:diamond_sword"));
        register(new GuType(id("moon_slash_gu"), "Moon Slash Gu", 1, DaoPath.BLADE, GuType.GuCategory.ATTACK, 8, 90, "minecraft:iron_axe"));
        register(new GuType(id("blade_armor_gu"), "Blade Armor Gu", 1, DaoPath.BLADE, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:leather"));
        register(new GuType(id("heaven_blade_gu"), "Heaven Blade Gu", 2, DaoPath.BLADE, GuType.GuCategory.ATTACK, 20, 120, "minecraft:netherite_scrap"));

        register(new GuType(id("starlight_gu"), "Starlight Gu", 1, DaoPath.STAR, GuType.GuCategory.ATTACK, 10, 90, "minecraft:amethyst_shard"));
        register(new GuType(id("star_shield_gu"), "Star Shield Gu", 1, DaoPath.STAR, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:prismarine_shard"));
        register(new GuType(id("star_fall_gu"), "Star Fall Gu", 2, DaoPath.STAR, GuType.GuCategory.ATTACK, 22, 120, "minecraft:nether_star"));
        register(new GuType(id("lucky_gu"), "Lucky Gu", 1, DaoPath.LUCK, GuType.GuCategory.SUPPORT, 8, 90, "minecraft:rabbit_foot"));
        register(new GuType(id("misfortune_ward_gu"), "Misfortune Ward Gu", 1, DaoPath.LUCK, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:totem_of_undying"));
        register(new GuType(id("heavens_secret_gu"), "Heavens Secret Gu", 2, DaoPath.LUCK, GuType.GuCategory.ATTACK, 25, 120, "minecraft:ender_eye"));

        register(new GuType(id("kill_intent_gu"), "Kill Intent Gu", 1, DaoPath.KILL, GuType.GuCategory.ATTACK, 10, 90, "minecraft:flint"));
        register(new GuType(id("killing_chance_gu"), "Killing Chance Gu", 1, DaoPath.KILL, GuType.GuCategory.SUPPORT, 8, 90, "minecraft:bone"));
        register(new GuType(id("death_strike_gu"), "Death Strike Gu", 2, DaoPath.KILL, GuType.GuCategory.ATTACK, 22, 120, "minecraft:wither_skeleton_skull"));
        register(new GuType(id("shrink_ground_gu"), "Shrink Ground Gu", 1, DaoPath.TRANSFORMATION, GuType.GuCategory.MOVEMENT, 10, 90, "minecraft:chorus_fruit"));
        register(new GuType(id("morph_gu"), "Morph Gu", 1, DaoPath.TRANSFORMATION, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:rabbit_hide"));
        register(new GuType(id("heaven_change_gu"), "Heaven Change Gu", 2, DaoPath.TRANSFORMATION, GuType.GuCategory.ATTACK, 20, 120, "minecraft:dragon_breath"));

        register(new GuType(id("formation_soldier_gu"), "Formation Soldier Gu", 1, DaoPath.SOLDIER, GuType.GuCategory.ATTACK, 10, 90, "minecraft:arrow"));
        register(new GuType(id("golden_armor_gu"), "Golden Armor Gu", 1, DaoPath.SOLDIER, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:iron_ingot"));
        register(new GuType(id("thousand_army_gu"), "Thousand Army Gu", 2, DaoPath.SOLDIER, GuType.GuCategory.ATTACK, 22, 120, "minecraft:crossbow"));
        register(new GuType(id("sound_wave_gu"), "Sound Wave Gu", 1, DaoPath.SOUND, GuType.GuCategory.ATTACK, 8, 90, "minecraft:note_block"));
        register(new GuType(id("silence_gu"), "Silence Gu", 1, DaoPath.SOUND, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:white_wool"));
        register(new GuType(id("heavenly_sound_gu"), "Heavenly Sound Gu", 2, DaoPath.SOUND, GuType.GuCategory.ATTACK, 20, 120, "minecraft:bell"));

        register(new GuType(id("bone_spear_gu"), "Bone Spear Gu", 1, DaoPath.BONE, GuType.GuCategory.ATTACK, 8, 90, "minecraft:bone"));
        register(new GuType(id("bone_armor_gu"), "Bone Armor Gu", 1, DaoPath.BONE, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:bone_meal"));
        register(new GuType(id("white_bone_gu"), "White Bone Gu", 2, DaoPath.BONE, GuType.GuCategory.ATTACK, 22, 120, "minecraft:bone_block"));
        register(new GuType(id("cloud_ride_gu"), "Cloud Ride Gu", 1, DaoPath.FLIGHT, GuType.GuCategory.MOVEMENT, 10, 90, "minecraft:feather"));
        register(new GuType(id("flight_wing_gu"), "Flight Wing Gu", 1, DaoPath.FLIGHT, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:phantom_membrane"));
        register(new GuType(id("sky_eagle_gu"), "Sky Eagle Gu", 2, DaoPath.FLIGHT, GuType.GuCategory.ATTACK, 20, 120, "minecraft:elytra"));

        register(new GuType(id("true_qi_gu"), "True Qi Gu", 1, DaoPath.QI, GuType.GuCategory.HEALING, 8, 90, "minecraft:glow_berries"));
        register(new GuType(id("qi_shield_gu"), "Qi Shield Gu", 1, DaoPath.QI, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:turtle_scute"));
        register(new GuType(id("profound_qi_gu"), "Profound Qi Gu", 2, DaoPath.QI, GuType.GuCategory.ATTACK, 22, 120, "minecraft:breeze_rod"));
        register(new GuType(id("yin_yang_gu"), "Yin Yang Gu", 1, DaoPath.YIN_YANG, GuType.GuCategory.ATTACK, 10, 90, "minecraft:ender_pearl"));
        register(new GuType(id("tai_chi_gu"), "Tai Chi Gu", 1, DaoPath.YIN_YANG, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:amethyst_shard"));
        register(new GuType(id("primordial_gu"), "Primordial Gu", 2, DaoPath.YIN_YANG, GuType.GuCategory.ATTACK, 22, 120, "minecraft:nether_star"));

        register(new GuType(id("warp_gu"), "Warp Gu", 1, DaoPath.SPACE, GuType.GuCategory.ATTACK, 8, 90, "minecraft:chorus_fruit"));
        register(new GuType(id("space_barrier_gu"), "Space Barrier Gu", 1, DaoPath.SPACE, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:ender_pearl"));

        register(new GuType(id("time_decel_gu"), "Time Decel Gu", 1, DaoPath.TIME, GuType.GuCategory.ATTACK, 10, 90, "minecraft:clock"));
        register(new GuType(id("time_shield_gu"), "Time Shield Gu", 1, DaoPath.TIME, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:echo_shard"));
        register(new GuType(id("time_reversal_gu"), "Time Reversal Gu", 2, DaoPath.TIME, GuType.GuCategory.HEALING, 22, 120, "minecraft:recovery_compass"));

        register(new GuType(id("charm_gu"), "Charm Gu", 1, DaoPath.CHARM, GuType.GuCategory.ATTACK, 10, 90, "minecraft:glow_berries"));
        register(new GuType(id("bewitch_gu"), "Bewitch Gu", 1, DaoPath.CHARM, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:pink_petals"));
        register(new GuType(id("soul_charm_gu"), "Soul Charm Gu", 2, DaoPath.CHARM, GuType.GuCategory.ENSLAVE, 22, 120, "minecraft:sculk_catalyst"));

        register(new GuType(id("thought_gu"), "Thought Gu", 1, DaoPath.WISDOM, GuType.GuCategory.ATTACK, 8, 90, "minecraft:book"));
        register(new GuType(id("mind_guard_gu"), "Mind Guard Gu", 1, DaoPath.WISDOM, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:amethyst_shard"));

        register(new GuType(id("void_bolt_gu"), "Void Bolt Gu", 1, DaoPath.VOID, GuType.GuCategory.ATTACK, 10, 90, "minecraft:ender_pearl"));
        register(new GuType(id("void_cloak_gu"), "Void Cloak Gu", 1, DaoPath.VOID, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:chorus_flower"));
        register(new GuType(id("void_annihilation_gu"), "Void Annihilation Gu", 2, DaoPath.VOID, GuType.GuCategory.ATTACK, 22, 120, "minecraft:end_crystal"));

        register(new GuType(id("seal_gu"), "Seal Gu", 1, DaoPath.RESTRICTION, GuType.GuCategory.ATTACK, 10, 90, "minecraft:chain"));
        register(new GuType(id("restriction_gu"), "Restriction Gu", 1, DaoPath.RESTRICTION, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:iron_bars"));
        register(new GuType(id("heaven_seal_gu"), "Heaven Seal Gu", 2, DaoPath.RESTRICTION, GuType.GuCategory.ENSLAVE, 22, 120, "minecraft:lodestone"));

        register(new GuType(id("heaven_will_gu"), "Heaven Will Gu", 1, DaoPath.HEAVEN, GuType.GuCategory.ATTACK, 10, 90, "minecraft:nether_star"));
        register(new GuType(id("heaven_shield_gu"), "Heaven Shield Gu", 1, DaoPath.HEAVEN, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:totem_of_undying"));
        register(new GuType(id("heaven_punishment_gu"), "Heaven Punishment Gu", 2, DaoPath.HEAVEN, GuType.GuCategory.ATTACK, 22, 120, "minecraft:end_crystal"));

        register(new GuType(id("rule_gu"), "Rule Gu", 1, DaoPath.RULE, GuType.GuCategory.ATTACK, 10, 90, "minecraft:chain"));
        register(new GuType(id("order_gu"), "Order Gu", 1, DaoPath.RULE, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:iron_bars"));
        register(new GuType(id("supreme_law_gu"), "Supreme Law Gu", 2, DaoPath.RULE, GuType.GuCategory.ATTACK, 22, 120, "minecraft:lodestone"));

        register(new GuType(id("shadow_dart_gu"), "Shadow Dart Gu", 1, DaoPath.SHADOW, GuType.GuCategory.ATTACK, 8, 90, "minecraft:ink_sac"));
        register(new GuType(id("shadow_veil_gu"), "Shadow Veil Gu", 1, DaoPath.SHADOW, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:coal"));
        register(new GuType(id("shadow_devour_gu"), "Shadow Devour Gu", 2, DaoPath.SHADOW, GuType.GuCategory.ATTACK, 22, 120, "minecraft:obsidian"));

        register(new GuType(id("mist_gu"), "Mist Gu", 1, DaoPath.CLOUD, GuType.GuCategory.ATTACK, 8, 90, "minecraft:white_wool"));
        register(new GuType(id("cloud_armor_gu"), "Cloud Armor Gu", 1, DaoPath.CLOUD, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:cobweb"));
        register(new GuType(id("cloud_storm_gu"), "Cloud Storm Gu", 2, DaoPath.CLOUD, GuType.GuCategory.ATTACK, 22, 120, "minecraft:lightning_rod"));

        register(new GuType(id("trap_formation_gu"), "Trap Formation Gu", 1, DaoPath.FORMATION, GuType.GuCategory.ATTACK, 10, 90, "minecraft:redstone"));
        register(new GuType(id("formation_shield_gu"), "Formation Shield Gu", 1, DaoPath.FORMATION, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:lapis_lazuli"));
        register(new GuType(id("grand_formation_gu"), "Grand Formation Gu", 2, DaoPath.FORMATION, GuType.GuCategory.ATTACK, 22, 120, "minecraft:enchanted_book"));

        register(new GuType(id("refine_fire_gu"), "Refine Fire Gu", 1, DaoPath.REFINEMENT, GuType.GuCategory.ATTACK, 10, 90, "minecraft:blaze_powder"));
        register(new GuType(id("refine_body_gu"), "Refine Body Gu", 1, DaoPath.REFINEMENT, GuType.GuCategory.HEALING, 10, 90, "minecraft:raw_iron"));
        register(new GuType(id("heaven_refine_gu"), "Heaven Refine Gu", 2, DaoPath.REFINEMENT, GuType.GuCategory.ATTACK, 22, 120, "minecraft:blaze_rod"));

        register(new GuType(id("pill_poison_gu"), "Pill Poison Gu", 1, DaoPath.PILL, GuType.GuCategory.ATTACK, 8, 90, "minecraft:spider_eye"));
        register(new GuType(id("pill_shield_gu"), "Pill Shield Gu", 1, DaoPath.PILL, GuType.GuCategory.HEALING, 10, 90, "minecraft:glistering_melon_slice"));
        register(new GuType(id("immortal_pill_gu"), "Immortal Pill Gu", 2, DaoPath.PILL, GuType.GuCategory.ATTACK, 22, 120, "minecraft:golden_apple"));

        register(new GuType(id("paint_brush_gu"), "Paint Brush Gu", 1, DaoPath.PAINT, GuType.GuCategory.ATTACK, 8, 90, "minecraft:pink_dye"));
        register(new GuType(id("paint_shield_gu"), "Paint Shield Gu", 1, DaoPath.PAINT, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:painting"));
        register(new GuType(id("myriad_paint_gu"), "Myriad Panit Gu", 2, DaoPath.PAINT, GuType.GuCategory.ATTACK, 22, 120, "minecraft:glow_ink_sac"));

        register(new GuType(id("steal_qi_gu"), "Steal Qi Gu", 1, DaoPath.STEAL, GuType.GuCategory.ATTACK, 8, 90, "minecraft:fermented_spider_eye"));
        register(new GuType(id("steal_hide_gu"), "Steal Hide Gu", 1, DaoPath.STEAL, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:leather"));
        register(new GuType(id("heaven_steal_gu"), "Heaven Steal Gu", 2, DaoPath.STEAL, GuType.GuCategory.ATTACK, 22, 120, "minecraft:ender_eye"));

        register(new GuType(id("info_dart_gu"), "Info Dart Gu", 1, DaoPath.INFORMATION, GuType.GuCategory.ATTACK, 8, 90, "minecraft:paper"));
        register(new GuType(id("info_net_gu"), "Info Net Gu", 1, DaoPath.INFORMATION, GuType.GuCategory.SUPPORT, 10, 90, "minecraft:book"));
        register(new GuType(id("heaven_info_gu"), "Heaven Info Gu", 2, DaoPath.INFORMATION, GuType.GuCategory.ATTACK, 22, 120, "minecraft:writable_book"));

        register(new GuType(id("human_heart_gu"), "Human Heart Gu", 1, DaoPath.HUMAN, GuType.GuCategory.ATTACK, 10, 90, "minecraft:golden_apple"));
        register(new GuType(id("human_bond_gu"), "Human Blood Gu", 1, DaoPath.HUMAN, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:bread"));
        register(new GuType(id("human_will_gu"), "Human Will Gu", 2, DaoPath.HUMAN, GuType.GuCategory.ATTACK, 22, 120, "minecraft:enchanted_golden_apple"));

        register(new GuType(id("enslave_worm_gu"), "Ensalve Worm Gu", 1, DaoPath.ENSLAVE, GuType.GuCategory.ATTACK, 8, 90, "minecraft:spider_eye"));
        register(new GuType(id("enslave_shield_gu"), "Enslave Shield Gu", 1, DaoPath.ENSLAVE, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:lead"));

        register(new GuType(id("feast_gu"), "Feast Gu", 1, DaoPath.FOOD, GuType.GuCategory.ATTACK, 8, 90, "minecraft:cooked_beef"));

        register(new GuType(id("snake_tongue_gu"), "Snake Tongue Gu", 2, DaoPath.WISDOM, GuType.GuCategory.DETECTION, 20, 120, "minecraft:string"));
        register(new GuType(id("earth_listener_gu"), "Earth Listener Gu", 2, DaoPath.WISDOM, GuType.GuCategory.DETECTION, 25, 120, "minecraft:glistering_melon_slice"));
        register(new GuType(id("keen_ear_gu"), "Keen Ear gu", 2, DaoPath.WISDOM, GuType.GuCategory.SUPPORT, 15, 120, "minecraft:brown_mushroom"));
        register(new GuType(id("hidden_scale_gu"), "Hidden Scale Gu", 2, DaoPath.SHADOW, GuType.GuCategory.DEFENSE, 20, 120, "minecraft:ink_sac"));
        register(new GuType(id("true_sight_gu"), "True Sight Gu", 3, DaoPath.LIGHT, GuType.GuCategory.DETECTION, 35, 150, "minecraft:glow_berries"));
        register(new GuType(id("electric_eye_gu"), "Electric Eye Gu", 1, DaoPath.LIGHTNING, GuType.GuCategory.DETECTION, 10, 90, "minecraft:copper_ingot"));

        register(new GuType(id("bronze_sarira_gu"), "Bronze Sarira Gu", 1, DaoPath.REFINEMENT, GuType.GuCategory.SUPPORT, 30, 0, "minecraft:clay_ball"));
        register(new GuType(id("iron_sarira_gu"), "Iron Sarira Gu", 2, DaoPath.REFINEMENT, GuType.GuCategory.SUPPORT, 60, 0, "minecraft:iron_nugget"));
        register(new GuType(id("silver_sarira_gu"), "Silver Sarira Gu", 3, DaoPath.REFINEMENT, GuType.GuCategory.SUPPORT, 100, 0, "minecraft:quartz"));
        register(new GuType(id("gold_sarira_gu"), "Gold Sarira Gu", 4, DaoPath.REFINEMENT, GuType.GuCategory.SUPPORT, 150, 0, "minecraft:gold_nugget"));
        register(new GuType(id("black_boar_gu"), "Black Boar Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.SUPPORT, 20, 90, "minecraft:porkchop"));
        register(new GuType(id("brown_bear_gu"), "Brown Bear Gu", 2, DaoPath.STRENGTH, GuType.GuCategory.SUPPORT, 40, 120, "minecraft:cooked_beef"));
        register(new GuType(id("flower_boar_gu"), "Flower Boar Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.ATTACK, 5, 90, "minecraft:pink_petals"));
        register(new GuType(id("yellow_camel_beetle_gu"), "Yellow Camel Beetle Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.DEFENSE, 5, 90, "minecraft:honeycomb"));

        register(new GuType(id("stone_skin_gu"), "Stone Skin Gu", 1, DaoPath.METAL, GuType.GuCategory.DEFENSE, 6, 90, "minecraft:cobblestone"));
        register(new GuType(id("iron_skin_gu"), "Iron Skin Gu", 1, DaoPath.METAL, GuType.GuCategory.DEFENSE, 10, 90, "minecraft:iron_ingot"));
        register(new GuType(id("beast_skin_gu"), "Beast Skin Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.DEFENSE, 4, 90, "minecraft:leather"));
        register(new GuType(id("black_bristle_gu"), "Black Bristle Gu", 2, DaoPath.STRENGTH, GuType.GuCategory.DEFENSE, 15, 120, "minecraft:black_wool"));
        register(new GuType(id("steel_bristle_gu"), "Steel Bristle Gu", 3, DaoPath.STRENGTH, GuType.GuCategory.DEFENSE, 25, 150, "minecraft:iron_block"));
        register(new GuType(id("heaven_canopy_gu"), "Heaven Canopy Gu", 3, DaoPath.METAL, GuType.GuCategory.DEFENSE, 40, 150, "minecraft:golden_apple"));
        register(new GuType(id("vitality_grass_gu"), "Vitality Grass Gu", 2, DaoPath.WOOD, GuType.GuCategory.HEALING, 20, 120, "minecraft:fern"));
        register(new GuType(id("vitality_leaf_gu"), "Vitality Leaf Gu", 1, DaoPath.WOOD, GuType.GuCategory.HEALING, 3, 90, "minecraft:oak_leaves"));
        register(new GuType(id("water_spider_gu"), "Water Spider Gu", 1, DaoPath.WATER, GuType.GuCategory.DEFENSE, 8, 90, "minecraft:prismarine_shard"));

        register(new GuType(id("signal_gu"), "Signal Gu", 1, DaoPath.INFORMATION, GuType.GuCategory.SUPPORT, 5, 0, "minecraft:firework_rocket"));
        register(new GuType(id("flash_gu"), "Flash Gu", 1, DaoPath.LIGHT, GuType.GuCategory.ATTACK, 8, 0, "minecraft:glowstone_dust"));
        register(new GuType(id("shadow_follower_gu"), "Shadow Follower Gu", 2, DaoPath.SHADOW, GuType.GuCategory.DEFENSE, 20, 120, "minecraft:ink_sac"));
        register(new GuType(id("dragon_cricket_gu"), "Dragon Cricket Gu", 1, DaoPath.STRENGTH, GuType.GuCategory.MOVEMENT, 8, 90, "minecraft:slime_ball"));
        register(new GuType(id("quiet_step_gu"), "Quiet Step Gu", 1, DaoPath.SHADOW, GuType.GuCategory.MOVEMENT, 6, 90, "minecraft:rabbit_foot"));
        register(new GuType(id("scent_lock_gu"), "Scent Lock Gu", 1, DaoPath.WISDOM, GuType.GuCategory.DETECTION, 5, 90, "minecraft:brown_mushroom"));
        register(new GuType(id("love_separation_gu"), "Love Seperation Gu", 2, DaoPath.CHARM, GuType.GuCategory.ATTACK, 30, 120, "minecraft:rose_bush"));
    }
}
