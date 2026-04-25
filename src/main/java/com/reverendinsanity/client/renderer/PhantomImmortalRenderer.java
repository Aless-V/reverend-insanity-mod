package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.PhantomImmortalEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

// Phantom Gu Immortal Renderer: Particle-only effect, no entity model rendered.
public class PhantomImmortalRenderer extends EntityRenderer<PhantomImmortalEntity> {

    public PhantomImmortalRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(PhantomImmortalEntity entity) {
        return ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
    }
}
