package com.ray3k.skincomposer.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class Utils {
    public static Color averageColor(FileHandle file) {
        Pixmap pixmap = new Pixmap(file);
        Color returnValue = averageColor(pixmap);
        pixmap.dispose();
        return returnValue;
    }
    
    /**
     * Does not dispose pixmap.
     * @param pixmap
     * @return 
     */
    public static Color averageColor(Pixmap pixmap) {
        Color temp = new Color();
        float sumR = 0.0f;
        float sumG = 0.0f;
        float sumB = 0.0f;
        int count = 0;
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                }
            }
        }
        
        if (count == 0) {
            return new Color(Color.BLACK);
        } else {
            return new Color(sumR / count, sumG / count, sumB / count, 1.0f);
        }
    }
    
    public static Color averageEdgeColor(FileHandle file) {
        Pixmap pixmap = new Pixmap(file);
        Color returnValue = averageEdgeColor(pixmap, file.name().matches("(?i).*\\.9\\.png$"));
        pixmap.dispose();
        return returnValue;
    }
    
    /**
     * Does not dispose pixmap.
     * @param pixmap
     * @param ninePatch
     * @return 
     */
    public static Color averageEdgeColor(Pixmap pixmap, boolean ninePatch) {
        int border = 0;
        if (ninePatch) {
            border = 1;
        }
        
        Color temp = new Color();
        float sumR = 0.0f;
        float sumG = 0.0f;
        float sumB = 0.0f;
        int count = 0;

        //left edge
        for (int y = border; y < pixmap.getHeight() - border; y++) {
            for (int x = border; x < pixmap.getWidth() - border; x++) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        //right edge
        for (int y = border; y < pixmap.getHeight() - border; y++) {
            for (int x = pixmap.getWidth() - 1 - border; x > border; x--) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        //top edge
        for (int x = border; x < pixmap.getWidth() - border; x++) {
            for (int y = border; y < pixmap.getHeight() - border; y++) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        //bottom edge
        for (int x = border; x < pixmap.getWidth() - border; x++) {
            for (int y = pixmap.getHeight() - 1 - border; y > border; y--) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        if (count == 0) {
            return new Color(Color.BLACK);
        } else {
            return new Color(sumR / count, sumG / count, sumB / count, 1.0f);
        }
    }
    
    public static Color inverseColor(Color color) {
        return new Color(1 - color.r, 1 - color.g, 1 - color.b, color.a);
    }
    
    public static float brightness(Color color) {
        return (float) (Math.sqrt(0.299f * Math.pow(color.r, 2) + 0.587 * Math.pow(color.g, 2) + 0.114 * Math.pow(color.b, 2)));
    }
}
