package notable.gui;

import java.io.IOException;

import notable.core.impl.TagManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private static final Logger LOG = LogManager.getLogger(Main.class);

	@Override
	public void start(Stage stage) throws IOException {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(
					"FXML_Interface.fxml"));
			Scene scene = new Scene(root);
			stage.setTitle("Notizverwaltung");
			stage.setScene(scene);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					stage.close();
					LOG.info("Application closed");
				}
			});
			stage.show();

		} catch (NullPointerException e) {
			e.printStackTrace();
			LOG.catching(Level.FATAL, e);
			LOG.fatal("FXML file could not be found");
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
