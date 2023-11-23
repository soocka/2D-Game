package fr.reworked.diablo;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import fr.reworked.DouchkaVania.GameMenu;

import com.badlogic.gdx.Game;

// DesktopLauncher
public class DesktopLauncher {
    

    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Diablo remake rebuild");
        // Mettre le jeu en plein écran
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        new Lwjgl3Application(new Game() {
            @Override
            public void create() {
                setScreen(new GameMenu(this));
            }
        }, config);
    }
}

