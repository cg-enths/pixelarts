package de.longuyen.core.polygon;

import de.longuyen.core.Transformer;
import de.longuyen.core.utils.ReturnFunction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.*;

/**
 * Polygon algorithm. But instead of calculating the RGB distance between centers and its neighbor, this algorithm simply
 * calculate the euclidean distance on the cartesian coordinating system.
 */
public class Polygon implements Transformer {
    protected final Parameters parameters;

    public Polygon(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Parameter class of Polygon
     */
    public static class Parameters {
        public final int centers;
        public final int iterations;

        public Parameters(int centers, int iterations) {
            this.centers = centers;
            this.iterations = iterations;
        }
    }

    /**
     * Calculate the new color for each pixel
     * @param bufferedImage input image
     * @return a pixel color mapping
     */
    public Map<List<Point>, Color> generatePoints(final BufferedImage bufferedImage) {
        // Generating random centers
        List<Point> centers = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < Polygon.this.parameters.centers; i++) {
            centers.add(new Point(random.nextInt(bufferedImage.getWidth()), random.nextInt(bufferedImage.getHeight())));
        }

        // Closure
        // Calculate the neighbors of each center
        ReturnFunction<Map<Point, List<Point>>> calculateIdentity = () -> {
            Map<Point, List<Point>> ret = new HashMap<>();
            for(int y = 0; y < bufferedImage.getHeight(); y++){
                for(int x = 0; x < bufferedImage.getWidth(); x++){
                    Point currentPixel = new Point(x, y);
                    Point nearestCenter = centers.get(0);
                    double nearestDistance = centers.get(0).distance(currentPixel);
                    for (Point center : centers) {
                        double currentDistance = center.distance(currentPixel);
                        if (currentDistance < nearestDistance) {
                            nearestDistance = currentDistance;
                            nearestCenter = center;
                        }
                    }
                    if(!ret.containsKey(nearestCenter)){
                        ret.put(nearestCenter, new ArrayList<>());
                    }
                    ret.get(nearestCenter).add(currentPixel);
                }
            }
            return ret;
        };

        for(int i = 0; i < Polygon.this.parameters.iterations; i++){
            // Calculate identites for every pixel.

            Map<Point, List<Point>> identities = calculateIdentity.apply();
            // Update the centers
            for(Map.Entry<Point, List<Point>> entry : identities.entrySet()){
                DoubleSummaryStatistics xs = new DoubleSummaryStatistics();
                DoubleSummaryStatistics ys = new DoubleSummaryStatistics();
                for(Point point : entry.getValue()){
                    xs.accept(point.getX());
                    ys.accept(point.getY());
                }
                entry.getKey().setLocation(xs.getAverage(), ys.getAverage());
            }
        }

        // Update the position of the centers with average position of its neighbors
        Map<Point, List<Point>> identities = calculateIdentity.apply();
        Map<List<Point>, Color> returnValue = new HashMap<>();
        for(Map.Entry<Point, List<Point>> entry : identities.entrySet()){
            DoubleSummaryStatistics sumBlue = new DoubleSummaryStatistics();
            DoubleSummaryStatistics sumGreen = new DoubleSummaryStatistics();
            DoubleSummaryStatistics sumRed = new DoubleSummaryStatistics();
            for(Point point : entry.getValue()){
                int color = bufferedImage.getRGB((int)point.getX(), (int)point.getY());
                int blue = color & 0xff;
                int green = (color & 0xff00) >> 8;
                int red = (color & 0xff0000) >> 16;
                sumBlue.accept(blue);
                sumGreen.accept(green);
                sumRed.accept(red);
            }
            int blue = (int) sumBlue.getAverage();
            int green = (int) sumGreen.getAverage();
            int red = (int) sumRed.getAverage();
            Color color = new Color(red, green, blue);
            returnValue.put(entry.getValue(), color);
        }
        return returnValue;
    }

    @Override
    public BufferedImage convert(BufferedImage bufferedImage) {
        // Deep copy the input to avoid unintended mutation
        ColorModel cm = bufferedImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bufferedImage.copyData(null);
        BufferedImage returnValue = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        // Rendering the result
        Map<List<Point>, Color> kmeans = generatePoints(bufferedImage);
        for(Map.Entry<List<Point>, Color> entry : kmeans.entrySet()){
            for(Point2D point : entry.getKey()){
                returnValue.setRGB((int)point.getX(), (int)point.getY(), entry.getValue().getRGB());
            }
        }
        return returnValue;
    }
}
