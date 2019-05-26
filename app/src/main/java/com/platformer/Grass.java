package com.platformer;

public class Grass extends GameObject
{
    Grass(float worldStartX, float worldStartY, char type)
    {
        final float HEIGHT = 1;
        final float WIDTH = 1;
        setHeight(HEIGHT); // 1 metre tall
        setWidth(WIDTH); // 1 metre wide
        setType(type);
        setBitmapName("turf");
        setWorldLocation(worldStartX, worldStartY, 0);
        setRectHitbox();
    }
    public void update(long fps, float gravity) {}
}