package fr.reworked.DouchkaVania.entities.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import fr.reworked.DouchkaVania.Player;
import fr.reworked.DouchkaVania.entities.NonPlayableCharacter;

import java.util.Random;


public class GoldenKnight extends NonPlayableCharacter {
    private Texture TextureSilver;
    private Body body;
    private Vector2 velocity;
    private float speed = 50f;
    private float stateTime = 0;
    private boolean isDead = false;
    private Random random = new Random();
    private float timeSinceLastDirectionChange = 0;
    private float directionChangeInterval = 1.0f;
    private float hitCooldown = 1.0f;
    private float timeSinceLastHit = 0;

    private boolean markedForRemoval = false;
    private float attackRange = 200f;
    private Vector2 recoilDirection = new Vector2();
    private boolean isRecoiling = false;
    private float recoilTime = 0.5f; // Durée du recul en secondes
    private float recoilSpeed = 16f; // Vitesse du recul


    public GoldenKnight(String name, int hp, int level, int damage, World world, Vector2 position, float startX, float startY, float width, float height) {
        super(world, position, "GoldenKnight", false);
        this.hp = 100;
        this.damage = 15;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);

        body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/3 , height/3 );
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        body.createFixture(fixtureDef).setUserData(this);


        TextureSilver = new Texture(Gdx.files.internal("GoldenKnight.png"));
    }


    
  
    public void takeDamage(int damage, Player player) {
        if (timeSinceLastHit >= hitCooldown) {
            this.hp -= damage;
            timeSinceLastHit = 0; 
            if (this.hp <= 0 && !isDead) { 
                isDead = true; 
                markedForRemoval = true; 
            }
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void safelyRemoveFromWorld() {
        if (isDead && body != null) {
            World world = body.getWorld();
            world.destroyBody(body);
            body = null;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!markedForRemoval) {
            float width = 100;
            float height = 100;
            float halfWidth = width / 2f;
            float halfHeight = height / 2f;
            batch.draw(TextureSilver,
                    body.getPosition().x - halfWidth,
                    body.getPosition().y - halfHeight,
                    halfWidth,
                    halfHeight,
                    width,
                    height,
                    1,
                    1,
                    body.getAngle() * MathUtils.radiansToDegrees,
                    0,
                    0,
                    TextureSilver.getWidth(),
                    TextureSilver.getHeight(),
                    false,
                    false);
        }
    }
    public Body getBody() {
        return this.body;
    }

    public void update(float deltaTime) {
        timeSinceLastHit += deltaTime;

        timeSinceLastDirectionChange += deltaTime;

        if (isRecoiling) {
            body.setLinearVelocity(recoilDirection.scl(recoilSpeed));
            recoilTime -= deltaTime;
            if (recoilTime <= 0) {
                isRecoiling = false;
                recoilTime = 0.5f;
                body.setLinearVelocity(0, 0);
            }
        } else if (timeSinceLastDirectionChange >= directionChangeInterval) {
            timeSinceLastDirectionChange = 0;
            int direction = random.nextBoolean() ? 1 : -1;
            velocity = new Vector2(speed * direction, body.getLinearVelocity().y);
            body.setLinearVelocity(velocity);
        }

        stateTime += deltaTime;
    }


    public void followAndAttackPlayer(Player player, float deltaTime) {
        Vector2 playerPosition = player.getBody().getPosition();
        Vector2 myPosition = body.getPosition();
        float distanceToPlayer = myPosition.dst(playerPosition);

        if (distanceToPlayer <= attackRange) {
            if (playerPosition.x < myPosition.x) {
                velocity.x = -speed;
            } else {
                velocity.x = speed;
            }

            if (timeSinceLastHit >= hitCooldown) {
                player.takeDamage(damage);
                timeSinceLastHit = 0;
            }
        } else {
            velocity.x = 0;
        }

        body.setLinearVelocity(velocity);
        stateTime += deltaTime;
    }


    @Override
    public void render(SpriteBatch batch){

    }
}

