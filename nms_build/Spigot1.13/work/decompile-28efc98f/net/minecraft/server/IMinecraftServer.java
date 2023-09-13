package net.minecraft.server;

public interface IMinecraftServer {

    int a(String s, int i);

    String a(String s, String s1);

    void a(String s, Object object);

    void c_();

    String d_();

    String e();

    int f();

    String e_();

    String getVersion();

    int A();

    int B();

    String[] getPlayers();

    String getWorld();

    String getPlugins();

    String executeRemoteCommand(String s);

    boolean isDebugging();

    void info(String s);

    void warning(String s);

    void f(String s);

    void g(String s);
}
