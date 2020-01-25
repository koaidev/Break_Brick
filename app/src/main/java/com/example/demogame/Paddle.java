package com.example.demogame;

import android.graphics.RectF;

public class Paddle {
    private RectF rect;
    private float length, height, x, y, paddleSpeed;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public int paddleMoving = STOPPED;

    public Paddle(int screenX, int screenY) {
        length = 200;
        height = 20;
        rect = new RectF();
        paddleSpeed = 350;
    }

    public RectF getRect() {
        return rect;
    }

    public void setMovementState(int state) {
        paddleMoving = state;
    }

    public void update(long fps) {
        if (paddleMoving == LEFT) {
            x = x - paddleSpeed / fps;
        }
        if (paddleMoving == RIGHT) {
            x = x + paddleSpeed / fps;
        }
        rect.left = x;
        rect.right = x + length;
    }
    public void clearObstacleY(float y) {
        rect.bottom = y;
        rect.top = y - height;
    }

    public void clearObstacleX(float x) {
        rect.left = x;
        rect.right = x + length;
    }


    public void reset(int x, int y) {
        rect.left = x / 2 - length / 2;
        rect.top = y - 40;
        rect.right = x / 2 + length / 2;
        rect.bottom = y - height;
    }
}
