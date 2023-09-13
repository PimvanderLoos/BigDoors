package net.minecraft.server;

public interface IMinecraftServer {

    int a(String s, int i);

    String a(String s, String s1);

    void a(String s, Object object);

    void a();

    String b();

    String d_();

    int e_();

    String f_();

    String getVersion();

    int H();

    int I();

    String[] getPlayers();

    String S();

    String getPlugins();

    String executeRemoteCommand(String s);

    boolean isDebugging();

    void info(String s);

    void warning(String s);

    void g(String s);

    void h(String s);
}
