package fr.reworked.DouchkaVania.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameHUD {
    private Stage stage;
    private Label hpLabel, nameLabel;
    private ShapeRenderer shapeRenderer;
    private float healthPercentage = 1;
    private float silverKnightHealthPercentage, goldenKnightHealthPercentage, darkKnightHealthPercentage;
    private final float knightBarWidth = 150;
    private final float knightBarHeight = 15;
    private final float knightBarYOffset = 30; 
    private final float barWidth = 200;
    private final float barHeight = 20;
    private final float barX = 10;
    private float barY;
    private final float radius = 5; 
    private BitmapFont font;
    private float fontScale = 0.5f;

    public GameHUD() {
        stage = new Stage(new ScreenViewport());
        shapeRenderer = new ShapeRenderer();
        
        // on  crée le font
        font = new BitmapFont(Gdx.files.internal("hud/bold-font.fnt"));
        font.getData().setScale(fontScale);
        Table table = new Table();
        table.top();
        table.left();
        table.setFillParent(true);

        // on crée les labels
        hpLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        nameLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));

        // on ajoute les labels à la table
        table.add(nameLabel).padTop(4).padLeft(10).left();
        table.row();
        
        

        // on ajoute la table au stage
        stage.addActor(table);
    }


    

    public void update(int hp,int maxHp, String name,   
        int silverKnightHp, int silverKnightMaxHp,int goldenKnightHp, 
        int goldenKnightMaxHp,int darkKnightHp, int darkKnightMaxHp) {

        nameLabel.setText(name);
        hpLabel.setText(hp + " / " + maxHp);
        healthPercentage = Math.max((float) hp / maxHp, 0);

        silverKnightHealthPercentage = Math.max((float) silverKnightHp / silverKnightMaxHp, 0);
        goldenKnightHealthPercentage = Math.max((float) goldenKnightHp / goldenKnightMaxHp, 0);
        darkKnightHealthPercentage = Math.max((float) darkKnightHp / darkKnightMaxHp, 0);
    
    }

    public void draw() {
        stage.getViewport().apply();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawKnightHealthBar(silverKnightHealthPercentage, Color.WHITE, 0);
        drawKnightHealthBar(goldenKnightHealthPercentage, Color.GOLD, 1);
        drawKnightHealthBar(darkKnightHealthPercentage, Color.DARK_GRAY, 2);
        // On dessine le contour de la barre de vie
        shapeRenderer.setColor(Color.DARK_GRAY);
        drawRoundedRect(shapeRenderer, barX, barY, barWidth, barHeight, radius);

        // On dessine la barre de vie
        shapeRenderer.setColor(Color.RED);
        drawRoundedRect(shapeRenderer, barX, barY, barWidth * healthPercentage, barHeight, radius);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // On draw les labels après le shapeRenderer pour qu'ils soient au dessus
        stage.getBatch().begin();
        font.draw(stage.getBatch(), hpLabel.getText(), barX + 5, barY + barHeight - 5, barWidth, Align.center, false);
        stage.getBatch().end();
    }

    private void drawKnightHealthBar(float healthPercentage, Color color, int index) {
        float y = barY - knightBarYOffset * (index + 1);
        shapeRenderer.setColor(color);
        drawRoundedRect(shapeRenderer, barX, y, knightBarWidth * healthPercentage, knightBarHeight, radius);
    }
    // on dessine un rectangle avec des coins arrondis
    private void drawRoundedRect(ShapeRenderer renderer, float x, float y, float width, float height, float radius) {
        // on  dessine les 4 rectangles
        renderer.rect(x + radius, y, width - 2 * radius, height);

        // on dessine les 4 rectangles
        renderer.rect(x, y + radius, radius, height - 2 * radius);
        renderer.rect(x + width - radius, y + radius, radius, height - 2 * radius);

        // on dessine les 4 cercles
        renderer.arc(x + radius, y + radius, radius, 180, 90);
        renderer.arc(x + width - radius, y + radius, radius, 270, 90);
        renderer.arc(x + width - radius, y + height - radius, radius, 0, 90);
        renderer.arc(x + radius, y + height - radius, radius, 90, 90);
    }

    public Stage getStage() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getCamera().position.set(stage.getCamera().viewportWidth / 2, stage.getCamera().viewportHeight / 2, 0);
        stage.getViewport().update(width, height, true);
        barY = stage.getCamera().viewportHeight - barHeight - 10;
       
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}
