package com.reverendinsanity.core.combat.ability.impl;

import com.reverendinsanity.core.combat.ability.GuAbility;
import com.reverendinsanity.core.combat.buff.impl.BeastSkinBuff;
import com.reverendinsanity.core.cultivation.Aperture;
import com.reverendinsanity.core.cultivation.GuMasterData;
import com.reverendinsanity.core.gu.GuRegistry;
import com.reverendinsanity.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

// Beast Skin Gu Skill: The cheapest Rank 1 Strength Path defense. +3 Armor +1 Knockback Resistance.
public class BeastSkinAbility extends GuAbility {

    public BeastSkinAbility() {
        super(GuRegistry.id("beast_skin_gu"), 4f, 300, AbilityType.BUFF);
    }

    @Override
    protected void onActivate(ServerPlayer player, Aperture aperture) {
        GuMasterData data = player.getData(ModAttachments.GU_MASTER_DATA.get());
        data.getBuffManager().applyBuff(player, new BeastSkinBuff());
    }
}
