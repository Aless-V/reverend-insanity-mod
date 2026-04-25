package com.reverendinsanity.block;

import com.reverendinsanity.network.ExitAperturePayload;
import com.reverendinsanity.world.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

// Immortal Aperture Exit Teleport Pad: Right-click to leave the Immortal Aperture dimension.
public class ApertureExitPortalBlock extends Block {

    public ApertureExitPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            if (level.dimension().equals(ModDimensions.APERTURE_DIM)) {
                PacketDistributor.sendToServer(new ExitAperturePayload());
            } else {
                player.displayClientMessage(Component.literal("This teleport pad can only be used within the Immortal Aperture."), true);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return 0.0f;
    }

    @Override
    public float getExplosionResistance() {
        return 3600000.0f;
    }
}
