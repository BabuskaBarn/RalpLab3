package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import com.hackoeur.jglm.Vec4;


import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;


public class PerspectiveDivisionFilter implements PushFilter<Face, Face>, PullFilter<Face, Face> {

    private Pipe<Face> next;
    private Pipe<Face> prev;

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
        Face divided = applyDivision(face);
        if (next != null) {
            next.push(divided);
        }
    }

    @Override
    public Face pull() {
        if (prev == null) return null;

        Face face = prev.pull();
        if (face == null) return null;

        return applyDivision(face);
    }

    private Face applyDivision(Face face) {
        return new Face(
                divide(face.getV1()),
                divide(face.getV2()),
                divide(face.getV3()),
                new Face(face.getN1(), face.getN2(), face.getN3(), null) // Normalen werden nicht geteilt!
        );
    }

    private Vec4 divide(Vec4 vec) {
        float w = vec.getW();
        if (w == 0f) return vec; // Sicherheitscheck
        return new Vec4(vec.getX() / w, vec.getY() / w, vec.getZ() / w, 1f);
    }
}



