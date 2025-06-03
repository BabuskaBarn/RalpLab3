package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Mat4;

public class ScreenSpaceTransformFilter implements PushFilter<Face, Face>, PullFilter<Face, Face> {

    private final Mat4 viewportTransform;
    private Pipe<Face> next;
    private Pipe<Face> prev;

    public ScreenSpaceTransformFilter(Mat4 viewportTransform) {
        this.viewportTransform = viewportTransform;
    }

    @Override
    public void setNext(Pipe<Face> next) {
        this.next = next;
    }

    @Override
    public void setPrevious(Pipe<Face> prev) {
        this.prev = prev;
    }

    @Override
    public void push(Face face) {
        Face screenSpace = applyViewportTransform(face);
        if (next != null) {
            next.push(screenSpace);
        }
    }

    @Override
    public Face pull() {
        if (prev == null) return null;

        Face face = prev.pull();
        if (face == null) return null;

        return applyViewportTransform(face);
    }

    private Face applyViewportTransform(Face face) {
        return new Face(
                viewportTransform.multiply(face.getV1()),
                viewportTransform.multiply(face.getV2()),
                viewportTransform.multiply(face.getV3()),
                new Face(face.getN1(), face.getN2(), face.getN3(), null)
        );
    }
}
