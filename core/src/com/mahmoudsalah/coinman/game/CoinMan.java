package com.mahmoudsalah.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manstate = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity;
	int manY;
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	Texture coin;
	int coinCount;
    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    ArrayList<Rectangle> bombRectangle = new ArrayList<>();
    Texture bomb;
    int bombCount;
	Random random;
    Rectangle manshape;
    Music coinMusic;
    Music bombMusic;
    Music gameMusic;
    int score = 0,lives=3, coinspeed=2, bombspeed=3,legspeed =20,numBomb=300;
    float time=0;
    BitmapFont font;
    int gameState;
    Texture dizzy;
    @Override
	public void create () {
		batch = new SpriteBatch();
      background = new Texture("bg.png");
      man = new Texture[4];
      man[0] = new Texture("frame-1.png");
      man[1] = new Texture("frame-2.png");
      man[2] = new Texture("frame-3.png");
      man[3] = new Texture("frame-4.png");
      manY = Gdx.graphics.getHeight()/2;
      coin = new Texture("coin.png");
      bomb = new Texture("bomb.png");
      random = new Random();
      font = new BitmapFont();
      font.setColor(Color.WHITE);
      font.getData().setScale(10);
      dizzy = new Texture("dizzy-1.png");
      coinMusic = Gdx.audio.newMusic(Gdx.files.internal("coin.mp3"));
      coinMusic.setVolume(1);
      bombMusic = Gdx.audio.newMusic(Gdx.files.internal("bmob.mp3"));
      bombMusic.setVolume(1);
      gameMusic = Gdx.audio.newMusic(Gdx.files.internal("game.ogg"));
      gameMusic.play();
      gameMusic.setLooping(true);
      gameMusic.setVolume(0.7f);

    }
	public  void makeCoin(){
	    float hight = random.nextFloat()*Gdx.graphics.getHeight();
        coinYs.add((int) hight);
        coinXs.add(Gdx.graphics.getWidth());
    }
    public  void makeBomb(){
        float hight = random.nextFloat()*Gdx.graphics.getHeight();
        bombYs.add((int) hight);
        bombXs.add(Gdx.graphics.getWidth());
    }
    public void gamestart(){
        timer();
        if (bombCount<numBomb){
            bombCount++;
        }else{
            bombCount = 0;
            makeBomb();
        }
        bombRectangle.clear();
        for (int i = 0; i < bombXs.size(); i++) {
            batch.draw(bomb,bombXs.get(i),bombYs.get(i));
            bombXs.set(i,bombXs.get(i) - bombspeed);
            bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
        }
        if (coinCount<100){
            coinCount++;
        }else{
            coinCount = 0;
            makeCoin();
        }
        coinRectangle.clear();
        for (int i = 0; i < coinXs.size(); i++) {
            batch.draw(coin,coinXs.get(i),coinYs.get(i));
            coinXs.set(i,coinXs.get(i) - coinspeed);
            coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
        }

        if (Gdx.input.justTouched()){
            velocity = -10;
        }
        if (pause < legspeed) {
            pause++;
        } else {
            pause = 0;
            if (manstate < 3) {
                manstate++;
            } else {
                manstate = 0;
            }
        }

        velocity += gravity;
        manY -= velocity;
        if (manY<=0) {
            manY=0;
        }

    }
    public void timer(){
        time +=0.01;
        if (time>10){
            coinspeed+=2;
            bombspeed+=3;
            legspeed+=4;
            numBomb+=20;
            time=0;
        }
    }
    @Override
	public void render () {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
          if (gameState == 0) {
                font.draw(batch,"START",Gdx.graphics.getWidth()/5,Gdx.graphics.getHeight()/2);
                if (Gdx.input.justTouched()){
                    gameState=1;

                }
            }else if (gameState==1) {
                gamestart();
            } else if (gameState==2) {
               gamestart();
            }else if (gameState==3){
               gamestart();
            }
            else if (gameState==4){
            batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manstate].getWidth() / 2, manY);
            font.draw(batch,"PLAY AGAIN",Gdx.graphics.getWidth()/6,Gdx.graphics.getHeight()/2);
            if (Gdx.input.justTouched()){
                gameState=1;
                lives=3;
                score=0;
                manY = Gdx.graphics.getHeight()/2;
                velocity=0;
                coinXs.clear();
                coinYs.clear();
                coinRectangle.clear();
                bombRectangle.clear();
                bombXs.clear();
                bombYs.clear();
                gamestart();
            }
            }

        batch.draw(man[manstate], Gdx.graphics.getWidth() / 2 - man[manstate].getWidth() / 2, manY);
        manshape = new Rectangle(Gdx.graphics.getWidth() / 2-man[manstate].getWidth()/2,manY,man[manstate].getWidth(),man[manstate].getHeight());
        for (int i = 0; i < coinRectangle.size(); i++) {
            if (Intersector.overlaps(manshape,coinRectangle.get(i))){
                score++;
                coinMusic.play();
                coinRectangle.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }
        for (int i = 0; i < bombRectangle.size(); i++) {
            if (Intersector.overlaps(manshape,bombRectangle.get(i))){
                gameState++;
                lives--;
                bombMusic.play();
                bombRectangle.remove(i);
                bombYs.remove(i);
                bombXs.remove(i);
                break;
            }
        }
        font.draw(batch,String.valueOf(score),0,Gdx.graphics.getHeight()/10);
        font.draw(batch,String.valueOf(lives),0,Gdx.graphics.getHeight());
        batch.end();
    }
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
