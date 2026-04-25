package com.reverendinsanity.core.combat.custom;

import com.reverendinsanity.core.path.DaoPath;
import java.util.*;

// 道叠加规则：同道蛊虫数量达阈值时效果质变
public class PathStackingRule {

    private static final Map<DaoPath, List<StackThreshold>> RULES = new EnumMap<>(DaoPath.class);

    public record StackThreshold(
        int requiredCount,
        StackEffect effect,
        float multiplier,
        String description
    ) {}

    public enum StackEffect {
        BEAST_PHANTOM,
        ENHANCED_SHIELD,
        ICE_DOMAIN,
        FIRE_DOMAIN,
        BLOOD_FRENZY,
        SOUL_SHATTER,
        BONE_CONSTRUCT,
        STEAL_POWER,
        DARK_DOMAIN,
        LIGHTNING_STORM,
        WIND_STORM,
        SWORD_RAIN,
        POISON_MIASMA,
        DREAM_REALM,
        STAR_PHANTOM,
        EARTH_FORTRESS,
        TIDAL_SURGE,
        LIGHT_JUDGMENT,
        SOUND_SUPPRESS,
        TRANSFORMATION_EVOLVE,
        NULLIFY,
        MIND_SCATTER,
        CLOUD_RING,
        METAL_CRUSH,
        SEAL_POWER,
        BLADE_STORM,
        QI_SURGE,
        PEOPLES_WILL,
        KILL_DOMAIN,
        SHADOW_DOMAIN,
        YIN_YANG_REVERSAL,
        MOON_DOMAIN,
        VOID_PHASE,
        CHARM_AURA,
        WAR_SPIRIT,
        SKY_SOVEREIGNTY,
        ILLUSION_REALM,
        SPACE_WARP,
        PUPPET_ARMY,
        HEAVEN_WRATH,
        FORTUNE_SHIFT
    }

    public static List<StackThreshold> check(Map<DaoPath, Integer> pathCounts) {
        if (pathCounts == null || pathCounts.isEmpty()) return Collections.emptyList();
        List<StackThreshold> triggered = new ArrayList<>();
        for (var entry : pathCounts.entrySet()) {
            List<StackThreshold> thresholds = RULES.get(entry.getKey());
            if (thresholds == null) continue;
            for (StackThreshold t : thresholds) {
                if (entry.getValue() >= t.requiredCount()) {
                    triggered.add(t);
                }
            }
        }
        return triggered;
    }

    private static void reg(DaoPath path, int count, StackEffect effect, float mult, String desc) {
        RULES.computeIfAbsent(path, k -> new ArrayList<>())
             .add(new StackThreshold(count, effect, mult, desc));
    }

    static {
        reg(DaoPath.STRENGTH, 2, StackEffect.ENHANCED_SHIELD, 2.0f, "Hair Armor");
        reg(DaoPath.STRENGTH, 3, StackEffect.BEAST_PHANTOM, 1.8f, "Beast Phantom");

        reg(DaoPath.ICE, 2, StackEffect.ICE_DOMAIN, 2.0f, "Ice Domain");

        reg(DaoPath.FIRE, 2, StackEffect.FIRE_DOMAIN, 2.0f, "Fire Domain");

        reg(DaoPath.BLOOD, 2, StackEffect.BLOOD_FRENZY, 2.0f, "Blood Frenzy");

        reg(DaoPath.SOUL, 2, StackEffect.SOUL_SHATTER, 2.0f, "Soul Shatter");

        reg(DaoPath.BONE, 2, StackEffect.BONE_CONSTRUCT, 2.0f, "Bone Armor");
        reg(DaoPath.BONE, 3, StackEffect.BONE_CONSTRUCT, 3.0f, "White Bone War Chariot");

        reg(DaoPath.STEAL, 2, StackEffect.STEAL_POWER, 1.5f, "Steal Heaven");

        reg(DaoPath.DARK, 2, StackEffect.DARK_DOMAIN, 2.0f, "Dark Domain");

        reg(DaoPath.LIGHTNING, 2, StackEffect.LIGHTNING_STORM, 2.0f, "Lightning Storm");

        reg(DaoPath.WIND, 2, StackEffect.WIND_STORM, 2.0f, "Wind Storm");

        reg(DaoPath.SWORD, 2, StackEffect.SWORD_RAIN, 2.0f, "Sword Rain");

        reg(DaoPath.POISON, 2, StackEffect.POISON_MIASMA, 2.0f, "Poison Miasma");

        reg(DaoPath.DREAM, 2, StackEffect.DREAM_REALM, 2.0f, "Dream Realm");

        reg(DaoPath.STAR, 2, StackEffect.STAR_PHANTOM, 2.0f, "Star Phantom");

        reg(DaoPath.EARTH, 2, StackEffect.EARTH_FORTRESS, 2.0f, "Earth Fortress");

        reg(DaoPath.WATER, 2, StackEffect.TIDAL_SURGE, 2.0f, "Tidal Surge");

        reg(DaoPath.LIGHT, 2, StackEffect.LIGHT_JUDGMENT, 2.0f, "Light Judgment");

        reg(DaoPath.SOUND, 2, StackEffect.SOUND_SUPPRESS, 2.0f, "Sound Suppression");

        reg(DaoPath.TRANSFORMATION, 2, StackEffect.TRANSFORMATION_EVOLVE, 2.0f, "Transformation Evolve");

        reg(DaoPath.RULE, 2, StackEffect.NULLIFY, 2.0f, "Purify Void");

        reg(DaoPath.WISDOM, 2, StackEffect.MIND_SCATTER, 2.0f, "Mind Scatter");

        reg(DaoPath.CLOUD, 2, StackEffect.CLOUD_RING, 2.0f, "Cloud Ring");

        reg(DaoPath.METAL, 2, StackEffect.METAL_CRUSH, 2.0f, "Metal Crush");

        reg(DaoPath.RESTRICTION, 2, StackEffect.SEAL_POWER, 2.0f, "Seal");

        reg(DaoPath.BLADE, 2, StackEffect.BLADE_STORM, 2.0f, "Blade Storm");

        reg(DaoPath.QI, 2, StackEffect.QI_SURGE, 2.0f, "Qi Surge");

        reg(DaoPath.HUMAN, 2, StackEffect.PEOPLES_WILL, 2.0f, "Peoples Will");

        reg(DaoPath.KILL, 2, StackEffect.KILL_DOMAIN, 2.0f, "Kill Domain");

        reg(DaoPath.SHADOW, 2, StackEffect.SHADOW_DOMAIN, 2.0f, "Shadow Domain");

        reg(DaoPath.YIN_YANG, 2, StackEffect.YIN_YANG_REVERSAL, 2.0f, "Yin-Yang Reversal");

        reg(DaoPath.MOON, 2, StackEffect.MOON_DOMAIN, 2.0f, "Moon Domain");

        reg(DaoPath.VOID, 2, StackEffect.VOID_PHASE, 2.0f, "Void Phase");

        reg(DaoPath.CHARM, 2, StackEffect.CHARM_AURA, 2.0f, "Charm Aura");

        reg(DaoPath.SOLDIER, 2, StackEffect.WAR_SPIRIT, 2.0f, "War Spirit");

        reg(DaoPath.FLIGHT, 2, StackEffect.SKY_SOVEREIGNTY, 2.0f, "Sky Sovereignty");

        reg(DaoPath.ILLUSION, 2, StackEffect.ILLUSION_REALM, 2.0f, "Illusion Realm");

        reg(DaoPath.SPACE, 2, StackEffect.SPACE_WARP, 2.0f, "Space Warp");

        reg(DaoPath.ENSLAVE, 2, StackEffect.PUPPET_ARMY, 2.0f, "Puppet Army");

        reg(DaoPath.HEAVEN, 2, StackEffect.HEAVEN_WRATH, 2.0f, "Heavenly Wrath");

        reg(DaoPath.LUCK, 2, StackEffect.FORTUNE_SHIFT, 2.0f, "Fortune Shift");
    }
}
