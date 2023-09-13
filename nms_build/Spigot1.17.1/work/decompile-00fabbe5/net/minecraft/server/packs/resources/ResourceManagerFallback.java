package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.packs.IResourcePack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceManagerFallback implements IResourceManager {

    static final Logger LOGGER = LogManager.getLogger();
    protected final List<IResourcePack> fallbacks = Lists.newArrayList();
    private final EnumResourcePackType type;
    private final String namespace;

    public ResourceManagerFallback(EnumResourcePackType enumresourcepacktype, String s) {
        this.type = enumresourcepacktype;
        this.namespace = s;
    }

    public void a(IResourcePack iresourcepack) {
        this.fallbacks.add(iresourcepack);
    }

    @Override
    public Set<String> a() {
        return ImmutableSet.of(this.namespace);
    }

    @Override
    public IResource a(MinecraftKey minecraftkey) throws IOException {
        this.e(minecraftkey);
        IResourcePack iresourcepack = null;
        MinecraftKey minecraftkey1 = d(minecraftkey);

        for (int i = this.fallbacks.size() - 1; i >= 0; --i) {
            IResourcePack iresourcepack1 = (IResourcePack) this.fallbacks.get(i);

            if (iresourcepack == null && iresourcepack1.b(this.type, minecraftkey1)) {
                iresourcepack = iresourcepack1;
            }

            if (iresourcepack1.b(this.type, minecraftkey)) {
                InputStream inputstream = null;

                if (iresourcepack != null) {
                    inputstream = this.a(minecraftkey1, iresourcepack);
                }

                return new Resource(iresourcepack1.a(), minecraftkey, this.a(minecraftkey, iresourcepack1), inputstream);
            }
        }

        throw new FileNotFoundException(minecraftkey.toString());
    }

    @Override
    public boolean b(MinecraftKey minecraftkey) {
        if (!this.f(minecraftkey)) {
            return false;
        } else {
            for (int i = this.fallbacks.size() - 1; i >= 0; --i) {
                IResourcePack iresourcepack = (IResourcePack) this.fallbacks.get(i);

                if (iresourcepack.b(this.type, minecraftkey)) {
                    return true;
                }
            }

            return false;
        }
    }

    protected InputStream a(MinecraftKey minecraftkey, IResourcePack iresourcepack) throws IOException {
        InputStream inputstream = iresourcepack.a(this.type, minecraftkey);

        return (InputStream) (ResourceManagerFallback.LOGGER.isDebugEnabled() ? new ResourceManagerFallback.a(inputstream, minecraftkey, iresourcepack.a()) : inputstream);
    }

    private void e(MinecraftKey minecraftkey) throws IOException {
        if (!this.f(minecraftkey)) {
            throw new IOException("Invalid relative path to resource: " + minecraftkey);
        }
    }

    private boolean f(MinecraftKey minecraftkey) {
        return !minecraftkey.getKey().contains("..");
    }

    @Override
    public List<IResource> c(MinecraftKey minecraftkey) throws IOException {
        this.e(minecraftkey);
        List<IResource> list = Lists.newArrayList();
        MinecraftKey minecraftkey1 = d(minecraftkey);
        Iterator iterator = this.fallbacks.iterator();

        while (iterator.hasNext()) {
            IResourcePack iresourcepack = (IResourcePack) iterator.next();

            if (iresourcepack.b(this.type, minecraftkey)) {
                InputStream inputstream = iresourcepack.b(this.type, minecraftkey1) ? this.a(minecraftkey1, iresourcepack) : null;

                list.add(new Resource(iresourcepack.a(), minecraftkey, this.a(minecraftkey, iresourcepack), inputstream));
            }
        }

        if (list.isEmpty()) {
            throw new FileNotFoundException(minecraftkey.toString());
        } else {
            return list;
        }
    }

    @Override
    public Collection<MinecraftKey> a(String s, Predicate<String> predicate) {
        List<MinecraftKey> list = Lists.newArrayList();
        Iterator iterator = this.fallbacks.iterator();

        while (iterator.hasNext()) {
            IResourcePack iresourcepack = (IResourcePack) iterator.next();

            list.addAll(iresourcepack.a(this.type, this.namespace, s, Integer.MAX_VALUE, predicate));
        }

        Collections.sort(list);
        return list;
    }

    @Override
    public Stream<IResourcePack> b() {
        return this.fallbacks.stream();
    }

    static MinecraftKey d(MinecraftKey minecraftkey) {
        return new MinecraftKey(minecraftkey.getNamespace(), minecraftkey.getKey() + ".mcmeta");
    }

    private static class a extends FilterInputStream {

        private final String message;
        private boolean closed;

        public a(InputStream inputstream, MinecraftKey minecraftkey, String s) {
            super(inputstream);
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

            (new Exception()).printStackTrace(new PrintStream(bytearrayoutputstream));
            this.message = "Leaked resource: '" + minecraftkey + "' loaded from pack: '" + s + "'\n" + bytearrayoutputstream;
        }

        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                ResourceManagerFallback.LOGGER.warn(this.message);
            }

            super.finalize();
        }
    }
}
