package com.bartish.eitr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bartish.eitr.actors.Room;
import com.bartish.eitr.stages.GameStage;
import com.bartish.eitr.stages.MyStage;

public class Main extends ApplicationAdapter {
	public static final int WIDTH = 270;
	public static final int HEIGHT = 270;
	public static float WORLD_WIDTH = 0;
	public static float WORLD_HEIGHT = 0;

	private static ExtendViewport viewport;
	private static MyStage stage;

	@Override
	public void create () {
		viewport = new ExtendViewport(WIDTH, HEIGHT);
		stage = new GameStage(viewport);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(225/255f, 230/255f, 215/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		WORLD_WIDTH = viewport.getWorldWidth();
		WORLD_HEIGHT = viewport.getWorldHeight();
	}
}
