package fr.reworked.DouchkaVania;
import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import fr.reworked.DouchkaVania.entities.monsters.DarkKnight;
import fr.reworked.DouchkaVania.entities.monsters.GoldenKnight;
import fr.reworked.DouchkaVania.entities.monsters.SilverKnight;
import fr.reworked.DouchkaVania.items.Potion;


public class Player extends Entity {
    private Body body;
    private Animation<TextureRegion> animationIdle;
    private Animation<TextureRegion> animationFall;
    private Animation<TextureRegion> animationDroite;
    private Animation<TextureRegion> attackAnimation;
    private Vector2 velocity;
    private boolean isFacingRight;
    private World world;
    private boolean isOnGround;
    private boolean isAttacking;
    private float jumpForce = 60000f;
    private boolean isJumping = false;
    private float stateTime = 0;
    private float speed = 100f;
    private float attackTime = 0;
    private float attackRadius = 50f;
    private List<SilverKnight> knightsInRange = new ArrayList<>();
    private List<GoldenKnight> goldenKnightsInRange = new ArrayList<>();
    private List<DarkKnight> darkKnightsInRange = new ArrayList<>();
    private float attackCooldown = 1.0f;
    private float timeSinceLastAttack = 0;
    private final float decreaseHpCD = 1.0f;
    private float getTimeSinceLastDecreaseHp = 0;
    private boolean isAlive = true;

    public Player(float startX, float startY, float width, float height, String name, int hp, int damage, int energy,World world, Vector2 position) {
        super(name, 100, 10, 20, 10);

        velocity = new Vector2();
        isFacingRight = true;
        isAttacking = false;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);

        body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2 , height /2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        body.createFixture(fixtureDef);

        // on crée un capteur pour détecter les collisions avec les tuiles, et ainsi savoir si on est au sol ou non
        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.isSensor = true;
        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox((width / 2) +10, 10, new Vector2(0, -height /2), 0f);
        sensorFixtureDef.shape = sensorShape;
        body.createFixture(sensorFixtureDef).setUserData("sensor");


        sensorShape.dispose();
        this.world = world;

        shape.dispose();

        Texture textureDroite = new Texture(Gdx.files.internal("Walking_KG_2.png"));
        TextureRegion[][] framesDroite = TextureRegion.split(textureDroite, 100, 64);
        animationDroite = new Animation<>(0.1f, framesDroite[0]);
        animationDroite.setPlayMode(Animation.PlayMode.LOOP);

        Texture idleTexture = new Texture(Gdx.files.internal("Idle_KG_1.png"));
        TextureRegion[][] framesIdle = TextureRegion.split(idleTexture, 100, 64);
        animationIdle = new Animation<>(0.1f, framesIdle[0]);
        animationIdle.setPlayMode(Animation.PlayMode.LOOP);

        Texture fallTexture = new Texture(Gdx.files.internal("Jump_KG_2.gif"));
        TextureRegion[][] framesFall = TextureRegion.split(fallTexture, 100, 64);
        animationFall = new Animation<>(0.5f, framesFall[0]);
        animationFall.setPlayMode(Animation.PlayMode.LOOP);

        Texture attackTexture = new Texture(Gdx.files.internal("Attack_KG_2.png"));
        TextureRegion[][] framesAttack = TextureRegion.split(attackTexture, 100, 64);
        attackAnimation = new Animation<>(0.1f, framesAttack[0]);
        attackAnimation.setPlayMode(Animation.PlayMode.NORMAL);


        createAttackSensor();
    }

    

    public boolean isPLayerDead(){
        return !isAlive || hp <=0;
    }

    private void createAttackSensor() {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(attackRadius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(this);
        circleShape.dispose();
    }

    private void decreaseHp(int amount){
          if (isAlive) {
              if (getTimeSinceLastDecreaseHp >= decreaseHpCD)
                  if (this.hp > amount) {
                      this.hp -= amount;
                  } else {
                      this.hp = 0;
                  }
              getTimeSinceLastDecreaseHp = 0;
          }
          if (isPlayerDead()) {
              isAlive = false;
          }
    }
//////////// Methode d'attaque sur les knights ////////////////
    public void attack() {
        if (isAttacking) {
            for (SilverKnight knight : knightsInRange) {
                if (!knight.isMarkedForRemoval()) {
                    knight.takeDamage(this.damage, this);
                }
            }
            for (GoldenKnight knight : goldenKnightsInRange) {
                if (!knight.isMarkedForRemoval()) {
                    knight.takeDamage(this.damage, this);
                }
            }
            for (DarkKnight knight : darkKnightsInRange) {
                if (!knight.isMarkedForRemoval()) {
                    knight.takeDamage(this.damage, this);
                }
            }
        }
    }

    public void addGoldenKnightInRange(GoldenKnight knight) {
        if (!goldenKnightsInRange.contains(knight)) {
            goldenKnightsInRange.add(knight);
        }
    }

    public void addDarkKnightInRange(DarkKnight knight) {
        if (!darkKnightsInRange.contains(knight)) {
            darkKnightsInRange.add(knight);
        }
    }

    public void addKnightInRange(SilverKnight knight) {
        if (!knightsInRange.contains(knight)) {
            knightsInRange.add(knight);
        }
    }


    public void removeKnightFromRange(SilverKnight knight) {
        knightsInRange.remove(knight);
    }


    public void draw(SpriteBatch batch) {
        float offsetX = 50f;
        float offsetY = 32f;
        Vector2 position = body.getPosition();
        TextureRegion currentFrame;

        if (isJumping) {
            currentFrame = animationFall.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, position.x - offsetX, position.y - offsetY, 100, 64);

        } else
        if (velocity.isZero()) {
            currentFrame = animationIdle.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, position.x - offsetX, position.y - offsetY, 100, 64);
        } else {
            currentFrame = animationDroite.getKeyFrame(stateTime, true);
            if (isFacingRight) {
                batch.draw(currentFrame, position.x - offsetX, position.y - offsetY, 100, 64);
            } else {
                batch.draw(currentFrame, position.x + offsetX, position.y - offsetY, -100, 64);
            }
        }
        if (isAttacking) {
            currentFrame = attackAnimation.getKeyFrame(attackTime, false);
            if (!attackAnimation.isAnimationFinished(attackTime)) {
                if (isFacingRight) {
                    batch.draw(currentFrame, position.x - offsetX, position.y - offsetY, 100, 64);
                } else {
                    batch.draw(currentFrame, position.x + offsetX, position.y - offsetY, -100, 64);
                }
            } else {
                isAttacking = false; // Reset attacking state
            }
        }
    }

    public Body getBody() {
        return this.body;
    }

    public void usePotion(Potion potion){
        potion.use(this);
    }

    private void checkCollisionWithKnights() {
        try {
            for (GoldenKnight knight : new ArrayList<>(goldenKnightsInRange)) {
                if (knight.getBody().getPosition().dst(body.getPosition()) < attackRadius) {
                    decreaseHp(10);
                    break;
                }
            }
        } catch (Exception e) {
        }

        try {
            for (SilverKnight knight : new ArrayList<>(knightsInRange)) {
                if (knight.getBody().getPosition().dst(body.getPosition()) < attackRadius) {
                    decreaseHp(5);
                    break;
                }
            }
        } catch (Exception e) {
        }

        try {
            for (DarkKnight knight : new ArrayList<>(darkKnightsInRange)) {
                if (knight.getBody().getPosition().dst(body.getPosition()) < attackRadius) {
                    decreaseHp(20);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public void update(float deltaTime) {
        checkCollisionWithKnights();
        stateTime += deltaTime;
        timeSinceLastAttack += deltaTime;
        getTimeSinceLastDecreaseHp += deltaTime;
        if (isAttacking) {
            attackTime += deltaTime;
            if (attackAnimation.isAnimationFinished(attackTime)) {
                isAttacking = false;
            } else {
                attack();
            }
        } else {
            handleInput();
        }
        for (GoldenKnight knight : goldenKnightsInRange) {
            if (!knight.isMarkedForRemoval()) {
                knight.followAndAttackPlayer(this, deltaTime);
            }
        }
        for (DarkKnight knight : darkKnightsInRange) {
            if (!knight.isMarkedForRemoval()) {
                knight.followAndAttackPlayer(this, deltaTime);
            }
        }
        for (SilverKnight knight : knightsInRange) {
            if (!knight.isMarkedForRemoval()) {
                knight.followAndAttackPlayer(this, deltaTime);
            }
        }

        body.setLinearVelocity(velocity.x, body.getLinearVelocity().y);
        Array<Contact> contacts = world.getContactList();
        for (Contact contact : contacts) {
            Object userDataA = contact.getFixtureA().getUserData();
            Object userDataB = contact.getFixtureB().getUserData();


            if (userDataA != null && userDataB != null && contact.isTouching() &&
                    (userDataA.equals("sensor") || userDataB.equals("sensor"))) {
                Fixture otherFixture = (userDataA.equals("sensor")) ? contact.getFixtureB() : contact.getFixtureA();
                Object otherUserData = otherFixture.getUserData();


                if (otherUserData instanceof Potion) {
                    Potion potion = (Potion) otherUserData;
                    usePotion(potion);
                }
            }
        }

        // on regarde si on se déplace vers la droite ou la gauche
        if (velocity.x > 0) {
            isFacingRight = true;
        } else if (velocity.x < 0) {
            isFacingRight = false;
        }
        body.setLinearVelocity(velocity.x, body.getLinearVelocity().y);
    }


    @Override
    public void render(SpriteBatch batch) {

    }

    private void handleInput() {
        velocity.set(0, body.getLinearVelocity().y);
        velocity.set(0, body.getLinearVelocity().y);

        if (Gdx.input.isKeyPressed(Input.Keys.Y) && getTimeSinceLastDecreaseHp >= decreaseHpCD) {
            decreaseHp(50);
            getTimeSinceLastDecreaseHp = 0;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || (Gdx.input.isKeyPressed(Input.Keys.UP)) && isOnGround) {
            isJumping = true;
            body.applyLinearImpulse(new Vector2(0, jumpForce), body.getWorldCenter(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y = -speed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A )|| (Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
            velocity.x = -speed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)|| (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
            velocity.x = speed;
        }

        // on attaque si on clique gauche et qu'on est pas en train d'attaquer
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !isAttacking && timeSinceLastAttack >= attackCooldown) {
            isAttacking = true;
            attackTime = 0; 
            timeSinceLastAttack = 0;
        }
    }

    public void takeDamage(int damage){

    }

    public boolean isPlayerDead() {
        return !isAlive || hp <= 0;
    }



    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
        if (isOnGround) {
            isJumping = false;
        }
    }
}
