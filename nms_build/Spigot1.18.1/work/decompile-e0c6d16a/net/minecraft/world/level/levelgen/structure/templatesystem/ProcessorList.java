package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;

public class ProcessorList {

    private final List<DefinedStructureProcessor> list;

    public ProcessorList(List<DefinedStructureProcessor> list) {
        this.list = list;
    }

    public List<DefinedStructureProcessor> list() {
        return this.list;
    }

    public String toString() {
        return "ProcessorList[" + this.list + "]";
    }
}
