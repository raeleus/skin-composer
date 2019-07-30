package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.AlphanumComparator;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.Comparator;
import java.util.regex.Pattern;

public class DialogTenPatchAnimation extends Dialog {
    private DrawableData drawableData;
    private DrawableData workingData;
    private Skin skin;
    private Main main;
    private Array<DrawableData> drawableDatas;
    private ButtonGroup<Button> buttonGroup;
    private int lastClicked;
    private static final AlphanumComparator alphanumComparator = new AlphanumComparator();
    private DialogDrawables dialogDrawables;
    private TenPatchDrawable animatedDrawable;
    
    public DialogTenPatchAnimation(DrawableData drawableData, Skin skin, Main main, DialogDrawables dialogDrawables) {
        super("TenPatch Animation", skin, "bg");
        lastClicked = 0;
        this.drawableData = drawableData;
        workingData = new DrawableData(drawableData);
        this.skin = skin;
        this.main = main;
        this.dialogDrawables = dialogDrawables;
        drawableDatas = new Array<>();
        
        for (var name : workingData.tenPatchData.regionNames) {
            drawableDatas.add(main.getAtlasData().getDrawable(name));
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
        splitPane.addListener(main.getVerticalResizeArrowListener());
        
        top.defaults().space(5);
        top.padBottom(5);
        var label = new Label("Animation", skin, "black");
        top.add(label).growX();
        
        top.row();
        
        animatedDrawable = new TenPatchDrawable();
        animatedDrawable.horizontalStretchAreas = new int[]{};
        animatedDrawable.verticalStretchAreas = new int[]{};
        animatedDrawable.setRegion(((SpriteDrawable) dialogDrawables.drawablePairs.get(main.getAtlasData().getDrawable(drawableData.file.nameWithoutExtension()))).getSprite());
        var table = new Table();
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
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
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
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.TAB) {
                    getStage().setKeyboardFocus(DialogTenPatchAnimation.this);
                }
                return super.keyDown(event, keycode);
            }
        });
    
        label = new Label("Preview BG:", skin);
        table.add(label).padLeft(15);
    
        var imageButton = new ImageButton(skin, "color");
        table.add(imageButton);
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {
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
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveLeft();
            }
        });
        var textTooltip = new TextTooltip("LEFT ARROW", main.getTooltipManager(), skin);
        button.addListener(textTooltip);
        
        button = new Button(skin, "move-frame-right");
        subTable.add(button);
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                moveRight();
            }
        });
        textTooltip = new TextTooltip("RIGHT ARROW", main.getTooltipManager(), skin);
        button.addListener(textTooltip);
        
        subTable.row();
        button = new Button(skin, "remove-frame");
        subTable.add(button);
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                eraseSelection();
            }
        });
        textTooltip = new TextTooltip("DEL", main.getTooltipManager(), skin);
        button.addListener(textTooltip);
    
        button = new Button(skin, "add-frame");
        subTable.add(button);
        button.addListener(main.getHandListener());
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
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                duplicateSelection();
            }
        });
        textTooltip = new TextTooltip("CTRL + D", main.getTooltipManager(), skin);
        textButton.addListener(textTooltip);
    
        subTable.row();
        textButton = new TextButton("Reverse", skin);
        subTable.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                reverseSelection();
            }
        });
    
        subTable.row();
        textButton = new TextButton("Sort A-Z", skin);
        subTable.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sortSelectionAscending();
            }
        });
    
        subTable.row();
        textButton = new TextButton("Sort Z-A", skin);
        subTable.add(textButton);
        textButton.addListener(main.getHandListener());
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
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, false);
        textButton.addListener(main.getHandListener());
        
        key(Input.Keys.ESCAPE, false).key(Input.Keys.ENTER, true);
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
        var iter = drawableDatas.iterator();
        while (iter.hasNext()) {
            var drawable = iter.next();
            if (!dialogDrawables.drawablePairs.containsKey(drawable)) {
                iter.remove();
            }
        }
        
        refreshAnimation();
        refreshFrames(selectedIndexes);
    }
    
    public void refreshAnimation() {
        workingData.tenPatchData.regionNames.clear();
        var drawables = new Array<TextureRegion>();
        for (var data : drawableDatas) {
            drawables.add(((SpriteDrawable)dialogDrawables.drawablePairs.get(data)).getSprite());
            workingData.tenPatchData.regionNames.add(data.name);
        }
        animatedDrawable.setRegions(drawables);
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
            button.addListener(main.getHandListener());
    
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
            Image image = new Image(dialogDrawables.drawablePairs.get(region));
            image.setScaling(Scaling.fit);
            button.add(image).colspan(3).expand();
            
            button.row();
            label = new Label(region.name, skin, "white");
            label.setAlignment(Align.center);
            label.setEllipsis("...");
            button.add(label).colspan(3).width(125f);
            var textTooltip = new TextTooltip(region.name, main.getTooltipManager(), skin);
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
        var dialog = main.getDialogFactory().showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
            @Override
            public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                if (drawable.file == null) {
                    main.getDialogFactory().showMessageDialog("Incorrect drawable!", "Incorrect drawable type! Select a \"texture\" drawable.", null);
                } else {
                    DialogTenPatchAnimation.this.dialogDrawables.produceAtlas();
                    
                    var pattern = Pattern.compile(".+(?=_\\d+$)");
                    var matcher = pattern.matcher(drawable.name);
                    if (matcher.find()) {
                        var name = matcher.group();
    
                        var matches = new Array<DrawableData>();
                        for (var drawableData : dialogDrawables.drawablePairs.keys()) {
                            if (drawableData.name.matches(Pattern.quote(name) + "_\\d+")) {
                                matches.add(drawableData);
                            }
                        }

                        if (matches.size > 1) {
                            main.getDialogFactory().yesNoDialog("Add all frames?", "Do you want to add all the frames of this animation?", new DialogFactory.ConfirmationListener() {
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
            }
        
            @Override
            public void emptied(DialogDrawables dialog) {
            
            }
        
            @Override
            public void cancelled(DialogDrawables dialog) {
            
            }
        }, null);
    
        dialog.setShowing9patchButton(false);
        dialog.setShowingOptions(false);
        var filterOptions = new DialogDrawables.FilterOptions();
        filterOptions.applied = true;
        filterOptions.texture = true;
        filterOptions.ninePatch = true;
        filterOptions.custom = false;
        filterOptions.tiled = false;
        filterOptions.tinted = false;
        filterOptions.tenPatch = false;
        dialog.setFilterOptions(filterOptions);
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
