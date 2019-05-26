package com.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.ArrayList;

public class LevelManager
{
    private String level;
    int mapWidth;
    int mapHeight;
    Player player;
    int playerIndex;
    private boolean playing;
    float gravity;
    LevelData levelData;
    ArrayList<GameObject> gameObjects;
    ArrayList<Rect> currentButtons;
    Bitmap[] bitmapsArray;

    public LevelManager(Context context,
                        int pixelsPerMetre, int screenWidth,
                        InputController ic,
                        String level,
                        float px, float py)
    {
        this.level = level;
        switch (level)
        {
            case "LevelCave":
                levelData = new LevelCave();
                break;
        }

        gameObjects = new ArrayList<>();
        bitmapsArray = new Bitmap[25];
        loadMapData(context, pixelsPerMetre, px, py);

    }

    public void switchPlayingStatus()
    {
        playing = !playing;
        if (playing)
            gravity = 6;
        else
            gravity = 0;
    }

    public boolean isPlaying()
    {
        return playing;
    }

    public Bitmap getBitmap(char blockType)
    {
        int index;
        switch (blockType)
        {
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            default:
                index = 0;
                break;
        }
        return bitmapsArray[index];
    }

    public int getBitmapIndex(char blockType)
    {
        int index;
        switch (blockType)
        {
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    private void loadMapData(Context context, int pixelsPerMetre, float px, float py)
    {
        char c;

        int currentIndex = -1;

        mapHeight = levelData.tiles.size();
        mapWidth = levelData.tiles.get(0).length();

        for (int i = 0; i < levelData.tiles.size(); i++)
        {
            for (int j = 0; j < levelData.tiles.get(i).length(); j++)
            {
                c = levelData.tiles.get(i).charAt(j);
                if (c != '.')
                {
                    currentIndex++;
                    switch (c)
                    {
                        case '1':
                            gameObjects.add(new Grass(j, i, c));
                            break;
                        case 'p':
                            gameObjects.add(new Player
                                    (context, px, py, pixelsPerMetre));
                            playerIndex = currentIndex;
                            player = (Player)
                                    gameObjects.get(playerIndex);
                            break;
                    }
                    if (bitmapsArray[getBitmapIndex(c)] == null)
                    {
                        bitmapsArray[getBitmapIndex(c)] =
                                gameObjects.get(currentIndex).prepareBitmap(context,
                                        gameObjects.get(currentIndex).getBitmapName(),
                                        pixelsPerMetre);
                    }
                }
            }
        }
    }
}
