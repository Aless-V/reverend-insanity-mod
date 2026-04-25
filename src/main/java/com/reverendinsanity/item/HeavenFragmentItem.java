package com.reverendinsanity.item;

import com.reverendinsanity.core.heaven.HeavenType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

// 九天碎片：太古九天的残骸碎片，极其珍贵的修仙资源
public class HeavenFragmentItem extends Item {

    private final HeavenType heavenType;

    public HeavenFragmentItem(HeavenType heavenType) {
        super(new Properties().stacksTo(16).rarity(Rarity.EPIC));
        this.heavenType = heavenType;
    }

    public HeavenType getHeavenType() {
        return heavenType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(heavenType.getDisplayName() + "Fragment").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Fragment of " + heavenType.getDisplayName() + " of the Ancient Nine Heavens").withStyle(ChatFormatting.GRAY));
        String desc = switch (heavenType) {
            case WHITE -> "Power of Purification — can cleanse impurities from Gu insects.";
            case RED -> "Flame of Destruction — contains destructive Primeval Essence.";
            case ORANGE -> "Starlight Firefly's Glow — illuminates the path ahead.";
            case YELLOW -> "Commercial Path fortune from the Treasure Yellow Heaven.";
            case GREEN -> "Vital, flourishing energy — the power of all growth.";
            case CYAN -> "Spiritual resonance of the Bamboo Sea — the essence of resilience.";
            case BLUE -> "Sea of Stars — contains the true meaning of the Star Path.";
            case PURPLE -> "Purple mist comes from the east — a mysterious and unfathomable power.";
            case BLACK -> "Void Abyss — the power to devour everything.";
        };
        tooltip.add(Component.literal(desc).withStyle(ChatFormatting.DARK_PURPLE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
