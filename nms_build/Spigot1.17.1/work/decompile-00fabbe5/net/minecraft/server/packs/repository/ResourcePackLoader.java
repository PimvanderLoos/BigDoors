package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.server.packs.metadata.pack.ResourcePackInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackLoader implements AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String id;
    private final Supplier<IResourcePack> supplier;
    private final IChatBaseComponent title;
    private final IChatBaseComponent description;
    private final EnumResourcePackVersion compatibility;
    private final ResourcePackLoader.Position defaultPosition;
    private final boolean required;
    private final boolean fixedPosition;
    private final PackSource packSource;

    @Nullable
    public static ResourcePackLoader a(String s, boolean flag, Supplier<IResourcePack> supplier, ResourcePackLoader.a resourcepackloader_a, ResourcePackLoader.Position resourcepackloader_position, PackSource packsource) {
        try {
            IResourcePack iresourcepack = (IResourcePack) supplier.get();

            label48:
            {
                ResourcePackLoader resourcepackloader;

                try {
                    ResourcePackInfo resourcepackinfo = (ResourcePackInfo) iresourcepack.a((ResourcePackMetaParser) ResourcePackInfo.SERIALIZER);

                    if (resourcepackinfo == null) {
                        ResourcePackLoader.LOGGER.warn("Couldn't find pack meta for pack {}", s);
                        break label48;
                    }

                    resourcepackloader = resourcepackloader_a.create(s, new ChatComponentText(iresourcepack.a()), flag, supplier, resourcepackinfo, resourcepackloader_position, packsource);
                } catch (Throwable throwable) {
                    if (iresourcepack != null) {
                        try {
                            iresourcepack.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    }

                    throw throwable;
                }

                if (iresourcepack != null) {
                    iresourcepack.close();
                }

                return resourcepackloader;
            }

            if (iresourcepack != null) {
                iresourcepack.close();
            }
        } catch (IOException ioexception) {
            ResourcePackLoader.LOGGER.warn("Couldn't get pack info for: {}", ioexception.toString());
        }

        return null;
    }

    public ResourcePackLoader(String s, boolean flag, Supplier<IResourcePack> supplier, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, EnumResourcePackVersion enumresourcepackversion, ResourcePackLoader.Position resourcepackloader_position, boolean flag1, PackSource packsource) {
        this.id = s;
        this.supplier = supplier;
        this.title = ichatbasecomponent;
        this.description = ichatbasecomponent1;
        this.compatibility = enumresourcepackversion;
        this.required = flag;
        this.defaultPosition = resourcepackloader_position;
        this.fixedPosition = flag1;
        this.packSource = packsource;
    }

    public ResourcePackLoader(String s, IChatBaseComponent ichatbasecomponent, boolean flag, Supplier<IResourcePack> supplier, ResourcePackInfo resourcepackinfo, EnumResourcePackType enumresourcepacktype, ResourcePackLoader.Position resourcepackloader_position, PackSource packsource) {
        this(s, flag, supplier, ichatbasecomponent, resourcepackinfo.a(), EnumResourcePackVersion.a(resourcepackinfo, enumresourcepacktype), resourcepackloader_position, false, packsource);
    }

    public IChatBaseComponent a() {
        return this.title;
    }

    public IChatBaseComponent b() {
        return this.description;
    }

    public IChatBaseComponent a(boolean flag) {
        return ChatComponentUtils.a(this.packSource.decorate(new ChatComponentText(this.id))).format((chatmodifier) -> {
            return chatmodifier.setColor(flag ? EnumChatFormat.GREEN : EnumChatFormat.RED).setInsertion(StringArgumentType.escapeIfRequired(this.id)).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, (new ChatComponentText("")).addSibling(this.title).c("\n").addSibling(this.description)));
        });
    }

    public EnumResourcePackVersion c() {
        return this.compatibility;
    }

    public IResourcePack d() {
        return (IResourcePack) this.supplier.get();
    }

    public String e() {
        return this.id;
    }

    public boolean f() {
        return this.required;
    }

    public boolean g() {
        return this.fixedPosition;
    }

    public ResourcePackLoader.Position h() {
        return this.defaultPosition;
    }

    public PackSource i() {
        return this.packSource;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ResourcePackLoader)) {
            return false;
        } else {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) object;

            return this.id.equals(resourcepackloader.id);
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public void close() {}

    @FunctionalInterface
    public interface a {

        @Nullable
        ResourcePackLoader create(String s, IChatBaseComponent ichatbasecomponent, boolean flag, Supplier<IResourcePack> supplier, ResourcePackInfo resourcepackinfo, ResourcePackLoader.Position resourcepackloader_position, PackSource packsource);
    }

    public static enum Position {

        TOP, BOTTOM;

        private Position() {}

        public <T> int a(List<T> list, T t0, Function<T, ResourcePackLoader> function, boolean flag) {
            ResourcePackLoader.Position resourcepackloader_position = flag ? this.a() : this;
            ResourcePackLoader resourcepackloader;
            int i;

            if (resourcepackloader_position == ResourcePackLoader.Position.BOTTOM) {
                for (i = 0; i < list.size(); ++i) {
                    resourcepackloader = (ResourcePackLoader) function.apply(list.get(i));
                    if (!resourcepackloader.g() || resourcepackloader.h() != this) {
                        break;
                    }
                }

                list.add(i, t0);
                return i;
            } else {
                for (i = list.size() - 1; i >= 0; --i) {
                    resourcepackloader = (ResourcePackLoader) function.apply(list.get(i));
                    if (!resourcepackloader.g() || resourcepackloader.h() != this) {
                        break;
                    }
                }

                list.add(i + 1, t0);
                return i + 1;
            }
        }

        public ResourcePackLoader.Position a() {
            return this == ResourcePackLoader.Position.TOP ? ResourcePackLoader.Position.BOTTOM : ResourcePackLoader.Position.TOP;
        }
    }
}
