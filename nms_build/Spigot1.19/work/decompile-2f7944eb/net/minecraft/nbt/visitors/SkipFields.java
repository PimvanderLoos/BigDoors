package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagType;
import net.minecraft.nbt.StreamTagVisitor;

public class SkipFields extends CollectToTag {

    private final Deque<FieldTree> stack = new ArrayDeque();

    public SkipFields(FieldSelector... afieldselector) {
        FieldTree fieldtree = FieldTree.createRoot();
        FieldSelector[] afieldselector1 = afieldselector;
        int i = afieldselector.length;

        for (int j = 0; j < i; ++j) {
            FieldSelector fieldselector = afieldselector1[j];

            fieldtree.addEntry(fieldselector);
        }

        this.stack.push(fieldtree);
    }

    @Override
    public StreamTagVisitor.a visitEntry(NBTTagType<?> nbttagtype, String s) {
        FieldTree fieldtree = (FieldTree) this.stack.element();

        if (fieldtree.isSelected(nbttagtype, s)) {
            return StreamTagVisitor.a.SKIP;
        } else {
            if (nbttagtype == NBTTagCompound.TYPE) {
                FieldTree fieldtree1 = (FieldTree) fieldtree.fieldsToRecurse().get(s);

                if (fieldtree1 != null) {
                    this.stack.push(fieldtree1);
                }
            }

            return super.visitEntry(nbttagtype, s);
        }
    }

    @Override
    public StreamTagVisitor.b visitContainerEnd() {
        if (this.depth() == ((FieldTree) this.stack.element()).depth()) {
            this.stack.pop();
        }

        return super.visitContainerEnd();
    }
}
