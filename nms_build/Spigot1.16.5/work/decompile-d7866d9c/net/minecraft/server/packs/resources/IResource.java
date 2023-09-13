package net.minecraft.server.packs.resources;

import java.io.Closeable;
import java.io.InputStream;

public interface IResource extends Closeable {

    InputStream b();

    String d();
}
