package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import com.hackoeur.jglm.Mat4;


import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;


public class ProjectionFilter implements PushFilter<Face, Face>, PullFilter<Face, Face> {

    private Pipe<Face> next;
    private Pipe<Face> prev;
    private final Mat4 projectionMatrix;

    public ProjectionFilter(Mat4 projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
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
        Face projected = applyProjection(face);
        if (next != null) {
            next.push(projected);
        }
    }

    @Override
    public Face pull() {
        if (prev == null) return null;

        Face face = prev.pull();
        if (face == null) return null;

        return applyProjection(face);
    }

    private Face applyProjection(Face face) {
        return new Face(
                projectionMatrix.multiply(face.getV1()),
                projectionMatrix.multiply(face.getV2()),
                projectionMatrix.multiply(face.getV3()),
                new Face(face.getN1(), face.getN2(), face.getN3(), null)
        );
    }
}
