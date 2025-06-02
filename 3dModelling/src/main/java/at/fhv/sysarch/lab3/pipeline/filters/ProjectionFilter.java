package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import com.hackoeur.jglm.Mat4;

public class ProjectionFilter implements PushFilter{
    private PushFilter next;
    private final Mat4 projectionMatrix;


    public ProjectionFilter(PushFilter next, Mat4 projectionMatrix){
        this.next=next;
        this.projectionMatrix=projectionMatrix;
    }
    @Override
    public void push(Face face) {
        Face projected = new Face(
                projectionMatrix.multiply(face.getV1()),
                projectionMatrix.multiply(face.getV2()),
                projectionMatrix.multiply(face.getV3()),
                new Face(face.getN1(), face.getN2(), face.getN3(), null)




        );
        next.push(projected);

    }
}
