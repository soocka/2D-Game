package fr.reworked.DouchkaVania;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
    protected String name;
    protected int hp;
    protected int ap;
    protected int damage;
    private int energy;

    public Entity(String name, int hp, int ap, int damage, int energy) {
        this.name = name;
        this.hp = hp;
        this.ap = ap;
        this.damage = damage;
        this.energy = energy;
    }

 
    public String getName(){
        return this.name;
    }
    public int getHp(){
        return this.hp;
    }
    public int getEnergy(){
        return this.energy;
    }

    public int getMaxHp(){
        return 100;
    }

    public void heal (int amount){
        this.hp = Math.min(this.hp + amount, this.getMaxHp());
    }

    public void restoreEnergy(int amount){
        this.energy = Math.min(this.energy + amount, this.getMaxEnergy());
    }

    public int getMaxEnergy(){
        return 10;
    }

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
}
