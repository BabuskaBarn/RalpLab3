package at.fhv.sysarch.lab3.pipeline.filters.sink;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.filters.PushFilter;
import at.fhv.sysarch.lab3.pipeline.filters.PullFilter;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import at.fhv.sysarch.lab3.rendering.RenderingMode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer implements PushFilter<Pair<Face, Color>, Void>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {

    private final GraphicsContext gpc;
    private final RenderingMode renderingMode;
    private Pipe<Pair<Face, Color>> previous;

    public Renderer(GraphicsContext gpc, RenderingMode renderingMode) {
        this.gpc = gpc;
        this.renderingMode = renderingMode;
    }

    // --- PushFilter Interface ---
    @Override
    public void setNext(Pipe<Void> next) {
        // intentionally left empty
    }

    @Override
    public void push(Pair<Face, Color> input) {
        render(input);
    }

    // --- PullFilter Interface ---
    @Override
    public void setPrevious(Pipe<Pair<Face, Color>> prev) {
        this.previous = prev;
    }

    @Override
    public Pair<Face, Color> pull() {
        boolean notEmpty = true;
        while (notEmpty) {
            Pair<Face, Color> render = previous.pull();
            if (render != null) {
                render(render);

            }else {
                notEmpty = false;
            }
        }
        return null;
    }


    // --- Render-Methode ---
    private void render(Pair<Face, Color> render) {
        gpc.setStroke(render.snd());
        int width = 1;
        int height = 1;
        Face face = render.fst();
        if(this.renderingMode == RenderingMode.POINT) {
            gpc.setFill(render.snd());
            gpc.fillOval(face.getV1().getX(), face.getV1().getY(), width, height);
            gpc.fillOval(face.getV2().getX(), face.getV2().getY(), width, height);
            gpc.fillOval(face.getV3().getX(), face.getV3().getY(), width, height);
        } else if(this.renderingMode == RenderingMode.WIREFRAME) {
            gpc.strokeLine(face.getV1().getX(), face.getV1().getY(), face.getV2().getX(), face.getV2().getY());
            gpc.strokeLine(face.getV2().getX(), face.getV2().getY(), face.getV3().getX(), face.getV3().getY());
            gpc.strokeLine(face.getV1().getX(), face.getV1().getY(), face.getV3().getX(), face.getV3().getY());
        } else if(this.renderingMode == RenderingMode.FILLED) {
            double[] xPoints = {face.getV1().getX(), face.getV2().getX(), face.getV3().getX()};
            double[] yPoints = {face.getV1().getY(), face.getV2().getY(), face.getV3().getY()};
            gpc.setFill(render.snd());
            gpc.fillPolygon(xPoints, yPoints, 3);
        }
    }
}



