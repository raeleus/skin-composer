package com.ray3k.skincomposer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.utils.Utils;

public class LeadingTruncateLabel extends OpenLabel {
    public LeadingTruncateLabel(CharSequence text, Skin skin) {
        super(text, skin);
    }
    
    public LeadingTruncateLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }
    
    public LeadingTruncateLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }
    
    public LeadingTruncateLabel(CharSequence text, Skin skin, String fontName, String colorName) {
        super(text, skin, fontName, colorName);
    }
    
    public LeadingTruncateLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }
    
    @Override
    public void layout() {
        BitmapFont font = cache.getFont();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        if (fontScaleChanged) font.getData().setScale(fontScaleX, fontScaleY);
    
        boolean wrap = this.wrap && ellipsis == null;
        if (wrap) {
            float prefHeight = getPrefHeight();
            if (prefHeight != lastPrefHeight) {
                lastPrefHeight = prefHeight;
                invalidateHierarchy();
            }
        }
    
        float width = getWidth(), height = getHeight();
        Drawable background = style.background;
        float x = 0, y = 0;
        if (background != null) {
            x = background.getLeftWidth();
            y = background.getBottomHeight();
            width -= background.getLeftWidth() + background.getRightWidth();
            height -= background.getBottomHeight() + background.getTopHeight();
        }
    
        GlyphLayout layout = this.layout;
        float textWidth, textHeight;
        if (wrap || text.indexOf("\n") != -1) {
            // If the text can span multiple lines, determine the text's actual size so it can be aligned within the label.
            layout.setText(font, text, 0, text.length, Color.WHITE, width, lineAlign, wrap, ellipsis);
            textWidth = layout.width;
            textHeight = layout.height;
        
            if ((labelAlign & Align.left) == 0) {
                if ((labelAlign & Align.right) != 0)
                    x += width - textWidth;
                else
                    x += (width - textWidth) / 2;
            }
        } else {
            textWidth = width;
            textHeight = font.getData().capHeight;
        }
    
        if ((labelAlign & Align.top) != 0) {
            y += cache.getFont().isFlipped() ? 0 : height - textHeight;
            y += style.font.getDescent();
        } else if ((labelAlign & Align.bottom) != 0) {
            y += cache.getFont().isFlipped() ? height - textHeight : 0;
            y -= style.font.getDescent();
        } else {
            y += (height - textHeight) / 2;
        }
        if (!cache.getFont().isFlipped()) y += textHeight;
        
        String formatted = Utils.leadingTruncate(font, text, ellipsis, textWidth);
        layout.setText(font, formatted, 0, formatted.length(), Color.WHITE, textWidth, lineAlign, wrap, null);
        cache.setText(layout, x, y);
    
        if (fontScaleChanged) font.getData().setScale(oldScaleX, oldScaleY);
    }
}
