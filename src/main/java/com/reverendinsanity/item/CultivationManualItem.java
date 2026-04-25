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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 修炼入门手册：右键翻页阅读蛊师入门知识
public class CultivationManualItem extends Item {

    private static final Map<UUID, Integer> playerPages = new HashMap<>();

    private static final String[][] PAGES = {
        {
            "[Chapter 1: The Path to Opening the Aperture]",
            "Find Hope Gu and right-click to use it. This will open your aperture.",
            "Talent is randomized: D Grade (40%), C Grade (30%), B Grade (20%), A Grade (8%), Ten Absolute Physique (2%).",
            "Talent determines your Primeval Essence recovery rate and cultivation limits."
        },
        {
            "[Chapter 2: Cultivation Basics]",
            "Primeval Essence (Green Bar): Energy used to release Gu techniques. Recovers naturally.",
            "Thought Power (Blue Bar): Mental energy used to control Gu insects. Recovers naturally.",
            "Sneak and remain still to meditate and break through minor sub-ranks.",
            "Use a Breakthrough Stone to break through to a major rank. (Rank 3 → Rank 4 requires a heavenly tribulation.)"
        },
        {
            "[Chapter 3: The Way of Gu Insects]",
            "Wild Gu (colorful firefly-like insects) can be encountered in the wild. Defeating them has a chance to drop Gu insects.",
            "Gu insects need to be fed regularly. (Press Tab to open the Aperture Management interface, then click Feed.)",
            "If a Gu insect's hunger level drops to 0, it will die. Feed them on time!",
            "The Gu Refining Furnace can combine Gu insects + materials to craft stronger Gu insects."
        },
        {
            "[Chapter 4: Combat Techniques]",
            "Once Gu insects are equipped, press keys 1-5 to unleash the corresponding Gu techniques.",
            "Killer Moves: Combine multiple Gu insects' techniques into a more powerful move.",
            "Press Z / X / C to unleash equipped Killer Moves.",
            "Different Paths have different effects: attack, defense, support, mobility, etc."
        },
        {
            "[Chapter 5: Exploring the World]",
            "Gu Caverns: Underground caves containing chests and Gu insects.",
            "Inheritance Secret Realms: Large underground ruins containing rare Gu insects and inheritances.",
            "Moon Orchid Sea: A magnificent underground sea of flowers within caves.",
            "Ancient Moon Stronghold: A Gu Master settlement with Gu merchants and Gu rooms.",
            "Cultivation speed is greatly enhanced near Spirit Springs!"
        },
        {
            "[Chapter 6: The Path to Progression]",
            "Rank 1 Bronze → Rank 2 Red Iron → Rank 3 Silver → Rank 4 Gold → Rank 5 Limit.",
            "After Rank 3, Primeval Essence becomes visible as particles. Rank 4 requires surviving a heavenly tribulation.",
            "Dao Marks: Using Gu techniques accumulates Dao Marks, enhancing the power of corresponding Path techniques.",
            "Blessed Land Seed: Placing it activates and forms a Blessed Land, greatly accelerating cultivation.",
            "Spring Autumn Cicada: A Gu that reverses time, allowing rebirth after death."
        }
    };

    public CultivationManualItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            UUID uuid = player.getUUID();
            int page = playerPages.getOrDefault(uuid, 0);

            String[] content = PAGES[page % PAGES.length];

            player.displayClientMessage(
                Component.literal(content[0]).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

            for (int i = 1; i < content.length; i++) {
                player.displayClientMessage(
                    Component.literal("  " + content[i]).withStyle(ChatFormatting.YELLOW), false);
            }

            player.displayClientMessage(
                Component.literal("--- Page " + (page % PAGES.length + 1) + "/" + PAGES.length + " (Right-click to flip) ---")
                    .withStyle(ChatFormatting.GRAY), false);

            playerPages.put(uuid, (page + 1) % PAGES.length);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Gu Master Cultivation Beginner's Guide.").withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.literal("Right-click to flip pages and read.").withStyle(ChatFormatting.GRAY));
    }
}
