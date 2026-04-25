package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.GoldBeamEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

// Golden Light Projectile Renderer
public class GoldBeamRenderer extends ThrownItemRenderer<GoldBeamEntity> {

    public GoldBeamRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0f, true);
    }
}
