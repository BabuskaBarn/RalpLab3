package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Vec3;

public class LightingFilter implements PushFilter<Face, Face>, PullFilter<Face, Face>{

    //Position des Lichts
    private final Vec3 lightPos;
    private Pipe<Face> next;

    private Pipe<Face> prev;

    public LightingFilter( Vec3 lightPos){

        this.lightPos=lightPos;
    }


    @Override
    public void setNext(Pipe<Face> next) {
        this.next = next;
    }

    @Override
    public void push(Face face) {

        Face litFace = applyLighting(face);
        if (next != null) {
            next.push(litFace);
        }


    }

    private Face applyLighting(Face face) {
        Vec3 n1 = toVec3(face.getN1());
        Vec3 n2 = toVec3(face.getN2());
        Vec3 n3 = toVec3(face.getN3());

        Vec3 normal = n1.add(n2).add(n3).scale(1f / 3f).getUnitVector();
        Vec3 v1 = toVec3(face.getV1());
        Vec3 lightDir = lightPos.subtract(v1).getUnitVector();

        float brightness = Math.max(0f, normal.dot(lightDir));

        // TODO: Hier könntest du brightness speichern (z.B. in einer Wrapperklasse),
        // aktuell gibt es keine Modifikation am Face-Objekt

        return face;
    }

    private Vec3 toVec3(com.hackoeur.jglm.Vec4 vec4) {
        return new Vec3(vec4.getX(), vec4.getY(), vec4.getZ());
    }

    @Override
    public void setPrevious(Pipe<Face> prev) {
        this.prev = prev;
    }

    @Override
    public Face pull() {
        if (prev == null) {
            return null;
        }
        Face face = prev.pull();
        if (face == null) {
            return null;
        }
        return applyLighting(face);
    }
}

/* Wir werden die Farbe bei den factorys applien


 */


