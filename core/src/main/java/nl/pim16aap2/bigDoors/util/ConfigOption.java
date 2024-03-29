package nl.pim16aap2.bigDoors.util;

import java.util.List;

public class ConfigOption
{
    private String   optionName;
    List<String>     listVal   = null;
    private Integer  intVal    = null;
    private Double   doubleVal = null;
    private Boolean  boolVal   = null;
    private String   stringVal = null;
    private String[] comment;

    public ConfigOption(String optionName, int value, String[] comment)
    {
        this.optionName = optionName;
        intVal = value;
        this.comment = comment;
    }

    public ConfigOption(String optionName, int value)
    {
        this.optionName = optionName;
        intVal = value;
        comment = null;
    }

    public ConfigOption(String optionName, boolean value, String[] comment)
    {
        this.optionName = optionName;
        boolVal = value;
        this.comment = comment;
    }

    public ConfigOption(String optionName, boolean value)
    {
        this.optionName = optionName;
        boolVal = value;
        comment = null;
    }

    public ConfigOption(String optionName, String value, String[] comment)
    {
        this.optionName = optionName;
        stringVal = value;
        this.comment = comment;
    }

    public ConfigOption(String optionName, String value)
    {
        this.optionName = optionName;
        stringVal = value;
        comment = null;
    }

    public ConfigOption(String optionName, List<String> value, String[] comment)
    {
        this.optionName = optionName;
        listVal = value;
        this.comment = comment;
    }

    public ConfigOption(String optionName, List<String> value)
    {
        this.optionName = optionName;
        listVal = value;
        comment = null;
    }

    public ConfigOption(String optionName, double value, String[] comment)
    {
        this.optionName = optionName;
        doubleVal = value;
        this.comment = comment;
    }

    public ConfigOption(String optionName, double value)
    {
        this.optionName = optionName;
        doubleVal = value;
        comment = null;
    }

    public String stringListToString()
    {
        if (listVal.isEmpty())
            return "  - NONE";

        String string = "";
        for (String s : listVal)
            string += "  - " + s + "\n";
        return string;
    }

    public String       getName()       { return optionName; }
    public List<String> getStringList() { return listVal   ; }
    public int          getInt()        { return intVal    ; }
    public double       getDouble()     { return doubleVal ; }
    public boolean      getBool()       { return boolVal   ; }
    public String       getString()     { return stringVal ; }
    public String[]     getComment()    { return comment   ; }

    @Override
    public String toString()
    {
        String string = "";

        // Print the comments, if there are any.
        if (comment != null)
        {
            for (String comLine : comment)
                // Prefix every line by a comment-sign (#).
                string += "# " + comLine + "\n";
        }

        // Then add the name of the option followed by its value (if it is an int/bool/String/String[]).
        string += optionName + ": " +
            (intVal    != null ? intVal    :
             doubleVal != null ? doubleVal :
             boolVal   != null ? boolVal   :
             stringVal != null ? "\'" + stringVal + "\'" :
             listVal   != null ? "\n" + stringListToString() : null);

        return string;
    }
}
