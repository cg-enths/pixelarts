package de.longuyen.core.voronoi;

import de.longuyen.core.ascii.TestAsciifierTransformer;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestSimpleVoronoi {
    @Test
    void testSimpleImage() throws IOException {
        InputStream targetTestImage = TestSimpleVoronoi.class.getResourceAsStream("/sunflower.jpg");
        BufferedImage bufferedImage = ImageIO.read(targetTestImage);

        BufferedImage trianglatedImage = new SimpleVoronoi(new SimpleVoronoi.Parameters(250)).convert(bufferedImage);
        ImageIO.write(trianglatedImage, "png", new File("target/test.png"));

    }
}