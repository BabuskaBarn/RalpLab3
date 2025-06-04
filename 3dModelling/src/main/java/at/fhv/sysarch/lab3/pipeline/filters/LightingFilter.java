package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Vec3;
import javafx.scene.paint.Color;

public class LightingFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {

    private final Vec3 lightPos;
    private Pipe<Pair<Face, Color>> next;
    private Pipe<Pair<Face, Color>> prev;

    public LightingFilter(Vec3 lightPos) {
        this.lightPos = lightPos;
    }

    @Override
    public void setNext(Pipe<Pair<Face, Color>> next) {
        this.next = next;
    }

    @Override
    public void setPrevious(Pipe<Pair<Face, Color>> prev) {
        this.prev = prev;
    }

    @Override
    public void push(Pair<Face, Color> input) {
        Pair<Face, Color> lit = applyLighting(input);
        if (next != null) {
            next.push(lit);
        }
    }

    @Override
    public Pair<Face, Color> pull() {
        if (prev == null) return null;
        Pair<Face, Color> input = prev.pull();
        if (input == null) return null;
        return applyLighting(input);
    }

    private Pair<Face, Color> applyLighting(Pair<Face, Color> input) {
        Face face = input.fst();
        Color baseColor = input.snd();

        Vec3 n1 = toVec3(face.getN1());
        Vec3 n2 = toVec3(face.getN2());
        Vec3 n3 = toVec3(face.getN3());

        Vec3 normal = n1.add(n2).add(n3).scale(1f / 3f).getUnitVector();
        Vec3 v1 = toVec3(face.getV1());
        Vec3 lightDir = lightPos.subtract(v1).getUnitVector();

        float brightness = Math.max(0f, normal.dot(lightDir));

        // Farbe wird mit der Helligkeit skaliert (Multiplikation der RGB-Komponenten)
        Color litColor = baseColor.deriveColor(0, 1, brightness, 1);

        return new Pair<>(face, litColor);
    }

    private Vec3 toVec3(com.hackoeur.jglm.Vec4 vec4) {
        return new Vec3(vec4.getX(), vec4.getY(), vec4.getZ());
    }
}
