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

public class PullPipelineFactory {
    public static AnimationTimer createPipeline(PipelineData pd) {
        // 0. Quelle (Model Source)
        Source source = new Source();

        // 1. Rotation
        RotationFilter rotationFilter = new RotationFilter();
        Pipe<Face> sourceToRotation = new Pipe<>();
        sourceToRotation.setPrevious(source);
        rotationFilter.setPrevious(sourceToRotation);

        // 2. Backface Culling
        BackfaceCullingFilter cullingFilter = new BackfaceCullingFilter();
        Pipe<Face> rotationToBackface = new Pipe<>();
        rotationToBackface.setPrevious(rotationFilter);
        cullingFilter.setPrevious(rotationToBackface);

        // 3. Depth Sorting
        DepthSortingFilter depthSortingFilter = new DepthSortingFilter(pd.getViewingEye());
        Pipe<Face> cullingToDepthSorting = new Pipe<>();
        cullingToDepthSorting.setPrevious(cullingFilter);
        depthSortingFilter.setPrevious(cullingToDepthSorting);

        // 4. Color Filter
        ColorFilter colorFilter = new ColorFilter(pd.getModelColor());
        Pipe<Face> depthToColor = new Pipe<>();
        depthToColor.setPrevious(depthSortingFilter);
        colorFilter.setPrevious(depthToColor);


        // 6. Projection + Model Transformation
        ProjectionModelTransformationFilter PMTF = new ProjectionModelTransformationFilter(pd.getProjTransform());
        Pipe<Pair<Face,Color>> projection= new Pipe<>();
        PMTF.setPrevious(projection);

        if (pd.isPerformLighting()) {
            LightingFilter lightingFilter = new LightingFilter(pd.getLightPos());
            Pipe<Pair<Face, Color>> colorToLighting = new Pipe<>();
            colorToLighting.setPrevious(colorFilter);
            lightingFilter.setPrevious(colorToLighting);

            projection.setPrevious(lightingFilter);
        } else {
            projection.setPrevious(colorFilter);
        }



        // 7. Screen Space Transformation
        ScreenSpaceTransformFilter screenSpaceTransformFilter = new ScreenSpaceTransformFilter(pd.getViewportTransform(), pd.getProjTransform());
        Pipe<Pair<Face, Color>> PMTFToScreen = new Pipe<>();
        PMTFToScreen.setPrevious(PMTF);
        screenSpaceTransformFilter.setPrevious(PMTFToScreen);

        // 8. Renderer (Sink)
        Renderer renderer = new Renderer(pd.getGraphicsContext(), pd.getRenderingMode());
        Pipe<Pair<Face, Color>> screenToRenderer = new Pipe<>();
        screenToRenderer.setPrevious(screenSpaceTransformFilter);
        renderer.setPrevious(screenToRenderer);

        // Animation Timer mit Pull-Aufruf
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
                Mat4 viewTransformationMatrix = pd.getViewTransform().multiply(modelTranslationMatrix);

                // Aktuelle Model-View-Matrix setzen
                rotationFilter.setRotationMatrix(viewTransformationMatrix);

                // Pipeline starten
                renderer.pull();
            }
        };
    }
}
