package com.reverendinsanity;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.reverendinsanity.registry.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

// 蛊真人模组主入口
@Mod(ReverendInsanity.MODID)
public class ReverendInsanity {

    public static final String MODID = "reverend_insanity";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ReverendInsanity(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModRegistries.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Humans are the spirit of all things; Gu are the true essence of heaven and earth. Reverend Insanity mod loaded successfully.");
    }
}
