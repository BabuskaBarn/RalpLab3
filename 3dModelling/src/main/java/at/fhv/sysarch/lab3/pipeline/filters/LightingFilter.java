package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import com.hackoeur.jglm.Vec3;

public class LightingFilter implements PushFilter{
    private final Vec3 lightPos;
    private PushFilter next;

    public LightingFilter(PushFilter next, Vec3 lightPos){
        this.next=next;
        this.lightPos=lightPos;
    }


    @Override
    public void push(Face face) {
        // Durchschnittsnormale berechnen
        Vec3 n1 = new Vec3(face.getN1().getX(), face.getN1().getY(), face.getN1().getZ());
        Vec3 n2 = new Vec3(face.getN2().getX(), face.getN2().getY(), face.getN2().getZ());
        Vec3 n3 = new Vec3(face.getN3().getX(), face.getN3().getY(), face.getN3().getZ());

        Vec3 normal = n1.add(n2).add(n3).scale(1.0f / 3f).getUnitVector();

        Vec3 v1 = new Vec3(face.getV1().getX(), face.getV1().getY(), face.getV1().getZ());
        Vec3 lightDir = lightPos.subtract(v1).getUnitVector();

        float brightness = Math.max(0f, normal.dot(lightDir));



        next.push(face);

    }
}
