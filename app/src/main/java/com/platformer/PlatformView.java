package com.platformer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class PlatformView extends SurfaceView implements Runnable
{
    private boolean debugging = true;
    private volatile boolean running;
    private Thread gameThread = null;

    private Paint paint;

    private Canvas canvas;
    private SurfaceHolder ourHolder;
    Context context;
    long startFrameTime;
    long timeThisFrame;
    long fps;

    // Our new engine classes
    private LevelManager lm;
    private Viewport vp;
    InputController ic;
    SoundManager sm;

    PlatformView(Context context, int screenWidth, int screenHeight)
    {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        vp = new Viewport(screenWidth, screenHeight);
        sm = new SoundManager();
        sm.loadSound(context);
        loadLevel("LevelCave", 15, 2);
    }

    @Override
    public void run()
    {
        while (running)
        {
            startFrameTime = System.currentTimeMillis();
            update();
            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1)
            {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    public void loadLevel(String level, float px, float py)
    {
        lm = null;
        lm = new LevelManager(context, vp.getPixelsPerMetreX(),
                vp.getScreenWidth(), ic, level, px, py);
        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());

        vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex)
                        .getWorldLocation().x,
                lm.gameObjects.get(lm.playerIndex)
                        .getWorldLocation().y);
    }

    private void update()
    {
        for (GameObject go : lm.gameObjects)
        {
            if (go.isActive())
            {
                if (!vp.clipObjects(go.getWorldLocation().x,
                        go.getWorldLocation().y,
                        go.getWidth(),
                        go.getHeight()))
                {
                    go.setVisible(true);

                    int hit = lm.player.checkCollisions(go.getHitbox());
                    if (hit > 0)
                    {
                        switch (go.getType())
                        {
                            default:// Probably a regular tile
                                if (hit == 1) {// Left or right
                                    lm.player.setxVelocity(0);
                                    lm.player.setPressingRight(false);
                                }
                                if (hit == 2) {// Feet
                                    lm.player.isFalling = false;
                                }
                                break;
                        }
                    }
                    if (lm.isPlaying())
                    {
                        go.update(fps, lm.gravity);
                    }
                }
                else
                {
                    go.setVisible(false);
                }
            }
        }
        if (lm.isPlaying())
        {
            vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex)
                            .getWorldLocation().x,
                    lm.gameObjects.get(lm.playerIndex)
                            .getWorldLocation().y);
        }
    }

    private void draw()
    {
        if (ourHolder.getSurface().isValid())
        {
            canvas = ourHolder.lockCanvas();

            paint.setColor(Color.argb(255, 0, 0, 255));
            canvas.drawColor(Color.argb(255, 0, 0, 255));

            Rect toScreen2d = new Rect();

            for (int layer = -1; layer <= 1; layer++)
                for (GameObject go : lm.gameObjects)
                    if (go.isVisible() && go.getWorldLocation().z == layer)
                    {
                        toScreen2d.set(vp.worldToScreen(go.getWorldLocation().x,
                                        go.getWorldLocation().y,
                                        go.getWidth(),
                                        go.getHeight()));

                        canvas.drawBitmap(
                                lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                toScreen2d.left,
                                toScreen2d.top, paint);
                    }

            if (debugging)
            {
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawText("fps:" + fps, 10, 60, paint);
                canvas.drawText("num objects:" +
                        lm.gameObjects.size(), 10, 100, paint);
                canvas.drawText("num clipped:" +
                        vp.getNumClipped(), 10, 140, paint);
                canvas.drawText("playerX:" +
                                lm.gameObjects.get(lm.playerIndex).
                                        getWorldLocation().x,
                        10, 180, paint);
                canvas.drawText("playerY:" +
                        lm.gameObjects.get(lm.playerIndex).getWorldLocation().y,
                        10, 220, paint);

                canvas.drawText("Gravity:" +
                        lm.gravity, 10, 260, paint);
                canvas.drawText("X velocity:" +
                                lm.gameObjects.get(lm.playerIndex).getxVelocity(),
                        10, 300, paint);
                canvas.drawText("Y velocity:" +
                                lm.gameObjects.get(lm.playerIndex).getyVelocity(),
                        10, 340, paint);

                vp.resetNumClipped();
            }

            paint.setColor(Color.argb(80, 255, 255, 255));
            ArrayList<Rect> buttonsToDraw;
            buttonsToDraw = ic.getButtons();
            for (Rect rect : buttonsToDraw)
            {
                RectF rf = new RectF(rect.left, rect.top,
                        rect.right, rect.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
            }

            if (!this.lm.isPlaying())
            {
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(120);
                canvas.drawText("Paused", vp.getScreenWidth() / 2,
                        vp.getScreenHeight() / 2, paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause()
    {
        running = false;
        try
        {
            gameThread.join();
        } catch (InterruptedException e)
        {
            Log.e("error", "failed to pause thread");
        }
    }

    public void resume()
    {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        if (lm != null)
        {
            ic.handleInput(motionEvent, lm, sm, vp);
        }
        return true;
    }
}