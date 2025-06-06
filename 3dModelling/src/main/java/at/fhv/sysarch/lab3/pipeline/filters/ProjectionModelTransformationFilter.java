package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Mat4;
import javafx.scene.paint.Color;



public class ProjectionModelTransformationFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {


    private Pipe<Pair<Face, Color>> next;


    private Mat4 projModelTranform;


    private Pipe<Pair<Face, Color>> prev;


    public ProjectionModelTransformationFilter(Mat4 projModelTranform ){
        this.projModelTranform = projModelTranform;
    }


    @Override
    public void setNext(Pipe<Pair<Face, Color>> next) {
        this.next = next;
    }

    // Wenn ein neues Dreieck hereingeschoben wird, wird es transformiert und weitergeschickt
    @Override
    public void push(Pair<Face, Color> input) {
        next.push(transformium(input));  // "transformium" macht die Matrixrechnung
    }

    // jedes Eck des Dreiecks wird mit der Matrix multipliziert
    private Pair<Face, Color> transformium(Pair<Face, Color> data) {
        Face face = data.fst();
        Face projectedFace = new Face(
                projModelTranform.multiply(face.getV1()),
                projModelTranform.multiply(face.getV2()),
                projModelTranform.multiply(face.getV3()),
                face  // das Original wird als Referenz mitgegeben
        );


        return new Pair<>(projectedFace, data.snd());
    }


    @Override
    public void setPrevious(Pipe<Pair<Face, Color>> prev) {
        this.prev = prev;
    }

    // wird vom Vorgänger gezogen und transformiert und zurückgegeben
    @Override
    public Pair<Face, Color> pull() {
        Pair<Face, Color> data = prev.pull();
        if(data != null) {
            return transformium(data);
        }
        return null;
    }


    //im raum richtig positionieren und perspektivisch transformieren dh wo sich der objekt befindet und perspektive sachen werden kleiner desto weiter sie sind
}
