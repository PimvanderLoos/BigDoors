package net.minecraft.util;

import net.minecraft.network.chat.IChatBaseComponent;

public interface IProgressUpdate {

    void progressStartNoAbort(IChatBaseComponent ichatbasecomponent);

    void progressStart(IChatBaseComponent ichatbasecomponent);

    void progressStage(IChatBaseComponent ichatbasecomponent);

    void progressStagePercentage(int i);

    void stop();
}
