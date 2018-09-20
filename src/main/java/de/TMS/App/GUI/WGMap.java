package de.TMS.App.GUI;

import de.TMS.App.GUI.Models.ZoomSP;
import de.TMS.App.Utilities.MarthalUtilities;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class WGMap extends Application {

	private Button back = new Button("Back");
	private ImageView imageView = new ImageView();
	private ListView<String> categories = new ListView<String>();
	private ObservableList<String> allCategories = FXCollections.observableArrayList();
	private Image[] listOfImages = { new Image("file:.\\\\images\\tank.png") };
	private double orgSceneX, orgSceneY;
	private double orgTranslateX, orgTranslateY;
	private double lastX, lastY;

	public void setMap(String map) {
		imageView.setImage(new Image("file:.\\images\\" + map + ".jpg"));
		this.imageView.setFitHeight(1000);
		this.imageView.setFitWidth(1000);
	}

	public void setZoomableScrollPane(ZoomSP zoomableScrollPane) {
		// this.zoomableScrollPane = zoomableScrollPane;
		this.imageView = (ImageView) zoomableScrollPane.getNode();
		this.imageView.setFitHeight(1000);
		this.imageView.setFitWidth(1000);
	}

	EventHandler<ScrollEvent> changeScale = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {
			Node node = (Node) event.getSource();
			double deltaY = event.getDeltaY();

			if (deltaY < 0 && node.getScaleX() > 1 && node.getScaleY() > 1) {

				node.setScaleX(node.getScaleX() - 1);
				node.setScaleY(node.getScaleY() - 1);
			} else if (deltaY > 0 && node.getScaleX() >= 1 && node.getScaleY() >= 1
					&& !(node.getScaleX() >= 5 && node.getScaleY() >= 5)) {

				node.setScaleX(node.getScaleX() + 1);
				node.setScaleY(node.getScaleY() + 1);
			}
		}
	};

	EventHandler<MouseEvent> imageOnMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			orgTranslateX = ((ImageView) (t.getSource())).getTranslateX();
			orgTranslateY = ((ImageView) (t.getSource())).getTranslateY();
		}
	};
	EventHandler<MouseEvent> imageOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			double offsetX = t.getSceneX() - orgSceneX;
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateX = orgTranslateX + offsetX;
			double newTranslateY = orgTranslateY + offsetY;

			((ImageView) (t.getSource())).setTranslateX(newTranslateX);
			((ImageView) (t.getSource())).setTranslateY(newTranslateY);
		}
	};

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialisation
		BorderPane root = new BorderPane();
		Group group = new Group();
		// Object settings
		switchToMainScreen(primaryStage);
		categories.setItems(allCategories);
		fillObservableList(allCategories);
		listViewIcons();

		group.getChildren().add(imageView);
		icon(group);
		// zoomSP = new ZoomSP(stackPane, false);

		ScrollPane s = (ScrollPane) createZoomPane(group);
		s.autosize();
		s.setVmax(0.5);
		root.setCenter(s);

		// Add to objects
		back.setAlignment(Pos.TOP_LEFT);
		root.setTop(back);
		root.setLeft(categories);

		primaryStage.setScene(new Scene(root, 800, 800));
		primaryStage.show();

	}

	private void addIcon(ImageView imageView, Group group) {
		this.categories.setOnMouseClicked(e -> {
			ImageView icon = new ImageView();
			group.getChildren().add(icon);
			icon.setLayoutX(e.getX());
			icon.setLayoutY(e.getY());
		});
	}

	/**
	 * @param group
	 */
	private void icon(Group group) {
		imageView.setOnDragExited(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasImage()) {
					ImageView imageView = new ImageView(db.getImage());
					imageView.setOnMousePressed(imageOnMousePressedEventHandler);
					imageView.setOnMouseDragged(imageOnMouseDraggedEventHandler);
					// imageView.setOnScroll(changeScale);
					group.getChildren().add(imageView);
					// imageView.setLayoutX(event.getX());
					// imageView.setLayoutY(event.getY());

				}
			}
		});
	}

	/**
	 * 
	 */
	private void listViewIcons() {
		categories.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				ListCell<String> listCell = new ListCell<String>() {
					private ImageView iV = new ImageView();

					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);

						if (empty) {
							setText(null);
							setGraphic(null);
						} else if (item.equals("Logistic Helicopter")) {
							iV.setImage(listOfImages[0]);
							setGraphic(iV);
						} else {
							setGraphic(null);
						}
						setText(item);
					}
				};

				listCell.setOnDragDetected((MouseEvent event) -> {
					System.out.println("listcell setOnDragDetected");
					Dragboard db = listCell.startDragAndDrop(TransferMode.ANY);
					ClipboardContent content = new ClipboardContent();
					if (listCell.getGraphic() != null) {
						Image imageToDrag = ((ImageView) listCell.getGraphic()).getImage();
						if (imageToDrag != null) {
							content.putImage(imageToDrag);
							db.setContent(content);
						}
					}
					event.consume();
				});
				return listCell;
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

	private Parent createZoomPane(Group group) {
		final double SCALE_DELTA = 1.1;
		final StackPane zoomPane = new StackPane();

		zoomPane.getChildren().add(group);

		final ScrollPane scroller = new ScrollPane();
		final Group scrollContent = new Group(zoomPane);
		scroller.setContent(scrollContent);

		scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
			}
		});

		scroller.setPrefViewportWidth(256);
		scroller.setPrefViewportHeight(256);
		zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				event.consume();

				if (event.getDeltaY() == 0) {
					return;
				}

				double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

				// amount of scrolling in each direction in scrollContent coordinate
				// units
				Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

				group.setScaleX(group.getScaleX() * scaleFactor);
				group.setScaleY(group.getScaleY() * scaleFactor);
				System.out.println(group.getScaleX() + " " + group.getScaleY());
				// move viewport so that old center remains in the center after the
				// scaling
				repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

			}
		});

		// Panning via drag....
		final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
		scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
			}
		});

		scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					double deltaX = event.getX() - lastMouseCoordinates.get().getX();
					double extraWidth = scrollContent.getLayoutBounds().getWidth()
							- scroller.getViewportBounds().getWidth();
					double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
					double desiredH = scroller.getHvalue() - deltaH;
					scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

					double deltaY = event.getY() - lastMouseCoordinates.get().getY();
					double extraHeight = scrollContent.getLayoutBounds().getHeight()
							- scroller.getViewportBounds().getHeight();
					double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
					double desiredV = scroller.getVvalue() - deltaV;
					scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
				}
			}
		});

		return scroller;
	}

	private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
		double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
		double hScrollProportion = (scroller.getHvalue() - scroller.getHmin())
				/ (scroller.getHmax() - scroller.getHmin());
		double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
		double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
		double vScrollProportion = (scroller.getVvalue() - scroller.getVmin())
				/ (scroller.getVmax() - scroller.getVmin());
		double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
		return new Point2D(scrollXOffset, scrollYOffset);
	}

	private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
		double scrollXOffset = scrollOffset.getX();
		double scrollYOffset = scrollOffset.getY();
		double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
		if (extraWidth > 0) {
			double halfWidth = scroller.getViewportBounds().getWidth() / 2;
			double newScrollXOffset = (scaleFactor - 1) * halfWidth + scaleFactor * scrollXOffset;
			scroller.setHvalue(
					scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
		} else {
			scroller.setHvalue(scroller.getHmin());
		}
		double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
		if (extraHeight > 0) {
			double halfHeight = scroller.getViewportBounds().getHeight() / 2;
			double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
			scroller.setVvalue(
					scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
		} else {
			scroller.setHvalue(scroller.getHmin());
		}
	}
}