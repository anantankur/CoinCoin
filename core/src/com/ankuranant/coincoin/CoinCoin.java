package com.ankuranant.coincoin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinCoin extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
	private Texture[] man;
	private Texture dizzyMan;
	private int manCounter = 0, pause = 0;
	private float gravity = 0.35f;
	private float velocity = 0;
	private int manY = 0;
	private float manWidth = 0, manHeight = 0;
	private Random random;
	private ArrayList<Integer> coinXs = new ArrayList<Integer>();
	private ArrayList<Integer> coinYs = new ArrayList<Integer>();
	private ArrayList<Circle> coinRectangle = new ArrayList<Circle>();

	private ArrayList<Integer> bombXs = new ArrayList<Integer>();
	private ArrayList<Integer> bombYs = new ArrayList<Integer>();
	private ArrayList<Circle> bombRectangle = new ArrayList<Circle>();

	private Texture coin;
	private Texture bomb;
	private int coinCount;
	private int bombCount;

	private int score = 0;
	private BitmapFont scoreFont;
	private BitmapFont startFont;

	private int gameState;

	private Rectangle manRectangle;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		dizzyMan = new Texture("dizzy-1.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");

		random = new Random();

		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(5);

		startFont = new BitmapFont();
		startFont.setColor(Color.WHITE);
		startFont.getData().setScale(4);
	}

	private void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	private void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1) {
			// game is live

			if(coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangle.clear();
			for(int i=0; i<coinYs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i), (float)coin.getWidth() / 2, (float)coin.getHeight() / 2);
				coinXs.set(i, coinXs.get(i) - 4);
				coinRectangle.add(new Circle(coinXs.get(i)+(float)coin.getWidth() / 4, coinYs.get(i)+(float)coin.getHeight() / 4, (float)coin.getWidth() / 4)); // <- 4 because image is half of original so /2/2
			}

			if(bombCount < 250) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangle.clear();
			for(int j=0; j<bombYs.size(); j++) {
				batch.draw(bomb, bombXs.get(j), bombYs.get(j), (float)bomb.getWidth() / 2, (float)bomb.getHeight() / 2);
				bombXs.set(j, bombXs.get(j) - 6);
				bombRectangle.add(new Circle(bombXs.get(j)+(float)bomb.getWidth() / 4, bombYs.get(j)+(float)bomb.getHeight() / 4, (float)bomb.getWidth() / 4)); // <- 4 because image is half of original so /2/2
			}

			if(Gdx.input.justTouched()) {
				velocity = -10;
			}

			if(pause < 8) {
				pause++;
			} else {
				pause = 0;
				if (manCounter < 3) {
					manCounter++;
				} else {
					manCounter = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if(manY <= 0) {
				manY = 0;
			}
		} else if(gameState == 0) {
			//waiting for player
			startFont.draw(batch, "Tap to start", 10, (float)Gdx.graphics.getHeight()/2);
			if(Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if(gameState == 2) {
			//game over

			startFont.draw(batch, "Game Over\nTap to restart", 10, (float)Gdx.graphics.getHeight()/2);
			if(Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinCount = 0;
				coinRectangle.clear();
				bombXs.clear();
				bombYs.clear();
				bombCount = 0;
				bombRectangle.clear();
			}
		}

		manWidth = man[manCounter].getWidth() / 2.5f;
		manHeight = man[manCounter].getHeight() / 2.5f;

		if(gameState == 2) {
			batch.draw(dizzyMan, (float)Gdx.graphics.getWidth() / 2 - man[manCounter].getWidth() / 1.5f, manY, manWidth, manHeight);
		} else {
			batch.draw(man[manCounter], (float)Gdx.graphics.getWidth() / 2 - man[manCounter].getWidth() / 1.5f, manY, manWidth, manHeight);
		}
		manRectangle = new Rectangle((float)Gdx.graphics.getWidth() / 2 - man[manCounter].getWidth() / 1.5f, manY, manWidth, manHeight);
//		Gdx.app.log("App", String.valueOf(man[manCounter].getWidth()/2.5f) + " ## " + String.valueOf( man[manCounter].getHeight()/2.5f));
//		Gdx.app.log("App", man[manCounter].getWidth() + " ## " + man[manCounter].getHeight());

		for(int i=0; i<coinRectangle.size(); i++) {
			if (Intersector.overlaps(coinRectangle.get(i), manRectangle)) {
				score++;
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
		for(int i=0; i<bombRectangle.size(); i++) {
			if (Intersector.overlaps(bombRectangle.get(i), manRectangle)) {

				gameState = 2;
			}
		}

		scoreFont.draw(batch, String.valueOf(score), 100, Gdx.graphics.getHeight()-100);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
