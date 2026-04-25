package com.reverendinsanity.core.gu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

// 炼蛊配方注册表
public class RefinementRecipe {

    public record Ingredient(String itemId, int count) {}

    private final ResourceLocation id;
    private final List<Ingredient> ingredients;
    private final String outputItemId;
    private final String displayName;

    private static final List<RefinementRecipe> RECIPES = new ArrayList<>();

    public RefinementRecipe(ResourceLocation id, List<Ingredient> ingredients, String outputItemId, String displayName) {
        this.id = id;
        this.ingredients = ingredients;
        this.outputItemId = outputItemId;
        this.displayName = displayName;
    }

    public static void register(String name, List<Ingredient> ingredients, String outputItemId, String displayName) {
        RECIPES.add(new RefinementRecipe(
            ResourceLocation.fromNamespaceAndPath("reverend_insanity", name),
            ingredients, outputItemId, displayName
        ));
    }

    public static void registerDefaults() {
        register("moonlight_to_moonscar", List.of(
            new Ingredient("reverend_insanity:moonlight_gu", 1),
            new Ingredient("reverend_insanity:moon_orchid_petal", 4),
            new Ingredient("minecraft:amethyst_shard", 1)
        ), "reverend_insanity:moonscar_gu", "Moon Scar Gu");

        register("liquor_to_four_flavors", List.of(
            new Ingredient("reverend_insanity:liquor_worm", 1),
            new Ingredient("minecraft:honey_bottle", 3),
            new Ingredient("minecraft:ghast_tear", 1)
        ), "reverend_insanity:four_flavors_liquor_worm", "Four Flavors Liquor Worm");

        register("jade_skin_to_iron_bone", List.of(
            new Ingredient("reverend_insanity:jade_skin_gu", 1),
            new Ingredient("minecraft:iron_ingot", 4),
            new Ingredient("minecraft:diamond", 1)
        ), "reverend_insanity:iron_bone_gu", "Iron Bone Gu");

        register("stealth_to_enslave", List.of(
            new Ingredient("reverend_insanity:stealth_scales_gu", 1),
            new Ingredient("minecraft:spider_eye", 4),
            new Ingredient("minecraft:ender_pearl", 1)
        ), "reverend_insanity:enslave_snake_gu", "Enslave Snake Gu");

        register("bear_to_gold_light", List.of(
            new Ingredient("reverend_insanity:bear_strength_gu", 1),
            new Ingredient("reverend_insanity:white_boar_gu", 1),
            new Ingredient("minecraft:gold_ingot", 3),
            new Ingredient("minecraft:blaze_powder", 1)
        ), "reverend_insanity:gold_light_worm", "Gold Light Worm");

        register("moonscar_to_silver_moon", List.of(
            new Ingredient("reverend_insanity:moonscar_gu", 1),
            new Ingredient("reverend_insanity:moon_orchid_petal", 6),
            new Ingredient("minecraft:amethyst_shard", 3)
        ), "reverend_insanity:silver_moon_gu", "Silver Moon Gu");

        register("iron_bone_to_white_jade", List.of(
            new Ingredient("reverend_insanity:iron_bone_gu", 1),
            new Ingredient("minecraft:emerald", 3),
            new Ingredient("minecraft:diamond", 2)
        ), "reverend_insanity:white_jade_gu", "White Jade Gu");

        register("enslave_to_heavens_eye", List.of(
            new Ingredient("reverend_insanity:enslave_snake_gu", 1),
            new Ingredient("minecraft:ender_pearl", 3),
            new Ingredient("minecraft:blaze_rod", 2)
        ), "reverend_insanity:heavens_eye_gu", "Heavens Eye Gu");

        register("supreme_cicada", List.of(
            new Ingredient("reverend_insanity:silver_moon_gu", 1),
            new Ingredient("reverend_insanity:heavens_eye_gu", 1),
            new Ingredient("minecraft:nether_star", 1),
            new Ingredient("minecraft:echo_shard", 3)
        ), "reverend_insanity:spring_autumn_cicada", "Spring Autumn Cicada");

        register("blood_to_blood_wing", List.of(
            new Ingredient("reverend_insanity:blood_gu", 1),
            new Ingredient("minecraft:spider_eye", 3),
            new Ingredient("minecraft:phantom_membrane", 2)
        ), "reverend_insanity:blood_wing_gu", "Blood Wing Gu");

        register("poison_to_gold_silkworm", List.of(
            new Ingredient("reverend_insanity:poison_bee_gu", 1),
            new Ingredient("minecraft:gold_ingot", 3),
            new Ingredient("minecraft:fermented_spider_eye", 2)
        ), "reverend_insanity:gold_silkworm_gu", "Gold Silkworm Gu");

        register("savage_bull_to_giant_strength", List.of(
            new Ingredient("reverend_insanity:savage_bull_gu", 1),
            new Ingredient("minecraft:raw_iron", 4),
            new Ingredient("minecraft:bone_block", 2)
        ), "reverend_insanity:giant_strength_gu", "Giant Strength Gu");

        register("cold_ice_to_ice_seal", List.of(
            new Ingredient("reverend_insanity:cold_ice_gu", 1),
            new Ingredient("minecraft:blue_ice", 4),
            new Ingredient("minecraft:prismarine_crystals", 2)
        ), "reverend_insanity:ice_seal_gu", "Ice Seal Gu");

        register("fire_seed_to_blazing_flame", List.of(
            new Ingredient("reverend_insanity:fire_seed_gu", 1),
            new Ingredient("minecraft:blaze_rod", 4),
            new Ingredient("minecraft:fire_charge", 2)
        ), "reverend_insanity:blazing_flame_gu", "Blazing Flame Gu");

        register("earth_split_to_petrify", List.of(
            new Ingredient("reverend_insanity:earth_split_gu", 1),
            new Ingredient("minecraft:deepslate", 4),
            new Ingredient("minecraft:amethyst_shard", 2)
        ), "reverend_insanity:petrify_gu", "Petrify Gu");

        register("breeze_to_gale", List.of(
            new Ingredient("reverend_insanity:breeze_gu", 1),
            new Ingredient("minecraft:wind_charge", 4),
            new Ingredient("minecraft:phantom_membrane", 2)
        ), "reverend_insanity:gale_gu", "Gale Gu");

        register("lightning_to_thunderstorm", List.of(
            new Ingredient("reverend_insanity:lightning_gu", 1),
            new Ingredient("minecraft:glowstone_dust", 4),
            new Ingredient("minecraft:blaze_powder", 2)
        ), "reverend_insanity:thunderstorm_gu", "Thunderstorm Gu");

        register("tide_to_torrent", List.of(
            new Ingredient("reverend_insanity:tide_gu", 1),
            new Ingredient("minecraft:nautilus_shell", 4),
            new Ingredient("minecraft:prismarine_crystals", 2)
        ), "reverend_insanity:torrent_gu", "Torrent Gu");

        register("soul_search_to_soul_crush", List.of(
            new Ingredient("reverend_insanity:soul_search_gu", 1),
            new Ingredient("minecraft:echo_shard", 4),
            new Ingredient("minecraft:sculk_catalyst", 2)
        ), "reverend_insanity:soul_crush_gu", "Soul Crush Gu");

        register("light_beam_to_blazing_light", List.of(
            new Ingredient("reverend_insanity:light_beam_gu", 1),
            new Ingredient("minecraft:sea_lantern", 4),
            new Ingredient("minecraft:glow_berries", 2)
        ), "reverend_insanity:blazing_light_gu", "Blazing Light Gu");

        register("dark_bolt_to_abyss_devour", List.of(
            new Ingredient("reverend_insanity:dark_bolt_gu", 1),
            new Ingredient("minecraft:obsidian", 4),
            new Ingredient("minecraft:crying_obsidian", 2)
        ), "reverend_insanity:abyss_devour_gu", "Abyss Devour Gu");

        register("dream_to_nightmare", List.of(
            new Ingredient("reverend_insanity:dream_gu", 1),
            new Ingredient("minecraft:fermented_spider_eye", 4),
            new Ingredient("minecraft:phantom_membrane", 2)
        ), "reverend_insanity:nightmare_gu", "Nightmare Gu");

        register("phantom_to_grand_illusion", List.of(
            new Ingredient("reverend_insanity:phantom_gu", 1),
            new Ingredient("minecraft:ender_pearl", 4),
            new Ingredient("minecraft:amethyst_shard", 2)
        ), "reverend_insanity:grand_illusion_gu", "Grand Illusion Gu");

        register("flying_sword_to_myriad_sword", List.of(
            new Ingredient("reverend_insanity:flying_sword_gu", 1),
            new Ingredient("minecraft:iron_sword", 2),
            new Ingredient("minecraft:diamond", 2)
        ), "reverend_insanity:myriad_sword_gu", "Myriad Sword Gu");

        register("moon_slash_to_heaven_blade", List.of(
            new Ingredient("reverend_insanity:moon_slash_gu", 1),
            new Ingredient("minecraft:netherite_scrap", 2),
            new Ingredient("minecraft:blaze_rod", 2)
        ), "reverend_insanity:heaven_blade_gu", "Heaven Blade Gu");

        register("starlight_to_star_fall", List.of(
            new Ingredient("reverend_insanity:starlight_gu", 1),
            new Ingredient("minecraft:nether_star", 1),
            new Ingredient("minecraft:amethyst_shard", 4)
        ), "reverend_insanity:star_fall_gu", "Star Fall Gu");

        register("lucky_to_heavens_secret", List.of(
            new Ingredient("reverend_insanity:lucky_gu", 1),
            new Ingredient("minecraft:ender_eye", 4),
            new Ingredient("minecraft:echo_shard", 2)
        ), "reverend_insanity:heavens_secret_gu", "Heavens Secret Gu");

        register("kill_intent_to_death_strike", List.of(
            new Ingredient("reverend_insanity:kill_intent_gu", 1),
            new Ingredient("minecraft:wither_skeleton_skull", 1),
            new Ingredient("minecraft:bone", 4)
        ), "reverend_insanity:death_strike_gu", "Death Strike Gu");

        register("shrink_ground_to_heaven_change", List.of(
            new Ingredient("reverend_insanity:shrink_ground_gu", 1),
            new Ingredient("minecraft:dragon_breath", 2),
            new Ingredient("minecraft:chorus_fruit", 4)
        ), "reverend_insanity:heaven_change_gu", "Heaven Change Gu");

        register("formation_to_thousand_army", List.of(
            new Ingredient("reverend_insanity:formation_soldier_gu", 1),
            new Ingredient("minecraft:arrow", 16),
            new Ingredient("minecraft:iron_ingot", 4)
        ), "reverend_insanity:thousand_army_gu", "Thousand Army Gu");

        register("sound_wave_to_heavenly_sound", List.of(
            new Ingredient("reverend_insanity:sound_wave_gu", 1),
            new Ingredient("minecraft:echo_shard", 3),
            new Ingredient("minecraft:amethyst_shard", 4)
        ), "reverend_insanity:heavenly_sound_gu", "Heavenly Sound Gu");

        register("bone_spear_to_white_bone", List.of(
            new Ingredient("reverend_insanity:bone_spear_gu", 1),
            new Ingredient("minecraft:bone_block", 4),
            new Ingredient("minecraft:wither_skeleton_skull", 1)
        ), "reverend_insanity:white_bone_gu", "White Bone Gu");

        register("cloud_ride_to_sky_eagle", List.of(
            new Ingredient("reverend_insanity:cloud_ride_gu", 1),
            new Ingredient("minecraft:phantom_membrane", 4),
            new Ingredient("minecraft:feather", 8)
        ), "reverend_insanity:sky_eagle_gu", "Sky Eagle Gu");

        register("true_qi_to_profound_qi", List.of(
            new Ingredient("reverend_insanity:true_qi_gu", 1),
            new Ingredient("minecraft:breeze_rod", 2),
            new Ingredient("minecraft:glow_berries", 8)
        ), "reverend_insanity:profound_qi_gu", "Profound Qi Gu");

        register("yin_yang_to_primordial", List.of(
            new Ingredient("reverend_insanity:yin_yang_gu", 1),
            new Ingredient("minecraft:nether_star", 1),
            new Ingredient("minecraft:ender_pearl", 4)
        ), "reverend_insanity:primordial_gu", "Primordial Gu");

        register("warp_to_displacement", List.of(
            new Ingredient("reverend_insanity:warp_gu", 1),
            new Ingredient("minecraft:chorus_fruit", 8),
            new Ingredient("minecraft:ender_pearl", 2)
        ), "reverend_insanity:displacement_gu", "Displacement Gu");

        register("time_decel_to_reversal", List.of(
            new Ingredient("reverend_insanity:time_decel_gu", 1),
            new Ingredient("minecraft:recovery_compass", 1),
            new Ingredient("minecraft:echo_shard", 4)
        ), "reverend_insanity:time_reversal_gu", "Time Reversal Gu");

        register("charm_to_soul_charm", List.of(
            new Ingredient("reverend_insanity:charm_gu", 1),
            new Ingredient("minecraft:sculk_catalyst", 2),
            new Ingredient("minecraft:echo_shard", 3)
        ), "reverend_insanity:soul_charm_gu", "Soul Charm Gu");

        register("void_bolt_to_annihilation", List.of(
            new Ingredient("reverend_insanity:void_bolt_gu", 1),
            new Ingredient("minecraft:ender_pearl", 4),
            new Ingredient("minecraft:end_crystal", 1)
        ), "reverend_insanity:void_annihilation_gu", "Void Annihilation Gu");

        register("seal_to_heaven_seal", List.of(
            new Ingredient("reverend_insanity:seal_gu", 1),
            new Ingredient("minecraft:chain", 4),
            new Ingredient("minecraft:lodestone", 1)
        ), "reverend_insanity:heaven_seal_gu", "Heaven Seal Gu");

        register("heaven_will_to_punishment", List.of(
            new Ingredient("reverend_insanity:heaven_will_gu", 1),
            new Ingredient("minecraft:end_crystal", 1),
            new Ingredient("minecraft:nether_star", 1)
        ), "reverend_insanity:heaven_punishment_gu", "Heaven Punishment Gu");

        register("rule_to_supreme_law", List.of(
            new Ingredient("reverend_insanity:rule_gu", 1),
            new Ingredient("minecraft:lodestone", 1),
            new Ingredient("minecraft:chain", 4)
        ), "reverend_insanity:supreme_law_gu", "Supreme Law Gu");

        register("shadow_dart_to_devour", List.of(
            new Ingredient("reverend_insanity:shadow_dart_gu", 1),
            new Ingredient("minecraft:obsidian", 4),
            new Ingredient("minecraft:ink_sac", 8)
        ), "reverend_insanity:shadow_devour_gu", "Shadow Devour Gu");

        register("mist_to_cloud_storm", List.of(
            new Ingredient("reverend_insanity:mist_gu", 1),
            new Ingredient("minecraft:lightning_rod", 1),
            new Ingredient("minecraft:white_wool", 4)
        ), "reverend_insanity:cloud_storm_gu", "Cloud Storm Gu");

        register("trap_to_grand_formation", List.of(
            new Ingredient("reverend_insanity:trap_formation_gu", 1),
            new Ingredient("minecraft:redstone", 8),
            new Ingredient("minecraft:lapis_lazuli", 4)
        ), "reverend_insanity:grand_formation_gu", "Grand Formation Gu");

        register("refine_fire_to_heaven", List.of(
            new Ingredient("reverend_insanity:refine_fire_gu", 1),
            new Ingredient("minecraft:blaze_rod", 4),
            new Ingredient("minecraft:diamond", 1)
        ), "reverend_insanity:heaven_refine_gu", "Heaven Refine Gu");

        register("pill_poison_to_immortal", List.of(
            new Ingredient("reverend_insanity:pill_poison_gu", 1),
            new Ingredient("minecraft:golden_apple", 1),
            new Ingredient("minecraft:glistering_melon_slice", 4)
        ), "reverend_insanity:immortal_pill_gu", "Immortal Pill Gu");

        register("paint_brush_to_myriad_paint", List.of(
            new Ingredient("reverend_insanity:paint_brush_gu", 1),
            new Ingredient("minecraft:glow_ink_sac", 4),
            new Ingredient("minecraft:pink_dye", 4)
        ), "reverend_insanity:myriad_paint_gu", "Myriad Paint Gu");

        register("steal_qi_to_heaven_steal", List.of(
            new Ingredient("reverend_insanity:steal_qi_gu", 1),
            new Ingredient("minecraft:ender_eye", 2),
            new Ingredient("minecraft:fermented_spider_eye", 4)
        ), "reverend_insanity:heaven_steal_gu", "Heaven Steal Gu");

        register("info_dart_to_heaven_info", List.of(
            new Ingredient("reverend_insanity:info_dart_gu", 1),
            new Ingredient("minecraft:writable_book", 2),
            new Ingredient("minecraft:echo_shard", 2)
        ), "reverend_insanity:heaven_info_gu", "Heaven Info Gu");

        register("human_heart_to_will", List.of(
            new Ingredient("reverend_insanity:human_heart_gu", 1),
            new Ingredient("minecraft:enchanted_golden_apple", 1),
            new Ingredient("minecraft:golden_apple", 2)
        ), "reverend_insanity:human_will_gu", "Human Will Gu");

        register("enslave_worm_to_snake", List.of(
            new Ingredient("reverend_insanity:enslave_worm_gu", 1),
            new Ingredient("minecraft:spider_eye", 4),
            new Ingredient("minecraft:ender_pearl", 1)
        ), "reverend_insanity:enslave_snake_gu", "Enslave Snake Gu");
    }

    public static RefinementRecipe findMatch(List<ItemStack> items) {
        for (RefinementRecipe recipe : RECIPES) {
            if (recipe.matches(items)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean matches(List<ItemStack> items) {
        Map<String, Integer> available = new HashMap<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                String key = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                available.merge(key, stack.getCount(), Integer::sum);
            }
        }
        for (Ingredient ingredient : ingredients) {
            int have = available.getOrDefault(ingredient.itemId(), 0);
            if (have < ingredient.count()) {
                return false;
            }
        }
        return true;
    }

    public ItemStack getOutputStack() {
        ResourceLocation loc = ResourceLocation.parse(outputItemId);
        return new ItemStack(BuiltInRegistries.ITEM.get(loc));
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static List<RefinementRecipe> getAllRecipes() {
        return RECIPES;
    }
}
