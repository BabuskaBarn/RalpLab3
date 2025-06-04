package at.fhv.sysarch.lab3.pipeline.filters.sink;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.filters.PushFilter;

import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import at.fhv.sysarch.lab3.rendering.RenderingMode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer implements PushFilter<Pair<Face, Color>, Void> {

    private final GraphicsContext gc;
    private final RenderingMode renderingMode;

    public Renderer(GraphicsContext gc, RenderingMode renderingMode) {
        this.gc = gc;
        this.renderingMode = renderingMode;
    }



    @Override
    public void setNext(Pipe<Void> next) {
        //does nothing

    }

    @Override
    public void push(Pair<Face, javafx.scene.paint.Color> input) {
        render(input);
    }

    private void render(Pair<Face, javafx.scene.paint.Color> pair) {
        Face face = pair.fst();
        javafx.scene.paint.Color color = pair.snd();
        gc.setStroke(color);
        gc.setFill(color);

        switch (renderingMode) {
            case POINT -> {
                gc.fillOval(face.getV1().getX(), face.getV1().getY(), 1, 1);
                gc.fillOval(face.getV2().getX(), face.getV2().getY(), 1, 1);
                gc.fillOval(face.getV3().getX(), face.getV3().getY(), 1, 1);
            }
            case WIREFRAME -> {
                gc.strokeLine(face.getV1().getX(), face.getV1().getY(), face.getV2().getX(), face.getV2().getY());
                gc.strokeLine(face.getV2().getX(), face.getV2().getY(), face.getV3().getX(), face.getV3().getY());
                gc.strokeLine(face.getV3().getX(), face.getV3().getY(), face.getV1().getX(), face.getV1().getY());
            }
            case FILLED -> {
                double[] x = {face.getV1().getX(), face.getV2().getX(), face.getV3().getX()};
                double[] y = {face.getV1().getY(), face.getV2().getY(), face.getV3().getY()};
                gc.fillPolygon(x, y, 3);
            }
        }
    }
}
