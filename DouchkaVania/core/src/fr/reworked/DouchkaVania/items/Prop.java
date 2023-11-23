package fr.reworked.DouchkaVania.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Prop {
    private Body body;
    private Texture texture;
    
    public Prop(World world, float x, float y, float width, float height, String texturePath) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        FixtureDef fixtureDef = new FixtureDef();
        // fix the position of the shape
        
        fixtureDef.shape = shape;
        // defining density and restitution and friction
        fixtureDef.density = 0.5f;
        
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef).setUserData("Prop");
        

        texture = new Texture(Gdx.files.internal(texturePath));
        
        shape.dispose();
    }


    public void draw(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        batch.draw(texture, position.x - texture.getWidth() / 2, position.y - texture.getHeight() / 2);
    }

    public void dispose() {
        texture.dispose();
    }
}
