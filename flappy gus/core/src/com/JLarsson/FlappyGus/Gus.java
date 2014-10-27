package com.JLarsson.FlappyGus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gus {
	
	public int x, y, width, height;
	float ySpeed, rotation;
	boolean canJump;
	int screenW, screenH, textureW, textureH;
	
	
	public Gus(int screenW, int screenH, int textureW, int textureH) {
		this.screenH = screenH;
		this.screenW = screenW;
		this.textureH = textureH;
		this.textureW = textureW;
		
		width = (int) (screenH * Game.gusWidth);
		height = width;
		
		canJump = true;
		x = (int) (screenW * .1f);
		y = (int) (screenH * .45f);
		ySpeed = 0;
		rotation = 0;
	}
	
	public void Update(boolean jumping, boolean dead) {
		if(ySpeed / screenH > Game.maxSpeed) ySpeed -= Game.gravity * screenH; //gravity and movement
		y += ySpeed;
		
		if(y >= screenH - height) { //stay at the bottom, game over if true
			y = screenH - height;
			ySpeed = 0;
		}
		else if(y <= 0) { //hit the floor
			y = 0;
			ySpeed *= .1;
		}
		
		if(jumping && canJump) { //jump
			canJump = false;
			ySpeed *= .1;
			ySpeed += Game.jumpSpeed * screenH;
			if(rotation == 0) rotation = 1;
		}
		else if (!jumping) canJump = true;
		
		if(!dead) { //rotate when jumping
			if(rotation > 0) rotation += 15;
			if(rotation > 360) rotation = 0;
		}
		else if(!touchingGround()) {
			rotation += 15; //and when dead
		}
	}
	
	public boolean touchingGround() {
		if (y <= 0) return true;
		else return false;
	}
	
	public void Draw(SpriteBatch batch, Texture t) {
		batch.draw(new TextureRegion(t), x, y, width / 2, height / 2, (float)width, (float)height, 1f, 1f, rotation);
	}
}
