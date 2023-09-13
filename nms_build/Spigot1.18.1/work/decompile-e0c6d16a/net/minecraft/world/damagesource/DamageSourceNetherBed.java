package net.minecraft.world.damagesource;

import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.world.entity.EntityLiving;

public class DamageSourceNetherBed extends DamageSource {

    protected DamageSourceNetherBed() {
        super("badRespawnPoint");
        this.setScalesWithDifficulty();
        this.setExplosion();
    }

    @Override
    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.wrapInSquareBrackets(new ChatMessage("death.attack.badRespawnPoint.link")).withStyle((chatmodifier) -> {
            return chatmodifier.withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText("MCPE-28723")));
        });

        return new ChatMessage("death.attack.badRespawnPoint.message", new Object[]{entityliving.getDisplayName(), ichatmutablecomponent});
    }
}
