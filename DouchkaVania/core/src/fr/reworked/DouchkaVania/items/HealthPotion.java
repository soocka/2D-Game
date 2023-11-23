package fr.reworked.DouchkaVania.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.*;

import fr.reworked.DouchkaVania.Player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HealthPotion extends Potion {
    private int contHp;
    private Body body;
    private Texture textureHealthPotion;
    private Animation<TextureRegion> animationIdle;
    private float stateTime;

    public HealthPotion(float startX, float startY, float width, World world, Vector2 position, float height, boolean typePotion, int quantity, int contHp) {
        super(true, 1);
        this.contHp = contHp;
        this.stateTime = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);

        body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width /  2, height / 2, new Vector2(width, height / 2), 0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        body.createFixture(fixtureDef).setUserData(this);

        textureHealthPotion = new Texture(Gdx.files.internal("Full-Red.png"));
        TextureRegion[][] frame = TextureRegion.split(textureHealthPotion, 32, 32);
        TextureRegion[] frames = frame[0];
        animationIdle = new Animation<TextureRegion>(0.25f, frames); // Change 0.25f avec la durée souhaitée entre les frames
    }

    

    public void draw(SpriteBatch batch){
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animationIdle.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, body.getPosition().x, body.getPosition().y, 32, 32);
    }

    @Override
    public void use(Player player) {
        if (this.getTypePotion() && this.getQuantity() > 0) {
            if (player.getHp() < player.getMaxHp()) {
                player.heal(contHp);
                isUsed();
                this.consumed = true;
            }
        }
    }

       public void safelyRemoveFromWorld() {
        if (consumed && body != null) {
            World world = body.getWorld();
            world.destroyBody(body);
            body = null;
        }
    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
