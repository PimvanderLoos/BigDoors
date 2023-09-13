package net.minecraft.tags;

import java.util.Map;
import java.util.stream.Collectors;

public class TagsInstance {

    private static volatile ITagRegistry a = ITagRegistry.a(Tags.a((Map) TagsBlock.b().stream().collect(Collectors.toMap(Tag.e::a, (tag_e) -> {
        return tag_e;
    }))), Tags.a((Map) TagsItem.b().stream().collect(Collectors.toMap(Tag.e::a, (tag_e) -> {
        return tag_e;
    }))), Tags.a((Map) TagsFluid.b().stream().collect(Collectors.toMap(Tag.e::a, (tag_e) -> {
        return tag_e;
    }))), Tags.a((Map) TagsEntity.b().stream().collect(Collectors.toMap(Tag.e::a, (tag_e) -> {
        return tag_e;
    }))));

    public static ITagRegistry a() {
        return TagsInstance.a;
    }

    public static void a(ITagRegistry itagregistry) {
        TagsInstance.a = itagregistry;
    }
}
