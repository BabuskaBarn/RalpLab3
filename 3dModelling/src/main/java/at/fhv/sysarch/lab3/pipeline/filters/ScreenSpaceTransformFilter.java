package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Mat4;
import javafx.scene.paint.Color;




public class ScreenSpaceTransformFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {

    private final Mat4 viewportTransform;
    private Mat4 projTranform;
    private Pipe<Pair<Face, Color>> next;

    private Pipe<Pair<Face, Color>> prev;
    public ScreenSpaceTransformFilter(Mat4 viewportTransform, Mat4 projTranform) {
        this.projTranform=projTranform;
        this.viewportTransform = viewportTransform;

    }

    @Override
    public void setNext(Pipe<Pair<Face, Color>> next) {
        this.next = next;
    }

    @Override
    public void setPrevious(Pipe<Pair<Face, Color>> prev) {
        this.prev = prev;
    }

    @Override
    public void push(Pair<Face, Color> input) {
       this.next.push(applyViewportTransform(input));
    }

    @Override
    public Pair<Face, Color> pull() {
        Pair<Face, Color> daten = prev.pull();
        if(daten!= null){
            return applyViewportTransform(daten);
        }
        return null;
    }

    private  Pair<Face, Color> applyViewportTransform(Pair<Face, Color> daten) {
        Face face = daten.fst();
        Face dividedFace = new Face(
                face.getV1().multiply((1 / face.getV1().getW())),
                face.getV2().multiply((1 / face.getV2().getW())),
                face.getV3().multiply((1 / face.getV3().getW())),
                face
        );
        /*Jeder Punkt in der Pipeline ist ein Vec4, also ein Vektor mit x, y, z, w.

        Nach der Projektionsmatrix haben die Punkte w != 1 →sie sind in homogenen Koordinaten.
        Teilen durch w für Perspektivteilung
         */

        Face transformedFace = new Face(
                viewportTransform.multiply(dividedFace.getV1()),
                viewportTransform.multiply(dividedFace.getV2()),
                viewportTransform.multiply(dividedFace.getV3()),
                dividedFace
        );

        //verschiebt und skaliert die Punkte vom [-1,1] in Pixelkoordinaten[0,800] × [0,600]


        return new Pair<>(transformedFace, daten.snd());
    }
}
