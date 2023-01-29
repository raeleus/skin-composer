package com.ray3k.skincomposer.dialog.scenecomposer.menulisteners;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.RunListener;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.dialog.DialogDrawables;
import com.ray3k.skincomposer.dialog.DialogListener;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents.WidgetType;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.Interpol;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTouchable;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimVisible;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTableClickListener;
import space.earlygrey.shapedrawer.scene2d.GraphDrawerDrawable;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.RunListener.rl;

public class GeneralListeners {
    public static EventListener widgetResetListener(String name, Runnable runnable) {
        var popTableClickListener = new PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        popTable.key(Keys.ESCAPE, popTable::hide);
        
        var label = new Label("Are you sure you want to reset this " + name + "?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("RESET", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("Resets the settings of the " + name + " to the defaults.", tooltipManager, skin, "scene")));
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
        var popTableClickListener = new PopTableClickListener(skin);
        var popTable = popTableClickListener.getPopTable();
        popTable.key(Keys.ESCAPE, popTable::hide);
        
        var label = new Label("Are you sure you want to delete this " + name + "?", skin, "scene-label-colored");
        popTable.add(label);
        
        popTable.row();
        var textButton = new TextButton("DELETE", skin, "scene-small");
        popTable.add(textButton).minWidth(100);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("Removes this " + name + " from its parent.", tooltipManager, skin, "scene")));
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
        var scrollPane = new ScrollPane(table, skin, "scene");
        var scrollFocus = scrollPane;
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        scrollPane.addListener(scrollFocusListener);
        
        
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
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
        var label = new Label("Widgets:", skin, "scene-label-colored");
        popTable.add(label);
        label.addListener((Main.makeTooltip("Widgets are interactive components of your UI.", tooltipManager, skin, "scene")));
    
        label = new Label("Layout:", skin, "scene-label-colored");
        popTable.add(label);
        label.addListener((Main.makeTooltip("Layout widgets help organize the components of your UI and make it more adaptable to varying screen size.", tooltipManager, skin, "scene")));
    
        popTable.row();
        popTable.defaults().top();
        popTable.add(scrollPane).grow();
    
        var textButton = new TextButton("Button", skin, "scene-med");
        table.add(textButton);
        var valid = jsonData.classHasValidStyles(Button.class);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nButtons are the most basic component to UI design. These are clickable widgets that can perform a certain action such as starting a game or activating a power.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(Button.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "Buttons are the most basic component to UI design. These are clickable widgets that can perform a certain action such as starting a game or activating a power.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.BUTTON, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("CheckBox", skin, "scene-med");
        valid = jsonData.classHasValidStyles(CheckBox.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nCheckBoxes are great for setting/displaying boolean values for an options screen.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(CheckBox.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "CheckBoxes are great for setting/displaying boolean values for an options screen.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.CHECK_BOX, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("Image", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("Images are not directly interactable elements of a layout, but are necessary to showcase graphics or pictures in your UI. Scaling options make them a very powerful tool.", tooltipManager, skin, "scene")));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.IMAGE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ImageButton", skin, "scene-med");
        valid = jsonData.classHasValidStyles(ImageButton.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nA Button with an image graphic in it. The image can change depending on the state of the button.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(ImageButton.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "A Button with an image graphic in it. The image can change depending on the state of the button.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.IMAGE_BUTTON, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("ImageTextButton", skin, "scene-med");
        valid = jsonData.classHasValidStyles(ImageTextButton.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nA Button with an image graphic followed by text in it. The image and text color can change depending on the state of the button.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(ImageTextButton.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "A Button with an image graphic followed by text in it. The image and text color can change depending on the state of the button.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.IMAGE_TEXT_BUTTON, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("Label", skin, "scene-med");
        valid = jsonData.classHasValidStyles(Label.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nThe most common way to display text in your layouts. Wrapping and ellipses options help mitigate sizing issues in small spaces.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(Label.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "The most common way to display text in your layouts. Wrapping and ellipses options help mitigate sizing issues in small spaces.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.LABEL, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("List", skin, "scene-med");
        valid = jsonData.classHasValidStyles(List.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nList presents text options in a clickable menu.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(List.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "List presents text options in a clickable menu.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.LIST, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("ProgressBar", skin, "scene-med");
        valid = jsonData.classHasValidStyles(ProgressBar.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nCommonly used to display loading progress or as a health/mana indicator in HUD's.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(ProgressBar.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "Commonly used to display loading progress or as a health/mana indicator in HUD's.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.PROGRESS_BAR, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("SelectBox", skin, "scene-med");
        valid = jsonData.classHasValidStyles(SelectBox.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nSelectBox is a kind of button that displays a selectable option list when opened.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(SelectBox.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "SelectBox is a kind of button that displays a selectable option list when opened.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.SELECT_BOX, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("Slider", skin, "scene-med");
        valid = jsonData.classHasValidStyles(Slider.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nSlider is a kind of user interactable ProgressBar that allows a user to select a value along a sliding scale.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(Slider.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "Slider is a kind of user interactable ProgressBar that allows a user to select a value along a sliding scale.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.SLIDER, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("TextButton", skin, "scene-med");
        valid = jsonData.classHasValidStyles(TextButton.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nA kind of button that contains a text element inside of it. The text color can change depending on the state of the button.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(TextButton.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "A kind of button that contains a text element inside of it. The text color can change depending on the state of the button.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.TEXT_BUTTON, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("TextField", skin, "scene-med");
        valid = jsonData.classHasValidStyles(TextField.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nTextFields are the primary way of getting text input from the user.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(TextField.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "TextFields are the primary way of getting text input from the user.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.TEXT_FIELD, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("TextArea", skin, "scene-med");
        valid = jsonData.classHasValidStyles(TextField.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nTextAreas are a multiline version of a TextField.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(TextArea.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "TextAreas are a multiline version of a TextField.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.TEXT_AREA, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("Touchpad", skin, "scene-med");
        valid = jsonData.classHasValidStyles(Touchpad.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nTouchpad is a UI element common to mobile games. It is used lieu of keyboard input, for example.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(Touchpad.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "Touchpad is a UI element common to mobile games. It is used lieu of keyboard input, for example.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.TOUCH_PAD, popTable);
                }
            });
        }
    
        table = new Table();
        scrollPane = new ScrollPane(table, skin, "scene");
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFlickScroll(false);
        popTable.add(scrollPane);
        scrollPane.addListener(scrollFocusListener);
    
        table.row();
        textButton = new TextButton("Container", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("Container is like a lightweight, single cell version of Table.", tooltipManager, skin, "scene")));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.CONTAINER, popTable);
            }
        });
        
        table.row();
        textButton = new TextButton("HorizontalGroup", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("Allows layout of multiple elements horizontally. It is most useful for its wrap functionality, which cannot be achieved with a Table.", tooltipManager, skin, "scene")));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.HORIZONTAL_GROUP, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("ScrollPane", skin, "scene-med");
        valid = jsonData.classHasValidStyles(ScrollPane.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nCreates a scrollable layout for your widgets. It is commonly used to adapt the UI to variable content and screen sizes.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(ScrollPane.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "Creates a scrollable layout for your widgets. It is commonly used to adapt the UI to variable content and screen sizes.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.SCROLL_PANE, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("Stack", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("Allows stacking of elements on top of each other.", tooltipManager, skin, "scene")));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.STACK, popTable);
            }
        });
        
        table.row();
        textButton = new TextButton("SplitPane", skin, "scene-med");
        valid = jsonData.classHasValidStyles(SplitPane.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nAn organizational layout that allows the user to adjust the width or height of two widgets next to each other.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(SplitPane.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "An organizational layout that allows the user to adjust the width or height of two widgets next to each other.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.SPLIT_PANE, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("Table", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("The most powerful layout widget available. Consisting of a series of configurable cells, it organizes elements in rows and columns. It serves as the basis of all layout design in Scene2D.UI.", tooltipManager, skin, "scene")));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                widgetSelectedListener.widgetSelected(WidgetType.TABLE, popTable);
            }
        });
    
        table.row();
        textButton = new TextButton("Tree", skin, "scene-med");
        valid = jsonData.classHasValidStyles(Tree.class);
        table.add(textButton);
        textButton.addListener(handListener);
        if (!valid) {
            textButton.setStyle(skin.get("scene-med-disabled", TextButtonStyle.class));
            textButton.addListener(new TextTooltip(
                    "Missing valid style for widget. Click to open in Skin Composer.\nTree is an organizational widget that allows collapsing and expanding elements like file structures.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(rl(() -> {
                dialogSceneComposer.hide();
                popTable.hide();
                rootTable.setSelectedClass(Tree.class);
                var toast = dialogFactory.showToast(2f, skin, "dialog-no-bg");
                toast.pad(10f);
                var l = new Label("Please enter all required fields for style", skin);
                toast.add(l);
            }));
        } else {
            textButton.addListener(new TextTooltip(
                    "Tree is an organizational widget that allows collapsing and expanding elements like file structures.",
                    tooltipManager, skin, "scene"));
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    widgetSelectedListener.widgetSelected(WidgetType.TREE, popTable);
                }
            });
        }
    
        table.row();
        textButton = new TextButton("VerticalGroup", skin, "scene-med");
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener((Main.makeTooltip("Allows layout of multiple elements vertically. It is most useful for its wrap functionality, which cannot be achieved with a Table.", tooltipManager, skin, "scene")));
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
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update();
            }
    
            public void update() {
                var popTable = getPopTable();
                popTable.clearChildren();
        
                var label = new Label("Drawable:", skin, "scene-label-colored");
                popTable.add(label);
        
                popTable.row();
                var stack = new Stack();
                popTable.add(stack).minSize(100).maxSize(300).grow();
                var background = new Image(skin, "scene-tile-ten");
                stack.add(background);
                Image image;
                if (originalDrawable != null) {
                    image = new Image(atlasData.drawablePairs.get(originalDrawable));
                } else {
                    image = new Image((Drawable) null);
                }
                stack.add(image);
        
                popTable.row();
                var textButton = new TextButton("Select Drawable", skin, "scene-small");
                popTable.add(textButton).minWidth(100);
                textButton.addListener(handListener);
                textButton.addListener((Main.makeTooltip(toolTipText, tooltipManager, skin, "scene")));
                textButton.addListener(new ChangeListener() {
                    boolean confirmed;
                    DrawableData drawableData;
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        popTable.hide();
                        dialogFactory.showDialogDrawables(true, new DialogDrawables.DialogDrawablesListener() {
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
                                    image.setDrawable(atlasData.drawablePairs.get(drawableData));
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
        var scrollPane = new ScrollPane(table, skin, "scene");
        var listener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
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
            var button = new Button(skin, "scene-med");
            table.add(button).growX();
            
            var stack = new Stack();
            button.add(stack).size(50);
            
            var image = new Image(skin.getDrawable("white"));
            stack.add(image);
            
            var graphDrawerDrawable = new GraphDrawerDrawable(graphDrawer);
            graphDrawerDrawable.setColor(Color.BLACK);
            graphDrawerDrawable.setInterpolation(interpol.interpolation);
            graphDrawerDrawable.setSamples(10);
            graphDrawerDrawables.add(graphDrawerDrawable);
            image = new Image(graphDrawerDrawable);
            var container = new Container(image);
            container.pad(5).fill();
            stack.add(container);
            
            var label = new Label(interpol.toString(), skin, "scene-label");
            button.add(label).expandX().left().space(5);
            button.addListener(handListener);
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
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
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
                var imageTextButton = new ImageTextButton("Touchable Enabled", skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simTouchable.getTouchable() == Touchable.enabled);
                imageTextButton.setUserObject(Touchable.enabled);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("The widget and all children can be clicked on.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(changeListener);
                
                table.row();
                imageTextButton = new ImageTextButton("Touchable Disabled", skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simTouchable.getTouchable() == Touchable.disabled);
                imageTextButton.setUserObject(Touchable.disabled);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("The widget and all children can not be clicked on.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(changeListener);
    
                table.row();
                imageTextButton = new ImageTextButton("Touchable Children Only", skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simTouchable.getTouchable() == Touchable.childrenOnly);
                imageTextButton.setUserObject(Touchable.childrenOnly);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("Only the widget's children can be clicked on.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(changeListener);
            }
        };
        
        popTableClickListener.update();
        
        return popTableClickListener;
    }
    
    public static EventListener visibleListener(SimActor simActor, VisibleSelected visibleSelected) {
        var simVisible = (SimVisible) simActor;
        var popTableClickListener = new PopTableClickListener(skin) {
            {
                getPopTable().key(Keys.ESCAPE, popTable::hide);
            }
            
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
                var imageTextButton = new ImageTextButton("Visible", skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(simVisible.isVisible());
                imageTextButton.setUserObject(true);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("The widget is visible in the stage.", tooltipManager, skin, "scene")));
                imageTextButton.addListener(changeListener);
                
                table.row();
                imageTextButton = new ImageTextButton("Invisible", skin, "scene-checkbox-colored");
                imageTextButton.setProgrammaticChangeEvents(false);
                imageTextButton.setChecked(!simVisible.isVisible());
                imageTextButton.setUserObject(false);
                table.add(imageTextButton);
                buttonGroup.add(imageTextButton);
                imageTextButton.addListener(handListener);
                imageTextButton.addListener((Main.makeTooltip("The widget is not visible in the stage.", tooltipManager, skin, "scene")));
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
