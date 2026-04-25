package com.reverendinsanity.item;

import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.registry.ModAttachments;
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

// 元石：消耗品，恢复20%最大真元
public class PrimevalStoneItem extends Item {

    public PrimevalStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
            Aperture aperture = data.getAperture();

            if (!aperture.isOpened()) {
                player.displayClientMessage(
                        Component.literal("Aperture not opened").withStyle(ChatFormatting.RED), true);
                return InteractionResultHolder.fail(stack);
            }

            if (aperture.getCurrentEssence() >= aperture.getMaxEssence()) {
                player.displayClientMessage(
                        Component.literal("Primeval Essence is full").withStyle(ChatFormatting.YELLOW), true);
                return InteractionResultHolder.fail(stack);
            }

            float restoreAmount = aperture.getMaxEssence() * 0.2f;
            aperture.regenerateEssence(restoreAmount);
            stack.shrink(1);
            player.displayClientMessage(
                    Component.literal("Essence Stone shatters, restoring " + (int) restoreAmount + " Primeval Essence")
                            .withStyle(ChatFormatting.GREEN), true);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Right-click to restore 20% of maximum Primeval Essence").withStyle(ChatFormatting.YELLOW));
        tooltipComponents.add(Component.literal("An essential item for Gu Master cultivation").withStyle(ChatFormatting.GRAY));
    }
}
