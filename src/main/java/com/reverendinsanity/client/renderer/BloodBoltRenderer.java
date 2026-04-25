package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.BloodBoltEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

// Blood Bullet Projectile Renderer: Uses Blood Gu item texture.
public class BloodBoltRenderer extends ThrownItemRenderer<BloodBoltEntity> {

    public BloodBoltRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0f, true);
    }
}
