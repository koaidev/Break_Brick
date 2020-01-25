package com.example.demogame;

import android.graphics.RectF;

public class Brick {
    private RectF rectF;
    private boolean isVisible;

    public Brick(int row, int column, int width, int height) {
        isVisible = true;
        int padding = 1;
        rectF = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    public RectF getRectF() {
        return rectF;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
