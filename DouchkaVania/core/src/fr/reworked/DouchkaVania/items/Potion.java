package fr.reworked.DouchkaVania.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import fr.reworked.DouchkaVania.Player;

public abstract class Potion {
    private final boolean typePotion;
    private int quantity;
    public boolean consumed;


    public Potion(boolean typePotion, int quantity) {
        this.typePotion = typePotion;
        this.quantity = quantity;

    }

    

    public boolean getTypePotion() {
        return this.typePotion;
    }

    public int getQuantity() {
        return this.quantity;
    }


    public void use(Player player) {
        if (this.getTypePotion() && this.getQuantity() > 0) {
            if (player.getHp() < player.getMaxHp()) {
                player.heal(100);
                this.quantity--;
                      consumed = true;
            }
        } else if (!this.getTypePotion() && this.getQuantity() > 0) {
            if (player.getEnergy() < player.getMaxEnergy()) {
                player.restoreEnergy(100);
                this.quantity--;
                      consumed = true;
            }
        }
  
    }
    public boolean isUsed() {
        return consumed;
    }
    public abstract void render(SpriteBatch batch);
}

