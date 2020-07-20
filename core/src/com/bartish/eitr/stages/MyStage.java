package com.bartish.eitr.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class MyStage extends Stage {
    public MyStage(Viewport viewport) {
        super(viewport);
    }

    public abstract void resize(float worldWidth, float worldHeight);
}
