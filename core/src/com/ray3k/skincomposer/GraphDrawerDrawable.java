package com.ray3k.skincomposer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * A convenience class that allows you to implement GraphDrawer in your Scene2D.UI layouts.
 *
 * @author Raymond "Raeleus" Buckley
 */
public class GraphDrawerDrawable extends ShapeDrawerDrawable {
    /**
     * The {@link GraphDrawer} used to draw the graph.
     */
    public transient GraphDrawer graphDrawer;
    
    /**
     * The Interpolation that describes the graph's formula. See {@link Interpolation} for available defaults or create
     * your own!
     */
    public Interpolation interpolation;
    
    /**
     * The color of the graph path in packed float bits.
     */
    public float color = new Color(Color.WHITE).toFloatBits();
    
    /**
     * The width of the lines used to draw the graph path.
     */
    public float lineWidth = 1;
    
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
     * Constructs a GraphDrawerDrawable. The Batch of the provided ShapeDrawer/GraphDrawer must be the same Batch at
     * rendering time.
     *
     * @param graphDrawer
     */
    public GraphDrawerDrawable(GraphDrawer graphDrawer) {
        super(graphDrawer.getShapeDrawer());
        this.graphDrawer = graphDrawer;
    }
    
    /**
     * Draws the graph at the specified coordinates with the current graph properties.
     *
     * @param shapeDrawer
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        float previousColor = shapeDrawer.getPackedColor();
        shapeDrawer.setColor(color);
        float previousLineWidth = shapeDrawer.getDefaultLineWidth();
        shapeDrawer.setDefaultLineWidth(lineWidth);
        graphDrawer.draw(interpolation, x, y, width, height, joinType, samples, plotBegin, plotEnd, domainBegin,
                domainEnd, rescale);
        shapeDrawer.setColor(previousColor);
        shapeDrawer.setDefaultLineWidth(previousLineWidth);
    }
    
    /**
     * Returns the {@link GraphDrawer} used to draw the graph.
     *
     * @return The {@link GraphDrawer}
     */
    public GraphDrawer getGraphDrawer() {
        return graphDrawer;
    }
    
    /**
     * Sets the {@link GraphDrawer} used to draw the graph.
     *
     * @param graphDrawer The {@link GraphDrawer}
     */
    public void setGraphDrawer(GraphDrawer graphDrawer) {
        this.graphDrawer = graphDrawer;
    }
    
    /**
     * The Interpolation that describes the graph's formula. See {@link Interpolation} for available defaults or create
     * your own!
     *
     * @return The {@link Interpolation}.
     */
    public Interpolation getInterpolation() {
        return interpolation;
    }
    
    /**
     * The Interpolation that describes the graph's formula. See {@link Interpolation} for available defaults or create
     * your own!
     *
     * @param interpolation The new {@link Interpolation}.
     */
    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }
    
    /**
     * Returns the color of the graph path in packed float bits.
     *
     * @return The color.
     */
    public float getPackedColor() {
        return color;
    }
    
    /**
     * Sets the color of the graph path in packed float bits.
     *
     * @param color The color.
     */
    public void setColor(float color) {
        this.color = color;
    }
    
    /**
     * Sets the color of the graph path in packed float bits.
     *
     * @param color The color.
     */
    public void setColor(Color color) {
        this.color = color.toFloatBits();
    }
    
    /**
     * Returns the width of the lines used to draw the graph path.
     *
     * @return the width of the lines.
     */
    public float getLineWidth() {
        return lineWidth;
    }
    
    /**
     * Sets the width of the lines used to draw the graph path.
     *
     * @param lineWidth the width of the lines.
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
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
