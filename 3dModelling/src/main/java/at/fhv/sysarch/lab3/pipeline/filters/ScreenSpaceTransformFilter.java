package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import com.hackoeur.jglm.Mat4;

public class ScreenSpaceTransformFilter implements PushFilter {
    private final Mat4 viewportTransform;
    private PushFilter next;

    public ScreenSpaceTransformFilter(Mat4 viewportTransform, PushFilter next) {
        this.viewportTransform = viewportTransform;
        this.next = next;
    }

    @Override
    public void push(Face face) {
        Face screenSpace = new Face(
                viewportTransform.multiply(face.getV1()),
                viewportTransform.multiply(face.getV2()),
                viewportTransform.multiply(face.getV3()),
                new Face(face.getN1(), face.getN2(), face.getN3(), null)
        );

        next.push(screenSpace);
    }
}

