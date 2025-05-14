package com.enriquealberto;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación que extiende de {@link javafx.application.Application}.
 * Esta clase inicializa la ventana principal y configura el gestor de escenas.
 *
 * @author Enrique
 */
public class App extends Application {

    /**
     * Método principal que inicia la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Método de inicio de la aplicación JavaFX. Configura la ventana principal,
     * inicializa el gestor de escenas y registra todas las escenas disponibles.
     *
     * @param stage El escenario principal proporcionado por JavaFX
     * @throws IOException Si ocurre un error al cargar las escenas
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Establece el título de la ventana
        stage.setTitle("Mazmorras frutales");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResource("/com/enriquealberto/imagenes/icono.png").toExternalForm()));

        // Inicialización del ManagerEscenas
        ManagerEscenas sm = ManagerEscenas.getInstance();
        sm.init(stage);

        // Registrar todas las escenas disponibles (sin cargar CONTENEDOR)
        sm.setScene(EscenaID.PORTADA, "Portada");
        sm.setScene(EscenaID.SELECTION, "selection");
        sm.setScene(EscenaID.CONTENEDOR, "contenedor"); // Solo configurar, no cargar todavía
        sm.setScene(EscenaID.DERROTA, "Derrota");
        sm.setScene(EscenaID.VICTORIA, "Victoria");
        sm.setScene(EscenaID.HISTORIA, "Historia");

        // Cargar la PORTADA primero (pantalla inicial)
        sm.loadScene(EscenaID.PORTADA);
    }
}