package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Mat4;
import javafx.scene.paint.Color;


// Der Filter kann sowohl Daten "pushen" (weitergeben) als auch "pullen" (abholen)
public class ProjectionModelTransformationFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {

    // Die nächste Station in der Pipeline (z. B. der Viewport-Transform)
    private Pipe<Pair<Face, Color>> next;

    // Die Matrix, mit der das Modell transformiert wird (Rotation, Projektion etc.)
    private Mat4 projModelTranform;

    // Die vorherige Station in der Pipeline (woher Daten geholt werden)
    private Pipe<Pair<Face, Color>> prev;

    // Konstruktor: bekommt die Transformationsmatrix
    public ProjectionModelTransformationFilter(Mat4 projModelTranform ){
        this.projModelTranform = projModelTranform;
    }

    // Verbinde diesen Filter mit dem nächsten
    @Override
    public void setNext(Pipe<Pair<Face, Color>> next) {
        this.next = next;
    }

    // Wenn ein neues Dreieck hereingeschoben wird, wird es transformiert und weitergeschickt
    @Override
    public void push(Pair<Face, Color> input) {
        next.push(transformium(input));  // "transformium" macht die Matrixrechnung
    }

    // Die eigentliche Transformation: jedes Eck des Dreiecks wird mit der Matrix multipliziert
    private Pair<Face, Color> transformium(Pair<Face, Color> data) {
        Face face = data.fst();
        Face projectedFace = new Face(
                projModelTranform.multiply(face.getV1()),
                projModelTranform.multiply(face.getV2()),
                projModelTranform.multiply(face.getV3()),
                face  // das Original wird als Referenz mitgegeben
        );

        // Gib ein neues Face mit transformierten Punkten und der Originalfarbe zurück
        return new Pair<>(projectedFace, data.snd());
    }

    // Vorherige Pipeline-Station setzen
    @Override
    public void setPrevious(Pipe<Pair<Face, Color>> prev) {
        this.prev = prev;
    }

    // Wenn jemand Daten anfordert (pull), wird vom Vorgänger gezogen, transformiert und zurückgegeben
    @Override
    public Pair<Face, Color> pull() {
        Pair<Face, Color> data = prev.pull();
        if(data != null) {
            return transformium(data);
        }
        return null;
    }
}
