package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.IceBoltEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

// Ice Bullet Projectile Renderer: Uses Ice Gu item texture.
public class IceBoltRenderer extends ThrownItemRenderer<IceBoltEntity> {

    public IceBoltRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0f, true);
    }
}
