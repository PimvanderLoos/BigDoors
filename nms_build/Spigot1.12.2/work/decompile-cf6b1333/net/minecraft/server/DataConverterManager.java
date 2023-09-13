package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataConverterManager implements DataConverter {

    private static final Logger a = LogManager.getLogger();
    private final Map<DataConverterType, List<DataInspector>> b = Maps.newHashMap();
    private final Map<DataConverterType, List<IDataConverter>> c = Maps.newHashMap();
    private final int d;

    public DataConverterManager(int i) {
        this.d = i;
    }

    public NBTTagCompound a(DataConverterType dataconvertertype, NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.hasKeyOfType("DataVersion", 99) ? nbttagcompound.getInt("DataVersion") : -1;

        return i >= 1343 ? nbttagcompound : this.a(dataconvertertype, nbttagcompound, i);
    }

    public NBTTagCompound a(DataConverterType dataconvertertype, NBTTagCompound nbttagcompound, int i) {
        if (i < this.d) {
            nbttagcompound = this.b(dataconvertertype, nbttagcompound, i);
            nbttagcompound = this.c(dataconvertertype, nbttagcompound, i);
        }

        return nbttagcompound;
    }

    private NBTTagCompound b(DataConverterType dataconvertertype, NBTTagCompound nbttagcompound, int i) {
        List list = (List) this.c.get(dataconvertertype);

        if (list != null) {
            for (int j = 0; j < list.size(); ++j) {
                IDataConverter idataconverter = (IDataConverter) list.get(j);

                if (idataconverter.a() > i) {
                    nbttagcompound = idataconverter.a(nbttagcompound);
                }
            }
        }

        return nbttagcompound;
    }

    private NBTTagCompound c(DataConverterType dataconvertertype, NBTTagCompound nbttagcompound, int i) {
        List list = (List) this.b.get(dataconvertertype);

        if (list != null) {
            for (int j = 0; j < list.size(); ++j) {
                nbttagcompound = ((DataInspector) list.get(j)).a(this, nbttagcompound, i);
            }
        }

        return nbttagcompound;
    }

    public void a(DataConverterTypes dataconvertertypes, DataInspector datainspector) {
        this.a((DataConverterType) dataconvertertypes, datainspector);
    }

    public void a(DataConverterType dataconvertertype, DataInspector datainspector) {
        this.a(this.b, dataconvertertype).add(datainspector);
    }

    public void a(DataConverterType dataconvertertype, IDataConverter idataconverter) {
        List list = this.a(this.c, dataconvertertype);
        int i = idataconverter.a();

        if (i > this.d) {
            DataConverterManager.a.warn("Ignored fix registered for version: {} as the DataVersion of the game is: {}", Integer.valueOf(i), Integer.valueOf(this.d));
        } else {
            if (!list.isEmpty() && ((IDataConverter) SystemUtils.a(list)).a() > i) {
                for (int j = 0; j < list.size(); ++j) {
                    if (((IDataConverter) list.get(j)).a() > i) {
                        list.add(j, idataconverter);
                        break;
                    }
                }
            } else {
                list.add(idataconverter);
            }

        }
    }

    private <V> List<V> a(Map<DataConverterType, List<V>> map, DataConverterType dataconvertertype) {
        Object object = (List) map.get(dataconvertertype);

        if (object == null) {
            object = Lists.newArrayList();
            map.put(dataconvertertype, object);
        }

        return (List) object;
    }
}
