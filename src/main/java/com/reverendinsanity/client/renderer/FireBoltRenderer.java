package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.FireBoltEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

// Fire Bullet Projectile Renderer, based on ThrownItemRenderer.
public class FireBoltRenderer extends ThrownItemRenderer<FireBoltEntity> {
    public FireBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
