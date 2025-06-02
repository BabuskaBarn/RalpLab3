package at.fhv.sysarch.lab3.pipeline.filters;
import at.fhv.sysarch.lab3.obj.Face;
import com.hackoeur.jglm.Mat4;
public class ModelViewTransformationFilter implements PushFilter{
    private PushFilter next;
    private Mat4 modelViewTransform;

public ModelViewTransformationFilter(PushFilter next){
    this.next=next;
}

public void setModelViewTransform(Mat4 transform){
    this.modelViewTransform=transform;
}

@Override
    public void push(Face face){
    Face transformed = new Face(
            modelViewTransform.multiply(face.getV1()),
            modelViewTransform.multiply(face.getV2()),
            modelViewTransform.multiply(face.getV3()),

            new Face(modelViewTransform.multiply(face.getN1()),
                    modelViewTransform.multiply(face.getN2()),
                    modelViewTransform.multiply(face.getN3()),
                    null
            )
    );

    next.push(transformed);
}
}
