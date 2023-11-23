package fr.reworked.DouchkaVania;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameMenu implements Screen {
    private Stage stage;
    private Game game;
    private BitmapFont font;
    private Image backgroundImage;
    private Texture backgroundTexture;
    private Music menuMusic;

    public GameMenu(Game game) {
    this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_song.mp3"));
        menuMusic.setLooping(true);
        menuMusic.play();

        // on charge la police d'écriture
        font = new BitmapFont(Gdx.files.internal("hud/bold-font.fnt"));
        font.getData().setScale(2);
        font.setColor(Color.WHITE);

        backgroundTexture = new Texture(Gdx.files.internal("wallpaper.png"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // on crée un style de bouton
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("button_up.png")));
        buttonStyle.down = new TextureRegionDrawable(new Texture(Gdx.files.internal("button_up.png")));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // on crée les boutons
        TextButton playButton = new TextButton("Play", buttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MapManager());
            }
        });
        table.add(playButton).fillX().uniformX().padBottom(20);

        TextButton quitButton = new TextButton("Quit", buttonStyle);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(quitButton).fillX().uniformX();

        // on ajoute la table au stage
        table.center();
    }



    @Override
    public void render(float delta) {
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        menuMusic.pause();
    }

    @Override 
    public void resume() {
        menuMusic.play();
    }

    @Override
    public void hide() {
        menuMusic.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        menuMusic.dispose();
    }
}