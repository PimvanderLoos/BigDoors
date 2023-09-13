package net.minecraft.server;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackLoader {

    private static final Logger a = LogManager.getLogger();
    private final String b;
    private final Supplier<IResourcePack> c;
    private final IChatBaseComponent d;
    private final IChatBaseComponent e;
    private final EnumResourcePackVersion f;
    private final ResourcePackLoader.Position g;
    private final boolean h;
    private final boolean i;

    @Nullable
    public static <T extends ResourcePackLoader> T a(String s, boolean flag, Supplier<IResourcePack> supplier, ResourcePackLoader.b<T> resourcepackloader_b, ResourcePackLoader.Position resourcepackloader_position) {
        try {
            IResourcePack iresourcepack = (IResourcePack) supplier.get();
            Throwable throwable = null;

            ResourcePackLoader resourcepackloader;

            try {
                ResourcePackInfo resourcepackinfo = (ResourcePackInfo) iresourcepack.a((ResourcePackMetaParser) ResourcePackInfo.a);

                if (resourcepackinfo == null) {
                    ResourcePackLoader.a.warn("Couldn\'t find pack meta for pack {}", s);
                    return null;
                }

                resourcepackloader = resourcepackloader_b.create(s, flag, supplier, iresourcepack, resourcepackinfo, resourcepackloader_position);
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (iresourcepack != null) {
                    if (throwable != null) {
                        try {
                            iresourcepack.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        iresourcepack.close();
                    }
                }

            }

            return resourcepackloader;
        } catch (IOException ioexception) {
            ResourcePackLoader.a.warn("Couldn\'t get pack info for: {}", ioexception.toString());
            return null;
        }
    }

    public ResourcePackLoader(String s, boolean flag, Supplier<IResourcePack> supplier, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, EnumResourcePackVersion enumresourcepackversion, ResourcePackLoader.Position resourcepackloader_position, boolean flag1) {
        this.b = s;
        this.c = supplier;
        this.d = ichatbasecomponent;
        this.e = ichatbasecomponent1;
        this.f = enumresourcepackversion;
        this.h = flag;
        this.g = resourcepackloader_position;
        this.i = flag1;
    }

    public ResourcePackLoader(String s, boolean flag, Supplier<IResourcePack> supplier, IResourcePack iresourcepack, ResourcePackInfo resourcepackinfo, ResourcePackLoader.Position resourcepackloader_position) {
        this(s, flag, supplier, new ChatComponentText(iresourcepack.a()), resourcepackinfo.a(), EnumResourcePackVersion.a(resourcepackinfo.b()), resourcepackloader_position, false);
    }

    public IChatBaseComponent a(boolean flag) {
        return ChatComponentUtils.a((IChatBaseComponent) (new ChatComponentText(this.b))).a((chatmodifier) -> {
            chatmodifier.setColor(flag ? EnumChatFormat.GREEN : EnumChatFormat.RED).setInsertion(StringArgumentType.escapeIfRequired(this.b)).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, (new ChatComponentText("")).addSibling(this.d).a("\n").addSibling(this.e)));
        });
    }

    public EnumResourcePackVersion c() {
        return this.f;
    }

    public IResourcePack d() {
        return (IResourcePack) this.c.get();
    }

    public String e() {
        return this.b;
    }

    public boolean f() {
        return this.h;
    }

    public boolean g() {
        return this.i;
    }

    public ResourcePackLoader.Position h() {
        return this.g;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ResourcePackLoader)) {
            return false;
        } else {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) object;

            return this.b.equals(resourcepackloader.b);
        }
    }

    public int hashCode() {
        return this.b.hashCode();
    }

    public static enum Position {

        TOP, BOTTOM;

        private Position() {}

        public <T, P extends ResourcePackLoader> int a(List<T> list, T t0, Function<T, P> function, boolean flag) {
            ResourcePackLoader.Position resourcepackloader_position = flag ? this.a() : this;
            int i;
            ResourcePackLoader resourcepackloader;

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

    @FunctionalInterface
    public interface b<T extends ResourcePackLoader> {

        @Nullable
        T create(String s, boolean flag, Supplier<IResourcePack> supplier, IResourcePack iresourcepack, ResourcePackInfo resourcepackinfo, ResourcePackLoader.Position resourcepackloader_position);
    }
}
