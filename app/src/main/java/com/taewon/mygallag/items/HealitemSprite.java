package com.taewon.mygallag.items;


import android.content.Context;

import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.sprites.Sprite;

import java.util.Timer;
import java.util.TimerTask;

public class HealitemSprite extends Sprite {
    SpaceInvadersView game;

    public HealitemSprite(Context context, SpaceInvadersView game, float x, float y, int dx, int dy) {
        super(context, R.drawable.heal_item, x, y); // 부모 class 의 생성자를 호출해 sprite를 저장된 이미지 리소스와 위치로 초기화
        this.game = game;
        this.dx = dx;
        this.dy = dy;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                autoRemove(); // 아이템 제거 메소드
            }
        }, 10000); // 10초 후
    }

    private void autoRemove() {
        game.removeSprite(this); // 게임에서 힐 아이템 제거
    }

    @Override
    public void move() {
        if ((dx < 0) && (x < 120)) { // 왼쪽으로 이동 중 & 왼쪽 끝에 도착 시
            dx *= -1; // 방향 전환
            return;
        }

        if ((dx > 0) && (x > game.screenW - 120)) { // 오른쪽으로 이동 중
            dx *= -1;
            return;
        }

        if ((dy < 0) && (y < 120)) { // 위
            dy *= -1;
            return;
        }

        if ((dy > 0) && (y > game.screenH - 120)) { // 아래
            dy *= -1;
            return;
        }

        super.move();
    }
}
