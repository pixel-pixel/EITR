package com.bartish.eitr.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.bartish.eitr.others.Block;

import static com.badlogic.gdx.math.Interpolation.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.bartish.eitr.others.Values.*;

public class Player extends Image {
    private static Sprite sprite = new Sprite(new Texture(Gdx.files.internal("player/duck.png")));

    private Room room;
    private int matrixX;
    private int matrixY;

    public int oldDirect = STAY;

    public Player() {
        super(sprite);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void up(){
        oldDirect = UP;
        int wayY = 0;

        for(int j = matrixY-1; j >= 0; j--){
            if(room.getMatrix()[matrixX][j] != Block.WALL){
                if(room.getMatrix()[matrixX][j] == Block.EXIT) room.setFinish(true);
                wayY++;
            } else break;
        }

        move(0, wayY);
    }
    public void right(){
        oldDirect = RIGHT;
        int wayX = 0;

        for(int i = matrixX+1; i < room.getMatrix().length; i++){
            if(room.getMatrix()[i][matrixY] != Block.WALL){
                if(room.getMatrix()[i][matrixY] == Block.EXIT) room.setFinish(true);
                wayX++;
            } else break;
        }

        if(!sprite.isFlipX())
            sprite.flip(true, false);
        setDrawable(new SpriteDrawable(sprite));

        move(wayX, 0);
    }
    public void down(){
        oldDirect = DOWN;
        int wayY = 0;

        for(int j = matrixY+1; j < room.getMatrix()[0].length; j++){
            if(room.getMatrix()[matrixX][j] != Block.WALL){
                if(room.getMatrix()[matrixX][j] == Block.EXIT) room.setFinish(true);
                wayY--;
            } else break;
        }

        move(0, wayY);
    }
    public void left(){
        oldDirect = LEFT;
        int wayX = 0;

        for(int i = matrixX-1; i >= 0; i--){
            if(room.getMatrix()[i][matrixY] != Block.WALL){
                if(room.getMatrix()[i][matrixY] == Block.EXIT) room.setFinish(true);
                wayX--;
            } else break;
        }

        if(sprite.isFlipX())
            sprite.flip(true, false);
        setDrawable(new SpriteDrawable(sprite));

        move(wayX, 0);
    }
    public void move(int way){
        if(way == UP)
            up();
        else if(way == RIGHT)
            right();
        else if(way == DOWN)
            down();
        else if(way == LEFT)
            left();
    }

    public void move(final int wayX, final int wayY){
        if(wayX != 0 || wayY != 0){
            matrixX += wayX;
            matrixY -= wayY;
            addAction(moveTo(
                    getX() + wayX * BLOCK_SIZE,
                    getY() + wayY * BLOCK_SIZE,
                    RIDE_TIME));

            room.addAction(delay(RIDE_TIME, parallel(
                    run(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.input.vibrate((int) (Math.pow(Math.abs(wayX + wayY), 0.2) * VIBRO_TIME));
                        }
                    }), sequence(
                    Actions.moveBy(
                            wayX * TREMBLE_AMPLITUDE,
                            wayY *TREMBLE_AMPLITUDE,
                            TREMBLE_TIME, exp5Out),
                    Actions.moveBy(
                            -wayX * TREMBLE_AMPLITUDE,
                            -wayY * TREMBLE_AMPLITUDE,
                            TREMBLE_TIME, exp5In)
            ))));
        }
    }


    public void setRoom(Room room, int x, int y){
        this.room = room;
        matrixX = x;
        matrixY = y;
    }
}
