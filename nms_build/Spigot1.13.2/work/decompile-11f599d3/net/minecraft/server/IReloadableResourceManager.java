package net.minecraft.server;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager {

    void a(List<IResourcePack> list);

    void a(IResourcePackListener iresourcepacklistener);
}
