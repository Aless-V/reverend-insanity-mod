package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.AncientGuImmortalEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

// Ancient Gu Immortal Remnant Soul Renderer: Renders a humanoid BOSS using the standard player model.
public class AncientGuImmortalRenderer extends HumanoidMobRenderer<AncientGuImmortalEntity, PlayerModel<AncientGuImmortalEntity>> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath("reverend_insanity", "textures/entity/ancient_gu_immortal.png");

    public AncientGuImmortalRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(AncientGuImmortalEntity entity) {
        return TEXTURE;
    }
}
