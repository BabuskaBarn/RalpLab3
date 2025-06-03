package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Mat4;

public class ModelViewTransformationFilter implements PushFilter<Face, Face>, PullFilter<Face,Face>{

    private Pipe<Face> next;
    private Pipe<Face> prev;
    private Mat4 modelViewTransform;


    public ModelViewTransformationFilter(Mat4 modelViewTransform){
        this.modelViewTransform=modelViewTransform;

    }

    @Override
    public void setPrevious(Pipe<Face> prev) {
        this.prev=prev;

    }

    @Override
    public Face pull() {
        if (prev== null) return null;

        Face face = prev.pull();
        if(face==null) return null;
        return transform(face);
    }

    @Override
    public void setNext(Pipe<Face> next) {
        this.next=next;

    }

    @Override
    public void push(Face input) {
        Face transformers= transform(input);
        if(next!=null){
            next.push(transformers);
        }

    }

    private Face transform(Face face) {
        return new Face(
                modelViewTransform.multiply(face.getV1()),
                modelViewTransform.multiply(face.getV2()),
                modelViewTransform.multiply(face.getV3()),

                new Face(
                        modelViewTransform.multiply(face.getN1()),
                        modelViewTransform.multiply(face.getN2()),
                        modelViewTransform.multiply(face.getN3()),
                        null
                )
        );
    }
}
