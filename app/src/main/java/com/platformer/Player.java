package com.platformer;

import android.content.Context;

public class Player extends GameObject
{
    final float MAX_X_VELOCITY = 10;
    boolean isPressingRight = false;
    boolean isPressingLeft = false;

    public boolean isFalling;
    private boolean isJumping;
    private long jumpTime;
    private long maxJumpTime = 700;// jump 7 10ths of second

    RectHitbox rectHitboxFeet;
    RectHitbox rectHitboxHead;
    RectHitbox rectHitboxLeft;
    RectHitbox rectHitboxRight;

    Player(Context context, float worldStartX,
           float worldStartY, int pixelsPerMetre)
    {
        final float HEIGHT = 2;
        final float WIDTH = 1;
        setHeight(HEIGHT); // 2 metre tall
        setWidth(WIDTH); // 1 metre wide

        setxVelocity(0);
        setyVelocity(0);
        setFacing(LEFT);
        isFalling = false;
        setMoves(true);
        setActive(true);
        setVisible(true);

        setType('p');
        setBitmapName("player");
        setWorldLocation(worldStartX, worldStartY, 0);
        rectHitboxFeet = new RectHitbox();
        rectHitboxHead = new RectHitbox();
        rectHitboxLeft = new RectHitbox();
        rectHitboxRight = new RectHitbox();
    }

    public void update(long fps, float gravity)
    {
        if (isPressingRight)
            this.setxVelocity(MAX_X_VELOCITY);
        else if (isPressingLeft)
            this.setxVelocity(-MAX_X_VELOCITY);
        else
            this.setxVelocity(0);

        if (this.getxVelocity() > 0)
            setFacing(RIGHT);
        else if (this.getxVelocity() < 0)
            setFacing(LEFT);

        if (isJumping)
        {
            long timeJumping = System.currentTimeMillis() - jumpTime;
            if (timeJumping < maxJumpTime)
            {
                if (timeJumping < maxJumpTime / 2)
                {
                    this.setyVelocity(-gravity);//on the way up
                } else if (timeJumping > maxJumpTime / 2)
                {
                    this.setyVelocity(gravity);//going down
                }
            } else {
                isJumping = false;
            }
        } else {
            this.setyVelocity(gravity);
            isFalling = true;
        }
        move(fps);

        Vector2Point5D location = getWorldLocation();
        float lx = location.x;
        float ly = location.y;

        rectHitboxFeet.top = ly + getHeight() * .95f;
        rectHitboxFeet.left = lx + getWidth() * .2f;
        rectHitboxFeet.bottom = ly + getHeight() * .98f;
        rectHitboxFeet.right = lx + getWidth() * .8f;

        rectHitboxHead.top = ly;
        rectHitboxHead.left = lx + getWidth() * .4f;
        rectHitboxHead.bottom = ly + getHeight() * .2f;
        rectHitboxHead.right = lx + getWidth() * .6f;

        rectHitboxLeft.top = ly + getHeight() * .2f;
        rectHitboxLeft.left = lx + getWidth() * .2f;
        rectHitboxLeft.bottom = ly + getHeight() * .8f;
        rectHitboxLeft.right = lx + getWidth() * .3f;

        rectHitboxRight.top = ly + getHeight() * .2f;
        rectHitboxRight.left = lx + getWidth() * .8f;
        rectHitboxRight.bottom = ly + getHeight() * .8f;
        rectHitboxRight.right = lx + getWidth() * .7f;
    }

    public int checkCollisions(RectHitbox rectHitbox)
    {
        int collided = 0;// No collision
// The left
        if (this.rectHitboxLeft.intersects(rectHitbox))
        {
// Left has collided
// Move player just to right of current hitbox
            this.setWorldLocationX(rectHitbox.right - getWidth() * .2f);
            collided = 1;
        }
// The right
        if (this.rectHitboxRight.intersects(rectHitbox))
        {
// Right has collided
// Move player just to left of current hitbox
            this.setWorldLocationX(rectHitbox.left - getWidth() * .8f);
            collided = 1;
        }
// The feet
        if (this.rectHitboxFeet.intersects(rectHitbox))
        {
// Feet have collided
// Move feet to just above current hitbox
            this.setWorldLocationY(rectHitbox.top - getHeight());
            collided = 2;
        }
// Now the head
        if (this.rectHitboxHead.intersects(rectHitbox))
        {
// Head has collided. Ouch!
// Move head to just below current hitbox bottom
            this.setWorldLocationY(rectHitbox.bottom);
            collided = 3;
        }
        return collided;
    }

    public void setWorldLocationY(float y)
    {
        this.worldLocation.y = y;
    }
    public void setWorldLocationX(float x)
    {
        this.worldLocation.x = x;
    }

    public void setPressingRight(boolean isPressingRight)
    {
        this.isPressingRight = isPressingRight;
    }
    public void setPressingLeft(boolean isPressingLeft)
    {
        this.isPressingLeft = isPressingLeft;
    }
    public void startJump(SoundManager sm)
    {
        if (!isFalling) {//can't jump if falling
            if (!isJumping) {//not already jumping
                isJumping = true;
                jumpTime = System.currentTimeMillis();
                sm.playSound("jump");
            }
        }
    }
}
