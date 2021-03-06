package de.longuyen.core.statistics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class InterQuartile {
    private final int Q25;
    private final int Q50;
    private final int Q75;
    private final int[] distributions;

    public InterQuartile(BufferedImage bufferedImage) {
       Set<Integer> grayScales = new HashSet<>();
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                Color color = new Color(bufferedImage.getRGB(x, y));
                Integer grayScale = (color.getBlue() + color.getGreen() + color.getRed()) / 3;
                grayScales.add(grayScale);
            }
        }
        int [] keys = grayScales.stream().sorted().mapToInt(integer -> integer).toArray();
        Q25 = keys[(int) (keys.length * 0.25)];
        Q50 = keys[(int) (keys.length * 0.5)];
        Q75 = keys[(int) (keys.length * 0.75)];
        this.distributions = keys;
    }

    public int getPercentile(final double p){
        return this.distributions[(int)(distributions.length * p)];
    }

    public int getQ25() {
        return Q25;
    }

    public int getQ50() {
        return Q50;
    }

    public int getQ75() {
        return Q75;
    }
}
