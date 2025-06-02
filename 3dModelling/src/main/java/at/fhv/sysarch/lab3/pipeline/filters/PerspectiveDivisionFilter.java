package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import com.hackoeur.jglm.Vec4;

public class PerspectiveDivisionFilter implements PushFilter{
    private PushFilter next;

    public PerspectiveDivisionFilter(PushFilter next) {
        this.next = next;
    }
    @Override
    public void push(Face face) {
        Face divided= new Face(
                divide(face.getV1()),
                divide(face.getV2()),
                divide(face.getV3()),
                new Face(face.getN1(), face.getN2(), face.getN3(), null)
        );
                next.push(divided);


    }
    private Vec4 divide(Vec4 vec) {
        float w = vec.getW();
        if (w == 0) {
            // Sicherheitscheck, um Division durch 0 zu vermeiden
            return vec;
        }
        return new Vec4(vec.getX() / w, vec.getY() / w, vec.getZ() / w, 1f);
    }
}
