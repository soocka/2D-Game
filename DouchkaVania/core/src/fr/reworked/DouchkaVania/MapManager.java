package fr.reworked.DouchkaVania;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;

import fr.reworked.DouchkaVania.entities.monsters.*;
import fr.reworked.DouchkaVania.items.*;
import fr.reworked.DouchkaVania.ui.GameHUD;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;


public class MapManager implements Screen {
    private OrthographicCamera camera;
    private Texture gameOverTexture;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private World world; 
    private int tileSize = 32; 
    private Box2DDebugRenderer debugRenderer;
    private Player player;
    private HealthPotion healthPotion;
    private SpriteBatch spriteBatch;
    private GameHUD gameHUD;
    private Prop box;
    private SilverKnight silverKnight;
    private DarkKnight darkKnight;
    private GoldenKnight goldenKnight;
    private Cat cat;
    private boolean debugMode = false;
    private boolean isVictory = false;
    private Texture winTexture;
    private Music gameMusic;
    private Music winMusic;
    private Music looseMusic;
    private Music menuMusic;



    @Override
    public void show() {
        gameHUD = new GameHUD();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Créer la caméra avec une position par défaut
        camera = new OrthographicCamera(screenWidth, screenHeight);
        float cameraX = 0; // Position X de la caméra par défaut
        float cameraY = 0; // Position Y de la caméra par défaut
        camera.position.set(cameraX, cameraY, 0);
        camera.update();

        // Initialiser le monde Box2D
        Box2D.init();
        world = new World(new Vector2(0, -300f), true);

        // Chargement de la carte
        tiledMap = new TmxMapLoader().load("map/map.tmx");
        spriteBatch = new SpriteBatch();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, spriteBatch);
        player = new Player(1050, 600, 32, 64, "Douchka", 100, 10, 25, world, new Vector2());
       
        box = new Prop(world, 2000, 600, 64, 64, "box.png");
        cat= new Cat("Cat", 600, 32, world, new Vector2(), 300, 600, 32, 64);

        healthPotion = new HealthPotion(5500, 600, 15, world, new Vector2(), 16, false, 1, 20);
       
        silverKnight= new SilverKnight("SilverKnight", 600, 32, 64, world, new Vector2(), 1500, 600, 64, 64);
        darkKnight= new DarkKnight("DarkKnight", 600, 32, 64, world, new Vector2(), 6500, 600, 64, 64);
        goldenKnight= new GoldenKnight("GoldenKnight", 600, 32, 64, world, new Vector2(), 4000, 650, 64, 64);

        gameOverTexture = new Texture(Gdx.files.internal("game_over.png"));
        winTexture = new Texture (Gdx.files.internal("Win.png"));

        // Créer les objets de collision à partir de la couche "collision" de la carte
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

        // Parcourir  toutes les cellules de la couche

        for (int x = 0; x < collisionLayer.getWidth(); x++) {
            for (int y = 0; y < collisionLayer.getHeight(); y++) {
                Cell cell = collisionLayer.getCell(x, y);
                if (cell != null) {
                    Boolean blocked = cell.getTile().getProperties().get("blocked", Boolean.class);
                    if (blocked != null && blocked) {
                        // Créer un Body pour cette tuile de collision
                        BodyDef bodyDef = new BodyDef();
                        bodyDef.type = BodyDef.BodyType.StaticBody;
                        bodyDef.position.set(x * tileSize + tileSize / 2, y * tileSize + tileSize / 2);
                        Body body = world.createBody(bodyDef);

                        // Créer une forme de boîte pour la tuile
                        PolygonShape shape = new PolygonShape();
                        shape.setAsBox(tileSize / 2, tileSize / 2);

                        // Créer un Fixture pour le Body

                        FixtureDef fixtureDef = new FixtureDef();
                        fixtureDef.shape = shape;
                        fixtureDef.density = 1.0f;
                        fixtureDef.friction = 0.4f;
                        body.createFixture(fixtureDef);

                        shape.dispose();
                    }
                }
            }
                debugRenderer = new Box2DDebugRenderer();

                gameMusic = Gdx.audio.newMusic(Gdx.files.internal("game_song.ogg"));
                winMusic = Gdx.audio.newMusic(Gdx.files.internal("win_song.wav"));
                looseMusic = Gdx.audio.newMusic(Gdx.files.internal("loose_song.mp3"));
                menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_song.mp3"));

                gameMusic.setLooping(true);
                winMusic.setLooping(true);
                looseMusic.setLooping(true);
                menuMusic.setLooping(true);

        }


        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if (fixtureA.getUserData() instanceof Player) {
                    handleAttackContact((Player) fixtureA.getUserData(), fixtureB);
                } else if (fixtureB.getUserData() instanceof Player) {
                    handleAttackContact((Player) fixtureB.getUserData(), fixtureA);
                }

                if (fixtureA.getUserData() instanceof Player || fixtureB.getUserData() instanceof Player) {
                    Fixture playerFixture = fixtureA.getUserData() instanceof Player ? fixtureA : fixtureB;
                    Fixture otherFixture = playerFixture == fixtureA ? fixtureB : fixtureA;
                    handleAttackContact((Player) playerFixture.getUserData(), otherFixture);
                }

                if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("sensor")) {
                    player.setOnGround(true);
                }

                if (fixtureB.getUserData() != null && fixtureB.getUserData().equals("sensor")) {
                    player.setOnGround(true);
                }

                if (silverKnight.isDead() && darkKnight.isDead() && goldenKnight.isDead() && !isVictory) {
                    isVictory = true;
                }

                if (silverKnight.isDead() && darkKnight.isDead() && goldenKnight.isDead()) {
                    isVictory = true; // on a gagné
                }
            }

            private void handleAttackContact(Player player, Fixture knightFixture) {
                if (knightFixture.getUserData() instanceof SilverKnight) {
                    SilverKnight knight = (SilverKnight) knightFixture.getUserData();
                    player.addKnightInRange(knight);
                }
                if (knightFixture.getUserData() instanceof DarkKnight) {
                    DarkKnight knight = (DarkKnight) knightFixture.getUserData();
                    player.addDarkKnightInRange(knight);
                }
                if (knightFixture.getUserData() instanceof GoldenKnight) {
                    GoldenKnight knight = (GoldenKnight) knightFixture.getUserData();
                    player.addGoldenKnightInRange(knight);
                }
            }
            
            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("sensor")) {
                    player.setOnGround(false);
                }

                if (fixtureB.getUserData() != null && fixtureB.getUserData().equals("sensor")) {
                    player.setOnGround(false);
                }
                if (fixtureA.getUserData() instanceof Player || fixtureB.getUserData() instanceof Player) {
                    Fixture playerFixture = fixtureA.getUserData() instanceof Player ? fixtureA : fixtureB;
                    Fixture otherFixture = playerFixture == fixtureA ? fixtureB : fixtureA;
                    handleAttackEndContact((Player) playerFixture.getUserData(), otherFixture);
                }
            }

            private void handleAttackEndContact(Player player, Fixture knightFixture) {
                if (knightFixture.getUserData() instanceof SilverKnight) {
                    SilverKnight knight = (SilverKnight) knightFixture.getUserData();
                    player.removeKnightFromRange(knight);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

    }

    

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Vector2 playerPosition = player.getBody().getPosition();
        float cameraX = playerPosition.x;
        float cameraY = playerPosition.y;
        camera.position.set(cameraX, cameraY, 0);
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        player.update(Gdx.graphics.getDeltaTime());
        player.draw(spriteBatch);

        cat.draw(spriteBatch);
        box.draw(spriteBatch);
        gameMusic.play();

        if (!healthPotion.isUsed()) {
            healthPotion.draw(spriteBatch);
        } else {
            healthPotion.safelyRemoveFromWorld();
        }

        if (!silverKnight.isDead()) {
            silverKnight.update(Gdx.graphics.getDeltaTime());
            silverKnight.draw(spriteBatch);
        } else {
            silverKnight.safelyRemoveFromWorld();
        }

        if (!darkKnight.isDead()) {
            darkKnight.update(Gdx.graphics.getDeltaTime());
            darkKnight.draw(spriteBatch);
        } else {
            darkKnight.safelyRemoveFromWorld();
        }

        if (!goldenKnight.isDead()) {
            goldenKnight.update(Gdx.graphics.getDeltaTime());
            goldenKnight.draw(spriteBatch);
        } else {
            goldenKnight.safelyRemoveFromWorld();
        }

        spriteBatch.end();

        if (player.isPlayerDead()) {
            gameMusic.stop();
            looseMusic.play();
            spriteBatch.begin();
            spriteBatch.draw(gameOverTexture, camera.position.x - gameOverTexture.getWidth() / 2, camera.position.y - gameOverTexture.getHeight() / 2, camera.viewportWidth, camera.viewportHeight);
            spriteBatch.end();
    
            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
    
                float x = camera.position.x - gameOverTexture.getWidth() / 2;
                float y = camera.position.y - gameOverTexture.getHeight() / 2;
    
                if (touchPos.x >= x && touchPos.x <= x + gameOverTexture.getWidth() &&
                    touchPos.y >= y && touchPos.y <= y + gameOverTexture.getHeight()) {
                    looseMusic.stop();
                    restartGame();
                }
            }
    
            return;
        }
        
        gameHUD.update(player.getHp(), player.getMaxHp(), player.getName(),
                silverKnight.getHp(), silverKnight.getMaxHp(),
                goldenKnight.getHp(), goldenKnight.getMaxHp(),
                darkKnight.getHp(), darkKnight.getMaxHp());
        gameHUD.getStage().act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        gameHUD.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            debugMode = !debugMode;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goToMenu();
        }
        if (debugMode) {
            debugRenderer.render(world, camera.combined);
        }

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        if (isVictory) {
            gameMusic.stop();
            winMusic.play();
            spriteBatch.begin();
            spriteBatch.draw(winTexture, camera.position.x - winTexture.getWidth() / 2, camera.position.y - winTexture.getHeight() / 2);
            spriteBatch.end();
    
            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
    
                float x = camera.position.x - winTexture.getWidth() / 2;
                float y = camera.position.y - winTexture.getHeight() / 2;
    
                if (touchPos.x >= x && touchPos.x <= x + winTexture.getWidth() &&
                    touchPos.y >= y && touchPos.y <= y + winTexture.getHeight()) {
                    goToMenu();
                }
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        gameHUD.resize(width, height);
        gameHUD.getStage().getViewport().update(width, height, true);
    }

    
    private void restartGame() {
    Gdx.app.postRunnable(new Runnable() {
        @Override
        public void run() {
            ((Game)Gdx.app.getApplicationListener()).setScreen(new MapManager());
            }
        });
    }

    private void goToMenu() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new GameMenu((Game)Gdx.app.getApplicationListener()));
                winMusic.stop();
            }
        });
    }
    

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        gameMusic.stop();
        box.dispose();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        world.dispose();
        spriteBatch.dispose();
        gameHUD.getStage().dispose();
        winTexture.dispose();
        gameMusic.dispose();
        winMusic.dispose();
        looseMusic.dispose();
        menuMusic.dispose();
    }

}