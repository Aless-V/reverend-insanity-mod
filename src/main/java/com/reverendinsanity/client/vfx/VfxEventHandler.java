package com.reverendinsanity.client.vfx;

import com.reverendinsanity.ReverendInsanity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

// VFX Event Handler: Subscribes to game event bus to trigger VFX updates and rendering.
@EventBusSubscriber(modid = ReverendInsanity.MODID, value = Dist.CLIENT)
public class VfxEventHandler {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        VfxManager.getInstance().render(event.getPoseStack(), event.getCamera(), partialTick);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        VfxManager.getInstance().tick();
    }
}
