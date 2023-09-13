package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Collection;

public class CommonComponents {

    public static final IChatBaseComponent EMPTY = IChatBaseComponent.empty();
    public static final IChatBaseComponent OPTION_ON = IChatBaseComponent.translatable("options.on");
    public static final IChatBaseComponent OPTION_OFF = IChatBaseComponent.translatable("options.off");
    public static final IChatBaseComponent GUI_DONE = IChatBaseComponent.translatable("gui.done");
    public static final IChatBaseComponent GUI_CANCEL = IChatBaseComponent.translatable("gui.cancel");
    public static final IChatBaseComponent GUI_YES = IChatBaseComponent.translatable("gui.yes");
    public static final IChatBaseComponent GUI_NO = IChatBaseComponent.translatable("gui.no");
    public static final IChatBaseComponent GUI_PROCEED = IChatBaseComponent.translatable("gui.proceed");
    public static final IChatBaseComponent GUI_BACK = IChatBaseComponent.translatable("gui.back");
    public static final IChatBaseComponent GUI_ACKNOWLEDGE = IChatBaseComponent.translatable("gui.acknowledge");
    public static final IChatBaseComponent CONNECT_FAILED = IChatBaseComponent.translatable("connect.failed");
    public static final IChatBaseComponent NEW_LINE = IChatBaseComponent.literal("\n");
    public static final IChatBaseComponent NARRATION_SEPARATOR = IChatBaseComponent.literal(". ");
    public static final IChatBaseComponent ELLIPSIS = IChatBaseComponent.literal("...");

    public CommonComponents() {}

    public static IChatMutableComponent days(long i) {
        return IChatBaseComponent.translatable("gui.days", i);
    }

    public static IChatMutableComponent hours(long i) {
        return IChatBaseComponent.translatable("gui.hours", i);
    }

    public static IChatMutableComponent minutes(long i) {
        return IChatBaseComponent.translatable("gui.minutes", i);
    }

    public static IChatBaseComponent optionStatus(boolean flag) {
        return flag ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
    }

    public static IChatMutableComponent optionStatus(IChatBaseComponent ichatbasecomponent, boolean flag) {
        return IChatBaseComponent.translatable(flag ? "options.on.composed" : "options.off.composed", ichatbasecomponent);
    }

    public static IChatMutableComponent optionNameValue(IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1) {
        return IChatBaseComponent.translatable("options.generic_value", ichatbasecomponent, ichatbasecomponent1);
    }

    public static IChatMutableComponent joinForNarration(IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1) {
        return IChatBaseComponent.empty().append(ichatbasecomponent).append(CommonComponents.NARRATION_SEPARATOR).append(ichatbasecomponent1);
    }

    public static IChatBaseComponent joinLines(IChatBaseComponent... aichatbasecomponent) {
        return joinLines((Collection) Arrays.asList(aichatbasecomponent));
    }

    public static IChatBaseComponent joinLines(Collection<? extends IChatBaseComponent> collection) {
        return ChatComponentUtils.formatList(collection, CommonComponents.NEW_LINE);
    }
}
