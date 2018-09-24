package de.TMS.App.GUI;

import de.TMS.App.GUI.Models.ZoomSP;
import de.TMS.App.Utilities.MarthalUtilities;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WGMapSelection extends Application {

	private String map = "(1v1) Mud Fight";
	private ImageView imageView = new ImageView(new Image("file:.\\images\\" + map + ".jpg"));
	private ListView<String> mapList = new ListView<String>();
	private ObservableList<String> allMaps = FXCollections.observableArrayList();
	private Button select = new Button("Select");
	private ZoomSP zoomableScrollPane = new ZoomSP(imageView, true);

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialisation
		BorderPane root = new BorderPane();
		fillObservableList();

		// Object settings
		mapList.setItems(allMaps);
		select.setMinSize(100, 25);
		imageViewSettings();
		switchToMapScreen(primaryStage);

		// Add to objects
		listViewEventHandler();
		root.setLeft(mapList);
		root.setCenter(zoomableScrollPane);
		root.setRight(select);
		// Window settings
		Scene scene = new Scene(root, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * 
	 */
	private void fillObservableList() {
		for (String map : MarthalUtilities.readFile(".\\maps.txt", "::")) {
			allMaps.add(map);
		}
	}

	/**
	 * @param primaryStage
	 */
	private void switchToMapScreen(Stage primaryStage) {
		select.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				WGMap mapScreen = new WGMap();
				//mapScreen.setMap(map);
				mapScreen.setMap(map);
				try {
					mapScreen.start(primaryStage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 
	 */
	private void imageViewSettings() {
		imageView.preserveRatioProperty().set(true);
		imageView.setFitHeight(200);
		imageView.setFitWidth(200);
	}

	private void listViewEventHandler() {
		mapList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				map = mapList.getSelectionModel().getSelectedItem();
				imageView.setImage(new Image("file:.\\images\\" + map + ".jpg"));

			}

		});
	}
}