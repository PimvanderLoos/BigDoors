package nl.pim16aap2.bigDoors.util;

public class Vector2D
{
    private int x, y;

    public Vector2D(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D other)
    {
        x = other.x;
        y = other.y;
    }

    public void add(Vector2D other)
    {
        add(other.x, other.y);
    }

    public void subtract(Vector2D other)
    {
        add(-other.x, -other.y);
    }

    public void addX(int val)
    {
        x += val;
    }

    public void addY(int val)
    {
        y += val;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void add(int x, int y)
    {
        this.x += x;
        this.y += y;
    }

    @Override
    public String toString()
    {
        return "(" + x + ":" + y + ")";
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 19 * hash + x;
        hash = 19 * hash + y;
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        Vector2D other = (Vector2D) o;
        return x == other.x && y == other.y;
    }
}