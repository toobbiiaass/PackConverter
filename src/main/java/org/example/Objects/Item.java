package org.example.Objects;

public class Item {
    private int id;
    private String name;
    private int x;
    private int y;
    private int xLength;
    private int yLength;
    private String path;

    public Item(int id, String name, int x, int y, int xLength, int yLength, String path) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.xLength = xLength;
        this.yLength = yLength;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getxLength() {
        return xLength;
    }

    public int getyLength() {
        return yLength;
    }

    public String getPath() {
        return path;
    }
}
