package de.TMS.App.GUI;

import de.TMS.App.GUI.Models.ZoomableScrollPane;
import de.TMS.App.Utilities.MarthalUtilities;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WGMap extends Application {

	private Button back = new Button("Back");
	private String map = "";
	private ZoomableScrollPane zoomableScrollPane;
	private ListView<String> categories = new ListView<String>();
	private ObservableList<String> allCategories = FXCollections.observableArrayList();
	private Image[] listOfImages = { new Image("file:.\\\\images\\tank.png") };

	public void setMap(String map) {
		this.map = map;
	}

	public void setZoomableScrollPane(ZoomableScrollPane zoomableScrollPane) {
		this.zoomableScrollPane = zoomableScrollPane;
		ImageView t = (ImageView) zoomableScrollPane.getNode();
		t.setFitHeight(1000);
		t.setFitWidth(1000);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialisation
		BorderPane root = new BorderPane();

		// Object settings
		switchToMainScreen(primaryStage);
		root.setCenter(zoomableScrollPane);
		
		categories.setItems(allCategories);

		listViewIcons();

		fillObservableList(allCategories);

		// Add to objects
		back.setAlignment(Pos.TOP_LEFT);
		root.setTop(back);
		root.setLeft(categories);

		primaryStage.setScene(new Scene(root, 800, 800));
		primaryStage.show();

	}

	/**
	 * 
	 */
	private void listViewIcons() {
		categories.setCellFactory(param -> new ListCell<String>() {
			private ImageView iV = new ImageView();

			@Override
			public void updateItem(String name, boolean empty) {
				super.updateItem(name, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					if (name.equals("Logistic Helicopter")) {
						iV.setImage(listOfImages[0]);
					}
					setText(name);
					setGraphic(iV);
				}
			}
		});
	}

	/**
	 * @param allCategories
	 */
	private void fillObservableList(ObservableList<String> allCategories) {
		for (String category : MarthalUtilities.readFile(".\\category.txt", ",")) {
			allCategories.add(category);
		}
	}

	/**
	 * @param primaryStage
	 */
	private void switchToMainScreen(Stage primaryStage) {
		back.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				WGMapSelection selectionScreen = new WGMapSelection();
				try {
					selectionScreen.start(primaryStage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
