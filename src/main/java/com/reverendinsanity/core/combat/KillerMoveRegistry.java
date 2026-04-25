package com.reverendinsanity.core.combat;

import com.reverendinsanity.ReverendInsanity;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.core.path.DaoPath;
import net.minecraft.resources.ResourceLocation;
import java.util.*;
import java.util.stream.Collectors;

// 杀招注册表
public class KillerMoveRegistry {

    private static final Map<ResourceLocation, KillerMove> REGISTRY = new LinkedHashMap<>();

    public static void register(KillerMove move) {
        REGISTRY.put(move.id(), move);
    }

    public static KillerMove get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<KillerMove> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static List<KillerMove> getByType(KillerMove.MoveType type) {
        return REGISTRY.values().stream()
                .filter(m -> m.moveType() == type)
                .collect(Collectors.toList());
    }

    public static List<KillerMove> getUsableByRank(int rank) {
        return REGISTRY.values().stream()
                .filter(m -> m.minRank() <= rank)
                .collect(Collectors.toList());
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(ReverendInsanity.MODID, name);
    }

    public static void registerDefaults() {
        register(new KillerMove(
                id("moon_blade_storm"), "Moon Blade Storm", DaoPath.MOON, 1,
                GuRegistry.id("moonlight_gu"),
                List.of(GuRegistry.id("bear_strength_gu"), GuRegistry.id("white_boar_gu")),
                40f, 50f, 20f, 200, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("bronze_wall"), "Bronze Wall", DaoPath.METAL, 1,
                GuRegistry.id("jade_skin_gu"),
                List.of(GuRegistry.id("bear_strength_gu"), GuRegistry.id("liquor_worm")),
                35f, 40f, 15f, 400, KillerMove.MoveType.DEFENSE));

        register(new KillerMove(
                id("shadow_strike"), "Shadow Strike", DaoPath.WOOD, 1,
                GuRegistry.id("stealth_scales_gu"),
                List.of(GuRegistry.id("moonlight_gu"), GuRegistry.id("white_boar_gu")),
                45f, 60f, 18f, 500, KillerMove.MoveType.MOVEMENT));

        register(new KillerMove(
                id("gold_iron_bastion"), "Gold Iron Bastion", DaoPath.METAL, 2,
                GuRegistry.id("iron_bone_gu"),
                List.of(GuRegistry.id("gold_light_worm"), GuRegistry.id("jade_skin_gu")),
                55f, 70f, 0f, 600, KillerMove.MoveType.DEFENSE));

        register(new KillerMove(
                id("moonscar_chain_slash"), "Moonscar Chain Slash", DaoPath.MOON, 2,
                GuRegistry.id("moonscar_gu"),
                List.of(GuRegistry.id("bear_strength_gu"), GuRegistry.id("four_flavors_liquor_worm")),
                60f, 80f, 30f, 400, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("silver_moon_tempest"), "Silver Moon Tempest", DaoPath.MOON, 3,
                GuRegistry.id("silver_moon_gu"),
                List.of(GuRegistry.id("moonscar_gu"), GuRegistry.id("moonlight_gu")),
                80f, 100f, 25f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("blood_rain_skyward"), "Blood Rain Skyward", DaoPath.BLOOD, 1,
                GuRegistry.id("blood_gu"),
                List.of(GuRegistry.id("solidify_origin_gu"), GuRegistry.id("bear_strength_gu")),
                50f, 60f, 25f, 300, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("poison_mist_binding"), "Poison Mist Binding", DaoPath.POISON, 1,
                GuRegistry.id("poison_bee_gu"),
                List.of(GuRegistry.id("gold_silkworm_gu"), GuRegistry.id("stealth_scales_gu")),
                45f, 55f, 15f, 400, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("savage_bull_smash"), "Savage Bull Smash", DaoPath.STRENGTH, 1,
                GuRegistry.id("taishan_gu"),
                List.of(GuRegistry.id("savage_bull_gu"), GuRegistry.id("bear_strength_gu")),
                55f, 65f, 20f, 300, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("frost_domain"), "Frost Domain", DaoPath.ICE, 2,
                GuRegistry.id("ice_seal_gu"),
                List.of(GuRegistry.id("cold_ice_gu"), GuRegistry.id("frost_armor_gu")),
                70f, 85f, 20f, 500, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("blazing_heaven"), "Blazing Heaven", DaoPath.FIRE, 2,
                GuRegistry.id("blazing_flame_gu"),
                List.of(GuRegistry.id("fire_seed_gu"), GuRegistry.id("flame_armor_gu")),
                65f, 80f, 25f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("stone_prison"), "Stone Prison", DaoPath.EARTH, 2,
                GuRegistry.id("petrify_gu"),
                List.of(GuRegistry.id("earth_wall_gu"), GuRegistry.id("earth_split_gu")),
                60f, 75f, 18f, 600, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("raging_gale"), "Raging Gale", DaoPath.WIND, 2,
                GuRegistry.id("gale_gu"),
                List.of(GuRegistry.id("breeze_gu"), GuRegistry.id("wind_blade_gu")),
                70f, 85f, 22f, 500, KillerMove.MoveType.MOVEMENT));

        register(new KillerMove(
                id("thunder_judgment"), "Thunder Judgment", DaoPath.LIGHTNING, 2,
                GuRegistry.id("thunderstorm_gu"),
                List.of(GuRegistry.id("lightning_gu"), GuRegistry.id("thunder_shield_gu")),
                75f, 90f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("tide_fury"), "Tidal Fury", DaoPath.WATER, 2,
                GuRegistry.id("torrent_gu"),
                List.of(GuRegistry.id("tide_gu"), GuRegistry.id("water_shield_gu")),
                70f, 85f, 22f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("soul_annihilation"), "Soul Annihilation", DaoPath.SOUL, 2,
                GuRegistry.id("soul_crush_gu"),
                List.of(GuRegistry.id("soul_search_gu"), GuRegistry.id("soul_shield_gu")),
                80f, 100f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("sacred_light_judgment"), "Sacred Light Judgment", DaoPath.LIGHT, 2,
                GuRegistry.id("blazing_light_gu"),
                List.of(GuRegistry.id("light_beam_gu"), GuRegistry.id("radiance_gu")),
                70f, 85f, 24f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("dark_erosion"), "Dark Erosion", DaoPath.DARK, 2,
                GuRegistry.id("abyss_devour_gu"),
                List.of(GuRegistry.id("dark_bolt_gu"), GuRegistry.id("shadow_cloak_gu")),
                75f, 90f, 26f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("golden_dream"), "Dream Of A Golden Millet", DaoPath.DREAM, 2,
                GuRegistry.id("nightmare_gu"),
                List.of(GuRegistry.id("dream_gu"), GuRegistry.id("lucid_dream_gu")),
                75f, 90f, 25f, 500, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("myriad_illusion"), "Myriad Illusion", DaoPath.ILLUSION, 2,
                GuRegistry.id("grand_illusion_gu"),
                List.of(GuRegistry.id("phantom_gu"), GuRegistry.id("mirage_gu")),
                70f, 85f, 22f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("myriad_swords_return"), "Myriad Swords Unite", DaoPath.SWORD, 2,
                GuRegistry.id("myriad_sword_gu"),
                List.of(GuRegistry.id("flying_sword_gu"), GuRegistry.id("sword_shield_gu")),
                75f, 90f, 26f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("heaven_blade_sky_split"), "Heavenly Blade Sky Splitter", DaoPath.BLADE, 2,
                GuRegistry.id("heaven_blade_gu"),
                List.of(GuRegistry.id("moon_slash_gu"), GuRegistry.id("blade_armor_gu")),
                70f, 85f, 28f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("star_fall_rain"), "Starfall Rain", DaoPath.STAR, 2,
                GuRegistry.id("star_fall_gu"),
                List.of(GuRegistry.id("starlight_gu"), GuRegistry.id("star_shield_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("reverse_fate"), "Reverse Destiny", DaoPath.LUCK, 2,
                GuRegistry.id("heavens_secret_gu"),
                List.of(GuRegistry.id("lucky_gu"), GuRegistry.id("misfortune_ward_gu")),
                85f, 105f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("execute_no_mercy"), "Execute Without Mercy", DaoPath.KILL, 2,
                GuRegistry.id("death_strike_gu"),
                List.of(GuRegistry.id("kill_intent_gu"), GuRegistry.id("killing_chance_gu")),
                80f, 100f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("myriad_changes_return"), "All Changes Return to Source", DaoPath.TRANSFORMATION, 2,
                GuRegistry.id("heaven_change_gu"),
                List.of(GuRegistry.id("shrink_ground_gu"), GuRegistry.id("morph_gu")),
                70f, 85f, 24f, 500, KillerMove.MoveType.MOVEMENT));

        register(new KillerMove(
                id("army_formation_assault"), "Military Formation Strike", DaoPath.SOLDIER, 2,
                GuRegistry.id("thousand_army_gu"),
                List.of(GuRegistry.id("formation_soldier_gu"), GuRegistry.id("golden_armor_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("heavenly_melody_destruction"), "Heavenly Melody Destruction", DaoPath.SOUND, 2,
                GuRegistry.id("heavenly_sound_gu"),
                List.of(GuRegistry.id("sound_wave_gu"), GuRegistry.id("silence_gu")),
                75f, 90f, 26f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("bone_forest"), "Bone Forest", DaoPath.BONE, 2,
                GuRegistry.id("white_bone_gu"),
                List.of(GuRegistry.id("bone_spear_gu"), GuRegistry.id("bone_armor_gu")),
                70f, 85f, 24f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("sky_eagle_dive"), "Sky Eagle Dive", DaoPath.FLIGHT, 2,
                GuRegistry.id("sky_eagle_gu"),
                List.of(GuRegistry.id("cloud_ride_gu"), GuRegistry.id("flight_wing_gu")),
                75f, 90f, 26f, 600, KillerMove.MoveType.MOVEMENT));

        register(new KillerMove(
                id("profound_qi_explosion"), "Profound Qi Blast", DaoPath.QI, 2,
                GuRegistry.id("profound_qi_gu"),
                List.of(GuRegistry.id("true_qi_gu"), GuRegistry.id("qi_shield_gu")),
                75f, 90f, 26f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("primordial_chaos"), "Primordial Chaos", DaoPath.YIN_YANG, 2,
                GuRegistry.id("primordial_gu"),
                List.of(GuRegistry.id("yin_yang_gu"), GuRegistry.id("tai_chi_gu")),
                85f, 105f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("space_sunder"), "Space Sunder", DaoPath.SPACE, 2,
                GuRegistry.id("displacement_gu"),
                List.of(GuRegistry.id("warp_gu"), GuRegistry.id("space_barrier_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("time_paradox"), "Time Paradox", DaoPath.TIME, 2,
                GuRegistry.id("time_reversal_gu"),
                List.of(GuRegistry.id("time_decel_gu"), GuRegistry.id("time_shield_gu")),
                85f, 105f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("soul_charm_storm"), "Soul Charm Storm", DaoPath.CHARM, 2,
                GuRegistry.id("soul_charm_gu"),
                List.of(GuRegistry.id("charm_gu"), GuRegistry.id("bewitch_gu")),
                75f, 90f, 26f, 500, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("omniscience"), "Omniscience", DaoPath.WISDOM, 3,
                GuRegistry.id("heavens_eye_gu"),
                List.of(GuRegistry.id("thought_gu"), GuRegistry.id("mind_guard_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("void_collapse"), "Void Collapse", DaoPath.VOID, 2,
                GuRegistry.id("void_annihilation_gu"),
                List.of(GuRegistry.id("void_bolt_gu"), GuRegistry.id("void_cloak_gu")),
                85f, 105f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("heaven_earth_seal"), "Heaven and Earth Seal", DaoPath.RESTRICTION, 2,
                GuRegistry.id("heaven_seal_gu"),
                List.of(GuRegistry.id("seal_gu"), GuRegistry.id("restriction_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("heaven_wrath"), "Divine Wrath", DaoPath.HEAVEN, 2,
                GuRegistry.id("heaven_punishment_gu"),
                List.of(GuRegistry.id("heaven_will_gu"), GuRegistry.id("heaven_shield_gu")),
                85f, 105f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("supreme_judgment"), "Supreme Judgment", DaoPath.RULE, 2,
                GuRegistry.id("supreme_law_gu"),
                List.of(GuRegistry.id("rule_gu"), GuRegistry.id("order_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("shadow_annihilation"), "Shadow Annihilation", DaoPath.SHADOW, 2,
                GuRegistry.id("shadow_devour_gu"),
                List.of(GuRegistry.id("shadow_dart_gu"), GuRegistry.id("shadow_veil_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("cloud_calamity"), "Cloud Calamity", DaoPath.CLOUD, 2,
                GuRegistry.id("cloud_storm_gu"),
                List.of(GuRegistry.id("mist_gu"), GuRegistry.id("cloud_armor_gu")),
                75f, 90f, 26f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("grand_formation_seal"), "Grand Formation Seal", DaoPath.FORMATION, 2,
                GuRegistry.id("grand_formation_gu"),
                List.of(GuRegistry.id("trap_formation_gu"), GuRegistry.id("formation_shield_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("heaven_refine_destroy"), "Heavenly Refinement Destruction", DaoPath.REFINEMENT, 2,
                GuRegistry.id("heaven_refine_gu"),
                List.of(GuRegistry.id("refine_fire_gu"), GuRegistry.id("refine_body_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("immortal_pill_destroy"), "Immortal Pill Destruction", DaoPath.PILL, 2,
                GuRegistry.id("immortal_pill_gu"),
                List.of(GuRegistry.id("pill_poison_gu"), GuRegistry.id("pill_shield_gu")),
                75f, 90f, 26f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("myriad_paint_storm"), "Myriad Paint Storm", DaoPath.PAINT, 2,
                GuRegistry.id("myriad_paint_gu"),
                List.of(GuRegistry.id("paint_brush_gu"), GuRegistry.id("paint_shield_gu")),
                75f, 90f, 26f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("heaven_steal_annihilation"), "Heavenly Steal Annihilation", DaoPath.STEAL, 2,
                GuRegistry.id("heaven_steal_gu"),
                List.of(GuRegistry.id("steal_qi_gu"), GuRegistry.id("steal_hide_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("heaven_info_blast"), "Heavenly Information Blast", DaoPath.INFORMATION, 2,
                GuRegistry.id("heaven_info_gu"),
                List.of(GuRegistry.id("info_dart_gu"), GuRegistry.id("info_net_gu")),
                80f, 100f, 28f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("human_sovereign"), "Human Sovereign", DaoPath.HUMAN, 2,
                GuRegistry.id("human_will_gu"),
                List.of(GuRegistry.id("human_heart_gu"), GuRegistry.id("human_bond_gu")),
                85f, 105f, 30f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("soul_enslave"), "Soul Enslave", DaoPath.ENSLAVE, 2,
                GuRegistry.id("enslave_snake_gu"),
                List.of(GuRegistry.id("enslave_worm_gu"), GuRegistry.id("enslave_shield_gu")),
                75f, 90f, 26f, 500, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("drunken_frenzy"), "Drunken Frenzy", DaoPath.FOOD, 2,
                GuRegistry.id("four_flavors_liquor_worm"),
                List.of(GuRegistry.id("liquor_worm"), GuRegistry.id("feast_gu")),
                70f, 85f, 24f, 500, KillerMove.MoveType.ATTACK));

        // === Signature Killer Moves of Reverend Insanity ===

        register(new KillerMove(
                id("beast_phantom"), "Beast Phantom", DaoPath.STRENGTH, 3,
                GuRegistry.id("savage_bull_gu"),
                List.of(GuRegistry.id("bear_strength_gu"), GuRegistry.id("taishan_gu")),
                50f, 70f, 40f, 600, KillerMove.MoveType.BUFF));

        register(new KillerMove(
                id("hair_armor"), "Hair Armor", DaoPath.STRENGTH, 1,
                GuRegistry.id("green_hair_gu"),
                List.of(GuRegistry.id("bear_strength_gu")),
                20f, 25f, 15f, 300, KillerMove.MoveType.DEFENSE));

        register(new KillerMove(
                id("ice_blade_storm"), "Ice Blade Storm", DaoPath.ICE, 3,
                GuRegistry.id("cold_ice_gu"),
                List.of(GuRegistry.id("ice_seal_gu"), GuRegistry.id("wind_blade_gu")),
                75f, 90f, 30f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("primordial_light_fist"), "Primordial Light Fist", DaoPath.LIGHT, 3,
                GuRegistry.id("blazing_light_gu"),
                List.of(GuRegistry.id("light_beam_gu"), GuRegistry.id("radiance_gu"), GuRegistry.id("taishan_gu")),
                90f, 110f, 35f, 600, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("five_finger_fist_sword"), "Five-Fingered Fist Sword", DaoPath.SWORD, 3,
                GuRegistry.id("flying_sword_gu"),
                List.of(GuRegistry.id("myriad_sword_gu"), GuRegistry.id("bear_strength_gu")),
                85f, 100f, 30f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("steal_sky_change_sun"), "Steal Sky Change Sun", DaoPath.STEAL, 3,
                GuRegistry.id("heaven_steal_gu"),
                List.of(GuRegistry.id("steal_qi_gu"), GuRegistry.id("steal_hide_gu")),
                60f, 80f, 40f, 800, KillerMove.MoveType.CONTROL));

        register(new KillerMove(
                id("white_bone_chariot"), "White Bone Chariot", DaoPath.BONE, 3,
                GuRegistry.id("white_bone_gu"),
                List.of(GuRegistry.id("bone_spear_gu"), GuRegistry.id("bone_armor_gu")),
                80f, 95f, 30f, 500, KillerMove.MoveType.ATTACK));

        register(new KillerMove(
                id("myriad_self"), "Myriad Self", DaoPath.STRENGTH, 3,
                GuRegistry.id("bear_strength_gu"),
                List.of(GuRegistry.id("taishan_gu"), GuRegistry.id("savage_bull_gu"),
                        GuRegistry.id("enslave_snake_gu")),
                100f, 150f, 50f, 800, KillerMove.MoveType.ULTIMATE));

        register(new KillerMove(
                id("bat_wing_return"), "Bat Wing Return", DaoPath.WIND, 2,
                GuRegistry.id("gale_gu"),
                List.of(GuRegistry.id("breeze_gu"), GuRegistry.id("morph_gu")),
                40f, 55f, 25f, 400, KillerMove.MoveType.MOVEMENT));

        register(new KillerMove(
                id("three_hearts_soul"), "Three Hearts Soul", DaoPath.ENSLAVE, 3,
                GuRegistry.id("enslave_snake_gu"),
                List.of(GuRegistry.id("enslave_worm_gu"), GuRegistry.id("soul_search_gu")),
                70f, 90f, 45f, 700, KillerMove.MoveType.CONTROL));

        ReverendInsanity.LOGGER.info("Killer moves registered: {} total", REGISTRY.size());
    }
}
