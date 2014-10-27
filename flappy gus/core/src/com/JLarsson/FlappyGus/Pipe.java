package com.JLarsson.FlappyGus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Pipe {
	
	static int tilesPerHeight = 10, tilesPerHole = 3, tilePadding = 2;
	
	public int x, height, width;
	int screenW, screenH;
	int pipeHoleHeight, padding, tileHeight;

	public Pipe(int screenW, int screenH) {
		this.screenH = screenH;
		this.screenW = screenW;
		tileHeight = Game.randInt(tilePadding, tilesPerHeight - tilesPerHole - tilePadding);
		width = (int) screenH / tilesPerHeight;
		height = width * tileHeight;
		x = screenW;
		pipeHoleHeight = width * tilesPerHole;
	}

	public void Update(float speed) {
		x -= (int) (screenW * Game.pipeSpeed * speed);
		if(speed > 0 && speed < 1) speed -= .1f;
	}
	
	public boolean isOffScreen() {
		if(x + width < 0) return true;
		else return false;
	}
	
	public boolean CollidingWith(int x, int y, int width, int height) {
		if((x + width >= this.x && x <= this.x + this.width) && (y <= this.height || y + height >= this.height + pipeHoleHeight)) return true;
		else return false;
	}

	public void Render(SpriteBatch batch, Texture gold, Texture obs) {
		batch.draw(obs, x, 0, width, width); //top pipe
		for(int i = 1; i < tileHeight; i++) {
			batch.draw(gold, x, width * i, width, width);
		}
		
		batch.draw(obs, x, screenH, width, - width); //bottom pipe
		for(int i = 1; i <= tilesPerHeight - tileHeight - tilesPerHole - 1; i++) {
			batch.draw(gold, x, screenH - (width * i), width, -width);
		}
	}
}