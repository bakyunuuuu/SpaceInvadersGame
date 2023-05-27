package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Sprite {
    protected float x, y;

    protected int width, height;
    protected float dx, dy;
    private Bitmap bitmap;

    protected int id;
    private RectF rect;

    public Sprite(Context context, int resourceId, float x, float y) {
        this.id = resourceId;
        this.x = x;
        this.y = y;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId); // 비트맵 만들기 (context를 사용해 resource에서 이미지를 디코딩하여 비트맵을 생성)
        width = bitmap.getWidth(); // 비트맵의 너비
        height = bitmap.getHeight(); // 비트맵의 높이
        rect = new RectF(); // sprite의 충돌 영역 (사각형)
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Canvas canvas, Paint paint) { // sprite 그리기
        canvas.drawBitmap(bitmap, x, y, paint);
    }

    public void move() {
        // 현재 축 위치 + 축 이동 거리
        x = x + dx;
        y = y + dy;

        // 충돌 영억 update
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public RectF getRect() {
        return rect;
    }

    public boolean checkCollision(Sprite other) {
        return RectF.intersects(this.getRect(), other.getRect()); // 서로의 충돌 영역이 부딪히는지 체크
    }

    public void handleCollision(Sprite other) {} // for 충돌 처리 => StarshipSprite

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
