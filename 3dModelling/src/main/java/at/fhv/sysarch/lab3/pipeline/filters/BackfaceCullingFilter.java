package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.filters.PushFilter;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;

public class BackfaceCullingFilter implements PushFilter {

    private PushFilter next;

    public BackfaceCullingFilter(PushFilter next) {
        this.next = next;
    }

    @Override
    public void push(Face face) {
        // Konvertiere Vec4 -> Vec3 (ignoriere w-Komponente)
        Vec3 v1 = new Vec3(face.getV1().getX(), face.getV1().getY(), face.getV1().getZ());
        Vec3 v2 = new Vec3(face.getV2().getX(), face.getV2().getY(), face.getV2().getZ());
        Vec3 v3 = new Vec3(face.getV3().getX(), face.getV3().getY(), face.getV3().getZ());

        // Kanten berechnen
        Vec3 edge1 = v2.subtract(v1);
        Vec3 edge2 = v3.subtract(v1);

        // Normalenvektor über Kreuzprodukt
        Vec3 normal = edge1.cross(edge2);

        // Sichtbarkeit prüfen (wenn z negativ → nach vorne gerichtet)
        if (normal.getZ() < 0) {
            next.push(face);
        }
    }
}
