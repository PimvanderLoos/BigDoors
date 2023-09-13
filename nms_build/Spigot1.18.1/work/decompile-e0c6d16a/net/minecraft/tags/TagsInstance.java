package net.minecraft.tags;

public class TagsInstance {

    private static volatile ITagRegistry instance = TagStatic.createCollection();

    public TagsInstance() {}

    public static ITagRegistry getInstance() {
        return TagsInstance.instance;
    }

    public static void bind(ITagRegistry itagregistry) {
        TagsInstance.instance = itagregistry;
    }
}
