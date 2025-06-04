package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec4;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

public class RotationFilter implements PushFilter<List<Face>, List<Face>>, PullFilter<Face, Face> {

    private Pipe<List<Face>> next;
    private Pipe<Face> prev;
    private Mat4 rotationMatrix;

    public void setRotationMatrix(Mat4 rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    @Override
    public void setNext(Pipe<List<Face>> next) {
        this.next = next;
    }

    @Override
    public void setPrevious(Pipe<Face> prev) {
        this.prev = prev;
    }

    @Override
    public void push(List<Face> faces) {
        List<Face> rotatedFaces = new LinkedList<>();
        for (Face f : faces) {
            rotatedFaces.add(applyRotation(f));
        }
        next.push(rotatedFaces);
    }

    @Override
    public Face pull() {
        Face face = prev.pull();
        if (face != null) {
            return applyRotation(face);
        }
        return null;
    }


    private Face applyRotation(Face face) {
        Vec4 newV1 = rotationMatrix.multiply(face.getV1());
        Vec4 newV2 = rotationMatrix.multiply(face.getV2());
        Vec4 newV3 = rotationMatrix.multiply(face.getV3());

        Vec4 newN1 = rotationMatrix.multiply(face.getN1());
        Vec4 newN2 = rotationMatrix.multiply(face.getN2());
        Vec4 newN3 = rotationMatrix.multiply(face.getN3());

        return new Face(newV1, newV2, newV3, newN1, newN2, newN3);
    }

}
