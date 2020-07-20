package com.bartish.eitr.others;

import static com.bartish.eitr.others.Values.STAY;

public class Cell {
    public int x;
    public int y;
    public int oldDirect;
    public int newDirect = STAY;

    public Cell(){
        x = 0;
        y = 0;
        oldDirect = STAY;
    }

    public Cell(int x, int y, int direction){
        this.x = x;
        this.y = y;
        this.oldDirect = direction;
    }

    @Override
    public boolean equals(Object o) {
        if(((Cell)o).x == x && ((Cell)o).y == y)
            return true;
        return false;
    }
}
