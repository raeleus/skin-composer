package com.ray3k.skincomposer.dialog.tenpatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.ray3k.skincomposer.HandListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.dialog.DialogDrawables;
import com.ray3k.skincomposer.dialog.DialogDrawables.FilterOptions;
import com.ray3k.skincomposer.dialog.DialogFactory.ConfirmationListener;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.stripe.Spinner;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.AlphanumComparator;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.Comparator;
import java.util.regex.Pattern;

import static com.ray3k.skincomposer.Main.*;

public class DialogTenPatchAnimation extends Dialog {
    private DrawableData drawableData;
    private DrawableData workingData;
    private Array<DrawableData> drawableDatas;
    private ObjectMap<DrawableData, TextureRegion> drawableToRegionMap;
    private ButtonGroup<Button> buttonGroup;
    private int lastClicked;
    private static final AlphanumComparator alphanumComparator = new AlphanumComparator();
    private TenPatchDrawable animatedDrawable;
    
    public DialogTenPatchAnimation(DrawableData drawableData) {
        super("TenPatch Animation", skin, "bg");
        drawableToRegionMap = new ObjectMap<>();
        lastClicked = 0;
        this.drawableData = drawableData;
        workingData = new DrawableData(drawableData);
        drawableDatas = new Array<>();
        
        for (var name : workingData.tenPatchData.regionNames) {
            drawableDatas.add(atlasData.getDrawable(name));
    
            if (!drawableToRegionMap.containsKey(drawableData)) {
                var data = atlasData.getDrawable(name);
                var region = atlasData.getAtlas().findRegion(name);
                drawableToRegionMap.put(data, region);
            }
        }
        
        this.setFillParent(true);
        getTitleTable().padLeft(5);
        
        var root = getContentTable();
        root.pad(10);
        
        Table top = new Table();
        top.setTouchable(Touchable.enabled);
        
        Table bottom = new Table();
        bottom.setTouchable(Touchable.enabled);
        bottom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getStage().setKeyboardFocus(DialogTenPatchAnimation.this);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        
        var splitPane = new SplitPane(top, bottom, true, skin);
        root.add(splitPane).grow();
        splitPane.addListener(verticalResizeArrowListener);
        
        top.defaults().space(5);
        top.padBottom(5);
        var label = new Label("Animation", skin, "black");
        top.add(label).growX();
        
        top.row();
        
        animatedDrawable = new TenPatchDrawable();
        animatedDrawable.horizontalStretchAreas = new int[]{};
        animatedDrawable.verticalStretchAreas = new int[]{};
    
        String name = drawableData.file.nameWithoutExtension();
        var matcher = Pattern.compile(".*(?=\\.9$)").matcher(name);
        if (matcher.find()) {
            name = matcher.group();
        }
        animatedDrawable.setRegion(atlasData.getAtlas().findRegion(name));
        var table = new Table();
        table.setTouchable(Touchable.enabled);
        table.setName("animation-table");
        table.setBackground(skin.newDrawable("white",Color.CLEAR));
        ScrollPane scrollPane = new ScrollPane(table, skin, "animation");
        scrollPane.setName("animation-scroll-pane");
        scrollPane.setFadeScrollBars(false);
        top.add(scrollPane).grow();
        scrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(event.getListenerActor());
            }
        });
    
        Image image = new Image(animatedDrawable);
        image.setName("animated-image");
        image.setScaling(Scaling.none);
        table.add(image);
        table.addListener(handListener);
        table.addListener(new ClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animatedDrawable.setTime(0);
            }
        });
        
        top.row();
        table = new Table();
        top.add(table);
        
        table.defaults().space(5);
        label = new Label("Frame Duration:", skin);
        table.add(label);
        
        var spinner = new Spinner(0, .01f, false, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setMinimum(0.0f);
        spinner.setValue(workingData.tenPatchData.frameDuration);
        table.add(spinner).minWidth(150);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.frameDuration = (float) spinner.getValue();
                animatedDrawable.setFrameDuration(workingData.tenPatchData.frameDuration);
            }
        });
        spinner.getTextField().addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Input.Keys.TAB) {
                    getStage().setKeyboardFocus(DialogTenPatchAnimation.this);
                }
                return super.keyDown(event, keycode);
            }
        });
    
        label = new Label("Play Mode:", skin);
        table.add(label).padLeft(15);
        
        var selectBox = new SelectBox<String>(skin);
        selectBox.setItems("Normal", "Reversed", "Loop", "Loop Reversed", "Loop Ping-Pong", "Loop Random");
        selectBox.setSelectedIndex(workingData.tenPatchData.playMode);
        table.add(selectBox);
        selectBox.addListener(new HandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.playMode = selectBox.getSelectedIndex();
                refreshAnimation();
            }
        });
    
        label = new Label("Preview BG:", skin);
        table.add(label).padLeft(15);
    
        var imageButton = new ImageButton(skin, "color");
        table.add(imageButton);
        imageButton.addListener(handListener);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                    Table table = findActor("animation-table");
                    if (colorData == null) {
                        table.setBackground(skin.newDrawable("white", Color.CLEAR));
                    } else {
                        table.setBackground(skin.newDrawable("white", colorData.color));
                    }
                }, null);
            }
        });
        
        bottom.defaults().space(5);
        bottom.padTop(5);
        label = new Label("Frames", skin, "black");
        bottom.add(label).growX();
        
        bottom.row();
        table = new Table();
        bottom.add(table).grow();
        
        table.defaults().space(5);
        var subTable = new Table();
        subTable.setName("scroll-table");
        scrollPane = new ScrollPane(subTable, skin, "animation");
        scrollPane.setName("scroll-pane");
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(true, false);
        table.add(scrollPane).grow();
        scrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(event.getListenerActor());
            }
        });
    
        buttonGroup = new ButtonGroup<Button>();
        buttonGroup.setMaxCheckCount(-1);
        buttonGroup.setMinCheckCount(-1);
        
        subTable = new Table();
        table.add(subTable);
    
        subTable.defaults().space(5);
        
        var button = new Button(skin, "move-frame-left");
        subTable.add(button);
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveLeft();
            }
        });
        var textTooltip = (Main.makeTooltip("LEFT ARROW", tooltipManager, skin));
        button.addListener(textTooltip);
        
        button = new Button(skin, "move-frame-right");
        subTable.add(button);
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveRight();
            }
        });
        textTooltip = (Main.makeTooltip("RIGHT ARROW", tooltipManager, skin));
        button.addListener(textTooltip);
        
        subTable.row();
        button = new Button(skin, "remove-frame");
        subTable.add(button);
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eraseSelection();
            }
        });
        textTooltip = (Main.makeTooltip("DEL", tooltipManager, skin));
        button.addListener(textTooltip);
    
        button = new Button(skin, "add-frame");
        subTable.add(button);
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showRegionDialog();
            }
        });
    
        subTable.defaults().uniform().fill().colspan(2);
        subTable.row();
        var textButton = new TextButton("Duplicate", skin);
        subTable.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                duplicateSelection();
            }
        });
        textTooltip = (Main.makeTooltip("CTRL + D", tooltipManager, skin));
        textButton.addListener(textTooltip);
    
        subTable.row();
        textButton = new TextButton("Reverse", skin);
        subTable.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                reverseSelection();
            }
        });
    
        subTable.row();
        textButton = new TextButton("Sort A-Z", skin);
        subTable.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sortSelectionAscending();
            }
        });
    
        subTable.row();
        textButton = new TextButton("Sort Z-A", skin);
        subTable.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sortSelectionDescending();
            }
        });
    
        getButtonTable().pad(10);
        getButtonTable().defaults().uniform().fill();
        textButton = new TextButton("OK", skin);
        button(textButton, true);
        textButton.addListener(handListener);
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, false);
        textButton.addListener(handListener);
        
        key(Input.Keys.ESCAPE, false).key(Input.Keys.ENTER, true).key(Keys.NUMPAD_ENTER, true);
        addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (getStage().getKeyboardFocus().equals(DialogTenPatchAnimation.this)) {
                    if (keycode == Input.Keys.A) {
                        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                                deselectAll();
                            } else {
                                selectAll();
                            }
                        }
                    } else if (keycode == Input.Keys.FORWARD_DEL) {
                        eraseSelection();
                    } else if (keycode == Input.Keys.D) {
                        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                            duplicateSelection();
                        }
                    } else if (keycode == Input.Keys.LEFT) {
                        moveLeft();
                    } else if (keycode == Input.Keys.RIGHT) {
                        moveRight();
                    }
                }
                return true;
            }
        });
        
        refresh();
    }
    
    public void moveLeft() {
        var selectedIndexes = new Array<Integer>();
        var newIndexes = new Array<Integer>();
    
        for (var button : buttonGroup.getAllChecked()) {
            selectedIndexes.add(buttonGroup.getButtons().indexOf(button, false));
        }
    
        selectedIndexes.sort();
        for (var index : selectedIndexes) {
            if (index - 1 >= 0 && !buttonGroup.getButtons().get(index - 1).isChecked()) {
                buttonGroup.getButtons().swap(index, index - 1);
                drawableDatas.swap(index, index - 1);
                newIndexes.add(index - 1);
            } else {
                newIndexes.add(index);
            }
        }
        
        if (selectedIndexes.size > 0) {
            lastClicked = newIndexes.first();
        }
        
        refresh(newIndexes);
    }
    
    public void moveRight() {
        var selectedIndexes = new Array<Integer>();
        var newIndexes = new Array<Integer>();
    
        for (var button : buttonGroup.getAllChecked()) {
            selectedIndexes.add(buttonGroup.getButtons().indexOf(button, false));
        }
    
        selectedIndexes.sort();
        selectedIndexes.reverse();
        for (var index : selectedIndexes) {
            if (index + 1 < buttonGroup.getButtons().size && !buttonGroup.getButtons().get(index + 1).isChecked()) {
                buttonGroup.getButtons().swap(index, index + 1);
                drawableDatas.swap(index, index + 1);
                newIndexes.add(index + 1);
            } else {
                newIndexes.add(index);
            }
        }
    
        if (selectedIndexes.size > 0) {
            lastClicked = newIndexes.first();
        }
        
        refresh(newIndexes);
    }
    
    public void addRegion(DrawableData drawableData) {
        int highest = -1;
    
        for (var button : buttonGroup.getAllChecked()) {
            int index = buttonGroup.getButtons().indexOf(button, false);
            if (index > highest) highest = index;
        }
    
        if (highest == -1) highest = buttonGroup.getButtons().size - 1;
        drawableDatas.insert(highest + 1, drawableData);
        
        var selectedIndexes = new Array<Integer>();
        selectedIndexes.add(highest + 1);
        lastClicked = highest + 1;
    
        var iter = drawableDatas.iterator();
        while (iter.hasNext()) {
            var drawable = iter.next();
            if (!atlasData.getDrawablePairs().containsKey(drawable)) {
                iter.remove();
            }
        }
    
        if (!drawableToRegionMap.containsKey(drawableData)) {
            String name = drawableData.file.nameWithoutExtension();
            var matcher = Pattern.compile(".*(?=\\.9$)").matcher(name);
            if (matcher.find()) {
                name = matcher.group();
            }
            var region = atlasData.getAtlas().findRegion(name);
            drawableToRegionMap.put(drawableData, region);
        }
        
        refresh(selectedIndexes);
    }
    
    public void addRegions(Array<DrawableData> drawableDatas) {
        var selectedIndexes = new Array<Integer>();
    
        int highest = -1;
    
        for (var button : buttonGroup.getAllChecked()) {
            int index = buttonGroup.getButtons().indexOf(button, false);
            if (index > highest) highest = index;
        }
    
        if (highest == -1) highest = buttonGroup.getButtons().size - 1;
        
        for (var drawableData : drawableDatas) {
            this.drawableDatas.insert(++highest, drawableData);
            
            selectedIndexes.add(highest);
            lastClicked = highest;
    
            if (!drawableToRegionMap.containsKey(drawableData)) {
                String name = drawableData.file.nameWithoutExtension();
                var matcher = Pattern.compile(".*(?=\\.9$)").matcher(name);
                if (matcher.find()) {
                    name = matcher.group();
                }
                var region = atlasData.getAtlas().findRegion(name);
                drawableToRegionMap.put(drawableData, region);
            }
        }
    
        var iter = drawableDatas.iterator();
        while (iter.hasNext()) {
            var drawable = iter.next();
            if (!atlasData.getDrawablePairs().containsKey(drawable)) {
                iter.remove();
            }
        }
        
        refresh(selectedIndexes);
    }
    
    public void eraseSelection() {
        var selectedIndexes = new Array<Integer>();
    
        for (var button : buttonGroup.getAllChecked()) {
            selectedIndexes.add(buttonGroup.getButtons().indexOf(button, false));
        }
        
        selectedIndexes.sort();
        selectedIndexes.reverse();
        for (var index : selectedIndexes) {
            drawableDatas.removeIndex(index);
        }
    
        refresh();
    }
    
    public void reverseSelection() {
        var selectedIndexes = new Array<Integer>();
        var checked = new Array<>(buttonGroup.getAllChecked());
        
        for (var button : checked) {
            selectedIndexes.add(buttonGroup.getButtons().indexOf(button, false));
        }
        
        selectedIndexes.reverse();
        checked.reverse();
        for (var index : selectedIndexes) {
            drawableDatas.removeIndex(index);
            drawableDatas.insert(index, (DrawableData) checked.pop().getUserObject());
        }
        
        refresh(selectedIndexes);
    }
    
    public void duplicateSelection() {
        var selectedIndexes = new Array<Integer>();
        int highest = -1;
    
        for (var button : buttonGroup.getAllChecked()) {
            var index = buttonGroup.getButtons().indexOf(button, false);
            selectedIndexes.add(index);
            if (index > highest) highest = index;
        }
        
        if (highest == -1) highest = drawableDatas.size - 1;
        selectedIndexes.sort();
        selectedIndexes.reverse();
        for (var index : selectedIndexes) {
            var region = drawableDatas.get(index);
            drawableDatas.insert(highest + 1, region);
        }
        
        var newIndexes = new Array<Integer>();
        for (int i = 0; i < selectedIndexes.size; i++) {
            newIndexes.add(highest + 1 + i);
        }
    
        if (selectedIndexes.size > 0) {
            lastClicked = selectedIndexes.first();
        }
        
        refresh(newIndexes);
    }
    
    public void sortSelectionAscending() {
        var selectedIndexes = new Array<Integer>();
        var regionsToSort = new Array<DrawableData>();
    
        for (var button : buttonGroup.getAllChecked()) {
            var index = buttonGroup.getButtons().indexOf(button, false);
            selectedIndexes.add(index);
            regionsToSort.add(drawableDatas.get(index));
        }
        
        regionsToSort.sort(new Comparator<DrawableData>() {
            @Override
            public int compare(DrawableData o1, DrawableData o2) {
                return alphanumComparator.compare(o1.name, o2.name);
            }
        });
    
        selectedIndexes.sort();
        int i = 0;
        for (var index : selectedIndexes) {
            drawableDatas.removeIndex(index);
            drawableDatas.insert(index, regionsToSort.get(i++));
        }
    
        refresh(selectedIndexes);
    }
    
    public void sortSelectionDescending() {
        var selectedIndexes = new Array<Integer>();
        var regionsToSort = new Array<DrawableData>();
        
        for (var button : buttonGroup.getAllChecked()) {
            var index = buttonGroup.getButtons().indexOf(button, false);
            selectedIndexes.add(index);
            regionsToSort.add(drawableDatas.get(index));
        }
        
        regionsToSort.sort(new Comparator<DrawableData>() {
            @Override
            public int compare(DrawableData o1, DrawableData o2) {
                return alphanumComparator.compare(o2.name, o1.name);
            }
        });
        
        selectedIndexes.sort();
        int i = 0;
        for (var index : selectedIndexes) {
            drawableDatas.removeIndex(index);
            drawableDatas.insert(index, regionsToSort.get(i++));
        }
        
        refresh(selectedIndexes);
    }
    
    public void selectAll() {
        for (var button : buttonGroup.getButtons()) {
            Button check = button.findActor("check");
            check.setChecked(true);
            button.setChecked(true);
        }
    }
    
    public void deselectAll() {
        for (var button : buttonGroup.getButtons()) {
            Button check = button.findActor("check");
            check.setChecked(false);
            button.setChecked(false);
        }
    }
    
    public void refresh() {
        refresh(new Array<>());
    }
    
    public void refresh(Array<Integer> selectedIndexes) {
        refreshAnimation();
        refreshFrames(selectedIndexes);
    }
    
    public void refreshAnimation() {
        workingData.tenPatchData.regionNames.clear();
        if (workingData.tenPatchData.regions == null) workingData.tenPatchData.regions = new Array<>();
        workingData.tenPatchData.regions.clear();
        var drawables = new Array<TextureRegion>();
        for (var data : drawableDatas) {
            var region = drawableToRegionMap.get(data);
            
            drawables.add(region);
            workingData.tenPatchData.regionNames.add(data.name);
            workingData.tenPatchData.regions.add(region);
        }
        animatedDrawable.setRegions(drawables);
        animatedDrawable.setFrameDuration(workingData.tenPatchData.frameDuration);
        animatedDrawable.setPlayMode(workingData.tenPatchData.playMode);
        animatedDrawable.setTime(0);
        Image image = findActor("animated-image");
        image.layout();
    }
    
    public void refreshFrames() {
        refreshFrames(new Array<>());
    }
    
    public void refreshFrames(Array<Integer> selectedIndexes) {
        Table table = findActor("scroll-table");
        table.clear();
        buttonGroup.clear();
        
        var i = 0;
        for (var region : drawableDatas) {
            var fixDuplicateTouchListener = new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    event.setBubbles(false);
                    return super.touchDown(event, x, y, pointer, button);
                }
            };
            
            var button = new Button(skin, "toggle");
            button.setName("button");
            button.setUserObject(region);
            button.setProgrammaticChangeEvents(false);
            table.add(button).size(150, 200).space(5);
            buttonGroup.add(button);
            button.addListener(handListener);
    
            final var check = new Button(skin, "check");
            check.setName("check");
            check.setProgrammaticChangeEvents(false);
            button.add(check).uniform();
            check.setTouchable(Touchable.disabled);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    var buttons = buttonGroup.getButtons();
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                        var allChecked = buttonGroup.getAllChecked();
                        allChecked.removeValue(button, false);
                        if (allChecked.size > 0) {
                            int index = buttons.indexOf(button, false);
                            int high = buttons.indexOf(allChecked.peek(), false);
                            int low = buttons.indexOf(allChecked.first(), false);
                            
                            for (var button : buttons) {
                                button.setChecked(false);
                                Button check = button.findActor("check");
                                check.setChecked(false);
                            }
                            
                            for (int i = Math.min(index, lastClicked); i <= Math.max(index, lastClicked); i++) {
                                var button = buttons.get(i);
                                button.setChecked(true);
                                Button check = button.findActor("check");
                                check.setChecked(true);
                            }
                        }
                    } else if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
                        for (var button : buttons) {
                            Button check = button.findActor("check");
                            check.setChecked(false);
                            button.setChecked(false);
                        }
                        button.setChecked(true);
                    }
                    
                    check.setChecked(button.isChecked());
                    lastClicked = buttons.indexOf(button, false);
                }
            });
            
            var label = new Label(Integer.toString(i++), skin, "white");
            button.add(label).expandX();
            
            button.add().uniform();
            
            button.row();
            Image image = new Image(atlasData.getDrawablePairs().get(region));
            image.setScaling(Scaling.fit);
            button.add(image).colspan(3).expand();
            
            button.row();
            label = new Label(region.name, skin, "white");
            label.setAlignment(Align.center);
            label.setEllipsis("...");
            button.add(label).colspan(3).width(125f);
            var textTooltip = (Main.makeTooltip(region.name, tooltipManager, skin));
            label.addListener(textTooltip);
        }
        
        Button firstButton = null;
        Button lastButton = null;
        
        for (var index : selectedIndexes) {
            var button = buttonGroup.getButtons().get(index);
            Button check = button.findActor("check");
            check.setChecked(true);
            button.setChecked(true);
            if (firstButton == null) firstButton = button;
            lastButton = button;
        }
        
        ScrollPane scrollPane = findActor("scroll-pane");
        scrollPane.layout();
        table.layout();
        if (firstButton != null) {
            scrollPane.scrollTo(firstButton.getX(), firstButton.getY(), lastButton.getX() - firstButton.getX() + lastButton.getWidth(), firstButton.getHeight());
        }
    }
    
    public void showRegionDialog() {
        var filterOptions = new FilterOptions();
        filterOptions.set(DialogDrawables.filterOptions);
        
        var dialog = dialogFactory.showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
            @Override
            public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                if (drawable.file == null) {
                    dialogFactory.showMessageDialog("Incorrect drawable!", "Incorrect drawable type! Select a \"texture\" drawable.", null);
                } else {
                    atlasData.produceAtlas();
                    
                    var pattern = Pattern.compile(".+(?=_\\d+$)");
                    var matcher = pattern.matcher(drawable.name);
                    if (matcher.find()) {
                        var name = matcher.group();
    
                        var matches = new Array<DrawableData>();
                        for (var drawableData : atlasData.getDrawablePairs().keys()) {
                            if (drawableData.name.matches(Pattern.quote(name) + "_\\d+")) {
                                matches.add(drawableData);
                            }
                        }

                        if (matches.size > 1) {
                            dialogFactory.yesNoDialog("Add all frames?", "Do you want to add all the frames of this animation?", new ConfirmationListener() {
                                @Override
                                public void selected(int selection) {
                                    if (selection == 0) {
                                        matches.sort(new Comparator<DrawableData>() {
                                            @Override
                                            public int compare(DrawableData o1, DrawableData o2) {
                                                return alphanumComparator.compare(o1.name, o2.name);
                                            }
                                        });
                                        
                                        addRegions(matches);
                                    } else {
                                        addRegion(drawable);
                                    }
                                }
                            }, new DialogListener() {
                                @Override
                                public void opened() {
        
                                }
    
                                @Override
                                public void closed() {
                                    getStage().setKeyboardFocus(DialogTenPatchAnimation.this);
                                }
                            });
                        } else {
                            addRegion(drawable);
                        }
                    } else {
                        addRegion(drawable);
                    }
                }
    
                DialogDrawables.filterOptions.set(filterOptions);
            }
        
            @Override
            public void emptied(DialogDrawables dialog) {
                DialogDrawables.filterOptions.set(filterOptions);
            }
        
            @Override
            public void cancelled(DialogDrawables dialog) {
                DialogDrawables.filterOptions.set(filterOptions);
            }
        }, null);
    
        dialog.setShowing9patchButton(false);
        dialog.setShowingOptions(false);
        var tempFilterOptions = new DialogDrawables.FilterOptions();
        tempFilterOptions.texture = true;
        tempFilterOptions.ninePatch = true;
        tempFilterOptions.custom = false;
        tempFilterOptions.tiled = false;
        tempFilterOptions.tinted = false;
        tempFilterOptions.tenPatch = false;
        dialog.setFilterOptions(tempFilterOptions);
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        super.show(stage, action);
        getStage().setScrollFocus(DialogTenPatchAnimation.this.findActor("scroll-pane"));
        return this;
    }
    
    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            drawableData.set(workingData);
            fire(new DialogTenPatchAnimationEvent(drawableData));
        }
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        animatedDrawable.update(delta);
    }
    
    public static class DialogTenPatchAnimationEvent extends Event {
        public DrawableData drawableData;
    
        public DialogTenPatchAnimationEvent(DrawableData drawableData) {
            this.drawableData = drawableData;
        }
    }
    
    public abstract static class DialogTenPatchAnimationListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof DialogTenPatchAnimationEvent) {
                animationUpdated((DialogTenPatchAnimationEvent) event);
            }
            return false;
        }
        
        public abstract void animationUpdated(DialogTenPatchAnimationEvent event);
    }
}
