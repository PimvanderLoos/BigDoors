package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;

public class ProcessorList {

    private final List<DefinedStructureProcessor> a;

    public ProcessorList(List<DefinedStructureProcessor> list) {
        this.a = list;
    }

    public List<DefinedStructureProcessor> a() {
        return this.a;
    }

    public String toString() {
        return "ProcessorList[" + this.a + "]";
    }
}
