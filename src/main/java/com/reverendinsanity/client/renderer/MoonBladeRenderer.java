package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.MoonBladeEntity;
import com.reverendinsanity.registry.ModItems;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

// Moon Blade Projectile Renderer: Uses Moonlight Gu item texture.
public class MoonBladeRenderer extends ThrownItemRenderer<MoonBladeEntity> {

    public MoonBladeRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0f, true);
    }
}
