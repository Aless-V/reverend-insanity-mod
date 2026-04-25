package com.reverendinsanity.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

// 蛊师手记：记录蛊师修炼心得的卷轴
public class LoreScrollItem extends Item {

    private static final String[] LORE_TEXTS = {
        "Humans are the spirit of all things; Gu are the true essence of heaven and earth.",
        "So-called cultivation is nothing more than humanity struggling against heaven.",
        "True强者 never complain about the injustice of fate.",
        "The strong prey on the weak. The fittest survive. Such is the law of the Gu Master's world.",
        "What does A Grade talent matter? Without opportunity and effort, it's all empty talk.",
        "Within the aperture lies an endless sea of Essence. Cultivation knows no bounds.",
        "Every Gu insect is a miraculous wonder born of heaven and earth.",
        "The Spring Autumn Cicada reverses time — yet in this world, is there truly a path to turn back?",
        "Within a Blessed Land lies a hidden world, brimming with celestial magnificence.",
        "Three thousand Paths, each leading to the heavens — yet all converge to the same destination.",
        "Gu insects are not mere tools. They possess their own spirituality.",
        "Rank 1 is bronze-green; Rank 2, crimson iron; Rank 3, silver-white; Rank 4, brilliant gold.",
        "Essence Stones are the condensed essence of heaven and earth — the foundation of cultivation.",
        "The way of the Gu Master lies in the trinity of refining Gu, raising Gu, and using Gu.",
        "The power of a Killer Move lies in Gu coordination, PE control, and perfect timing.",
        "The vast mountains — the true stage of the Gu Master's world."
    };

    public LoreScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            String lore = LORE_TEXTS[level.getRandom().nextInt(LORE_TEXTS.length)];
            player.displayClientMessage(
                Component.literal("【Gu Master's Journal】").withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(lore).withStyle(ChatFormatting.ITALIC, ChatFormatting.YELLOW)),
                false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Right-click to read the cultivation insights of past Gu Masters.").withStyle(ChatFormatting.GRAY));
    }
}
