package com.reverendinsanity.item;

import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.core.gu.GuInstance;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

// 春秋蝉：六转仙蛊，死后自动重生
public class SpringAutumnCicadaItem extends Item {

    public SpringAutumnCicadaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();

            if (!aperture.isOpened()) {
                player.displayClientMessage(Component.literal("Aperture not opened. Cannot house Gu insects"), true);
                return InteractionResultHolder.fail(stack);
            }

            ResourceLocation cicadaId = GuRegistry.id("spring_autumn_cicada");
            for (GuInstance gu : aperture.getStoredGu()) {
                if (gu.getTypeId().equals(cicadaId)) {
                    player.displayClientMessage(Component.literal("The Spring Autumn Cicada is already in your aperture"), true);
                    return InteractionResultHolder.fail(stack);
                }
            }

            GuInstance instance = new GuInstance(cicadaId);
            instance.refine(10001f);
            aperture.addGu(instance);

            stack.shrink(1);
            player.displayClientMessage(
                Component.literal("The Spring Autumn Cicada resides in your aperture. The power of time swirls around you..."),
                false
            );
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Rank 6 Immortal Gu · Time Path").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltipComponents.add(Component.literal("The Gu of Rebirth. Upon the Gu Master's death, this Gu will be consumed to save its master's life").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("Right-click to directly house this Gu in your aperture").withStyle(ChatFormatting.YELLOW));
    }
}
