package de.TMS.App.GUI;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class WGMap extends Application {

	private Button back = new Button("Back");
	private ImageView imageView = new ImageView();
	private double orgSceneX, orgSceneY;
	private double orgTranslateX, orgTranslateY;
	private double mouseX, mouseY, iconW, iconH;
	private List<ImageView> icons = new ArrayList<ImageView>();
	private Map<String, ColorInput> colorInputs = new LinkedHashMap<String, ColorInput>();
	private List<Button> buttons = new ArrayList<Button>();
	double ivWidth, ivHeight;
	private boolean d = false;
	private Color drawColor = Color.RED;
	private Color iconColor = Color.TRANSPARENT;
	private Blend blend = new Blend();
	private ColorAdjust monochrome = new ColorAdjust();
	private double strokeWidth = 10;

	public void setMap(String map) {
		imageView.setImage(new Image("file:.\\images\\" + map + ".jpg"));
		ivWidth = 1920;
		ivHeight = 1080;
		this.imageView.setFitHeight(ivHeight);
		this.imageView.setFitWidth(ivWidth);
		;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialisation
		BorderPane root = new BorderPane();
		Group group = new Group();
		iconW = 32;
		iconH = 20;
		// Object settings
		switchToMainScreen(primaryStage);

		group.getChildren().add(imageView);
		addIconToMap(group);
		// zoomSP = new ZoomSP(stackPane, false);

		ScrollPane s = (ScrollPane) createZoomPane(group);
		s.autosize();
		s.setVmax(0.5);
		context(group, imageView);
		
		Pane pane = new Pane();
		pane.getChildren().add(s);
		int rows = 5;
		int columns = 5;
		GridPane grid = new GridPane();
		for (int i = 0; i < columns; i++) {
			ColumnConstraints column = new ColumnConstraints(40);
			grid.getColumnConstraints().add(column);
		}

		for (int i = 0; i < rows; i++) {
			RowConstraints row = new RowConstraints(40);
			grid.getRowConstraints().add(row);
		}
		grid.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");
		Pane pane2 = new Pane();
		pane.getChildren().add(grid);
		Pane work = new Pane();
		work.getChildren().addAll(pane, pane2);
		pane2.toFront();
		root.setCenter(work);

		// Add to objects
		VBox toolBox = new VBox(5);
		Button eraser = new Button("Eraser");
		eraser.setId("eraser");
		Button increaseSize = new Button("Increase Size");
		Button decreaseSize = new Button("Decrease Size");
		increaseSizeOfIcon(increaseSize);
		decreaseSizeOfIcon(decreaseSize);
		drawLine(group);

		toolBox.getChildren().add(increaseSize);
		toolBox.getChildren().add(decreaseSize);
		toolBox.getChildren().add(eraser);
		for (Node button : toolBox.getChildren()) {
			if (button instanceof Button) {
				buttons.add((Button) button);
			}
		}
		buttonSettings();
		toolBox.setAlignment(Pos.TOP_LEFT);
		root.setLeft(toolBox);

		HBox topBox = new HBox(5);
		topBox.getChildren().add(back);

		createBar(topBox);
		root.setTop(topBox);
		
		Scene scene = new Scene(root, 800, 800);

		setKeyForEraser(eraser, scene, KeyCode.D);

		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setAlwaysOnTop(true);

		primaryStage.show();

	}

	private void createBar(HBox root) {
		MenuBar mBar = new MenuBar();
		Menu data = new Menu("Data");
		Menu drawSettings = new Menu("Draw Settings");
		Menu colors = new Menu("Color");
		MenuItem red = new MenuItem("Red");
		red.setId("red");
		MenuItem blue = new MenuItem("Blue");
		blue.setId("blue");
		MenuItem white = new MenuItem("White");
		white.setId("white");
		MenuItem black = new MenuItem("Black");
		black.setId("black");
		colors.getItems().addAll(red, blue, white, black);
		Menu size = new Menu("Size");
		Menu icon = new Menu("Icon Settings");
		MenuItem iconRed = new MenuItem("Red");
		iconRed.setId("red");
		MenuItem iconBlue = new MenuItem("Blue");
		iconBlue.setId("blue");
		MenuItem iconWhite = new MenuItem("White");
		iconWhite.setId("white");

		icon.getItems().addAll(iconRed, iconBlue, iconWhite);

		changeIconColor(icon);

		setSizes(size);
		changeSize(size);

		drawSettings.getItems().addAll(colors, size);
		colorConfig(colors);

		mBar.getMenus().addAll(data, drawSettings, icon);

		root.getChildren().add(mBar);
		System.out.println();
	}

	private void changeIconColor(Menu icon) {
		for (MenuItem m : icon.getItems()) {
			changeIconColor(m);
		}
	}

	private void changeIconColor(MenuItem col) {
		col.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				MenuItem item = (MenuItem) event.getSource();
				iconColor = Color.web(item.getId());
				monochrome.setSaturation(-1.0);
			}
		});
	}

	private void blendSettings(ImageView imageView, ColorInput cI) {
		cI.setWidth(imageView.getFitWidth());
		cI.setHeight(imageView.getFitHeight());
		cI.setX(imageView.getX());
		cI.setY(imageView.getY());
		blend = new Blend(BlendMode.MULTIPLY, monochrome, cI);

	}

	private void changeSize(Menu size) {
		for (MenuItem menu : size.getItems()) {
			Integer number = Integer.valueOf(menu.getId());
			menu.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					strokeWidth = number;
				}
			});
		}
	}

	private void colorConfig(Menu colors) {
		for (MenuItem menu : colors.getItems()) {
			changeColor(menu);
		}
	}

	private void setSizes(Menu size) {
		for (int i = 1; i < 25; i++) {
			if (i % 2 == 0) {
				MenuItem m = new MenuItem("" + i);
				m.setId("" + i);
				size.getItems().add(m);
			}
		}
	}

	private void changeColor(MenuItem red) {
		red.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				MenuItem item = (MenuItem) event.getSource();
				drawColor = Color.web(item.getId());
			}
		});
	}

	private void buttonSettings() {
		for (Button button : buttons) {
			button.setMinWidth(100);
			button.setStyle("-fx-color: lightgrey");
			if (button.getId() == "eraser") {
				button.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						if (!d) {
							d = true;
							button.setStyle("-fx-color: red");
						} else {
							d = false;
							button.setStyle("-fx-color: lightgrey");
						}
					}
				});
			}

		}
	}

	private void setKeyForEraser(Button eraser, Scene scene, KeyCode key) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (!d && event.getCode() == key) {
					System.out.println("T");
					eraser.setStyle("-fx-color: red");
					d = true;
				} else if (d && event.getCode() == key) {
					eraser.setStyle("-fx-color: lightgrey");
					d = false;
				}
			}
		});
	}

	private void drawLine(Group group) {
		imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent me) {
				// System.out.println("Dragged, x:" + me.getSceneX() + " y:" + me.getSceneY());
				double mX = me.getX();
				double mY = me.getY();

				if (me.isAltDown() && me.isPrimaryButtonDown() && mX < 1920 && mX > 1 && mY < 1080 && mY > 1) {
					Line line = new Line(mouseX, mouseY, me.getX(), me.getY());
					line.setFill(null);
					line.setStroke(drawColor);
					line.setStrokeWidth(strokeWidth);
					line.setOnMouseMoved(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent event) {
							if (event.isAltDown() && d) {
								line.setVisible(false);
							}
						}
					});
					line.setOnDragEntered(new EventHandler<DragEvent>() {

						@Override
						public void handle(DragEvent event) {
							line.setVisible(false);
						}
					});
					group.getChildren().add(line);
					mouseX = me.getX();
					mouseY = me.getY();
				}
				// mouseX = me.getScreenX() > mouseX ? mouseX : me.getScreenX();
				// mouseY = me.getScreenY() > mouseY ? mouseY : me.getScreenY();
			}
		});
	}

	private void increaseSizeOfIcon(Button increaseSize) {
		increaseSize.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (iconW < 128) {
					iconW = iconW * 2;
					iconH = iconH * 2;
					for (ImageView icon : icons) {
						icon.setFitHeight(iconH);
						icon.setFitWidth(iconW);
						if (colorInputs.containsKey(icon.getId())) {
							blendSettings(icon, colorInputs.get(icon.getId()));
							setEffectOfIcon(icon, true);
							setEffectOfIcon(icon, false);
						}
					}
				}

			}
		});
	}

	private void decreaseSizeOfIcon(Button decreaseSize) {
		decreaseSize.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (iconW > 8) {
					iconW = iconW / 2;
					iconH = iconH / 2;
					for (ImageView icon : icons) {
						icon.setFitHeight(iconH);
						icon.setFitWidth(iconW);
						if (colorInputs.containsKey(icon.getId())) {
							blendSettings(icon, colorInputs.get(icon.getId()));
							setEffectOfIcon(icon, true);
							setEffectOfIcon(icon, false);
						}
					}
				}

			}
		});
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
			if (!t.isPrimaryButtonDown()) {
				orgSceneX = t.getSceneX();
				orgSceneY = t.getSceneY();
				orgTranslateX = ((ImageView) (t.getSource())).getTranslateX();
				orgTranslateY = ((ImageView) (t.getSource())).getTranslateY();

			}
		}
	};
	EventHandler<MouseEvent> imageOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			if (!t.isPrimaryButtonDown()) {
				double offsetX = t.getSceneX() - orgSceneX;
				double offsetY = t.getSceneY() - orgSceneY;
				double newTranslateX = orgTranslateX + offsetX;
				double newTranslateY = orgTranslateY + offsetY;

				((ImageView) (t.getSource())).setTranslateX(newTranslateX);
				((ImageView) (t.getSource())).setTranslateY(newTranslateY);
				Node node = ((Node) t.getSource());
				node.setCursor(Cursor.DISAPPEAR);
			}
		}
	};

	// Right-Click Menu
	private void context(Group group, ImageView imageView) {

		final ContextMenu contextMenu = new ContextMenu();

		Menu log = new Menu("Logistic");

		Menu cu = new Menu("Command Unit");

		MenuItem cInf = new MenuItem("Command Infantry");
		cInf.setId("Command Infantry");
		MenuItem cVeh = new MenuItem("Light Command Vehicle");
		cVeh.setId("Command Light Vehicle");
		MenuItem cArmored = new MenuItem("Armored Command Vehicle");
		cArmored.setId("Command Armored Vehicle");
		MenuItem cHeli = new MenuItem("Command Helicopter");
		cHeli.setId("Command Helicopter");
		cu.getItems().addAll(cInf, cVeh, cArmored, cHeli);

		addImages(cu, group);
		Menu supply = new Menu("Supply");

		MenuItem supplyTruck = new MenuItem("Supply Truck");
		supplyTruck.setId("Supply Truck");
		MenuItem supplyHeli = new MenuItem("Supply Helicopter");
		supplyHeli.setId("Supply Helicopter");
		supply.getItems().addAll(supplyTruck, supplyHeli);

		addImages(supply, group);

		MenuItem fob = new MenuItem("Forward Operating Base");
		fob.setId("Forward Operating Base");

		log.getItems().addAll(cu, supply, fob);

		Menu infantry = new Menu("Infantry");
		MenuItem infRifle = new MenuItem("Rifle Squad");
		infRifle.setId("Infantry (Riflemen)");
		MenuItem infSF = new MenuItem("Special Forces");
		infSF.setId("Infantry (SF)");
		MenuItem infAA = new MenuItem("Anti-Air Infantry");
		infAA.setId("Infantry (AA)");
		MenuItem infAT = new MenuItem("Anti-Tank Infantry");
		infAT.setId("Infantry (AT)");
		MenuItem infEngi = new MenuItem("Infantry (Engineers)");
		infEngi.setId("Infantry (E)");
		infantry.getItems().addAll(infRifle, infSF, infAA, infAT, infEngi);
		addImages(infantry, group);

		Menu tanks = new Menu("Tanks");
		MenuItem tank = new MenuItem("Tank");
		tank.setId("Tank");
		MenuItem fTank = new MenuItem("Flamethrower Tank");
		fTank.setId("Tank (F)");
		tanks.getItems().addAll(tank, fTank);
		addImages(tanks, group);

		Menu support = new Menu("Support");
		MenuItem aav = new MenuItem("Anti-Air Vehicle");
		aav.setId("Anti-Air Vehicle");
		MenuItem aavr = new MenuItem("Anti-Air Vehicle (Radar)");
		aavr.setId("Anti-Air Vehicle (Radar)");
		MenuItem sam = new MenuItem("SAM");
		sam.setId("SAM");
		MenuItem mortar = new MenuItem("Mortar");
		mortar.setId("Mortar");
		MenuItem artillery = new MenuItem("Artillery");
		artillery.setId("Artillery");
		support.getItems().addAll(aav, aavr, sam, mortar, artillery);
		addImages(support, group);

		Menu rRecon = new Menu("Recon");
		MenuItem recon = new MenuItem("Recon");
		recon.setId("Recon");
		MenuItem reconArmored = new MenuItem("Recon (Armored)");
		reconArmored.setId("Recon Armored");
		MenuItem reconHeli = new MenuItem("Recon (Helicopter)");
		reconHeli.setId("Recon Helicopter");
		rRecon.getItems().addAll(recon, reconHeli);
		addImages(rRecon, group);

		Menu vehicles = new Menu("Vehicles");
		MenuItem tankDest = new MenuItem("Tank-Destroyer");
		tankDest.setId("Tank-Destroyer");
		MenuItem apc = new MenuItem("Armored Personal Carrier (APC)");
		apc.setId("APC");
		MenuItem ifv = new MenuItem("Infantry Fighting Vehicle (IFV)");
		ifv.setId("IFV");
		MenuItem truck = new MenuItem("Truck");
		truck.setId("Truck");
		vehicles.getItems().addAll(tankDest, apc, ifv, truck);
		addImages(vehicles, group);

		Menu helicopters = new Menu("Helicopters");
		MenuItem transHeli = new MenuItem("Transport Helicopter");
		transHeli.setId("TransportHelicopter");
		MenuItem heli = new MenuItem("Helicopter");
		heli.setId("Attack Helicopter");
		helicopters.getItems().addAll(transHeli, heli);
		addImages(helicopters, group);

		Menu aircrafts = new Menu("Aircraft");
		MenuItem cap = new MenuItem("CAP");
		cap.setId("CAP");
		MenuItem cas = new MenuItem("CAS");
		cas.setId("CAS");
		MenuItem mrca = new MenuItem("MRCA");
		mrca.setId("MRCA");
		MenuItem sead = new MenuItem("SEAD");
		sead.setId("SEAD");
		aircrafts.getItems().addAll(cap, cas, mrca, sead);
		addImages(aircrafts, group);

		contextMenu.getItems().addAll(log, infantry, tanks, support, rRecon, vehicles, helicopters, aircrafts);
		// mX < 1920 && mX > 1 && mY < 1080 && mY > 1
		imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown() && event.getX() < ivWidth && event.getX() > 1
						&& event.getY() < ivHeight && event.getY() > 1) {
					contextMenu.show(imageView, event.getScreenX(), event.getScreenY());
					System.out.println("Width: " + ivWidth + " Height: " + ivHeight);
					event.consume();
				} else {
					contextMenu.hide();
				}
				mouseX = event.getX();
				mouseY = event.getY();
			}
		});
	}

	private Integer iconId = 0;

	private void addImages(Menu context, Group group) {
		for (MenuItem menu : context.getItems()) {
			String id = menu.getId();
			ImageView iv = new ImageView(new Image("file:.\\\\images\\\\natoIcons\\\\" + id + ".png"));
			iv.setFitWidth(iconW);
			iv.setFitHeight(iconH);
			menu.setGraphic(iv);
			menu.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					ImageView icon = new ImageView(new Image("file:.\\\\images\\\\natoIcons\\\\" + id + ".png"));
					icon.setFitWidth(iconW);
					icon.setFitHeight(iconH);
					icon.setOnMousePressed(imageOnMousePressedEventHandler);
					icon.setOnMouseDragged(imageOnMouseDraggedEventHandler);
					icon.setX(mouseX);
					icon.setY(mouseY);
					icon.setId(String.valueOf(iconId));
					iconId++;
					icon.setOnMouseClicked(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent event) {
							if (event.getButton() == MouseButton.MIDDLE) {
								icon.setImage(null);

							}
							if (event.getButton() == MouseButton.PRIMARY) {
								ColorInput cI = new ColorInput(icon.getX(), icon.getY(), icon.getFitWidth(),
										icon.getFitHeight(), iconColor);
								colorInputs.put(icon.getId(), cI);
								blendSettings(icon, cI);
								setEffectOfIcon(icon, false);
								System.out.println(
										"IconID: " + icon.getId() + " " + colorInputs.get(icon.getId()).toString());

							}
						}
					});

					icons.add(icon);
					group.getChildren().add(icon);
				}

			});
		}
	}

	private void setEffectOfIcon(ImageView icon, boolean clear) {
		if (!clear) {
			icon.effectProperty()
					.bind(Bindings.when(icon.visibleProperty()).then((Effect) blend).otherwise((Effect) null));
		} else {
			icon.effectProperty()
					.bind(Bindings.when(icon.visibleProperty()).then((Effect) null).otherwise((Effect) null));
		}

	}

	/**
	 * @param group
	 */
	private void addIconToMap(Group group) {

		// ContextMenu contextMenu = new ContextMenu();

		imageView.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				mouseX = event.getX();
				mouseY = event.getY();

			}
		});
		imageView.setOnDragExited(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasImage()) {
					ImageView imageView = new ImageView(db.getImage());
					imageView.setOnMousePressed(imageOnMousePressedEventHandler);
					imageView.setOnMouseDragged(imageOnMouseDraggedEventHandler);
					imageView.setFitWidth(32);
					imageView.setFitHeight(20);

					group.getChildren().add(imageView);

				}
			}
		});
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

				double newScaleX = group.getScaleX() * scaleFactor;
				double newScaleY = group.getScaleY() * scaleFactor;
				/*
				 * if (group.getScaleX() > 1 && group.getScaleX() <= 8.0 ) { newScaleX =
				 * group.getScaleX() * scaleFactor; newScaleY = group.getScaleY() * scaleFactor;
				 * System.out.println(1); } else if (group.getScaleX() > 8.8) { newScaleX =
				 * group.getScaleX(); newScaleY = group.getScaleY(); System.out.println(2); }
				 * else if (group.getScaleX() < 1) { newScaleX = 1; newScaleY = 1;
				 * System.out.println(3); }
				 */
				group.setScaleX(newScaleX);
				group.setScaleY(newScaleY);
				// System.out.println("X " + group.getScaleX() + "Y " + group.getScaleY());
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
				if (event.isPrimaryButtonDown() && !event.isAltDown()) {
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