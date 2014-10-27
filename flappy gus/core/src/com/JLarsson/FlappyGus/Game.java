package com.JLarsson.FlappyGus;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game extends ApplicationAdapter {

	public static float gusWidth = .085f, gravity = .00132f, jumpSpeed = .0234f, maxSpeed = -.02f,
			spaceBetweenPipes = .37f, pipeSpeed = .01f;

	enum GameState {
		Before, Running, Over
	};

	GameState gameState;
	BitmapFont font;
	SpriteBatch batch;
	Texture gusTexture, goldTexture, obsTexture;
	Gus gus;
	//Pipe pipe;
	int screenW, screenH, score, highscore, flashCounter;
	ArrayList<Pipe> pipes;
	boolean touching, newHighscore;
	static Random random = new Random();
	HighscoreHandler highscoreHandler;
	float pipeSpeedMultiplier;
	Sound gameOver, gameStart, pipeCleared;

	@Override
	public void create() {
		Gdx.graphics.setDisplayMode(480, 800, false);
		highscoreHandler = new HighscoreHandler();
		font = new BitmapFont();
		screenW = Gdx.graphics.getWidth();
		screenH = Gdx.graphics.getHeight();
		batch = new SpriteBatch();
		gusTexture = new Texture("gus.PNG");
		goldTexture = new Texture("gold.png");
		obsTexture = new Texture("obs.png");
		font.setColor(Color.BLACK);
		font.setScale((float) screenW / 200);
		highscore = highscoreHandler.Get();
		pipes = new ArrayList<Pipe>();
		gameOver = Gdx.audio.newSound(Gdx.files.internal("game over.wav"));
		gameStart = Gdx.audio.newSound(Gdx.files.internal("game start.wav"));
		pipeCleared = Gdx.audio.newSound(Gdx.files.internal("pipe cleared.wav"));

		Restart();

		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean touchDown(int x, int y, int pointer, int button) {
				touching = true;
				return true; // return true to indicate the event was handled
			}

			public boolean touchUp(int x, int y, int pointer, int button) {
				touching = false;
				return true; // return true to indicate the event was handled
			}
		});
	}

	void Restart() { // sets all variables that are changed on restart, to
						// reduce time taken on restart
		newHighscore = false;
		gameState = GameState.Before;
		score = 0;
		gus = new Gus(screenW, screenH, gusTexture.getWidth(), gusTexture.getHeight());
		touching = false;
		pipes.clear();
		pipes.add(new Pipe(screenW, screenH));
		pipeSpeedMultiplier = 1;
		flashCounter = 0;
	}

	void Update() {
		if(flashCounter > 0) flashCounter--;
		
		switch (gameState) {
		case Before:
				if (touching)
				{
					gameState = GameState.Running; //start the game
					gameStart.play();
				}
			break;
			
		case Over:
			gus.Update(false, true); // move gus
			if (touching && gus.touchingGround())
				Restart(); // if touching screen restart game
			if (pipeSpeedMultiplier > 0) //code for slowing down the pipes on death
				pipeSpeedMultiplier -= .02f;
			else
				pipeSpeedMultiplier = 0;
			for (int i = 0; i < pipes.size(); i++) {
				pipes.get(i).Update(pipeSpeedMultiplier);
			}
			break;
			
		case Running:
			// spawn pipes
			if (pipes.size() > 0) {
				if (screenW - pipes.get(pipes.size() - 1).x > spaceBetweenPipes * screenH) {
					pipes.add(new Pipe(screenW, screenH));
				}
			}
			// update gus
			gus.Update(touching, false);
			// move pipes
			for (int i = 0; i < pipes.size(); i++) {
				pipes.get(i).Update(pipeSpeedMultiplier);
			}
			// remove pipes
			for (int i = pipes.size() - 1; i >= 0; i--) {
				if (pipes.get(i).isOffScreen())
					pipes.remove(i);
			}
			// check for death
			int cornerBuffer = (int) (gus.width * .25f);
			if (pipes.get(0).CollidingWith(gus.x, gus.y, gus.width, gus.height) || //horizontal rectangle
					pipes.get(0).CollidingWith(gus.x + cornerBuffer, gus.y, gus.width - cornerBuffer * 2, gus.height) //and vertical, to make the corners more realistic
					|| gus.y <= 0) {
				gameState = GameState.Over;
				flashCounter = 3;
				gameOver.play();
				if (score > highscore) {
					highscoreHandler.Set(score);
					highscore = score;
					newHighscore = true;
				}
			}
			// and scoring
			float distanceToEdge = (float) (gus.x - pipes.get(0).x) / screenW;
			if (distanceToEdge <= pipeSpeed && distanceToEdge > 0) {
				score++;
				pipeCleared.play();
			}
			break;
			
		default:
			create();
			break;
		}

	}

	@Override
	public void render() {
		Update();

		if (flashCounter > 0) Gdx.gl.glClearColor(1, 1, 1, 1);
		else Gdx.gl.glClearColor(.9f, .9f, .95f, 1);

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();

			if(gameState != GameState.Before) for (int i = 0; i < pipes.size(); i++) { //draw pipes
				pipes.get(i).Render(batch, goldTexture, obsTexture);
			}
			
			gus.Draw(batch, gusTexture); //draw gus

			if (gameState == GameState.Over) {
				//draw highscore
				if (!newHighscore)
					font.draw(batch, "Highscore: " + highscore, screenW * .25f, screenH * .56f);
				else {
					font.setColor(Color.RED);
					font.draw(batch, "New HighScore!", screenW * .23f, screenH * .56f);
					font.setColor(Color.BLACK);
				}
				font.draw(batch, "Tap To Restart", screenW * .25f, screenH * .5f);
			} else if (gameState == GameState.Before) {
				font.draw(batch, "Tap To Start", screenW * .28f, screenH * .5f);
			}
			font.draw(batch, Float.toString(score).subSequence(0, Float.toString(score).length() - 2), screenW * .46f, screenH * .65f);
			batch.end();
		}

	

	public static int randInt(int min, int max) {
		int randomNum = random.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
