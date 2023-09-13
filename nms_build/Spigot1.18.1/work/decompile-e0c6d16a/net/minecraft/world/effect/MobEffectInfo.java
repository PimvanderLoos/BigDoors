package net.minecraft.world.effect;

import net.minecraft.EnumChatFormat;

public enum MobEffectInfo {

    BENEFICIAL(EnumChatFormat.BLUE), HARMFUL(EnumChatFormat.RED), NEUTRAL(EnumChatFormat.BLUE);

    private final EnumChatFormat tooltipFormatting;

    private MobEffectInfo(EnumChatFormat enumchatformat) {
        this.tooltipFormatting = enumchatformat;
    }

    public EnumChatFormat getTooltipFormatting() {
        return this.tooltipFormatting;
    }
}
