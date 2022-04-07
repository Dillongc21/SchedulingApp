package com.scheduler.client;

import com.scheduler.client.util.Navigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MainApp extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        MainApp.primaryStage = primaryStage;
        MainApp.primaryStage.setTitle("Scheduler");
        Navigator.goToLogin();
    }

    /**
     * Sets the app screen to the given FXML page and size.
     *
     * @param filename  Name of the FXML file to which the screen will be set.
     * @param width     Width of new window.
     * @param height    Height of new window.
     */
    public static void setScreen(String filename, int width, int height, Initializable controller, boolean isPopup) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClassLoader.getSystemResource("view/" + filename));
            loader.setController(controller);
            Parent root = loader.load();

            if (isPopup) {
                Stage popup = new Stage();
                Scene scene = new Scene(root, width, height);
                popup.initOwner(primaryStage);
                popup.setScene(scene);
                centerPopupToParent(popup, primaryStage, (double) width, (double) height);
                popup.showAndWait();
            } else if (primaryStage.isFullScreen() || primaryStage.isMaximized()) {
                primaryStage.getScene().setRoot(root);
            } else {
                Scene scene = new Scene(root, width, height);
                primaryStage.setScene(scene);
                primaryStage.centerOnScreen();
                primaryStage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void centerPopupToParent(Stage popup, Stage parent, double popupWidth, double popupHeight) {
        popup.setX(parent.getX() + (parent.getWidth()/2) - (popupWidth / 2));
        popup.setY(parent.getY() + (parent.getHeight()/2) - (popupHeight / 2));
    }
}
