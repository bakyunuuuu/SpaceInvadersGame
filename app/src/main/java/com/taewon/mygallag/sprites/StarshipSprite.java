package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealItemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;

public class StarshipSprite extends Sprite{
    Context context;
    SpaceInvadersView game;
    public float speed;
    private int bullets, life = 3, powerLevel;
    private int specialShotCount;
    private boolean isSpecialShooting;
    private static ArrayList<Integer> bulletSprites = new ArrayList<Integer>();
    private final static float MAX_SPEED = 3.5f;
    private final static int MAX_HEART = 3;
    // ?
    private RectF rectF;
    private boolean isReloading = false;

    public StarshipSprite(Context context, SpaceInvadersView game, int resID, int x, int y, float speed) {
        super(context, resID, x, y);
        this.context = context;
        this.game = game;
        this.speed = speed;
        init();
    }

    public void init() {
        dx = dy = 0;
        bullets = 30;
        life = 3;
        specialShotCount = 3;
        powerLevel = 0;
        Integer[] shots = {R.drawable.shot_001, R.drawable.shot_002, R.drawable.shot_003, R.drawable.shot_004,
                R.drawable.shot_005, R.drawable.shot_006, R.drawable.shot_007};

        for (int i = 0; i < shots.length; i++) {
            bulletSprites.add(shots[i]);
        }
    }

    @Override
    public void move() {
        if ((dx < 0) && (x < 120)) return;
        if ((dx > 0) && (x > game.screenW - 120)) return;
        if ((dy < 0) && (y < 120)) return;
        if ((dy > 0) && (y > game.screenH - 120)) return;
        super.move(); // super class 가서 x, y 위치 다시 지정
    }

    // 이동
    public void moveRight(double force) {
        setDx((float) (1 * force * speed));
    }

    public void moveLeft(double force) {
        setDx((float) (-1 * force * speed));
    }

    public void moveDown(double force) {
        setDy((float) (1 * force * speed));
    }

    public void moveUp(double force) {
        setDy((float) (-1 * force * speed));
    }

    //reset dx, dy
    public void resetDx() {
        setDx(0);
    }

    public void resetDy() {
        setDy(0);
    }

    // 총알 수
    public int getBulletsCount() {
        return bullets;
    }

    // 생명 수
    public  int getLife() {
        return life;
    }

    // 파워 레벨
    public int getPowerLevel() {
        return powerLevel;
    }

    public void fire() {
        if (isReloading | isSpecialShooting) {
            return;
        }

        MainActivity.effectSound(MainActivity.PLAYER_SHOT);

        // make bullet
        ShotSprite shot = new ShotSprite(context, game, bulletSprites.get(powerLevel), getX() + 10, getY() - 30, -16);
        game.getSprites().add(shot);
        bullets--;

        MainActivity.bulletCount.setText(bullets + "/30");
        Log.d("bullets", bullets + "/30");

        if (bullets == 0) { // 총알이 없으면
            reloadBullets(); // 재장전
            return;
        }
    }

    public void reloadBullets() {
        isReloading = true;
        MainActivity.effectSound(MainActivity.PLAYER_RELOAD);
        MainActivity.btnFire.setEnabled(false); // 공격 안 되게 (비활성화)
        MainActivity.btnReload.setEnabled(false); // 제장전 안 되게

        // Thread sleep 사용하지 않고 delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bullets = 30; // 총알 수 = 30
                MainActivity.btnFire.setEnabled(true); // 공격 버튼 활성화
                MainActivity.btnReload.setEnabled(true); // 재장전 버튼 활성화
                MainActivity.bulletCount.setText(bullets + "/30");
                MainActivity.bulletCount.invalidate(); // 화면 새로 고침 (뷰를 다시 그림)
                isReloading = false;
            }
        }, 2000); // 2초 뒤
    }

    public void specialShot() {
        specialShotCount--;

        //specialShotSprite 구현
        SpecialshotSprite shot = new SpecialshotSprite(context, game, R.drawable.laser, getRect().right = getRect().left, 0);

        // game -> spaceInvadersView의 getSprites(): sprites에 shot 추가
        game.getSprites().add(shot);
    }

    public int getSpecialShotCount() {
        return specialShotCount;
    }

    public boolean isSpecialShooting() {
        return isSpecialShooting;
    }

    public void setSpecialShooting(boolean specialShooting) {
        isSpecialShooting = specialShooting;
    }

    public void hurt() { // 적과 부딪치면 or 적의 총알에 맞으면
        life--;
        if (life <= 0) {
            ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource((R.drawable.ic_baseline_favorite_border_24));

            // spaceInvadersView 의 endGame() 에서 game 종료
            game.endGame();
            return;
        }

        Log.d("hurt", Integer.toString(life));
        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }

    private void speedUp() { // 스피드업 아이템을 먹으면
        if (MAX_SPEED >= speed + 0.2f) {
            plusSpeed(0.2f);
        } else {
            game.setScore(game.getScore() + 1);
            MainActivity.tvScore.setText(Integer.toString(game.getScore()));
        }
    }

    public void plusSpeed(float speed) { // 스피드 업 아이템을 먹으면 2
        this.speed += speed;
    }

    public void powerUp() { // 파워업 아이템을 먹으면
        if (powerLevel >= bulletSprites.size() - 1) {
            game.setScore(game.getScore() + 1);
            MainActivity.tvScore.setText(Integer.toString(game.getScore()));
            return;
        }

        powerLevel++;
        MainActivity.btnFire.setImageResource(bulletSprites.get(powerLevel));
        MainActivity.btnFire.setBackgroundResource(R.drawable.round_button_shape);
    }

    public void heal() { // 힐 아이템을 먹으면
        Log.d("heal", Integer.toString(life));
        if (life + 1 > MAX_HEART) {
            game.setScore(game.getScore() + 1);
            MainActivity.tvScore.setText(Integer.toString(game.getScore()));
            return;
        }

        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(R.drawable.ic_baseline_favorite_border_24);
        life++;
    }

    // Sprite 의 handleCollision() -> 충돌 처리
    @Override
    public void handleCollision(Sprite other) {
        if (other instanceof AlienSprite) {
            // 적과 부딪치면
            game.removeSprite(other);
            MainActivity.effectSound(MainActivity.PLAYER_HURT);
            hurt();
        }

        if (other instanceof SpeedItemSprite) {
            // 스피드 아이템을 먹으면
            game.removeSprite(other);
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            speedUp();
        }

        if (other instanceof AlienShotSprite) {
            // 적의 총알을 맞으면
            MainActivity.effectSound(MainActivity.PLAYER_HURT);
            game.removeSprite(other);
            hurt();
        }

        if (other instanceof PowerItemSprite) {
            // 파워업 아이템을 먹으면
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            powerUp();
            game.removeSprite(other);
        }

        if (other instanceof HealItemSprite) {
            // 힐 아이템을 먹으면
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            game.removeSprite(other);
            heal();
        }
    }
}
