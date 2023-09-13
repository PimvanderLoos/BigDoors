package net.minecraft;

import java.util.Date;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.world.level.storage.DataVersion;

public interface WorldVersion {

    DataVersion getDataVersion();

    String getId();

    String getName();

    int getProtocolVersion();

    int getPackVersion(EnumResourcePackType enumresourcepacktype);

    Date getBuildTime();

    boolean isStable();
}
