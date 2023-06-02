package com.taewon.mygallag.sprites;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealItemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;
import java.util.Random;

public class AlienSprite extends Sprite{
    private  Context context;
    private SpaceInvadersView game;
    ArrayList<AlienShotSprite> alienShotSprites; // Alien의 총알이 담기는 List
    android.os.Handler fireHandler = null; // 적 공격을 위한 handler
    boolean isDestroyed = false;

    public AlienSprite(Context context, SpaceInvadersView game, int resourceId, int x, int y) {
        super(context, resourceId, x, y);
        this.context = context;
        this.game = game;
        alienShotSprites = new ArrayList<>();

        Random r = new Random();
        int randomDx = r.nextInt(5);
        int randomDy = r.nextInt(5);
        if (randomDy <= 0) { // 수직 이동 거리가 0 이하면
            randomDy = 1; // 수정 필요
        }
        dx = randomDx;
        dy = randomDy;

        fireHandler = new Handler(Looper.getMainLooper()); // 적 공격을 위한 handler --> mainThread의 looper를 가지고 옴
        fireHandler.postDelayed(
                // delay 주는 함수
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("run", "동작");
                        Random r = new Random();
                        boolean isFire = r.nextInt(100) + 1 <=30; // 30%의 확률로 공격

                        if (isFire && !isDestroyed) {
                            fire(); // 공격 메소드
                            fireHandler.postDelayed(this, 1000); // 1초 후에 다시 실행
                        }
                    }
                }, 1000); // 1초 후애
    }

    @Override
    public void move() {
        super.move();
        if (((dx < 0) && (x < 10)) || ((dx > 0) && (x > 800))) { // 좌, 우 부딪칠 때
            dx = -dx; // 반대로

            if (y > game.screenH) { // 게임 화면 아래로 넘어갔을 때
                game.removeSprite(this); // Alien을 게임의 sprites에서 제거
                destroyAlien(); // Alien이 제거될 때 수행할 일
                return;
            }
        }
    }

    @Override
    public void handleCollision(Sprite other) {
        if (other instanceof ShotSprite) { // 부딪힌 sprite가 ShotSprite(플레이어 총알)의 인스턴스일 때
            game.removeSprite(other); // 부딪힌 sprite(플레이어 총알) 제거
            game.removeSprite(this); // 자신(Alien) 제거
            destroyAlien(); // Alien이 제거될 때 수행할 일
            return;
        }

        if (other instanceof SpecialshotSprite) { // 부딪힌 sprite가 SpecialShotSprite(플레이어 특수 공격)일 때
            game.removeSprite(this); // 자신(Alien) 제거
            destroyAlien(); // Alien이 제거될 때 수행할 일
            return;
        }
    }

    private void destroyAlien() { // Alien이 제거될 때
        isDestroyed = true;
        game.setCurrEnemyCount(game.getCurrEnemyCount() - 1); // 현재 Alien의 수 1 감소

        for (int i = 0; i < alienShotSprites.size(); i++) {
            game.removeSprite(alienShotSprites.get(i)); // 적의 총알을 sprite 리스트에서 제거
        }

        spawnHealItem(); // 힐 아이템 생성
        spawnPowerItem(); // 파워업 아이템 생성
        spawnSpeedItem(); // 스피드업 아이템 생성
        game.setScore(game.getScore() + 1); // 점수 1 증가
        MainActivity.tvScore.setText(Integer.toString(game.getScore())); // MainActivity 에 점수를 표시
    }

    private void fire() {
        AlienShotSprite alienShotSprite = new AlienShotSprite(context, game, getX(), getY() + 30, 16);
        alienShotSprites.add(alienShotSprite); // Alien의 총알을 AlienShotSprite에 추가
        game.getSprites().add(alienShotSprite); // AlienShotSprite를 게임 sprites에 추가
    }

    public void spawnHealItem() {
        Random r = new Random();
        int healItemDrop = r.nextInt(100) + 1; // 1 ~ 100

        if (healItemDrop <= 1) { // 1%
            int dx = r.nextInt(10) + 1; // x 축 이동 거리 1 ~ 10
            int dy = r.nextInt(10) + 5; // y 축 이동 거리 5 ~ 15

            game.getSprites().add(new HealItemSprite(context, game, (int) this.getX(), (int) this.getY(), dx, dy)); // HealItemSprite를 게임 sprites에 추가 (시작 위치는 Alien의 위치)
        }
    }

    private void spawnPowerItem() {
        Random r = new Random();
        int powerItemDrop = r.nextInt(100) + 1;
        if(powerItemDrop <= 3) {
            int dx = r.nextInt(10) + 1;
            int dy = r.nextInt(10) + 5;
            game.getSprites().add(new PowerItemSprite(context, game, (int)this.getX(), (int)this.getY(), dx, dy));
        }
    }

    private void spawnSpeedItem() {
        Random r = new Random();
        int speedItemDrop = r.nextInt(100) + 1;
        if(speedItemDrop <= 5) {
            int dx = r.nextInt(10) + 1;
            int dy = r.nextInt(10) + 5;
            game.getSprites().add(new SpeedItemSprite(context, game, (int)this.getX(), (int)this.getY(), dx, dy));
        }
    }
}
