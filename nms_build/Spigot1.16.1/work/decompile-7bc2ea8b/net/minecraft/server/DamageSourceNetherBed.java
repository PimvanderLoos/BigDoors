package net.minecraft.server;

public class DamageSourceNetherBed extends DamageSource {

    protected DamageSourceNetherBed() {
        super("badRespawnPoint");
        this.r();
        this.setExplosion();
    }

    @Override
    public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
        IChatMutableComponent ichatmutablecomponent = ChatComponentUtils.a((IChatBaseComponent) (new ChatMessage("death.attack.badRespawnPoint.link"))).format((chatmodifier) -> {
            return chatmodifier.setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText("MCPE-28723")));
        });

        return new ChatMessage("death.attack.badRespawnPoint.message", new Object[]{entityliving.getScoreboardDisplayName(), ichatmutablecomponent});
    }
}
