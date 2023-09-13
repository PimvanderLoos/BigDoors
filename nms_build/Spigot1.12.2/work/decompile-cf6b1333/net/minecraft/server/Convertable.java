package net.minecraft.server;

import java.io.File;

public interface Convertable {

    IDataManager a(String s, boolean flag);

    boolean isConvertable(String s);

    boolean convert(String s, IProgressUpdate iprogressupdate);

    File b(String s, String s1);
}
