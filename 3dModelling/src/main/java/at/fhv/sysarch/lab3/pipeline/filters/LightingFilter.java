package at.fhv.sysarch.lab3.pipeline.filters;

import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import javafx.scene.paint.Color;

public class LightingFilter implements PushFilter<Pair<Face, Color>, Pair<Face, Color>>, PullFilter<Pair<Face, Color>, Pair<Face, Color>> {

    Vec3 LightPos;

    private Pipe<Pair<Face, Color>> pipe;

    public LightingFilter(Vec3 lightPos) {
        this.LightPos = lightPos;
    }

    @Override
    public void setNext(Pipe<Pair<Face, Color>> next) {
        this.pipe = next;
    }

    @Override
    public void setPrevious(Pipe<Pair<Face, Color>> prev) {
        this.pipe = prev;
    }

    @Override
    public void push(Pair<Face, Color> input) {
        pipe.push(applyLighting(input));
    }

    @Override
    public Pair<Face, Color> pull() {
        Pair<Face, Color> data = pipe.pull();
        if (data != null) {
            return applyLighting(data);
        }
        return null;
    }
    private Pair<Face, Color> applyLighting(Pair<Face, Color> data) {
        Face face = data.fst();

        float dot = face.getN1().toVec3().dot(LightPos.getUnitVector());
        if (dot <= 0) {
            return new Pair<>(face, Color.BLACK);
        } else {
            return new Pair<>(face, data.snd().deriveColor(0, 1, dot, 1));
        }
    }
}



