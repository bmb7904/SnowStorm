package bernardi.snowStorm;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.tools.jstat.Scale;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Displays a big snowflake against a black sky. When the user clicks in the sky, a small
 * rotating snowflake with a scale transition appears. Clicking on an existing snowflake
 * causes it to be removed.
 *
 * Three controls are provided for manipulating the colors of the small flakes:
 *
 * 1. Reset button: restores the color of every small flake to the default color.
 *
 * 2. Color picker: sets the color of all small flakes to a single chosen color.
 *
 * 3. Random button: sets the color of each small flake to a random bright color.
 *
 * Three radio buttons control the rotation of the large flake.
 *
 * 1. Calm button: stops rotation.
 *
 * 2. Breeze button: clockwise rotation (that is, around the z-axis).
 *
 * 3. Cyclone button: sets the axis of rotation to the line through the origin containing
 * the point (1, 1, 1). This gives the effect of rotation in 3D space.
 *
 * @author Brett Bernardi
 */

public class SnowStorm extends Application {

    private static Pane pane = new Pane();

    // create two FlowPanes with the helper method.
    private static FlowPane fpTOP = createFlowPane();
    private FlowPane fpBOTTOM = createFlowPane();

    // various components that will be added to each flowPane
    private static RadioButton calmRB = new RadioButton("Calm");
    private static RadioButton breezeRB = new RadioButton("Breeze");
    private static RadioButton cycloneRB = new RadioButton("Cyclone");
    // array to hold all of the radio buttons
    private static RadioButton radioButtons[] = {calmRB,breezeRB,cycloneRB};
    private static Button resetButton = new Button("Reset");
    private static Button randomButton = new Button("Random");
    private static ColorPicker colorPicker = new ColorPicker();

    // the large snowflake in the center of the pane
    private static Shape bigSnowflake;
    // the default color of the little snowflakes
    private final static Color defaultLittleSnowflakeColor = Color.ALICEBLUE;
    // Stores the selected or randomly generated color for the little snowflakes
    private static Color selectedLittleSnowflakeColor;

    // the field containing the rotate transition for the big snow flake
    // this will accessed by private helper methods
    private static RotateTransition bigFlakeBreezeTransition;



    @Override
    public void start(Stage primaryStage) {
        // the root of the entire application
        VBox root = new VBox();
        root.setPadding(new Insets(15));
        // sets the size and styles the pane
        String paneStyleString = "-fx-background-color: BLACK;";
        final int sizeOfPane = 800;
        pane.setStyle(paneStyleString);
        pane.setPrefSize(sizeOfPane,sizeOfPane);

        Scene scene = new Scene(root);


        fpBOTTOM.getChildren().addAll(calmRB,breezeRB,cycloneRB);
        colorPicker.setPrefWidth(100);
        fpTOP.getChildren().addAll(resetButton,colorPicker,randomButton);

        // adds all radio buttons to toggleGroup...this makes them mutually exclusive
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(calmRB,breezeRB,cycloneRB);

        bigSnowflake = createSnowflake();

        pane.getChildren().add(bigSnowflake);
        root.getChildren().addAll(fpTOP,pane,fpBOTTOM);
        initMouseHandler();
        initColorButtonHandlers();
        initWindButtonHandlers();

        // Boiler-plate JavaFX application code
        primaryStage.setScene(scene);
        primaryStage.setTitle("Snow Storm");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    /**
     * Returns the large snowflake as a custom Shape object made from the union of four
     * lines of the same length but different rotation. A rotate transition is added to
     * the snowflake before it is returned.
     */
    private static Shape createSnowflake() {
        Line line1 = new Line(20,400,780,400);
        Line line2 = new Line(20,400,780,400);
        Line line3 = new Line(20,400,780,400);
        Line line4 = new Line(20,400,780,400);

        line2.setRotate(45);
        line3.setRotate(-45);
        line4.setRotate(90);

        Shape snowflake = Shape.union(Shape.union(line1,line2),Shape.union(line3,line4));
        snowflake.setStrokeWidth(8);
        snowflake.setStroke(Color.SLATEGRAY);

        // adds the rotate transition to the big snowflake
        bigFlakeBreezeTransition = new RotateTransition(Duration.millis(5000), snowflake);
        bigFlakeBreezeTransition.setCycleCount(Animation.INDEFINITE);

        return snowflake;
    }
    /**
     * Initializes the color button handlers in the top flow pane
     */
    private static void initColorButtonHandlers() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        resetButton.setOnAction(event -> {
            for(Node n:pane.getChildren()) {
                Shape s = (Shape)n;
                // ignore the big snowflake
                if(s!=bigSnowflake) {
                    // set to a random color with opacity = 1.0 (bright)
                    s.setStroke(defaultLittleSnowflakeColor);
                }
            }
            // sets colorpicker back to default color of white
            colorPicker.setValue(Color.WHITE);
        });

        randomButton.setOnAction(event -> {
            for(Node n:pane.getChildren()) {
                Shape s = (Shape)n;
                // ignore the big snowflake
                if(s!=bigSnowflake) {
                    // set to a random color with opacity = 1.0 (bright)
                    s.setStroke(Color.rgb(rand.nextInt(256),rand.nextInt(256),
                            rand.nextInt(256),1.0));
                }
            }
        });

        colorPicker.setOnAction(event -> {
            Color selectedColor = colorPicker.getValue();
            for(Node n:pane.getChildren()) {
                Shape s = (Shape)n;
                // ignore big snowflake
                if(s != bigSnowflake) {
                    // set each little snowflake in the pane to the selected color
                    s.setStroke(selectedColor);
                }
            }
        });
    }

    /**
     * Helper method that sdds fade and scale transitiions to the specified snowflake
     * in the parameter.
     */
    private static void initTransitions(Shape littleSnowFlake) {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(3000),
                littleSnowFlake);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setAutoReverse(false);
        rotateTransition.setByAngle(90);
        rotateTransition.play();

        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(2000), littleSnowFlake);
        scaleTransition.setCycleCount(Animation.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setByX(1.5);
        scaleTransition.setByY(1.5);
        scaleTransition.play();
    }


    /**
     * Initializes the MouseClick Handler for the background Pane
     *
     * I initially had a for each loop to iterate through the list that is return from
     * pane.getChildren(). This resulted in an java.util
     * .ConcurrentModificationException being thrown when I removed a little snowflake
     * by clicking on it. Using a for loop got rid of this problem, so I left it in.
     */
    private static void initMouseHandler() {
        pane.setOnMouseClicked(event -> {
            boolean clickedOnLittleSnowflake = false;
            for (int i = 0; i < pane.getChildren().size(); i++) {
                Shape s = (Shape) pane.getChildren().get(i);
                if(s != bigSnowflake) {
                    Bounds bounds = s.getBoundsInLocal();
                    if(bounds.contains(event.getX(),event.getY())) {
                        pane.getChildren().remove(s);
                        clickedOnLittleSnowflake = true;
                    }
                }
            }
            if(!clickedOnLittleSnowflake) {
                Shape littleSnowFlake = createLittleSnowFlake(event.getX(), event.getY()
                        , defaultLittleSnowflakeColor);
                initTransitions(littleSnowFlake);
                pane.getChildren().add(littleSnowFlake);
            }
        });
    }

    /**
     * Initializes the wind radio button handlers
     */
    private static void initWindButtonHandlers() {
        calmRB.setOnAction(event -> {
            bigFlakeBreezeTransition.stop();
            bigFlakeBreezeTransition.setAxis(new Point3D(0, 0, 1));
            bigFlakeBreezeTransition.play(); // resets to original position
            bigFlakeBreezeTransition.stop();

        });
        breezeRB.setOnAction(event -> {
            bigFlakeBreezeTransition.stop();
            bigFlakeBreezeTransition.setAxis(new Point3D(0, 0, 1));
            bigFlakeBreezeTransition.setAutoReverse(false);
            bigFlakeBreezeTransition.setByAngle(90);
            bigFlakeBreezeTransition.setDuration(Duration.millis(2000));
            bigFlakeBreezeTransition.play();;
        });
        cycloneRB.setOnAction(event -> {
            bigFlakeBreezeTransition.stop();
            bigFlakeBreezeTransition.setAutoReverse(true);
            bigFlakeBreezeTransition.setByAngle(360);
            bigFlakeBreezeTransition.setDuration(Duration.millis(8000));
            bigFlakeBreezeTransition.setAxis(new Point3D(1, 1, 1));
            bigFlakeBreezeTransition.play(); // resets to original position

        });
    }

    /**
     * Returns a FlowPane with the appropriate settings for the application
     */
    private static FlowPane createFlowPane() {
        FlowPane fp = new FlowPane();
        fp.setPadding(new Insets(15,5,10,5));
        fp.setHgap(20);
        fp.setAlignment(Pos.CENTER);
        return fp;
    }



    /**
     * Creates and returns little snowflakes shape of the specified color at the specified
     * (x,y) location. Will have a stroke width of 1.
     */
    private static Shape createLittleSnowFlake(double x, double y, Color c) {
        Line line1 = new Line(x-25,y,x+25,y);
        Line line2 = new Line(x-25,y,x+25,y);
        Line line3 = new Line(x-25,y,x+25,y);
        Line line4 = new Line(x-25,y,x+25,y);

        line1.setRotate(10);
        line2.setRotate(55);
        line3.setRotate(-35);
        line4.setRotate(100);

        Shape littleSnowFlake = Shape.union(Shape.union(line1,line2),Shape.union(line3,
                line4));
        littleSnowFlake.setStrokeWidth(1); // stroke width of 1 is good for little flakes
        littleSnowFlake.setStroke(c);

        return littleSnowFlake;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
