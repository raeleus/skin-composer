package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTouchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimVisible;
import com.ray3k.stripe.PopTable;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.dialog.DialogDrawables;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents.WidgetType;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.Interpol;
import com.ray3k.stripe.PopTableClickListener;
import space.earlygrey.shapedrawer.scene2d.GraphDrawerDrawable;

public class GeneralListeners {
    public static EventListener widgetResetListener(String name, Runnable runnable) {
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to reset this " + name + "?", DialogSceneComposer.skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", DialogSceneComposer.skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Resets the settings of the " + name + " to the defaults.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                runnable.run();
            }
        });
        
        return popTableClickListener;
    }
    
    public static EventListener widgetDeleteListener(String name, Runnable runnable) {
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin);
        var popTable = popTableClickListener.getPopTable();
        
        var label = new Label("Are you sure you want to delete this " + name + "?", DialogSceneComposer.skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", DialogSceneComposer.skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Removes this " + name + " from its parent.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                popTable.hide();
                runnable.run();
            }
        });
        
        return popTableClickListener;
    }
    
    public static PopTableClickListener setWidgetListener(final DialogSceneComposer dialogSceneComposer,
                                                                   WidgetSelectedListener widgetSelectedListener) {
        var table = new Table();
        var scrollPane = new ScrollPane(table, DialogSceneComposer.skin, "scene");
        var scrollFocus = scrollPane;
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(DialogSceneComposer.main.getScrollFocusListener());
        
        
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
    
                var popTable = getPopTable();
                popTable.setWidth(popTable.getPrefWidth() + 50);
                popTable.validate();
                
                dialogSceneComposer.getStage().setScrollFocus(scrollFocus);
            }
        };
    
        var popTable = popTableClickListener.getPopTable();
        var label = new Label("Widgets:", DialogSceneComposer.skin, "scene-label-colored");
        popTable.add(label);
        label.addListener(new TextTooltip("Widgets are interactive components of your UI.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
    
        label = new Label("Layout:", DialogSceneComposer.skin, "scene-label-colored");
        popTable.add(label);
        label.addListener(new TextTooltip("Layout widgets help organize the components of your UI and make it more adaptable to varying screen size.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
    
        popTable.row();
        popTable.defaults().top();
        popTable.add(scrollPane).grow();
    
        var textButton = new TextButton("Button", DialogSceneComposer.skin, "scene-med");
        var valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(Button.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Buttons are the most basic component to UI design. These are clickable widgets that can perform a certain action such as starting a game or activating a power.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("CheckBox", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(CheckBox.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("CheckBoxes are great for setting/displaying boolean values for an options screen.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.CHECK_BOX, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Image", DialogSceneComposer.skin, "scene-med");
        table.add(textButton);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Images are not directly interactable elements of a layout, but are necessary to showcase graphics or pictures in your UI. Scaling options make them a very powerful tool.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.IMAGE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ImageButton", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(ImageButton.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("A Button with an image graphic in it. The image can change depending on the state of the button.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.IMAGE_BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ImageTextButton", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(ImageTextButton.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("A Button with an image graphic followed by text in it. The image and text color can change depending on the state of the button.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.IMAGE_TEXT_BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Label", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(Label.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("The most common way to display text in your layouts. Wrapping and ellipses options help mitigate sizing issues in small spaces.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.LABEL, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("List", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(List.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("List presents text options in a clickable menu.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.LIST, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ProgressBar", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(ProgressBar.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Commonly used to display loading progress or as a health/mana indicator in HUD's.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.PROGRESS_BAR, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("SelectBox", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(SelectBox.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("SelectBox is a kind of button that displays a selectable option list when opened.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SELECT_BOX, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Slider", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(Slider.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Slider is a kind of user interactable ProgressBar that allows a user to select a value along a sliding scale.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SLIDER, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("TextButton", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(TextButton.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("A kind of button that contains a text element inside of it. The text color can change depending on the state of the button.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TEXT_BUTTON, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("TextField", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(TextField.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("TextFields are the primary way of getting text input from the user.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TEXT_FIELD, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("TextArea", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(TextField.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("TextAreas are a multiline version of a TextField.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TEXT_AREA, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Touchpad", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(Touchpad.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Touchpad is a UI element common to mobile games. It is used lieu of keyboard input, for example.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TOUCH_PAD, popTable);
            }
        });
    
        table = new Table();
        scrollPane = new ScrollPane(table, DialogSceneComposer.skin, "scene");
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        popTable.add(scrollPane);
        scrollPane.addListener(DialogSceneComposer.main.getScrollFocusListener());
    
        table.row();
        textButton = new TextButton("Container", DialogSceneComposer.skin, "scene-med");
        table.add(textButton);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Container is like a lightweight, single cell version of Table.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.CONTAINER, popTable);
            }
        });
        
        table.row();
        textButton = new TextButton("HorizontalGroup", DialogSceneComposer.skin, "scene-med");
        table.add(textButton);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Allows layout of multiple elements horizontally. It is most useful for its wrap functionality, which cannot be achieved with a Table.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.HORIZONTAL_GROUP, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ScrollPane", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(ScrollPane.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Creates a scrollable layout for your widgets. It is commonly used to adapt the UI to variable content and screen sizes.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SCROLL_PANE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Stack", DialogSceneComposer.skin, "scene-med");
        table.add(textButton);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Allows stacking of elements on top of each other.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.STACK, popTable);
            }
        });
        
        table.row();
        textButton = new TextButton("SplitPane", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(SplitPane.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("An organizational layout that allows the user to adjust the width or height of two widgets next to each other.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.SPLIT_PANE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Table", DialogSceneComposer.skin, "scene-med");
        table.add(textButton);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("The most powerful layout widget available. Consisting of a series of configurable cells, it organizes elements in rows and columns. It serves as the basis of all layout design in Scene2D.UI.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TABLE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Tree", DialogSceneComposer.skin, "scene-med");
        valid = DialogSceneComposer.main.getJsonData().classHasValidStyles(Tree.class);
        textButton.setDisabled(!valid);
        table.add(textButton);
        if (valid) textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Tree is an organizational widget that allows collapsing and expanding elements like file structures.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TREE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("VerticalGroup", DialogSceneComposer.skin, "scene-med");
        table.add(textButton);
        textButton.addListener(DialogSceneComposer.main.getHandListener());
        textButton.addListener(new TextTooltip("Allows layout of multiple elements vertically. It is most useful for its wrap functionality, which cannot be achieved with a Table.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.VERTICAL_GROUP, popTable);
            }
        });
        
        return popTableClickListener;
    }
    
    public static EventListener selectDrawableListener(DrawableData originalDrawable, String toolTipText,
                                                       DrawableSelected drawableSelected) {
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
    
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
        
                var label = new Label("Drawable:", DialogSceneComposer.skin, "scene-label-colored");
                popTable.add(label);
        
                popTable.row();
                var stack = new Stack();
                popTable.add(stack).minSize(100).maxSize(300).grow();
                var background = new Image(DialogSceneComposer.skin, "scene-tile-ten");
                stack.add(background);
                Image image;
                if (originalDrawable != null) {
                    image = new Image(DialogSceneComposer.main.getAtlasData().drawablePairs.get(originalDrawable));
                } else {
                    image = new Image((Drawable) null);
                }
                stack.add(image);
        
                popTable.row();
                var textButton = new TextButton("Select Drawable", DialogSceneComposer.skin, "scene-small");
                popTable.add(textButton).minWidth(100);
                textButton.addListener(DialogSceneComposer.main.getHandListener());
                textButton.addListener(new TextTooltip(toolTipText, DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                textButton.addListener(new ChangeListener() {
                    boolean confirmed;
                    DrawableData drawableData;
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        DialogSceneComposer.main.getDialogFactory().showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
                            @Override
                            public void confirmed(DrawableData drawable, DialogDrawables dialog) {
                                confirmed = true;
                                drawableData = drawable;
                            }
    
                            @Override
                            public void emptied(DialogDrawables dialog) {
                                confirmed = false;
                            }
    
                            @Override
                            public void cancelled(DialogDrawables dialog) {
        
                            }
                        }, new DialogListener() {
                            @Override
                            public void opened() {
    
                            }
    
                            @Override
                            public void closed() {
                                if (confirmed) {
                                    drawableSelected.selected(drawableData);
                                    image.setDrawable(DialogSceneComposer.main.getAtlasData().drawablePairs.get(drawableData));
                                } else {
                                    drawableSelected.selected(null);
                                    image.setDrawable(null);
                                }
                            }
                        });
                    }
                });
            }
        };
    
        popTableClickListener.update();
    
        return popTableClickListener;
    }
    
    public static PopTableClickListener interpolationListener(final DialogSceneComposer dialogSceneComposer,
                                                              InterpolationSelected interpolationSelected) {
        var graphDrawerDrawables = new Array<GraphDrawerDrawable>();
        
        var table = new Table();
        var scrollPane = new ScrollPane(table, DialogSceneComposer.skin, "scene");
        var listener = new PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void tableShown(Event event) {
                dialogSceneComposer.getStage().setScrollFocus(scrollPane);
                for (var graphDrawerDrawable : graphDrawerDrawables) {
                    graphDrawerDrawable.setColor(Color.BLACK);
                }
            }
    
            @Override
            public void tableHidden(Event event) {
                for (var graphDrawerDrawable : graphDrawerDrawables) {
                    graphDrawerDrawable.setColor(Color.CLEAR);
                }
            }
        };
        var popTable = listener.getPopTable();
        
        scrollPane.setFadeScrollBars(false);
        popTable.add(scrollPane);
        
        table.defaults().space(5);
        for (Interpol interpol : Interpol.values()) {
            var button = new Button(DialogSceneComposer.skin, "scene-med");
            table.add(button).growX();
            
            var stack = new Stack();
            button.add(stack).size(50);
            
            var image = new Image(DialogSceneComposer.skin.getDrawable("white"));
            stack.add(image);
            
            var graphDrawerDrawable = new GraphDrawerDrawable(dialogSceneComposer.graphDrawer);
            graphDrawerDrawable.setColor(Color.BLACK);
            graphDrawerDrawable.setInterpolation(interpol.interpolation);
            graphDrawerDrawable.setSamples(10);
            graphDrawerDrawables.add(graphDrawerDrawable);
            image = new Image(graphDrawerDrawable);
            var container = new Container(image);
            container.pad(5).fill();
            stack.add(container);
            
            var label = new Label(interpol.toString(), DialogSceneComposer.skin, "scene-label");
            button.add(label).expandX().left().space(5);
            button.addListener(DialogSceneComposer.main.getHandListener());
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    popTable.hide();
                    interpolationSelected.selected(interpol);
                }
            });
            
            table.row();
        }
        
        dialogSceneComposer.getStage().setScrollFocus(scrollPane);
        
        return listener;
    }
    
    public static EventListener touchableListener(SimActor simActor, TouchableSelected touchableSelected) {
        var simTouchable = (SimTouchable) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                final var buttonGroup = new ButtonGroup<ImageTextButton>();
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        touchableSelected.selected((Touchable) buttonGroup.getChecked().getUserObject());
                    }
                };
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().left().spaceRight(5);
                var imageTextButton = new ImageTextButton("Touchable Enabled", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simTouchable.getTouchable() == Touchable.enabled);
                imageTextButton.setUserObject(Touchable.enabled);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget and all children can be clicked on.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(changeListener);
                
                table.row();
                imageTextButton = new ImageTextButton("Touchable Disabled", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simTouchable.getTouchable() == Touchable.disabled);
                imageTextButton.setUserObject(Touchable.disabled);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget and all children can not be clicked on.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(changeListener);
    
                table.row();
                imageTextButton = new ImageTextButton("Touchable Children Only", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simTouchable.getTouchable() == Touchable.childrenOnly);
                imageTextButton.setUserObject(Touchable.childrenOnly);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("Only the widget's children can be clicked on.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener visibleListener(SimActor simActor, VisibleSelected visibleSelected) {
        var simVisible = (SimVisible) simActor;
        var popTableClickListener = new PopTableClickListener(DialogSceneComposer.skin) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
            
            public void update() {
                final var buttonGroup = new ButtonGroup<ImageTextButton>();
                var popTable = getPopTable();
                popTable.clearChildren();
                
                var changeListener = new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        visibleSelected.selected((boolean) buttonGroup.getChecked().getUserObject());
                    }
                };
                
                var table = new Table();
                popTable.add(table);
                
                table.defaults().left().spaceRight(5);
                var imageTextButton = new ImageTextButton("Visible", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simVisible.isVisible());
                imageTextButton.setUserObject(true);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget is visible in the stage.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(changeListener);
                
                table.row();
                imageTextButton = new ImageTextButton("Invisible", DialogSceneComposer.skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(!simVisible.isVisible());
                imageTextButton.setUserObject(false);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(DialogSceneComposer.main.getHandListener());
                imageTextButton.addListener(new TextTooltip("The widget is not visible in the stage.", DialogSceneComposer.main.getTooltipManager(), DialogSceneComposer.skin, "scene"));
                imageTextButton.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public interface DrawableSelected {
        void selected(DrawableData drawableData);
    }
    
    public interface WidgetSelectedListener {
        void widgetSelected(WidgetType widgetType, PopTable popTable);
    }
    
    public interface InterpolationSelected {
        void selected(Interpol selection);
    }
    
    public interface TouchableSelected {
        void selected(Touchable touchable);
    }
    
    public interface VisibleSelected {
        void selected(boolean visible);
    }
}
