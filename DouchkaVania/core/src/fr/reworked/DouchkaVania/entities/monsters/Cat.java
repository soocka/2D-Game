package fr.reworked.DouchkaVania.entities.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import fr.reworked.DouchkaVania.entities.NonPlayableCharacter;

import com.badlogic.gdx.physics.box2d.*;


public class Cat extends NonPlayableCharacter {
    private Texture TextureCat;
    private Body body;

    public Cat(String name, int hp, int damage, World world, Vector2 position, float startX, float startY, float width, float height) {
        super(world, position, "Cat", false);
        this.hp = 50;
        this.damage = 15;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);

        body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/20 , height/20 );
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        body.createFixture(fixtureDef);


        TextureCat = new Texture(Gdx.files.internal("Cat(1).png"));
    }

    
  
    
    /** 
     * @param batch
     */
    public void draw(SpriteBatch batch) {
        batch.draw(TextureCat, body.getPosition().x, body.getPosition().y, 80, 80);
    }
    
    /** 
     * @return Body
     */
    public Body getBody() {
        return this.body;
    }

    @Override
    public void render(SpriteBatch batch){

    }
}
