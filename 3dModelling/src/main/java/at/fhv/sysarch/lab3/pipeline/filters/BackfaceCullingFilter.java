package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;


import java.util.ArrayList;
import java.util.List;

public class BackfaceCullingFilter implements PushFilter <List<Face>, List<Face>>, PullFilter<Face, Face> {

    /* Batch loading

     */
    private Pipe<List<Face>> next;

    private Pipe<Face> prev;


    @Override
    public void setNext(Pipe<List<Face>> next) {
        this.next = next;

    }

    @Override
    public void push(List<Face> input) {
        List<Face> output = new ArrayList<>();

        for (Face face : input) {
            if ((isFrontFace(face))) {
                output.add(face);
            }
        }
        if (next != null) {
            next.push(output); // ganzen Batch weitergeben
        }
    }

    private boolean isFrontFace(Face face) {
        Vec3 v1 = toVec3(face.getV1());
        Vec3 v2 = toVec3(face.getV2());
        Vec3 v3 = toVec3(face.getV3());

        Vec3 edge1 = v2.subtract(v1);
        Vec3 edge2 = v3.subtract(v1);
        Vec3 normal = edge1.cross(edge2);

        return normal.getZ() < 0;
    }

    private Vec3 toVec3(Vec4 vec4) {
        return new Vec3(vec4.getX(), vec4.getY(), vec4.getZ()); // w wird ignoriert
    }


    @Override
    public void setPrevious(Pipe<Face> prev) {
        this.prev = prev;

    }

    @Override
    public Face pull() {
        Face data = prev.pull();
        if (data != null) {
            if (data.getV1().dot(data.getN1()) <= 0) {
                return data;
            }
            throw new IllegalArgumentException("not visible");
        }
        return data;
    }
}



/*  checkt ob das object nach hinten oder vorne gedreht ist
    Wir rendern nur die vorderseite des Objekts
    wir werden den output im psuh verarbeiten
 */
