/*
 * The MIT License
 *
 * Copyright (c) 2024 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.NinePatchWidget;
import com.ray3k.skincomposer.ResizeFourArrowListener;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopColorPicker.PopColorPickerAdapter;
import com.ray3k.stripe.ResizeWidget;
import com.ray3k.stripe.Spinner;

import java.io.File;
import java.util.Locale;

import static com.ray3k.skincomposer.Main.*;

/**
 *
 * @author Raymond
 */
public class Dialog9Patch extends Dialog {
    private final ResizeFourArrowListener horizontalResizeListener;
    private final ResizeFourArrowListener verticalResizeListener;
    private final ResizeFourArrowListener nwResizeListener;
    private final ResizeFourArrowListener neResizeListener;
    private NinePatch preview;
    private NinePatch previewZoomed;
    private int ninePatchContentTop, ninePatchContentBottom, ninePatchContentLeft, ninePatchContentRight;
    private int ninePatchTop, ninePatchBottom, ninePatchLeft, ninePatchRight;
    private int ninePatchContentTopOriginal, ninePatchContentBottomOriginal, ninePatchContentLeftOriginal, ninePatchContentRightOriginal;
    private int ninePatchTopOriginal, ninePatchBottomOriginal, ninePatchLeftOriginal, ninePatchRightOriginal;
    private Actor previewContentActor;
    private FileHandle loadedFile;
    private Array<Dialog9PatchListener> listeners;
    private FilesDroppedListener filesDroppedListener;
    private Color previewBGcolor;
    private ObjectMap<DrawableData, Drawable> drawablePairs;

    public Dialog9Patch(ObjectMap<DrawableData, Drawable> drawablePairs) {
        super("", skin, "dialog");
        previewBGcolor = new Color(Color.WHITE);
        listeners = new Array<>();
        this.drawablePairs = drawablePairs;
        
        horizontalResizeListener = new ResizeFourArrowListener(SystemCursor.HorizontalResize);
        
        verticalResizeListener = new ResizeFourArrowListener(SystemCursor.VerticalResize);
        
        nwResizeListener = new ResizeFourArrowListener(SystemCursor.NWSEResize);
        
        neResizeListener = new ResizeFourArrowListener(SystemCursor.NESWResize);

        filesDroppedListener = (Array<FileHandle> files) -> {
            if (files.size > 0 && files.first().extension().equalsIgnoreCase("png")) {
                Runnable runnable = () -> {
                    Gdx.app.postRunnable(() -> {
                        loadImage(files.first());
                    });
                };
                
                dialogFactory.showDialogLoading(runnable);
            }
        };
        
        desktopWorker.addFilesDroppedListener(filesDroppedListener);
        
        populate();
    }

    private void populate() {
        var root = getContentTable();
        root.clear();
        root.pad(10);

        var horizontalGroup = new HorizontalGroup();
        horizontalGroup.space(5.0f);
        horizontalGroup.wrap();
        horizontalGroup.wrapSpace(5.0f);
        horizontalGroup.center();
        root.add(horizontalGroup).growX();

        var textButton = new TextButton("Load Image", getSkin());
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showLoadImageDialog();
            }
        });

        textButton = new TextButton("Reset", getSkin());
        textButton.setName("reset-button");
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var widget = (NinePatchWidget) findActor("ninePatchWidget");

                var spinnerItem = (Spinner) findActor("spinner-padding-left");
                spinnerItem.setValue(ninePatchLeftOriginal);
                widget.setPaddingLeft(ninePatchLeftOriginal);
                ninePatchLeft = ninePatchLeftOriginal;

                spinnerItem = (Spinner) findActor("spinner-padding-right");
                spinnerItem.setValue(ninePatchRightOriginal);
                widget.setPaddingRight(ninePatchRightOriginal);
                ninePatchRight = ninePatchRightOriginal;

                spinnerItem = (Spinner) findActor("spinner-padding-bottom");
                spinnerItem.setValue(ninePatchBottomOriginal);
                widget.setPaddingBottom(ninePatchBottomOriginal);
                ninePatchBottom = ninePatchBottomOriginal;

                spinnerItem = (Spinner) findActor("spinner-padding-top");
                spinnerItem.setValue(ninePatchTopOriginal);
                widget.setPaddingTop(ninePatchTopOriginal);
                ninePatchTop = ninePatchTopOriginal;

                spinnerItem = (Spinner) findActor("spinner-content-left");
                spinnerItem.setValue(ninePatchContentLeftOriginal);
                widget.setContentLeft(ninePatchContentLeftOriginal);
                ninePatchContentLeft = ninePatchContentLeftOriginal;

                spinnerItem = (Spinner) findActor("spinner-content-right");
                spinnerItem.setValue(ninePatchContentRightOriginal);
                widget.setContentRight(ninePatchContentRightOriginal);
                ninePatchContentRight = ninePatchContentRightOriginal;

                spinnerItem = (Spinner) findActor("spinner-content-bottom");
                spinnerItem.setValue(ninePatchContentBottomOriginal);
                widget.setContentBottom(ninePatchContentBottomOriginal);
                ninePatchContentBottom = ninePatchContentBottomOriginal;

                spinnerItem = (Spinner) findActor("spinner-content-top");
                spinnerItem.setValue(ninePatchContentTopOriginal);
                widget.setContentTop(ninePatchContentTopOriginal);
                ninePatchContentTop = ninePatchContentTopOriginal;

                updatePreviewSplits();
            }
        });

        textButton = new TextButton("Auto Patches", getSkin());
        textButton.setName("auto-button");
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                autoPatches();
            }
        });

        textButton = new TextButton("Load Patches From File", getSkin());
        textButton.setName("load-patches-button");
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showLoadPatchesDialog();
            }
        });

        textButton = new TextButton("Batch Apply to File(s)", getSkin());
        textButton.setName("batch-button");
        horizontalGroup.addActor(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showBatchApplyDialog();
            }
        });

        root.row();
        var image = new Image(getSkin(), "welcome-separator");
        root.add(image).growX().space(15.0f);
        image.setScaling(Scaling.stretch);

        var top = new Table();
        top.setTouchable(Touchable.enabled);

        var bottom = new Table();
        bottom.setBackground(getSkin().getDrawable("white"));
        bottom.setTouchable(Touchable.enabled);

        root.row();
          var splitPane = new SplitPane(top, bottom, true, getSkin());
        splitPane.setName("split");
        root.add(splitPane).grow();

        splitPane.addListener(verticalResizeListener);

          var table = new Table();
        top.add(table).growX();

          var spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, getSkin());
        spinner.setName("spinner-padding-left");
        spinner.setValue(ninePatchLeft);
        spinner.setMinimum(0);
        table.add(spinner).expandX().left().padLeft(50.0f);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var widget = (NinePatchWidget) findActor("ninePatchWidget");
                var spinner = (Spinner) actor;

                ninePatchLeft = spinner.getValueAsInt();
                
                widget.setPaddingLeft(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(false);

                var otherSpinner = (Spinner) findActor("spinner-padding-right");
                otherSpinner.setMaximum(widget.getRegionWidth() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        var label = new Label("STRETCH REGION", getSkin());
        table.add(label);
        
        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL_FLIPPED, getSkin(), "horizontal-reversed");
        spinner.setName("spinner-padding-right");
        spinner.setValue(ninePatchRight);
        spinner.setMinimum(0);
        table.add(spinner).expandX().right().padRight(50.0f);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var widget = (NinePatchWidget) findActor("ninePatchWidget");
                var spinner = (Spinner) actor;

                ninePatchRight = spinner.getValueAsInt();

                widget.setPaddingRight(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(false);

                var otherSpinner = (Spinner) findActor("spinner-padding-left");
                otherSpinner.setMaximum(widget.getRegionWidth() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        top.row();
        table = new Table();
        top.add(table).grow();

          var subTable = new Table();
        table.add(subTable).growY();

        spinner = new Spinner(0, 1, true, Spinner.Orientation.VERTICAL_FLIPPED, getSkin(), "vertical-reversed");
        spinner.setName("spinner-padding-top");
        spinner.setValue(ninePatchTop);
        spinner.setMinimum(0);
        subTable.add(spinner).expandY().top();
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var widget = (NinePatchWidget) findActor("ninePatchWidget");
                var spinner = (Spinner) actor;

                ninePatchTop = spinner.getValueAsInt();

                widget.setPaddingTop(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(false);

                var otherSpinner = (Spinner) findActor("spinner-padding-bottom");
                otherSpinner.setMaximum(widget.getRegionHeight() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        subTable.row();
          var group = new Table();
        group.setTransform(true);
        subTable.add(group).size(0).minHeight(100.0f);

        label = new Label("STRETCH REGION", getSkin());
        group.add(label);
        group.setOriginY(50.0f);
        group.rotateBy(90.0f);

        subTable.row();
        spinner = new Spinner(0, 1, true, Spinner.Orientation.VERTICAL, getSkin(), "vertical");
        spinner.setName("spinner-padding-bottom");
        spinner.setValue(ninePatchBottom);
        spinner.setMinimum(0);
        subTable.add(spinner).expandY().bottom();
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                  var widget = (NinePatchWidget) findActor("ninePatchWidget");
                  var spinner = (Spinner) actor;

                ninePatchBottom = spinner.getValueAsInt();

                widget.setPaddingBottom(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(false);

                  var otherSpinner = (Spinner) findActor("spinner-padding-top");
                otherSpinner.setMaximum(widget.getRegionHeight() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        var ninePatchWidget = new NinePatchWidget(getSkin());
        ninePatchWidget.setName("ninePatchWidget");
        ninePatchWidget.setTouchable(Touchable.enabled);
        table.add(ninePatchWidget).grow().pad(5.0f);
        ninePatchWidget.getPaddingButton().addListener(handListener);
        ninePatchWidget.addListener((NinePatchWidget.HandleType handle, int value) -> {
            switch (handle) {
                case PADDING_LEFT:
                    var spinnerItem = (Spinner) findActor("spinner-padding-left");
                    spinnerItem.setValue(value);

                    ninePatchLeft = value;

                    var otherSpinner = (Spinner) findActor("spinner-padding-right");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionWidth() - value);
                    break;
                case PADDING_RIGHT:
                    spinnerItem = (Spinner) findActor("spinner-padding-right");
                    spinnerItem.setValue(value);

                    ninePatchRight = value;

                    otherSpinner = (Spinner) findActor("spinner-padding-left");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionWidth() - value);
                    break;
                case PADDING_BOTTOM:
                    spinnerItem = (Spinner) findActor("spinner-padding-bottom");
                    spinnerItem.setValue(value);

                    ninePatchBottom = value;

                    otherSpinner = (Spinner) findActor("spinner-padding-top");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionHeight() - value);
                    break;
                case PADDING_TOP:
                    spinnerItem = (Spinner) findActor("spinner-padding-top");
                    spinnerItem.setValue(value);

                    ninePatchTop = value;

                    otherSpinner = (Spinner) findActor("spinner-padding-bottom");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionHeight() - value);
                    break;
                case CONTENT_LEFT:
                    spinnerItem = (Spinner) findActor("spinner-content-left");
                    spinnerItem.setValue(value);

                    ninePatchContentLeft = value;

                    otherSpinner = (Spinner) findActor("spinner-content-right");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionWidth() - value);
                    break;
                case CONTENT_RIGHT:
                    spinnerItem = (Spinner) findActor("spinner-content-right");
                    spinnerItem.setValue(value);

                    ninePatchContentRight = value;

                    otherSpinner = (Spinner) findActor("spinner-content-left");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionWidth() - value);
                    break;
                case CONTENT_BOTTOM:
                    spinnerItem = (Spinner) findActor("spinner-content-bottom");
                    spinnerItem.setValue(value);

                    ninePatchContentBottom = value;

                    otherSpinner = (Spinner) findActor("spinner-content-top");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionHeight() - value);
                    break;
                case CONTENT_TOP:
                    spinnerItem = (Spinner) findActor("spinner-content-top");
                    spinnerItem.setValue(value);

                    ninePatchContentTop = value;

                    otherSpinner = (Spinner) findActor("spinner-content-bottom");
                    otherSpinner.setMaximum(ninePatchWidget.getRegionHeight() - value);
                    break;
            }

            updatePreviewSplits();
        });

          var inputListener = new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(ninePatchWidget);
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                var slider = (Slider) findActor("top-zoom");
                slider.setValue(slider.getValue() - amountY * 3);
                return true;
            }
        };
        ninePatchWidget.addCaptureListener(inputListener);

        subTable = new Table();
        table.add(subTable).growY();

        spinner = new Spinner(0, 1, true, Spinner.Orientation.VERTICAL_FLIPPED, getSkin(), "vertical-reversed");
        spinner.setName("spinner-content-top");
        spinner.setValue(ninePatchContentTop);
        spinner.setMinimum(0);
        subTable.add(spinner).expandY().top();
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var widget = (NinePatchWidget) findActor("ninePatchWidget");
                var spinner = (Spinner) actor;

                ninePatchContentTop = spinner.getValueAsInt();

                widget.setContentTop(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(true);

                var otherSpinner = (Spinner) findActor("spinner-content-bottom");
                otherSpinner.setMaximum(widget.getRegionHeight() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        subTable.row();
        group = new Table();
        group.setTransform(true);
        subTable.add(group).size(0).minHeight(100.0f);

        label = new Label("CONTENT", getSkin());
        group.add(label);
        group.setOriginY(50.0f);
        group.rotateBy(-90);

        subTable.row();
        spinner = new Spinner(0, 1, true, Spinner.Orientation.VERTICAL, getSkin(), "vertical");
        spinner.setName("spinner-content-bottom");
        spinner.setValue(ninePatchContentBottom);
        spinner.setMinimum(0);
        subTable.add(spinner).expandY().bottom();
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var widget = (NinePatchWidget) findActor("ninePatchWidget");
                var spinner = (Spinner) actor;

                ninePatchContentBottom = spinner.getValueAsInt();

                widget.setContentBottom(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(true);

                var otherSpinner = (Spinner) findActor("spinner-content-top");
                otherSpinner.setMaximum(widget.getRegionHeight() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        top.row().padBottom(15.0f);
        table = new Table();
        top.add(table).growX();

        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, getSkin());
        spinner.setName("spinner-content-left");
        spinner.setValue(ninePatchContentLeft);
        spinner.setMinimum(0);
        table.add(spinner).expandX().left().padLeft(50.0f);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                 var widget = (NinePatchWidget) findActor("ninePatchWidget");
                var spinner = (Spinner) actor;

                ninePatchContentLeft = spinner.getValueAsInt();

                widget.setContentLeft(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(true);

                  var otherSpinner = (Spinner) findActor("spinner-content-right");
                otherSpinner.setMaximum(widget.getRegionWidth() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        label = new Label("CONTENT", getSkin());
        table.add(label);

        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL_FLIPPED, getSkin(), "horizontal-reversed");
        spinner.setName("spinner-content-right");
        spinner.setValue(ninePatchContentRight);
        spinner.setMinimum(0);
        table.add(spinner).expandX().right().padRight(50.0f);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var widget = (NinePatchWidget) findActor("ninePatchWidget");
                var spinner = (Spinner) actor;

                ninePatchContentRight = spinner.getValueAsInt();

                widget.setContentRight(spinner.getValueAsInt());
                widget.getPaddingButton().setChecked(true);

                var otherSpinner = (Spinner) findActor("spinner-content-left");
                otherSpinner.setMaximum(widget.getRegionWidth() - spinner.getValueAsInt());

                updatePreviewSplits();
            }
        });

        top.row();
        table = new Table();
        table.pad(5);
        top.add(table).growX();

        table.defaults().space(5);
        var imageButton = new ImageButton(getSkin(), "grid-light");
        imageButton.setName("grid-light");
        imageButton.setProgrammaticChangeEvents(false);
        table.add(imageButton);
        imageButton.addListener(handListener);

        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                  var widget = (NinePatchWidget) findActor("ninePatchWidget");
                if (((ImageButton) actor).isChecked()) {
                    widget.setGridType(NinePatchWidget.GridType.LIGHT);
                } else {
                    widget.setGridType(NinePatchWidget.GridType.NONE);
                }

                ((ImageButton) findActor("grid-dark")).setChecked(false);
            }
        });

        imageButton = new ImageButton(getSkin(), "grid-dark");
        imageButton.setName("grid-dark");
        imageButton.setProgrammaticChangeEvents(false);
        table.add(imageButton);
        imageButton.addListener(handListener);

        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                  var widget = (NinePatchWidget) findActor("ninePatchWidget");
                if (((ImageButton) actor).isChecked()) {
                    widget.setGridType(NinePatchWidget.GridType.DARK);
                } else {
                    widget.setGridType(NinePatchWidget.GridType.NONE);
                }

                ((ImageButton) findActor("grid-light")).setChecked(false);
            }
        });

        imageButton = new ImageButton(getSkin(), "resize");
        table.add(imageButton).expandX().right();
        imageButton.addListener(handListener);

        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                zoomAndRecenter();
            }
        });

        var slider = new Slider(1, 50, 1, false, getSkin(), "zoom-horizontal");
        slider.setName("top-zoom");
        table.add(slider);
        slider.addListener(handListener);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                  var slider = (Slider) findActor("top-zoom");
                ninePatchWidget.setZoom((int) slider.getValue());
            }
        });

        label = new Label("Preview", getSkin(), "title");
        bottom.add(label);

        bottom.row();

        var resizer = new ResizeWidget(null, getSkin());
        resizer.setTouchable(Touchable.enabled);
        resizer.setName("resizer");
        table = new Table();
        resizer.setActor(table);
        if (previewZoomed == null) {
            resizer.setMinWidth(100);
            resizer.setMinHeight(100);
        } else {
            resizer.setMinWidth(previewZoomed.getTotalWidth());
            resizer.setMinHeight(previewZoomed.getTotalHeight());
            table.setBackground(new NinePatchDrawable(previewZoomed));

        }
        resizer.setResizingFromCenter(true);
        resizer.getBottomLeftHandle().addListener(neResizeListener);
        resizer.getBottomRightHandle().addListener(nwResizeListener);
        resizer.getTopLeftHandle().addListener(nwResizeListener);
        resizer.getTopRightHandle().addListener(neResizeListener);
        resizer.getBottomHandle().addListener(verticalResizeListener);
        resizer.getTopHandle().addListener(verticalResizeListener);
        resizer.getLeftHandle().addListener(horizontalResizeListener);
        resizer.getRightHandle().addListener(horizontalResizeListener);
        resizer.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(resizer);
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                  var slider = (Slider) findActor("bottom-zoom");
                slider.setValue(slider.getValue() - amountY * 5);
                return true;
            }
        });

        var scrollPane = new ScrollPane(resizer, getSkin());
        scrollPane.setName("scroll");
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        bottom.add(scrollPane).grow();

        bottom.row();
        table = new Table();
        table.padLeft(5);
        table.padRight(5);
        bottom.add(table).growX();

        table.defaults().space(5);
        label = new Label("Content:", getSkin());
        table.add(label);

        var selectBox = new SelectBox<String>(getSkin());
        selectBox.setName("contentSelectBox");
        table.add(selectBox);
        selectBox.setItems("None", "Text", "Color", "Drawable");
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        selectBox.getList().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectBox.fire(new ChangeListener.ChangeEvent());
            }
        });
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                switch (selectBox.getSelected()) {
                    case "None":
                        updatePreviewContentActor(null);
                        break;
                    case "Text":
                        dialogFactory.showInputDialog("Text Content", "Enter the text to be displayed inside of the preview:", "Lorem Ipsum", new DialogFactory.InputDialogListener() {
                            @Override
                            public void confirmed(String text) {
                                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                                    if (colorData == null) {
                                        selectBox.setSelected("None");
                                    } else {
                                          var label = new Label(text, getSkin(), "white");
                                        label.setAlignment(Align.center);
                                        label.setColor(colorData.color);
                                        label.setUserObject("Text");

                                        updatePreviewContentActor(label);
                                    }
                                }, null);
                            }

                            @Override
                            public void cancelled() {
                                selectBox.setSelected("None");
                            }
                        });
                        break;
                    case "Color":
                        dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                            if (colorData == null) {
                                selectBox.setSelected("None");
                            } else {
                                  var image = new Image(getSkin(), "white");
                                image.setScaling(Scaling.stretch);
                                image.setColor(colorData.color);
                                image.setUserObject("Color");

                                updatePreviewContentActor(image);
                            }
                        }, null);
                        break;
                    case "Drawable":
                          var dialog = dialogFactory.showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
                            @Override
                            public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                                var image = new Image(drawablePairs.get(drawable));
                                image.setScaling(Scaling.none);
                                image.setUserObject("Drawable");

                                updatePreviewContentActor(image);
                            }

                            @Override
                            public void emptied(DialogDrawables dialog) {
                                selectBox.setSelected("None");
                            }

                            @Override
                            public void cancelled(DialogDrawables dialog) {
                                selectBox.setSelected("None");
                            }
                        }, null);

                        dialog.setShowing9patchButton(false);
                        break;
                }
            }
        });
        
        imageButton = new ImageButton(getSkin(), "color");
        table.add(imageButton);
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColorPicker(previewBGcolor, new PopColorPickerAdapter() {
                    @Override
                    public void picked(Color color) {
                        previewBGcolor.set(color);
                        bottom.setColor(color);
                    }
                });
            }
        });
        var toolTip = (Main.makeTooltip("Background color for preview pane.", tooltipManager, getSkin()));
        imageButton.addListener(toolTip);

        slider = new Slider(1.0f, 100.0f, 1.0f, false, getSkin(), "zoom-horizontal");
        slider.setName("bottom-zoom");
        slider.setValue(1.0f);
        table.add(slider).expandX().right();
        slider.addListener(handListener);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                  var slider = (Slider) findActor("bottom-zoom");
                  var zoom = slider.getValue();

                if (previewZoomed != null) {
                    previewZoomed.setLeftWidth(preview.getLeftWidth() * zoom);
                    previewZoomed.setRightWidth(preview.getRightWidth() * zoom);
                    previewZoomed.setTopHeight(preview.getTopHeight() * zoom);
                    previewZoomed.setBottomHeight(preview.getBottomHeight() * zoom);

                    if (preview.getPadLeft() != -1) {
                        previewZoomed.setPadLeft(preview.getPadLeft() * zoom);
                    }
                    if (preview.getPadRight() != -1) {
                        previewZoomed.setPadRight(preview.getPadRight() * zoom);
                    }
                    if (preview.getPadTop() != -1) {
                        previewZoomed.setPadTop(preview.getPadTop() * zoom);
                    }
                    if (preview.getPadBottom() != -1) {
                        previewZoomed.setPadBottom(preview.getPadBottom() * zoom);
                    }

                      var table = new Table();
                    resizer.setActor(table);
                    resizer.setMinWidth(previewZoomed.getTotalWidth() + previewZoomed.getPadLeft() + preview.getPadRight());
                    resizer.setMinHeight(previewZoomed.getTotalHeight() + previewZoomed.getPadTop() + preview.getPadBottom());
                    table.setBackground(new NinePatchDrawable(previewZoomed));

                    updatePreviewContentActor(previewContentActor);
                    if (previewContentActor != null) {

                          var selectBox = (SelectBox<String>) findActor("contentSelectBox");

                          var listeners = new Array<EventListener>(selectBox.getListeners());
                        for (  var listener : listeners) {
                            if (listener instanceof ChangeListener) {
                                selectBox.removeListener(listener);
                            }
                        }

                        selectBox.setSelected((String) previewContentActor.getUserObject());

                        for (  var listener : listeners) {
                            if (listener instanceof ChangeListener) {
                                selectBox.addListener(listener);
                            }
                        }
                    }
                }
            }
        });

        getButtonTable().clearChildren();
        getButtonTable().pad(10);
        getButtonTable().defaults().space(10).minWidth(100);

        textButton = new TextButton("Save", getSkin());
        textButton.setName("save-button");
        getButtonTable().add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                dialogFactory.showDialogLoading(() -> {
                    String defaultPath = loadedFile.path();
                    if (!defaultPath.toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                        defaultPath = loadedFile.sibling(loadedFile.nameWithoutExtension() + ".9.png").path();
                    }

                    File file = desktopWorker.saveDialog("Save nine patch file as...", defaultPath, "9.png", "Nine Patch files");
                    if (file != null) {
                        Gdx.app.postRunnable(() -> {
                            FileHandle fileHandle = new FileHandle(file);
                            if (fileHandle.extension() == null || !fileHandle.name().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                                fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".9.png");
                            }
                            saveNinePatch(fileHandle);
                            projectData.setLastDrawablePath(fileHandle.parent().path() + "/");
                            hide();

                            for (var listener : listeners) {
                                listener.fileSaved(fileHandle);
                            }
                        });
                    }
                });
            }
        });

        textButton = new TextButton("Cancel", getSkin());
        button(textButton, false);
        textButton.addListener(handListener);

        key(Keys.ESCAPE, false);
        
        updateDisabled();
    }

    @Override
    protected void result(Object object) {
        if (!(Boolean) object) {
            for (var listener : listeners) {
                listener.cancelled();
            }
        }
    }

    @Override
    public boolean remove() {
        desktopWorker.removeFilesDroppedListener(filesDroppedListener);
        return super.remove();
    }

    private void updatePreviewSplits() {
        if (preview != null) {
            preview = new NinePatch(preview.getTexture(), ninePatchLeft, ninePatchRight, ninePatchTop, ninePatchBottom);
            preview.setPadding(ninePatchContentLeft, ninePatchContentRight, ninePatchContentTop, ninePatchContentBottom);
            previewZoomed = new NinePatch(preview);

            var resizer = (ResizeWidget) findActor("resizer");
            var table = (Table) resizer.getActor();
            table.setBackground(new NinePatchDrawable(previewZoomed));
        }
    }

    private void updatePreviewContentActor(Actor actor) {
        previewContentActor = actor;
        var resizer = (ResizeWidget) findActor("resizer");
        var table = (Table) resizer.getActor();
        table.clearChildren();

        if (actor != null && preview != null) {
            table.add(actor).grow();
            resizer.getStack().pack();
            resizer.setMinWidth(actor.getWidth() + previewZoomed.getTotalWidth() + previewZoomed.getPadLeft() + preview.getPadRight());
            resizer.setMinHeight(actor.getHeight() + previewZoomed.getTotalHeight() + previewZoomed.getPadTop() + preview.getPadBottom());
            table.pack();
        }
    }

    @Override
    public Dialog show(Stage stage) {
        super.show(stage);
        ((SplitPane) findActor("split")).setSplitAmount(.75f);
        validate();

        stage.setScrollFocus(findActor("scroll"));

        return this;
    }

    private void showLoadImageDialog() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastDrawablePath();

            File file = desktopWorker.openDialog("Open Image...", defaultPath, "png,jpg", "Image files");
            if (file != null) {
                Gdx.app.postRunnable(() -> {
                    var fileHandle = new FileHandle(file);
                    loadImage(fileHandle);
                });
            }
        };

        dialogFactory.showDialogLoading(runnable);
    }
    
    private void loadImage(FileHandle fileHandle) {
        loadedFile = fileHandle;

        ninePatchLeft = 0;
        ninePatchRight = 0;
        ninePatchTop = 0;
        ninePatchBottom = 0;
        ninePatchContentLeft = 0;
        ninePatchContentRight = 0;
        ninePatchContentTop = 0;
        ninePatchContentBottom = 0;

        var pixmap = new Pixmap(fileHandle);

        if (fileHandle.nameWithoutExtension().endsWith(".9") && pixmap.getWidth() >= 3 && pixmap.getHeight() >= 3) {
            var cropped = new Pixmap(pixmap.getWidth() - 2, pixmap.getHeight() - 2, Pixmap.Format.RGBA8888);

            cropped.setBlending(Pixmap.Blending.None);
            cropped.drawPixmap(pixmap, 0, 0, 1, 1, pixmap.getWidth() - 2, pixmap.getHeight() - 2);

            for (int x = 1; x < pixmap.getWidth() - 2; x++) {
                  var color = new Color(pixmap.getPixel(x, pixmap.getHeight() - 1));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentLeft = x - 1;
                    break;
                }
            }

            for (int x = pixmap.getWidth() - 2; x > 0; x--) {
                  var color = new Color(pixmap.getPixel(x, pixmap.getHeight() - 1));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentRight = cropped.getWidth() - x;
                    break;
                }
            }

            for (int y = 1; y < pixmap.getHeight() - 2; y++) {
                  var color = new Color(pixmap.getPixel(0, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchTop = y - 1;
                    break;
                }
            }

            for (int y = pixmap.getHeight() - 2; y > 0; y--) {
                  var color = new Color(pixmap.getPixel(0, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchBottom = cropped.getHeight() - y;
                    break;
                }
            }

            for (int x = 1; x < pixmap.getWidth() - 2; x++) {
                  var color = new Color(pixmap.getPixel(x, 0));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchLeft = x - 1;
                    break;
                }
            }

            for (int x = pixmap.getWidth() - 2; x > 0; x--) {
                  var color = new Color(pixmap.getPixel(x, 0));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchRight = cropped.getWidth() - x;
                    break;
                }
            }

            for (int y = 1; y < pixmap.getHeight() - 2; y++) {
                  var color = new Color(pixmap.getPixel(pixmap.getWidth() - 1, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentTop = y - 1;
                    break;
                }
            }

            for (int y = pixmap.getHeight() - 2; y > 0; y--) {
                  var color = new Color(pixmap.getPixel(pixmap.getWidth() - 1, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentBottom = cropped.getHeight() - y;
                    break;
                }
            }
            pixmap.dispose();
            pixmap = cropped;
        }

        ninePatchLeftOriginal = ninePatchLeft;
        ninePatchRightOriginal = ninePatchRight;
        ninePatchBottomOriginal = ninePatchBottom;
        ninePatchTopOriginal = ninePatchTop;
        ninePatchContentLeftOriginal = ninePatchContentLeft;
        ninePatchContentRightOriginal = ninePatchContentRight;
        ninePatchContentBottomOriginal = ninePatchContentBottom;
        ninePatchContentTopOriginal = ninePatchContentTop;

        var texture = new Texture(pixmap);
        preview = new NinePatch(texture, ninePatchLeft, ninePatchRight, ninePatchTop, ninePatchBottom);
        preview.setPadding(ninePatchContentLeft, ninePatchContentRight, ninePatchContentTop, ninePatchContentBottom);
        previewZoomed = new NinePatch(preview);
        pixmap.dispose();
        populate();

        updateDisabled();

        updatePreviewContentActor(previewContentActor);
        if (previewContentActor != null) {
            var selectBox = (SelectBox<String>) findActor("contentSelectBox");

            var listeners = new Array<EventListener>(selectBox.getListeners());
            for (  var listener : listeners) {
                if (listener instanceof ChangeListener) {
                    selectBox.removeListener(listener);
                }
            }

            selectBox.setSelected((String) previewContentActor.getUserObject());

            for (  var listener : listeners) {
                if (listener instanceof ChangeListener) {
                    selectBox.addListener(listener);
                }
            }
        }

        var ninePatchWidget = (NinePatchWidget) findActor("ninePatchWidget");
        var existingDrawable = ninePatchWidget.getDrawable();
        if (existingDrawable != null) {
            ((TextureRegionDrawable) existingDrawable).getRegion().getTexture().dispose();
        }
        var region = new TextureRegion(texture);
        ninePatchWidget.setDrawable(new TextureRegionDrawable(region));
        ninePatchWidget.setRegionWidth(region.getRegionWidth());
        ninePatchWidget.setRegionHeight(region.getRegionHeight());

        ninePatchWidget.setPaddingLeft(ninePatchLeft);
        ninePatchWidget.setPaddingRight(ninePatchRight);
        ninePatchWidget.setPaddingBottom(ninePatchBottom);
        ninePatchWidget.setPaddingTop(ninePatchTop);
        ninePatchWidget.setContentLeft(ninePatchContentLeft);
        ninePatchWidget.setContentRight(ninePatchContentRight);
        ninePatchWidget.setContentBottom(ninePatchContentBottom);
        ninePatchWidget.setContentTop(ninePatchContentTop);

        zoomAndRecenter();

        var spinner = (Spinner) findActor("spinner-padding-left");
        spinner.setMaximum(region.getRegionWidth() - ninePatchRight);

        spinner = (Spinner) findActor("spinner-padding-right");
        spinner.setMaximum(region.getRegionWidth() - ninePatchLeft);

        spinner = (Spinner) findActor("spinner-padding-bottom");
        spinner.setMaximum(region.getRegionWidth() - ninePatchTop);

        spinner = (Spinner) findActor("spinner-padding-top");
        spinner.setMaximum(region.getRegionWidth() - ninePatchBottom);

        spinner = (Spinner) findActor("spinner-content-left");
        spinner.setMaximum(region.getRegionWidth() - ninePatchContentRight);

        spinner = (Spinner) findActor("spinner-content-right");
        spinner.setMaximum(region.getRegionWidth() - ninePatchContentLeft);

        spinner = (Spinner) findActor("spinner-content-bottom");
        spinner.setMaximum(region.getRegionWidth() - ninePatchContentTop);

        spinner = (Spinner) findActor("spinner-content-top");
        spinner.setMaximum(region.getRegionWidth() - ninePatchContentBottom);

        projectData.setLastDrawablePath(fileHandle.parent().path() + "/");
    }
    
    private void saveNinePatch(FileHandle fileHandle) {
        saveNinePatch(loadedFile, fileHandle, ninePatchLeft, ninePatchRight, ninePatchBottom, ninePatchTop, ninePatchContentLeft, ninePatchContentRight, ninePatchContentBottom, ninePatchContentTop);
    }
    
    private void saveNinePatch(FileHandle originalFile, FileHandle targetFile, int ninePatchLeft, int ninePatchRight, int ninePatchBottom, int ninePatchTop, int ninePatchContentLeft, int ninePatchContentRight, int ninePatchContentBottom, int ninePatchContentTop) {
        var originalImage = new Pixmap(originalFile);
        
        if (originalFile.path().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
            var cropped = new Pixmap(originalImage.getWidth() - 2, originalImage.getHeight() - 2, Pixmap.Format.RGBA8888);
            cropped.setBlending(Pixmap.Blending.None);
            
            cropped.drawPixmap(originalImage, 0, 0, 1, 1, originalImage.getWidth() - 2, originalImage.getHeight() - 2);
            originalImage.dispose();
            originalImage = cropped;
        }
        
        var savePixmap = new Pixmap(originalImage.getWidth() + 2, originalImage.getHeight() + 2, Pixmap.Format.RGBA8888);
        savePixmap.setBlending(Pixmap.Blending.None);
        savePixmap.drawPixmap(originalImage, 1, 1);
        
        savePixmap.setColor(Color.BLACK);
        savePixmap.drawRectangle(ninePatchLeft + 1, 0, savePixmap.getWidth() - ninePatchLeft - ninePatchRight - 2, 1);
        savePixmap.drawRectangle(0, ninePatchTop + 1, 1, savePixmap.getHeight() - ninePatchBottom - ninePatchTop - 2);
        savePixmap.drawRectangle(ninePatchContentLeft + 1, savePixmap.getHeight() - 1, savePixmap.getWidth() - ninePatchContentLeft - ninePatchContentRight - 2, 1);
        savePixmap.drawRectangle(savePixmap.getWidth() - 1, ninePatchContentTop + 1, 1, savePixmap.getHeight() - ninePatchContentBottom - ninePatchContentTop - 2);
        
        PixmapIO.writePNG(targetFile, savePixmap);
        
        originalImage.dispose();
        savePixmap.dispose();
    }

    private void zoomAndRecenter() {
        pack();
        var widget = (NinePatchWidget) findActor("ninePatchWidget");
        var slider = (Slider) findActor("top-zoom");
        
        var widthRatio = MathUtils.floor(widget.getWidth() / (widget.getRegionWidth() + 4));
        var heightRatio = MathUtils.floor(widget.getHeight() / (widget.getRegionHeight() + 4));
        slider.setValue(Math.min(widthRatio, heightRatio));
        
        widget.setPositionX(-widget.getRegionWidth() / 2.0f);
        widget.setPositionY(-widget.getRegionHeight() / 2.0f);
    }
    
    public static interface Dialog9PatchListener {
        public void fileSaved(FileHandle fileHandle);
        public void cancelled();
    }
    
    public void addDialog9PatchListener(Dialog9PatchListener listener) {
        listeners.add(listener);
    }
    
    public void removeDialog9PatchListener(Dialog9PatchListener listener) {
        listeners.removeValue(listener, false);
    }

    public Array<Dialog9PatchListener> getDialog9PatchListeners() {
        return new Array<>(listeners);
    }
    
    private void autoPatches() {
        var originalImage = new Pixmap(loadedFile);
        
        if (loadedFile.path().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
            var cropped = new Pixmap(originalImage.getWidth() - 2, originalImage.getHeight() - 2, Pixmap.Format.RGBA8888);
            cropped.setBlending(Pixmap.Blending.None);
            
            cropped.drawPixmap(originalImage, 0, 0, 1, 1, originalImage.getWidth() - 2, originalImage.getHeight() - 2);
            originalImage.dispose();
            originalImage = cropped;
        }
        
        var patches = Utils.calculatePatches(originalImage);
        ninePatchLeft = patches.left;
        ninePatchRight = patches.right;
        ninePatchTop = patches.top;
        ninePatchBottom = patches.bottom;
        
        var widget = (NinePatchWidget) findActor("ninePatchWidget");
        var spinnerItem = (Spinner) findActor("spinner-padding-left");
        spinnerItem.setValue(ninePatchLeft);
        widget.setPaddingLeft(ninePatchLeft);
        
        ninePatchContentLeft = ninePatchLeft;
        spinnerItem = findActor("spinner-content-left");
        spinnerItem.setValue(ninePatchContentLeft);
        widget.setContentLeft(ninePatchContentLeft);
        
        spinnerItem = findActor("spinner-padding-right");
        spinnerItem.setValue(ninePatchRight);
        widget.setPaddingRight(ninePatchRight);
        
        ninePatchContentRight = ninePatchRight;
        spinnerItem = findActor("spinner-content-right");
        spinnerItem.setValue(ninePatchContentRight);
        widget.setContentRight(ninePatchContentRight);
        
        spinnerItem = findActor("spinner-padding-top");
        spinnerItem.setValue(ninePatchTop);
        widget.setPaddingTop(ninePatchTop);
        
        ninePatchContentTop = ninePatchTop;
        spinnerItem = findActor("spinner-content-top");
        spinnerItem.setValue(ninePatchContentTop);
        widget.setContentTop(ninePatchContentTop);
        
        spinnerItem = (Spinner) findActor("spinner-padding-bottom");
        spinnerItem.setValue(ninePatchBottom);
        widget.setPaddingBottom(ninePatchBottom);
        
        ninePatchContentBottom = ninePatchBottom;
        spinnerItem = (Spinner) findActor("spinner-content-bottom");
        spinnerItem.setValue(ninePatchContentBottom);
        widget.setContentBottom(ninePatchContentBottom);
        
        updatePreviewSplits();
    }
    
    private void showLoadPatchesDialog() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastDrawablePath();

            File file = desktopWorker.openDialog("Load Patches from File...", defaultPath, "9.png", "Nine Patch files");
            if (file != null) {
                Gdx.app.postRunnable(() -> {
                    var fileHandle = new FileHandle(file);
                    loadPatches(fileHandle);
                });
            }
        };

        dialogFactory.showDialogLoading(runnable);
    }
    
    private void loadPatches(FileHandle fileHandle) {
        ninePatchLeft = 0;
        ninePatchRight = 0;
        ninePatchTop = 0;
        ninePatchBottom = 0;
        ninePatchContentLeft = 0;
        ninePatchContentRight = 0;
        ninePatchContentTop = 0;
        ninePatchContentBottom = 0;
        
        var pixmap = new Pixmap(fileHandle);

        if (fileHandle.nameWithoutExtension().endsWith(".9") && pixmap.getWidth() >= 3 && pixmap.getHeight() >= 3) {
            var cropped = new Pixmap(pixmap.getWidth() - 2, pixmap.getHeight() - 2, Pixmap.Format.RGBA8888);

            cropped.setBlending(Pixmap.Blending.None);
            cropped.drawPixmap(pixmap, 0, 0, 1, 1, pixmap.getWidth() - 2, pixmap.getHeight() - 2);

            for (int x = 1; x < pixmap.getWidth() - 2; x++) {
                  var color = new Color(pixmap.getPixel(x, pixmap.getHeight() - 1));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentLeft = x - 1;
                    break;
                }
            }

            for (int x = pixmap.getWidth() - 2; x > 0; x--) {
                  var color = new Color(pixmap.getPixel(x, pixmap.getHeight() - 1));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentRight = cropped.getWidth() - x;
                    break;
                }
            }

            for (int y = 1; y < pixmap.getHeight() - 2; y++) {
                  var color = new Color(pixmap.getPixel(0, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchTop = y - 1;
                    break;
                }
            }

            for (int y = pixmap.getHeight() - 2; y > 0; y--) {
                  var color = new Color(pixmap.getPixel(0, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchBottom = cropped.getHeight() - y;
                    break;
                }
            }

            for (int x = 1; x < pixmap.getWidth() - 2; x++) {
                  var color = new Color(pixmap.getPixel(x, 0));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchLeft = x - 1;
                    break;
                }
            }

            for (int x = pixmap.getWidth() - 2; x > 0; x--) {
                  var color = new Color(pixmap.getPixel(x, 0));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchRight = cropped.getWidth() - x;
                    break;
                }
            }

            for (int y = 1; y < pixmap.getHeight() - 2; y++) {
                  var color = new Color(pixmap.getPixel(pixmap.getWidth() - 1, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentTop = y - 1;
                    break;
                }
            }

            for (int y = pixmap.getHeight() - 2; y > 0; y--) {
                  var color = new Color(pixmap.getPixel(pixmap.getWidth() - 1, y));
                if (color.a > 0) {
                    if (color.r != 0 || color.g != 0 || color.b != 0) {
                        break;
                    }

                    ninePatchContentBottom = cropped.getHeight() - y;
                    break;
                }
            }
            pixmap.dispose();
            cropped.dispose();
            
            var widget = (NinePatchWidget) findActor("ninePatchWidget");

            var spinnerItem = (Spinner) findActor("spinner-padding-left");
            spinnerItem.setValue(ninePatchLeft);
            widget.setPaddingLeft(ninePatchLeft);

            spinnerItem = (Spinner) findActor("spinner-padding-right");
            spinnerItem.setValue(ninePatchRight);
            widget.setPaddingRight(ninePatchRight);

            spinnerItem = (Spinner) findActor("spinner-padding-bottom");
            spinnerItem.setValue(ninePatchBottom);
            widget.setPaddingBottom(ninePatchBottom);

            spinnerItem = (Spinner) findActor("spinner-padding-top");
            spinnerItem.setValue(ninePatchTop);
            widget.setPaddingTop(ninePatchTop);

            spinnerItem = (Spinner) findActor("spinner-content-left");
            spinnerItem.setValue(ninePatchContentLeft);
            widget.setContentLeft(ninePatchContentLeft);

            spinnerItem = (Spinner) findActor("spinner-content-right");
            spinnerItem.setValue(ninePatchContentRight);
            widget.setContentRight(ninePatchContentRight);

            spinnerItem = (Spinner) findActor("spinner-content-bottom");
            spinnerItem.setValue(ninePatchContentBottom);
            widget.setContentBottom(ninePatchContentBottom);

            spinnerItem = (Spinner) findActor("spinner-content-top");
            spinnerItem.setValue(ninePatchContentTop);
            widget.setContentTop(ninePatchContentTop);

            updatePreviewSplits();
        }
    }
    
    private void showBatchApplyDialog() {
        Runnable runnable = () -> {
            String defaultPath = projectData.getLastDrawablePath();
            

            var files = desktopWorker.openMultipleDialog("Batch apply to files", defaultPath, "png,jpg", "Image files");
            if (files != null && files.size() > 0) {
                Gdx.app.postRunnable(() -> {
                    var fileHandles = new Array<FileHandle>();
                    for (var file : files) {
                        var fileHandle = new FileHandle(file);
                        fileHandles.add(fileHandle);
                    }
                    batchApply(fileHandles);
                });
            }
        };

        dialogFactory.showDialogLoading(runnable);
    }
    
    private void batchApply(Array<FileHandle> fileHandles) {
        for (var fileHandle : fileHandles) {
            var targetFile = fileHandle;
            if (!fileHandle.name().toLowerCase(Locale.ROOT).endsWith(".9.png")) {
                targetFile = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".9.png");
            }
            saveNinePatch(fileHandle, targetFile, ninePatchLeft, ninePatchRight, ninePatchBottom, ninePatchTop, ninePatchContentLeft, ninePatchContentRight, ninePatchContentBottom, ninePatchContentTop);
        }
    }
    
    private void updateDisabled() {
        var disabled = loadedFile == null;
        var textButton = (TextButton) findActor("reset-button");
        textButton.setDisabled(disabled);

        textButton = (TextButton) findActor("auto-button");
        textButton.setDisabled(disabled);

        textButton = (TextButton) findActor("load-patches-button");
        textButton.setDisabled(disabled);

        textButton = (TextButton) findActor("batch-button");
        textButton.setDisabled(disabled);
        
        textButton = (TextButton) findActor("save-button");
        textButton.setDisabled(disabled);
    }
}
