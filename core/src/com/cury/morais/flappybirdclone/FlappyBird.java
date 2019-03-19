package com.cury.morais.flappybirdclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch; //criar as animacoes
    private BitmapFont points, msgRestart;
    private Texture background, pipeTop, pipeBot;
    private Texture gameOver;
    private Texture[] bird = new Texture[3];
    private Circle birdHitbox;
    private Rectangle pipeTopHitbox;
    private Rectangle pipeBotHitbox;
    private ShapeRenderer shape; //draw the hitboxes


    //atributos de configuracao
    private int gameState = 0;
    private int movimentoCounter = 0;
    private float phoneWidth, phoneHeight;
    private float birdAnim = 0;
    private float birdFall = 0;
    private float birdFly = 7;// higher = easy
    private float birdVerticalPosition;
    private float pipeHorizontalMov = 0;
    private float spaceBetPipes = 0;
    private float heightBetPipes = 0;
    private float deltaTime =0;
    private Random randomNumber = new Random();
    private int pontuacao = 0;


    //camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 1080;//1024;
    private final float VIRTUAL_HEIGHT = 1920;//1280;

	@Override
	public void create () {
		Gdx.app.log("create","inicio do jogo");
		batch = new SpriteBatch();

		points = new BitmapFont();
		points.getData().setScale(6);
		points.setColor(Color.WHITE);

        msgRestart = new BitmapFont();
        msgRestart.getData().setScale(3);
        msgRestart.setColor(Color.WHITE);

		bird[0] = new Texture("passaro1.png");
        bird[1] = new Texture("passaro2.png");
        bird[2] = new Texture("passaro3.png");
		background = new Texture("fundo.png");
		pipeBot = new Texture("cano_baixo.png");
		pipeTop = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");

		//hitbox
        shape = new ShapeRenderer(); //desenha as hitbox
        pipeBotHitbox = new Rectangle();
        pipeTopHitbox = new Rectangle();
        birdHitbox = new Circle();

        //-----------------------
        //configuracoes da CAMERA
        //-----------------------
//        camera = new OrthographicCamera();
//        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
//        viewport = new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,camera);

        phoneHeight = Gdx.graphics.getHeight();//VIRTUAL_HEIGHT;//Gdx.graphics.getHeight();
        phoneWidth = Gdx.graphics.getWidth();//VIRTUAL_WIDTH;//Gdx.graphics.getWidth();

        birdVerticalPosition = phoneHeight/2;
        pipeHorizontalMov = phoneWidth - 100;
        spaceBetPipes = 250;
	}

	@Override
	public void render () {
//	    camera.update();

	    //limpar frames anteriores aument desempenho
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		deltaTime = Gdx.graphics.getDeltaTime();
        birdAnim += deltaTime * 10; //incrementa as animacoes do passaro

        if (birdAnim > 2) { //reseta animacao do passaro
            birdAnim = 0;
        }

        if (gameState == 0) {

            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else { //game iniciado


            birdFall += deltaTime * 20; //velocidade de queda
            if (birdVerticalPosition > 0 || birdFall < 0) { //controla a queda do passaro
                birdVerticalPosition = birdVerticalPosition - birdFall;
            }

            if(gameState == 1){
                pipeHorizontalMov -= deltaTime * 450;//decrementa o desenho dos canos para criar animacao de movimento do cano

                if (Gdx.input.justTouched()) { //controla o Voo do passaro
                    birdFall = (float) -birdFly;
                }

                if (pipeHorizontalMov < -pipeTop.getWidth()) { //controle dos canos do cenario
                    pipeHorizontalMov = phoneWidth - 100;
                    heightBetPipes = randomNumber.nextInt(400) - 200;
                    pontuacao++;
                }

            }else{ //game over

                if(Gdx.input.justTouched()){
                    pontuacao = 0;
                    gameState = 0;
                    birdFall = 0;
                    birdVerticalPosition = phoneHeight/2;
                    pipeHorizontalMov = phoneWidth - 100;
                }

            }

        }

        //Draw imgs
        batch.begin();
//        batch.setProjectionMatrix(camera.combined);//configurando a projecao
        batch.draw(background, 0, 0, phoneWidth, phoneHeight);
        batch.draw(pipeTop, pipeHorizontalMov, phoneHeight / 2 + spaceBetPipes / 2 + heightBetPipes);
        batch.draw(pipeBot, pipeHorizontalMov, phoneHeight / 2 - pipeBot.getHeight() - spaceBetPipes / 2 + heightBetPipes);
        batch.draw(bird[(int) birdAnim], 50, birdVerticalPosition);
        points.draw(batch, String.valueOf(pontuacao), (int) phoneWidth/2, (int) phoneHeight-50);
        if (gameState == 2){
            batch.draw(gameOver,phoneWidth/2-gameOver.getWidth()/2,phoneHeight/2);
            msgRestart.draw(batch,"Toque para reiniciar.",phoneWidth/2-gameOver.getWidth()/2,phoneHeight/2-gameOver.getHeight()+20);
        }
        batch.end();


        //draw hitbox
        birdHitbox.set(50+bird[0].getWidth()/2,birdVerticalPosition+bird[0].getHeight()/2,30);
        pipeBotHitbox.set(pipeHorizontalMov,phoneHeight / 2 - pipeBot.getHeight() - spaceBetPipes / 2 + heightBetPipes,pipeBot.getWidth(),pipeBot.getHeight());
        pipeTopHitbox.set(pipeHorizontalMov, phoneHeight / 2 + spaceBetPipes / 2 + heightBetPipes, pipeTop.getWidth(),pipeTop.getHeight());
//        shape.begin(ShapeRenderer.ShapeType.Filled);
//        shape.circle(50+bird[0].getWidth()/2,birdVerticalPosition+bird[0].getHeight()/2,birdHitbox.radius);
//        shape.rect(pipeBotHitbox.x,pipeBotHitbox.y,pipeBotHitbox.width,pipeBotHitbox.height);
//        shape.rect(pipeTopHitbox.x,pipeTopHitbox.y,pipeTopHitbox.width,pipeTopHitbox.height);
//
//        shape.setColor(Color.RED);
//        shape.end();


        //teste colisao
        if (Intersector.overlaps(birdHitbox,pipeBotHitbox) || Intersector.overlaps(birdHitbox,pipeTopHitbox)){
            gameState = 2;
            Gdx.app.log("render","colidiu");

        }

    }
//
//    @Override
//    public void resize(int width, int height) {
//        viewport.update(width,height);
//    }
}
