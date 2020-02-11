package com.ray3k.skincomposer;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.FloatArray;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Contains functions for drawing graphs of {@link Interpolation} instances. Use the default interpolations or create
 * your own! GraphDrawer assumes a single, continuous graph formula is provided.
 *
 * @author Raymond "Raeleus" Buckley
 */
public class GraphDrawer {
    /**
     * The {@link ShapeDrawer} used to render the graph path.
     */
    public ShapeDrawer shapeDrawer;
    
    /**
     * The type of mitre joint used for connecting the sample points.
     */
    public JoinType joinType = JoinType.SMOOTH;
    
    /**
     * The number of sample points to estimate the shape of the graph. Increase this for better accuracy in larger
     * drawings or complicated formulas. Note that the actual number of samples drawn will depend on domainBegin and
     * domainEnd. Value must be greater than 2.
     */
    public int samples = 50;
    
    /**
     * The alpha value to begin plotting at x. Typical interpolations begin at 0. Alpha is the term used to describe the
     * linear value along the x axis.
     */
    public float plotBegin = 0;
    
    /**
     * The alpha value where plotting ends at x + width. Typical interpolations end at 1. Alpha is the term used to
     * describe the linear value along the x axis.
     */
    public float plotEnd = 1;
    
    /**
     * Alpha values less than domainBegin are not plotted. Alpha is the term used to describe the linear value along the
     * x axis. To show the full graph, domainBegin must equal plotBegin and domainEnd must equal plotEnd.
     */
    public float domainBegin = 0;
    
    /**
     * Alpha values greater than domainEnd are not plotted. Alpha is the term used to describe the linear value along
     * the x axis. To show the full graph, domainBegin must equal plotBegin and domainEnd must equal plotEnd.
     */
    public float domainEnd = 1;
    
    /**
     * Given any particular alpha, the resulting y value can exceed the height of the graph bounds or dip below the x
     * axis. Setting rescale = true scales the graph so that y values will stay entirely inside the height of the graph
     * bounds.
     */
    public boolean rescale = true;
    
    /**
     * Cached FloatArray used to pass sample points to ShapeDrawer for rendering.
     */
    private static final FloatArray path = new FloatArray();
    
    public GraphDrawer(ShapeDrawer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
    }
    
    /**
     * <p>Draws a graph with the specified Interpolation and values. GraphDrawer assumes a single, continuous graph
     * formula.
     * <p>
     * Alpha is the term used to describe the linear value along the x axis. Typical y values are expected to be between
     * 0 and 1 and is scaled to the height of the drawn graph. However, y values can still be outside of this range. See
     * the "rescale" parameter.</p>
     * <p>This method overrides the class provided values</p>
     *
     * @param interpolation The Interpolation that describes the graph's formula. See {@link Interpolation} for
     *                      available defaults or create your own!
     * @param x             The x coordinate of where the graph will be drawn.
     * @param y             The y coordinate of where the graph will be drawn.
     * @param width         The width of the graph drawing.
     * @param height        The height of the graph drawing.
     * @param joinType      The type of mitre joint used for connecting the sample points.
     * @param samples       The number of sample points to estimate the shape of the graph. Increase this for better
     *                      accuracy in larger drawings or complicated formulas. Note that the actual number of samples
     *                      drawn will depend on domainBegin and domainEnd. Value must be greater than 2.
     * @param plotBegin     The alpha value to begin plotting at x. Typical interpolations begin at 0.
     * @param plotEnd       The alpha value where plotting ends at x + width. Typical interpolations end at 1.
     * @param domainBegin   Alpha values less than domainBegin are not plotted.
     * @param domainEnd     Alpha values greater than domainEnd are not plotted.
     * @param rescale       Given any particular alpha, the resulting y value can exceed the height of the graph bounds
     *                      or dip below the x axis. Setting rescale = true scales the graph so that y values will stay
     *                      entirely inside the height of the graph bounds.
     */
    public void draw(Interpolation interpolation, float x, float y, float width, float height, JoinType joinType,
                     int samples, float plotBegin, float plotEnd, float domainBegin, float domainEnd, boolean rescale) {
        if (plotBegin >= plotEnd) {
            throw new IllegalArgumentException("plotBegin must be less than plotEnd");
        } else if (domainBegin >= domainEnd) {
            throw new IllegalArgumentException("domainBegin must be less than domainEnd");
        } else if (samples <= 2) {
            throw new IllegalArgumentException("samples must be greater than 2");
        }
        
        path.clear();
        float alpha = plotBegin;
        float high = 0;
        float low = 0;
        
        //Create sample points. Points consist of an x and y pair
        for (int i = 0; i < samples * 2; i += 2) {
            float pointX = x + (alpha - plotBegin) / (plotEnd - plotBegin) * width;
            float pointY = y + interpolation.apply(alpha) * height;
            //don't add the point unless we are within the domain
            if (alpha >= domainBegin) {
                path.add(pointX);
                path.add(pointY);
            }
            
            //calculate high and low points
            if (i == 0) {
                high = pointY;
                low = pointY;
            } else {
                if (pointY > high) high = pointY;
                if (pointY < low) low = pointY;
            }
            
            //stop adding sample points if we reached the end
            if (alpha >= plotEnd || alpha >= domainEnd) break;
            
            //prepare alpha for next sample point
            alpha += (plotEnd - plotBegin) / (samples - 1);
            if (alpha > plotEnd) alpha = plotEnd;
            if (alpha > domainEnd) alpha = domainEnd;
        }
        
        if (rescale) {
            //if the lowest point is below the specified y, translate all points upwards until the lowest point is aligned with y
            if (low < y) {
                float dif = y - low;
                for (int i = 1; i < path.size; i += 2) {
                    path.set(i, path.get(i) + dif);
                }
                high += dif;
            }
            
            //if the highest point is above y + height, scale all points down until the highest point is aligned with y + height
            if (high > y + height) {
                float scale = height / (high - y);
                for (int i = 1; i < path.size; i += 2) {
                    path.set(i, (path.get(i) - y) * scale + y);
                }
            }
        }
        
        //Draw the graph.
        shapeDrawer.path(path, shapeDrawer.getDefaultLineWidth(), joinType, true);
    }
    
    /**
     * Draws a graph with the specified Interpolation and values. GraphDrawer assumes a single, continuous graph
     * formula.
     *
     * @param interpolation The Interpolation that describes the graph's formula. See {@link Interpolation} for
     *                      available defaults or create your own!
     * @param x             The x coordinate of where the graph will be drawn.
     * @param y             The y coordinate of where the graph will be drawn.
     * @param width         The width of the graph drawing.
     * @param height        The height of the graph drawing.
     */
    public void draw(Interpolation interpolation, float x, float y, float width, float height) {
        draw(interpolation, x, y, width, height, joinType, samples, plotBegin, plotEnd, domainBegin, domainEnd,
                rescale);
    }
    
    /**
     * Draws a graph with the specified Interpolation and values. GraphDrawer assumes a single, continuous graph
     * formula.
     *
     * @param interpolation The Interpolation that describes the graph's formula. See {@link Interpolation} for
     *                      available defaults or create your own!
     * @param x             The x coordinate of where the graph will be drawn.
     * @param y             The y coordinate of where the graph will be drawn.
     * @param width         The width of the graph drawing.
     * @param height        The height of the graph drawing.
     * @param joinType      The type of mitre joint used for connecting the sample points.
     */
    public void draw(Interpolation interpolation, float x, float y, float width, float height, JoinType joinType) {
        draw(interpolation, x, y, width, height, joinType, samples, plotBegin, plotEnd, domainBegin, domainEnd,
                rescale);
    }
    
    /**
     * Draws a graph with the specified Interpolation and values. GraphDrawer assumes a single, continuous graph
     * formula.
     *
     * @param interpolation The Interpolation that describes the graph's formula. See {@link Interpolation} for
     *                      available defaults or create your own!
     * @param rectangle     A rectangle that defines the x, y, width, and height of the graph drawing.
     */
    public void draw(Interpolation interpolation, Rectangle rectangle) {
        draw(interpolation, rectangle.x, rectangle.y, rectangle.width, rectangle.height, joinType, samples, plotBegin,
                plotEnd, domainBegin, domainEnd, rescale);
    }
    
    /**
     * Draws a graph with the specified Interpolation and values. GraphDrawer assumes a single, continuous graph
     * formula. Alpha is the term used to describe the linear value along the x axis. Typical y values are expected to
     * be between 0 and 1 and is scaled to the height of the drawn graph. However, y values can still be outside of this
     * range. See the "rescale" parameter.
     *
     * @param interpolation The Interpolation that describes the graph's formula. See {@link Interpolation} for
     *                      available defaults or create your own!
     * @param rectangle     A rectangle that defines the x, y, width, and height of the graph drawing.
     * @param joinType      The type of mitre joint used for connecting the sample points.
     */
    public void draw(Interpolation interpolation, Rectangle rectangle, JoinType joinType) {
        draw(interpolation, rectangle.x, rectangle.y, rectangle.width, rectangle.height, joinType, samples, plotBegin,
                plotEnd, domainBegin, domainEnd, rescale);
    }
    
    /**
     * Returns the {@link ShapeDrawer} used to render the graph path.
     *
     * @return The {@link ShapeDrawer}
     */
    public ShapeDrawer getShapeDrawer() {
        return shapeDrawer;
    }
    
    /**
     * Sets the {@link ShapeDrawer} used to render the graph path.
     *
     * @param shapeDrawer The {@link ShapeDrawer}
     */
    public void setShapeDrawer(ShapeDrawer shapeDrawer) {
        this.shapeDrawer = shapeDrawer;
    }
    
    /**
     * Returns the type of mitre joint used for connecting the sample points.
     *
     * @return The {@link JoinType}.
     */
    public JoinType getJoinType() {
        return joinType;
    }
    
    /**
     * Sets the type of mitre joint used for connecting the sample points.
     *
     * @param joinType The {@link JoinType}.
     */
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }
    
    /**
     * Returns the number of sample points to estimate the shape of the graph. An increase in samples means better
     * accuracy in larger drawings or complicated formulas. Note that the actual number of samples drawn will depend on
     * domainBegin and domainEnd.
     *
     * @return The number of sample points.
     */
    public int getSamples() {
        return samples;
    }
    
    /**
     * Sets the number of sample points to estimate the shape of the graph. An increase in samples means better accuracy
     * in larger drawings or complicated formulas. Note that the actual number of samples drawn will depend on
     * domainBegin and domainEnd.
     *
     * @param samples The number of sample points. Value must be greater than 2.
     */
    public void setSamples(int samples) {
        this.samples = samples;
    }
    
    /**
     * Returns the alpha value to begin plotting at x.  Alpha is the term used to describe the linear value along the x
     * axis. Typical interpolations begin at 0.
     *
     * @return The beginning alpha value.
     */
    public float getPlotBegin() {
        return plotBegin;
    }
    
    /**
     * Sets the alpha value to begin plotting at x. Alpha is the term used to describe the linear value along the x
     * axis. Typical interpolations begin at 0.
     *
     * @param plotBegin The beginning alpha value.
     */
    public void setPlotBegin(float plotBegin) {
        this.plotBegin = plotBegin;
    }
    
    /**
     * Returns the alpha value where plotting ends at x + width. Alpha is the term used to describe the linear value
     * along the x axis. Typical interpolations end at 1.
     *
     * @return The ending alpha value.
     */
    public float getPlotEnd() {
        return plotEnd;
    }
    
    /**
     * Sets the alpha value where plotting ends at x + width. Alpha is the term used to describe the linear value along
     * the x axis. Typical interpolations end at 1.
     *
     * @param plotEnd The ending alpha value.
     */
    public void setPlotEnd(float plotEnd) {
        this.plotEnd = plotEnd;
    }
    
    /**
     * Returns the domainBegin value. Alpha values less than domainBegin are not plotted. Alpha is the term used to
     * describe the linear value along the x axis. To show the full graph, domainBegin must equal plotBegin and
     * domainEnd must equal plotEnd.
     *
     * @return The beginning of the domain.
     */
    public float getDomainBegin() {
        return domainBegin;
    }
    
    /**
     * Sets the domainBegin value. Alpha values less than domainBegin are not plotted. Alpha is the term used to
     * describe the linear value along the x axis. To show the full graph, domainBegin must equal plotBegin and
     * domainEnd must equal plotEnd.
     *
     * @param domainBegin The beginning of the domain.
     */
    public void setDomainBegin(float domainBegin) {
        this.domainBegin = domainBegin;
    }
    
    /**
     * Returns the domainEnd value. Alpha values greater than domainEnd are not plotted. Alpha is the term used to
     * describe the linear value along the x axis. To show the full graph, domainBegin must equal plotBegin and
     * domainEnd must equal plotEnd.
     *
     * @return The end of the domain.
     */
    public float getDomainEnd() {
        return domainEnd;
    }
    
    /**
     * Sets the domainEnd value. Alpha values greater than domainEnd are not plotted. Alpha is the term used to describe
     * the linear value along the x axis. To show the full graph, domainBegin must equal plotBegin and domainEnd must
     * equal plotEnd.
     *
     * @param domainEnd The end of the domain.
     */
    public void setDomainEnd(float domainEnd) {
        this.domainEnd = domainEnd;
    }
    
    /**
     * Returns the rescale value. Given any particular alpha, the resulting y value can exceed the height of the graph
     * bounds or dip below the x axis. Setting rescale = true scales the graph so that y values will stay entirely
     * inside the height of the graph bounds.
     *
     * @return The rescale value.
     */
    public boolean isRescale() {
        return rescale;
    }
    
    /**
     * Sets the rescale value. Given any particular alpha, the resulting y value can exceed the height of the graph
     * bounds or dip below the x axis. Setting rescale = true scales the graph so that y values will stay entirely
     * inside the height of the graph bounds.
     *
     * @param rescale The rescale value.
     */
    public void setRescale(boolean rescale) {
        this.rescale = rescale;
    }
}
