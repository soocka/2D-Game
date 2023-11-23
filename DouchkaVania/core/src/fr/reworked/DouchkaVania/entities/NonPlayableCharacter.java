package fr.reworked.DouchkaVania.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import fr.reworked.DouchkaVania.Entity;

public class NonPlayableCharacter extends Entity {

    public NonPlayableCharacter(World world, Vector2 position, String name, boolean isFriendly) {
        super(name, 100, 10, 20, 50);
    }



    

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(SpriteBatch batch) {
    }
}
