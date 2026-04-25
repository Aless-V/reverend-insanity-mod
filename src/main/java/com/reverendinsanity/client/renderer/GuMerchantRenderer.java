package com.reverendinsanity.client.renderer;

import com.reverendinsanity.entity.GuMerchantEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

// Gu Merchant Renderer: Renders humanoid Gu merchants using the standard player model.
public class GuMerchantRenderer extends HumanoidMobRenderer<GuMerchantEntity, PlayerModel<GuMerchantEntity>> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath("reverend_insanity", "textures/entity/gu_merchant.png");

    public GuMerchantRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GuMerchantEntity entity) {
        return TEXTURE;
    }
}
