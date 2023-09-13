package net.minecraft.tags;

public class TagsInstance {

    private static volatile ITagRegistry instance = TagStatic.c();

    public TagsInstance() {}

    public static ITagRegistry a() {
        return TagsInstance.instance;
    }

    public static void a(ITagRegistry itagregistry) {
        TagsInstance.instance = itagregistry;
    }
}
