package com.bartish.eitr.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.bartish.eitr.others.Block;
import com.bartish.eitr.others.Cell;

import java.util.LinkedList;
import java.util.Random;

import static com.bartish.eitr.others.Values.*;

public class Room extends Group {
    private static ShapeRenderer renderer;

    private Block[][] matrix;
    private boolean finish;
    private int enterX;
    private int enterY;
    private int enterDirection;
    private int exitX;
    private int exitY;
    private int exitDirection;

    public Room(int n, int m, int enter, int exit){
        matrix = new Block[n+2][m+2];
        finish = false;

        Cell cell = clockToMatrixSystem(enter , n , m);
        enterX = cell.x;
        enterY = cell.y;
        enterDirection = cell.newDirect;

        cell = clockToMatrixSystem(exit, n, m);
        exitX = cell.x;
        exitY = cell.y;
        exitDirection = cell.newDirect;

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);

        generateLabirint(matrix, enterX, enterY, exitX, exitY);

        setSize(n * BLOCK_SIZE, m * BLOCK_SIZE);
    }

    float timer = 0;
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(0.2f, 0.2f, 0.2f, parentAlpha * getColor().a);

        for(int i = 1; i < matrix.length-1; i++){
            for(int j = 1; j < matrix[i].length-1; j++){
                if(matrix[i][j] == Block.WALL){

                    renderer.rect(getParent().getX() + getX() + (i-1) * BLOCK_SIZE,
                            getParent().getY() + getY() + (matrix[i].length - (j+1) - 1) * BLOCK_SIZE,
                            BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        if(exitDirection == UP) timer -= 0.1;

        renderer.rectLine(
                getParent().getX() + getX() - LINE_WIDTH/2 - timer/2,
                getParent().getY() + getY() + getHeight() + LINE_WIDTH/2,
                getParent().getX() + getX() + getWidth() + LINE_WIDTH/2 + timer/2,
                getParent().getY() + getY() + getHeight() + LINE_WIDTH/2,
                LINE_WIDTH);
        renderer.rectLine(
                getParent().getX() + getX() + getWidth() + LINE_WIDTH/2 + timer/2,
                getParent().getY() + getY() + getHeight() + LINE_WIDTH/2,
                getParent().getX() + getX() + getWidth() + LINE_WIDTH/2 + timer/2,
                getParent().getY() + getY() - LINE_WIDTH/2 - timer,
                LINE_WIDTH);
        renderer.rectLine(
                getParent().getX() + getX() + getWidth() + LINE_WIDTH/2 + timer/2,
                getParent().getY() + getY() - LINE_WIDTH/2 - timer,
                getParent().getX() + getX() - LINE_WIDTH/2 - timer/2,
                getParent().getY() + getY() - LINE_WIDTH/2 - timer,
                LINE_WIDTH);
        renderer.rectLine(
                getParent().getX() + getX() - LINE_WIDTH/2 - timer/2,
                getParent().getY() + getY() - LINE_WIDTH/2 - timer,
                getParent().getX() + getX() - LINE_WIDTH/2 - timer/2,
                getParent().getY() + getY() + getHeight() + LINE_WIDTH/2,
                LINE_WIDTH);

        renderer.setColor(225/255f, 230/255f, 215/255f, 1);

        renderer.rect(getParent().getX() + getX() + (exitX-1) * BLOCK_SIZE,
                getParent().getY() + getY() + (matrix[exitX].length - (exitY+1) - 1) * BLOCK_SIZE,
                BLOCK_SIZE, BLOCK_SIZE);

        renderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        super.draw(batch, parentAlpha);
    }

    public static void generateLabirint(Block matrix[][], int enterX, int enterY, int exitX, int exitY){
        generateWalls(matrix);

        matrix[exitX][exitY] = Block.EXIT;
        print(matrix);

        Cell cell;
        Cell preExit;

        if(enterX == 0)
            cell = new Cell(1, enterY, RIGHT);
        else if(enterX == matrix.length-1)
            cell = new Cell(matrix.length-2, enterY, LEFT);
        else if(enterY == 0)
            cell = new Cell(enterX, 1, UP);
        else
            cell = new Cell(enterX, matrix[0].length-2, DOWN);

        if(exitX == 0)
            preExit = new Cell(1, exitY, STAY);
        else if(exitX == matrix.length-1)
            preExit = new Cell(matrix.length-2, exitY, STAY);
        else if(exitY == 0)
            preExit = new Cell(exitX, 1, STAY);
        else
            preExit = new Cell(exitX, matrix[0].length-2, STAY);
        matrix[preExit.x][preExit.y] = Block.PRE_EXIT;

        LinkedList<Cell> list = new LinkedList<>();
        list.addLast(cell);
        matrix[cell.x][cell.y] = Block.WAY;

        while (list.size() != 0){
            if(matrix[exitX][exitY] == Block.EXIT)
                cell.newDirect = next(matrix, cell, 0);

            if(cell.newDirect == STAY || matrix[exitX][exitY] != Block.EXIT){
                list.removeLast();
                cell = list.peekLast();
            }else{
                cell = buildWay(matrix, cell.x, cell.y, cell.oldDirect, cell.newDirect);
                if(!list.contains(cell))
                    list.addLast(cell);
            }
        }
        if(matrix[preExit.x][preExit.y] == Block.PRE_EXIT)
            generateLabirint(matrix, enterX, enterY, exitX, exitY);
        else
            matrix[exitX][exitY] = Block.EXIT;
    }
    private static void generateWalls(Block matrix[][]){
        for(int i = 1; i < matrix.length-1; i++){
            for(int j = 1; j < matrix[i].length-1; j++){
                matrix[i][j] = Block.NONE;
            }
        }
        for(int i = 0; i < matrix.length; i++){
            matrix[i][0] = Block.WALL;
            matrix[i][matrix[i].length-1] = Block.WALL;
        }
        for(int j = 0; j < matrix[0].length; j++){
            matrix[0][j] = Block.WALL;
            matrix[matrix.length-1][j] = Block.WALL;
        }
    }

    private static int next(Block matrix[][], Cell cell, int prob){
        LinkedList<Integer> list = new LinkedList<>();

        if(matrix[cell.x][cell.y+1] == Block.PRE_EXIT || matrix[cell.x][cell.y+1] == Block.EXIT)
            return UP;
        else if(matrix[cell.x+1][cell.y] == Block.PRE_EXIT || matrix[cell.x+1][cell.y] == Block.EXIT)
            return RIGHT;
        else if(matrix[cell.x][cell.y-1] == Block.PRE_EXIT || matrix[cell.x][cell.y-1] == Block.EXIT)
            return DOWN;
        else if(matrix[cell.x-1][cell.y] == Block.PRE_EXIT || matrix[cell.x-1][cell.y] == Block.EXIT)
            return LEFT;

        if(cell.oldDirect == UP && matrix[cell.x][cell.y+1] != Block.WAY && matrix[cell.x][cell.y+1] != Block.EXIT){
            if(matrix[cell.x][cell.y+1] == Block.NONE)
                list.add(UP);
            if(matrix[cell.x+1][cell.y] == Block.NONE)
                list.add(RIGHT);
            if(matrix[cell.x-1][cell.y] == Block.NONE)
                list.add(LEFT);

        }else if(cell.oldDirect == RIGHT && matrix[cell.x+1][cell.y] != Block.WAY && matrix[cell.x+1][cell.y] != Block.EXIT){
            if(matrix[cell.x+1][cell.y] == Block.NONE)
                list.add(RIGHT);
            if(matrix[cell.x][cell.y+1] == Block.NONE)
                list.add(UP);
            if(matrix[cell.x][cell.y-1] == Block.NONE)
                list.add(DOWN);

        }else if(cell.oldDirect == DOWN && matrix[cell.x][cell.y-1] != Block.WAY && matrix[cell.x][cell.y-1] != Block.EXIT){
            if(matrix[cell.x][cell.y-1] == Block.NONE)
                list.add(DOWN);
            if(matrix[cell.x+1][cell.y] == Block.NONE)
                list.add(RIGHT);
            if(matrix[cell.x-1][cell.y] == Block.NONE)
                list.add(LEFT);

        }else if(cell.oldDirect == LEFT && matrix[cell.x-1][cell.y] != Block.WAY && matrix[cell.x-1][cell.y] != Block.EXIT){
            if(matrix[cell.x-1][cell.y] == Block.NONE)
                list.add(LEFT);
            if(matrix[cell.x][cell.y+1] == Block.NONE)
                list.add(UP);
            if(matrix[cell.x][cell.y-1] == Block.NONE)
                list.add(DOWN);
        }


        if(list.size() > 1){
            if(list.contains(cell.oldDirect)) {
                for (int i = 0; i < prob; i++) {
                    list.add(cell.oldDirect);
                }
            }
        }

        int k;
        if(list.size() == 0) k = STAY;
        else k = list.get(new Random().nextInt(list.size()));
        return k;

    }
    private static Cell buildWay(Block matrix[][], int x, int y, int oldDirect, int newDirect){
        Cell next = new Cell(x, y, newDirect);
        if(newDirect == UP){
            matrix[x][y+1] = Block.WAY;
            next.y += 1;
        }else if(newDirect == RIGHT){
            matrix[x+1][y] = Block.WAY;
            next.x += 1;
        }else if(newDirect == DOWN){
            matrix[x][y-1] = Block.WAY;
            next.y -= 1;
        }else{
            matrix[x-1][y] = Block.WAY;
            next.x -= 1;
        }

        if(oldDirect != newDirect){
            if(oldDirect == UP){
                matrix[x][y+1] = Block.WALL;
            }else if(oldDirect == RIGHT){
                matrix[x+1][y] = Block.WALL;
            }else if(oldDirect == DOWN){
                matrix[x][y-1] = Block.WALL;
            }else if(oldDirect == LEFT){
                matrix[x-1][y] = Block.WALL;
            }
        }
        return next;
    }

    public void addPlayer(Player player){
        addActor(player);
        player.setPosition((enterX-1) * BLOCK_SIZE, getHeight() - enterY * BLOCK_SIZE);

        player.setRoom(this, enterX , enterY );
    }

    public static Cell clockToMatrixSystem(int clock, int n, int m){
        Cell cell = new Cell();

        if(clock <= n){
            cell.x = clock;
            cell.y = 0;
            cell.newDirect = UP;
        }else if(clock <= n+m){
            cell.x = n+1;
            cell.y = clock - n;
            cell.newDirect = RIGHT;
        }else if(clock <= 2*n+m){
            cell.x = n - (clock - n - m) + 1;
            cell.y = m+1;
            cell.newDirect = DOWN;
        }else if(clock <= 2*(n+m)){
            cell.x = 0;
            cell.y = m - (clock - 2*n - m) + 1;
            cell.newDirect = LEFT;
        }else{
            cell = clockToMatrixSystem(clock - 2*(n+m), n, m);
        }

        return cell;
    }

    public Block[][] getMatrix() {
        return matrix;
    }

    public int getEnterX() {
        return enterX;
    }

    public int getEnterY() {
        return enterY;
    }

    public int getExitX() {
        return exitX;
    }

    public int getExitY() {
        return exitY;
    }

    public int getEnterDirection() {
        return enterDirection;
    }

    public int getExitDirection() {
        return exitDirection;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    private static void print(Block matrix[][]){
        for(int j = 0; j < matrix[0].length; j++){
            for(int i = 0; i < matrix.length; i++){
                if(matrix[i][j] == Block.WALL){
                    System.out.print(" # ");
                }else if(matrix[i][j] == Block.NONE){
                    System.out.print("   ");
                }else if (matrix[i][j] == Block.WAY){
                    System.out.print(" . ");
                }else if (matrix[i][j] == Block.EXIT){
                    System.out.print(" X ");
                }else if(matrix[i][j] == Block.ENTER){
                    System.out.print(" _ ");
                }else{
                    System.out.print(" * ");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }



}
