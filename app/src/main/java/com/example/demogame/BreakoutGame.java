package com.example.demogame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BreakoutGame extends BaseActivity {
    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    //inner class breakoutView
    private class BreakoutView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder gameHolder;
        volatile boolean isPlaying;
        boolean isPaused = true;
        Canvas canvas;
        Paint paint;
        long fps;
        private long timeThisFrame;
        Paddle paddle;
        Ball ball;
        Brick[] bricks = new Brick[200];
        int numBricks = 0;
        //start get resolution screen
        int screenX, screenY;
        SoundPool soundPool;
        int soundID1 = -1;
        int soundID2 = -1;
        int soundID3 = -1;
        int explodeID = -1;
        int score = 0;


        public BreakoutView(Context context) {
            super(context);
            gameHolder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;
            paddle = new Paddle(screenX, screenY);
            ball = new Ball(screenX, screenY);
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            try {
                AssetManager manager = context.getAssets();
                AssetFileDescriptor descriptor;
                descriptor = manager.openFd("fail.wav");
                soundID1 = soundPool.load(descriptor, 0);
                descriptor = manager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);

            } catch (Exception e) {
                Log.e("error", "failed to load sound files");
            }
            createBricksAndRestart();
        }

        public void createBricksAndRestart() {

            // Put the ball back to the start
            ball.reset(screenX, screenY);
            paddle.reset(screenX, screenY);
            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;
            numBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 4; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }
            score = 0;

        }

        @Override
        public void run() {
            while (isPlaying) {
                long startTimeFrame = System.currentTimeMillis();
                if (!isPaused) {
                    update();
                }
                draw();
                timeThisFrame = System.currentTimeMillis() - startTimeFrame;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }

        }

        private void draw() {
            if (gameHolder.getSurface().isValid()) {
                canvas = gameHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);
                paint.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawText("FPS: " + fps, 10, 50, paint);
                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score, 200, 50, paint);
                canvas.drawRect(paddle.getRect(), paint);
                canvas.drawRect(ball.getRect(), paint);
                paint.setColor(Color.argb(100, 255, 44, 12));
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].isVisible()) {
                        canvas.drawRect(bricks[i].getRectF(), paint);
                    }
                }

                // Has the player cleared the screen?
                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);
                }
                gameHolder.unlockCanvasAndPost(canvas);
            }
        }

        private void update() {
            paddle.update(fps);
            ball.update(fps);
            // Check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].isVisible()) {
                    if (RectF.intersects(bricks[i].getRectF(), ball.getRect())) {
                        bricks[i].setVisible(false);
                        ball.reverseYVelocity();
                        score = score + 10;
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }
            // Check for ball colliding with paddle
            if (RectF.intersects(ball.getRect(), paddle.getRect())) {
                ball.reverseYVelocity();
                ball.setRandomXVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
                soundPool.play(soundID1, 1, 1, 0, 0, 1);
            }
            // Bounce the ball back when it hits the bottom of screen
            // And deduct a life
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);
                paddle.clearObstacleX(screenX / 2);
                isPaused = true;
                createBricksAndRestart();
            }
            // Bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);
            }
            // If the ball hits left wall bounce
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
            }
            // If the ball hits right wall bounce
            if (ball.getRect().right > screenX - 10) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);
            }
            // Pause if cleared screen
        }

        public void pause() {
            isPlaying = false;
            try {
                gameThread.join();
            } catch (Exception e) {
                Log.d("Error: ", "" + e);
            }
        }

        public void resume() {
            isPlaying = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isPaused = false;
                    if (event.getX() > screenX / 2) {
                        paddle.setMovementState(paddle.LEFT);
                    } else {
                        paddle.setMovementState(paddle.RIGHT);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        breakoutView.pause();
    }
}