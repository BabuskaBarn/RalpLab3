package at.fhv.sysarch.lab3.pipeline;

import at.fhv.sysarch.lab3.animation.AnimationRenderer;
import at.fhv.sysarch.lab3.obj.Face;
import at.fhv.sysarch.lab3.obj.Model;
import at.fhv.sysarch.lab3.pipeline.data.Pair;
import at.fhv.sysarch.lab3.pipeline.filters.*;
import at.fhv.sysarch.lab3.pipeline.filters.sink.Renderer;
import at.fhv.sysarch.lab3.pipeline.filters.source.Source;
import at.fhv.sysarch.lab3.pipeline.pipe.Pipe;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

import java.util.List;

public class PushPipelineFactory {

    public static AnimationTimer createPipeline(PipelineData pd) {

        // 1. Quelle liefert List<Face>
        Source source = new Source();

        // 2. Rotation (List<Face>)
        RotationFilter rotationFilter = new RotationFilter();
        Pipe<List<Face>> pipeSourceToRotation = new Pipe<>();
        source.setNext(pipeSourceToRotation);
        pipeSourceToRotation.setNext(rotationFilter);



        // 3. Backface Culling (List<Face>)
        BackfaceCullingFilter backfaceCulling = new BackfaceCullingFilter();
        Pipe<List<Face>> pipeModelViewToCulling = new Pipe<>();
        rotationFilter.setNext(pipeModelViewToCulling);
        pipeModelViewToCulling.setNext(backfaceCulling);

        // 4. Depth Sorting (List<Face>)
        DepthSortingFilter depthSortingFilter = new DepthSortingFilter(pd.getViewingEye());
        Pipe<List<Face>> pipeCullingToDepthSorting = new Pipe<>();
        backfaceCulling.setNext(pipeCullingToDepthSorting);
        pipeCullingToDepthSorting.setNext(depthSortingFilter);

        // 5. ColorFilter (Face -> Pair<Face, Color>)

        ColorFilter colorFilter = new ColorFilter(pd.getModelColor());
       Pipe<Face> depthSortingToColor= new Pipe<>();
       depthSortingToColor.setNext(colorFilter);
       depthSortingFilter.setNext(depthSortingToColor);

       ProjectionModelTransformationFilter PMTF = new ProjectionModelTransformationFilter(pd.getProjTransform());
       Pipe<Pair<Face, Color>> projection = new Pipe<>();
       projection.setNext(PMTF);
        if (pd.isPerformLighting()) {
            LightingFilter lightingFilter = new LightingFilter(pd.getLightPos());
            Pipe<Pair<Face, Color>> colorLightningPipe = new Pipe<>();
            colorLightningPipe.setNext(lightingFilter);
            colorFilter.setNext(colorLightningPipe);

            // 6. perform projection transformation on VIEW SPACE coordinates
            lightingFilter.setNext(projection);
        } else {
            // 6. perform projection transformation
            colorFilter.setNext(projection);
        }

        // 7. Screen Space Transform
        ScreenSpaceTransformFilter screenTransformFilter = new ScreenSpaceTransformFilter(
                pd.getViewportTransform(), pd.getProjTransform());  // <- second argument was wrong in your code
        Pipe<Pair<Face, Color>> PMTFToScreen = new Pipe<>();
        PMTF.setNext(PMTFToScreen);
        PMTFToScreen.setNext(screenTransformFilter);

// 8. Renderer (sink)
        Renderer renderer = new Renderer(pd.getGraphicsContext(), pd.getRenderingMode());
        Pipe<Pair<Face, Color>> pipeScreenToRenderer = new Pipe<>();
        screenTransformFilter.setNext(pipeScreenToRenderer);
        pipeScreenToRenderer.setNext(renderer);





        // AnimationTimer mit Rotation und Render-Callback
        return new AnimationRenderer(pd) {
            private float rotation = 0;
            /**
             * This method is called for every frame from the JavaFX Animation
             * system (using an AnimationTimer, see AnimationRenderer).
             *
             * @param fraction the time which has passed since the last render call in a fraction of a second
             * @param model    the model to render
             */

            @Override
            protected void render(float fraction, Model model) {
                rotation += ((fraction) % (2 * Math.PI));
                source.setModel(model);
                source.reset();

                Mat4 rotationMatrix = Matrices.rotate(rotation, pd.getModelRotAxis());
                Mat4 modelTranslationMatrix = pd.getModelTranslation().multiply(rotationMatrix);

                // compute updated model-view tranformation
                Mat4 viewTransformationMatrix = pd.getViewTransform().multiply(modelTranslationMatrix);

                // update model-view filter
                rotationFilter.setRotationMatrix(viewTransformationMatrix);

                // trigger rendering of the pipeline
                source.push(model);
            }

        };
    }
}
