package com.bartish.eitr.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bartish.eitr.Main;
import com.bartish.eitr.actors.Player;
import com.bartish.eitr.actors.Room;
import com.bartish.eitr.others.Values;

import java.util.Random;

import static com.badlogic.gdx.math.Interpolation.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.bartish.eitr.others.Values.*;
import static java.lang.Math.abs;

public class GameStage extends MyStage{
    private Group bag;
    private Room newRoom;
    private Room oldRoom;
    private Player player;

    private static Random random = new Random();

    public GameStage(Viewport viewport) {
        super(viewport);


        //INIT
        bag = new Group();
        newRoom = new Room(7, 7, 4,  11);
        player = new Player();

        //ADD TO SCENE
        addActor(bag);
        bag.addActor(newRoom);
        newRoom.addPlayer(player);

        bag.setSize(newRoom.getWidth(), newRoom.getHeight());
        bag.setOrigin(bag.getWidth()/2, bag.getHeight()/2);
        bag.setPosition(
                (Main.WIDTH - bag.getWidth()) / 2,
                -bag.getHeight());
        bag.addAction(moveTo(
                bag.getX(),
                (Main.HEIGHT - bag.getHeight()) / 2,
                START_TIME, pow3Out));

        player.setOrigin(player.getWidth() / 2, player.getHeight() / 2);
        player.setScale(3);
        player.addAction(alpha(0));
        player.addAction(delay(START_TIME, parallel(
                scaleTo(1, 1, BIRTH_TIME, exp5Out),
                alpha(1, BIRTH_TIME, exp5Out)
        )));
        addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (player.getActions().size == 0) {
                    switch (keycode) {
                        case Input.Keys.UP:
                            player.up();
                            break;
                        case Input.Keys.RIGHT:
                            player.right();
                            break;
                        case Input.Keys.DOWN:
                            player.down();
                            break;
                        case Input.Keys.LEFT:
                            player.left();
                            break;
                    }


                }
                return true;
            }
        });
        addListener(new ActorGestureListener(){

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                super.pan(event, x, y, deltaX, deltaY);
                if (player.getActions().size == 0) {
                    if (deltaX > 1 && abs(deltaX) > abs(deltaY * 2)) {
                        player.right();
                    } else if (deltaX < -1 && abs(deltaX) > abs(deltaY * 2)) {
                        player.left();
                    } else if (deltaY > 1 && abs(deltaY) > abs(deltaX * 2)) {
                        player.up();
                    } else if (deltaY < -1 && abs(deltaY) > abs(deltaX * 2)) {
                        player.down();
                    }
                }
            }
        });
    }

    @Override
    public void act() {
        super.act();

        if(bag.getActions().size == 0 && bag.getChildren().size == 1)
            bag.setPosition(bagXByPlayer(), bagYByPlayer());

        if(newRoom.isFinish()){
            newRoom.setFinish(false);
            Gdx.input.vibrate(new long[]{0, VIBRO_TIME*2, VIBRO_TIME, VIBRO_TIME*2, VIBRO_TIME, VIBRO_TIME*2}, -1);
            nextRoom();
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

    private void nextRoom(){
        Room temp = newRoom;
        newRoom = oldRoom;
        oldRoom = temp;

        newRoom = roomByRoom(oldRoom);
        bag.setSize(newRoom.getWidth(), newRoom.getHeight());
        bag.addActor(newRoom);

        nextRoomAnimation();

        addAction(delay(RIDE_TIME, run(new Runnable() {
            @Override
            public void run() {
                newRoom.addPlayer(player);
                player.move(player.oldDirect);
                newRoom.setPosition(0, 0);
                bag.removeActor(oldRoom);
            }
        })));
    }

    private void nextRoomAnimation(){
        newRoom.addAction(alpha(0));
        newRoom.addAction(alpha(1, RIDE_TIME));

        float shift;
        if(newRoom.getEnterDirection() == UP) {
            shift = roomByRoomShift(oldRoom, newRoom, UP);
            newRoom.setPosition(shift, -newRoom.getHeight() -BLOCK_SIZE);
        }else if(newRoom.getEnterDirection() == RIGHT) {
            shift = roomByRoomShift(oldRoom, newRoom, RIGHT);
            newRoom.setPosition(-newRoom.getWidth() -BLOCK_SIZE, shift);
        }else if(newRoom.getEnterDirection() == DOWN) {
            shift = roomByRoomShift(oldRoom, newRoom, DOWN);
            newRoom.setPosition(shift, oldRoom.getHeight() +BLOCK_SIZE);
        }else if(newRoom.getEnterDirection() == LEFT) {
            shift = roomByRoomShift(oldRoom, newRoom, LEFT);
            newRoom.setPosition(oldRoom.getWidth() +BLOCK_SIZE, shift);
        }

        bag.addAction(moveTo(
                bagXByRoom(newRoom, newRoom.getEnterX()),
                bagYByRoom(newRoom, newRoom.getEnterY()),
                RIDE_TIME));

        oldRoom.addAction(alpha(0, RIDE_TIME));
    }

    private static Room roomByRoom(Room oldRoom){
        int n = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE) + MIN_ROOM_SIZE;
        int m = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE) + MIN_ROOM_SIZE;
        int enter = 0;
        int exit = 0;

        if(oldRoom.getExitDirection() == UP){
            enter = random.nextInt(n) + m + n + 1;
        }else if(oldRoom.getExitDirection() == RIGHT){
            enter = random.nextInt(m) + 2*n + m + 1;
        }else if(oldRoom.getExitDirection() == DOWN){
            enter = random.nextInt(n) + 1;
        }else if(oldRoom.getExitDirection() == LEFT){
            enter = random.nextInt(m) + n + 1;
        }

        exit = random.nextInt(2*(n+m) - 3) + enter + 2;

        Room newRoom = new Room(n, m, enter, exit);
        return newRoom;
    }

    private static float roomByRoomShift(Room oldRoom, Room newRoom, int oldExitDirect){
        int shift;

        if(oldExitDirect == UP || oldExitDirect == DOWN)
            shift = oldRoom.getExitX() - newRoom.getEnterX();
        else
            shift = (oldRoom.getMatrix()[0].length - oldRoom.getExitY())
                    - (newRoom.getMatrix()[0].length - newRoom.getEnterY());


        return shift * BLOCK_SIZE;
    }

    private float bagXByPlayer(){
        return (Main.WORLD_WIDTH - bag.getWidth()) / 2
                - (player.getX() + player.getWidth()/2
                - (bag.getWidth() - player.getWidth()) / 2) * ROOM_SHIFT;
    }

    private float bagYByPlayer(){
        return (Main.WORLD_HEIGHT - bag.getHeight()) / 2
                - (player.getY() + player.getHeight()/2
                - (bag.getHeight() - player.getHeight()) / 2) * ROOM_SHIFT;
    }

    private float bagXByRoom(Room room, int x){
        x -= 2;
        return (Main.WORLD_WIDTH - room.getWidth()) / 2
                - (x * BLOCK_SIZE + BLOCK_SIZE/2
                - (room.getWidth() - BLOCK_SIZE) / 2) * ROOM_SHIFT
                - room.getX();
    }

    private float bagYByRoom(Room room, int y){
        y = room.getMatrix()[0].length - y;
        y -= 2;
        return (Main.WORLD_HEIGHT - room.getHeight()) / 2
                - (y * BLOCK_SIZE + BLOCK_SIZE/2
                - (room.getHeight() - BLOCK_SIZE) / 2) * ROOM_SHIFT
                - room.getY();
    }

    @Override
    public void resize(float worldWidth, float worldHeight) {

    }
}
