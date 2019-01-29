package com.ray3k.skincomposer.data;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisCheckBox.VisCheckBoxStyle;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisImageTextButton.VisImageTextButtonStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisRadioButton;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ray3k.skincomposer.BrowseField;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.RootTable;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;

public class PreviewActorCreator {
	
	static Object createStyle(RootTable table, Class clazz, CustomStyle style) {
		try {
		Object styleInstance = clazz.newInstance();
				
		for(CustomProperty pp : style.getProperties()) {
			
			boolean exists = true;
			try {
			ClassReflection.getField(clazz, pp.getName());
			}catch(Exception e) {
				exists = false;
			}
			
			if(exists) {
				switch(pp.getType()) {
				case COLOR:
					if (!(pp.getValue() instanceof String)) {
	                    pp.setValue("");
	                }
	
	                ColorData colorData = null;
	
	                String colorName = (String) pp.getValue();
	                for (ColorData cd : table.getMain().getJsonData().getColors()) {
	                    if (cd.getName().equals(colorName)) {
	                        colorData = cd;
	                        break;
	                    }
	                }
	
	                if (colorData != null) {
	     				ClassReflection.getField(clazz, pp.getName()).set(styleInstance, colorData.color);
	
	                }
					break;
				case DRAWABLE:
					if (!(pp.getValue() instanceof String)) {
	                    pp.setValue("");
	                }
	
	                DrawableData drawable = null;
	
	                String drawableName = (String) pp.getValue();
	                for (DrawableData dd : table.getMain().getAtlasData().getDrawables()) {
	                    if (dd.name.equals(drawableName)) {
	                        drawable = dd;
	                        break;
	                    }
	                }
	
	                if (drawable != null) {
	     				ClassReflection.getField(clazz, pp.getName()).set(styleInstance, table.getDrawablePairs().get(drawable.name));
	                }
					break;
				case FONT:
					 if (!(pp.getValue() instanceof String)) {
	                     pp.setValue("");
	                 }
	
	                 BitmapFont font = null;
	
	                 String fontName = (String) pp.getValue();
	                 for (FontData fd : table.getMain().getJsonData().getFonts()) {
	                     if (fd.getName().equals(fontName)) {
	                         font = new BitmapFont(fd.file);
	                         table.getPreviewFonts().add(font);
	                         break;
	                     }
	                 }
	
	                 if (font != null) {
	     				ClassReflection.getField(clazz, pp.getName()).set(styleInstance, font);
	                 }
					break;
				case NUMBER:
					float v = ((Double)pp.getValue()).floatValue();
					ClassReflection.getField(clazz, pp.getName()).set(styleInstance, v);
					break;
				case STYLE:
					if(pp.getName().equals("listStyle")) {
						if(((String)pp.getValue()).length() == 0) {
							pp.setValue("default");
						}
						StyleData stl = findStyleByName(List.class,
								Main.main.getJsonData().getClassStyleMap(), (String)pp.getValue());
						ListStyle ls = table.createPreviewStyle(ListStyle.class, stl);
						ClassReflection.getField(clazz, pp.getName()).set(styleInstance, ls);

					}
					if(pp.getName().equals("scrollStyle")) {
						if(((String)pp.getValue()).length() == 0) {
							pp.setValue("default");
						}
						StyleData stl = findStyleByName(ScrollPane.class,
								Main.main.getJsonData().getClassStyleMap(), (String)pp.getValue());
						ScrollPaneStyle sp = table.createPreviewStyle(ScrollPaneStyle.class, stl);
						ClassReflection.getField(clazz, pp.getName()).set(styleInstance, sp);
					}
	
					break;
				default:
					System.out.println(pp.getType() + ": " + pp.getValue());
					ClassReflection.getField(clazz, pp.getName()).set(styleInstance, pp.getValue());
					break;
				}
			}
		}
		return styleInstance;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static StyleData findStyleByName(Class clazz, OrderedMap<Class, Array<StyleData>> data, String name) {
		for(StyleData sd : data.get(clazz)) {
			if(sd.name.equals(name)) return sd;
		}
		return null;
	}
	
	public static Actor createActorToPreview(RootTable table, Class clazz, CustomStyle style, ObjectMap<String, Object> previewProperties) {
		try {
			
			//TODO: inserir novas classes aqui
		if(clazz.equals(VisCheckBox.class)) {
				populateStyle(VisCheckBoxStyle.class, style);				
				
				VisCheckBox checkBox = new VisCheckBox("");
				VisCheckBoxStyle visStyle = (VisCheckBoxStyle)createStyle(table, VisCheckBoxStyle.class, style);
				table.refreshStyleProperties(true);
				//Checa se tá tudo certo
				if(visStyle.font == null) {
					visStyle.font = new BitmapFont();
				}
				table.previewSizeSelectBox.setSelectedIndex(1);
				checkBox.setStyle(visStyle);
				checkBox.setDisabled((boolean)table.getPreviewProperties().get("disabled"));
				return checkBox;			
		}
		else if(clazz.equals(VisWindow.class)) {

			populateStyle(WindowStyle.class, style);
			VisWindow actor = new VisWindow((String) previewProperties.get("text", ""));
			WindowStyle winStyle = (WindowStyle)createStyle(table, WindowStyle.class, style);
			table.refreshStyleProperties(true);
			//Checa se tá tudo certo
			if(winStyle.titleFont == null) {
				winStyle.titleFont = new BitmapFont();
			}
			actor.setStyle(winStyle);
			return actor;
		
		}
		else if(clazz.equals(VisDialog.class)) {
			populateStyle(WindowStyle.class, style);
			VisDialog actor = new VisDialog((String) previewProperties.get("text", ""));
			WindowStyle winStyle = (WindowStyle)createStyle(table, WindowStyle.class, style);
			table.refreshStyleProperties(true);
			//Checa se tá tudo certo
			if(winStyle.titleFont == null) {
				winStyle.titleFont = new BitmapFont();
			}
			actor.setStyle(winStyle);
			return actor;
		}
		else if(clazz.equals(VisImageButton.class)) {
			populateStyle(VisImageButtonStyle.class, style);
			VisImageButton actor = new VisImageButton(new VisImageButtonStyle());
			VisImageButtonStyle btnStyle = (VisImageButtonStyle)createStyle(table, VisImageButtonStyle.class, style);
			table.refreshStyleProperties(true);
			actor.setStyle(btnStyle);
			return actor;
		}
		else if(clazz.equals(VisImageTextButton.class)) {
			populateStyle(VisImageTextButtonStyle.class, style);
			VisImageTextButtonStyle btnStyle = (VisImageTextButtonStyle)createStyle(table, VisImageTextButtonStyle.class, style);
			if(btnStyle.font == null) {
				btnStyle.font = new BitmapFont();
			}
			VisImageTextButton actor = new VisImageTextButton((String) previewProperties.get("text", ""), btnStyle);
			table.refreshStyleProperties(true);
			return actor;
		}
		else if(clazz.equals(VisLabel.class)) {
			populateStyle(LabelStyle.class, style);
			LabelStyle labelStyle = (LabelStyle)createStyle(table, LabelStyle.class, style);
			if(labelStyle.font == null) {
				labelStyle.font = new BitmapFont();
			}
			VisLabel actor = new VisLabel();
			actor.setStyle(labelStyle);
			actor.setText((String) previewProperties.get("text", ""));
			table.refreshStyleProperties(true);
			return actor;

		}
		else if(clazz.equals(VisList.class)) {
			populateStyle(ListStyle.class, style);
			ListStyle listStyle = (ListStyle)createStyle(table, ListStyle.class, style);
			if(listStyle.font == null) {
				listStyle.font = new BitmapFont();
			}
			VisList<String> actor = new VisList<String>();
			actor.setStyle(listStyle);
			table.refreshStyleProperties(true);
			try {actor.setItems("Item 1", "Item 2", "Item 3");}catch(Exception e) {return null;}
			return actor;
		}
		else if(clazz.equals(VisProgressBar.class)) {
			populateStyle(ProgressBarStyle.class, style);
			ProgressBarStyle visStyle = (ProgressBarStyle)createStyle(table, ProgressBarStyle.class, style);
			VisProgressBar actor = new VisProgressBar(0, 100, 1, (boolean)previewProperties.get("vertical", false), visStyle);
			table.refreshStyleProperties(true);
			actor.setValue((float)((double) previewProperties.get("value", 0)));
			actor.setDisabled((boolean)previewProperties.get("disabled", false));
			return actor;
		}
		else if(clazz.equals(VisRadioButton.class)) {
			populateStyle(VisCheckBoxStyle.class, style);				
			
			VisRadioButton actor = new VisRadioButton("");
			VisCheckBoxStyle visStyle = (VisCheckBoxStyle)createStyle(table, VisCheckBoxStyle.class, style);
			table.refreshStyleProperties(true);
			//Checa se tá tudo certo
			if(visStyle.font == null) {
				visStyle.font = new BitmapFont();
			}
			table.previewSizeSelectBox.setSelectedIndex(1);
			actor.setStyle(visStyle);
			actor.setDisabled((boolean)table.getPreviewProperties().get("disabled"));
			return actor;	
		}
		else if(clazz.equals(VisScrollPane.class)) {
			populateStyle(ScrollPaneStyle.class, style);
			ScrollPaneStyle visStyle = (ScrollPaneStyle)createStyle(table, ScrollPaneStyle.class, style);
			table.refreshStyleProperties(true);

			Label label = new Label("", Main.main.getSkin());
			VisScrollPane actor = new VisScrollPane(label, visStyle);
			actor.setScrollbarsOnTop((boolean) table.getPreviewProperties().get("scrollbarsOnTop"));
			actor.setScrollBarPositions((boolean) table.getPreviewProperties().get("hScrollBarPosition"), (boolean) table.getPreviewProperties().get("vScrollBarPosition"));
			actor.setScrollingDisabled((boolean) table.getPreviewProperties().get("hScrollDisabled"), (boolean) table.getPreviewProperties().get("vScrollDisabled"));
			actor.setForceScroll((boolean) table.getPreviewProperties().get("forceHscroll"), (boolean) table.getPreviewProperties().get("forceVscroll"));
			actor.setVariableSizeKnobs((boolean) table.getPreviewProperties().get("variableSizeKnobs"));
			actor.setOverscroll((boolean) table.getPreviewProperties().get("hOverscroll"), (boolean) table.getPreviewProperties().get("vOverscroll"));
			actor.setFadeScrollBars((boolean) table.getPreviewProperties().get("fadeScroll"));
			actor.setSmoothScrolling((boolean) table.getPreviewProperties().get("smoothScroll"));
			actor.setFlickScroll((boolean) table.getPreviewProperties().get("flickScroll"));
			actor.setClamp((boolean) table.getPreviewProperties().get("clamp"));
            label.setText((String) table.getPreviewProperties().get("text"));
            
            return actor;
		}
		else if(clazz.equals(VisSelectBox.class)) {
			populateStyle(SelectBoxStyle.class, style);
			SelectBoxStyle visStyle = (SelectBoxStyle)createStyle(table, SelectBoxStyle.class, style);
			table.refreshStyleProperties(true);
			
			VisSelectBox<String> actor = new VisSelectBox<String>();
			
			try {
			actor.setStyle(visStyle);
			actor.setItems("Item 1", "Item 2", "Item 3");
			}
			catch(Exception e) {System.out.println("Not enough");return null;}
			
			return actor;

		}

		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void createPreviewProperties(Class clazz, Table table, RootTable root) {
		//TODO: inserir novas classes aqui
		addSizeChooser(table, root);

		if(clazz.equals(VisWindow.class)) {
			addTextProperty("Window title", "text", table, root);
		}
		else if(clazz.equals(VisCheckBox.class)) {
			addBooleanPicker("Disable", "disabled", table, root);
		}
		else if(clazz.equals(VisDialog.class)) {
			addTextProperty("Dialog title", "text", table, root);
		}
		else if(clazz.equals(VisImageTextButton.class)) {
			addTextProperty("Button text", "text", table, root);
		}
		else if(clazz.equals(VisLabel.class)) {
			addTextProperty("Label text", "text", table, root);
		}
		else if(clazz.equals(VisProgressBar.class)) {
			addBooleanPicker("Vertical", "vertical", table, root);
			addBooleanPicker("Disable", "disabled", table, root);
			addSpinnerProperty("Progress", "value", 0, 1, table, root);
		}
		else if(clazz.equals(VisRadioButton.class)) {
			addBooleanPicker("Disable", "disabled", table, root);
		}
		else if(clazz.equals(VisScrollPane.class)) {
			addBooleanPicker("Scroll bars on top", "scrollbarsOnTop", table, root);
			addBooleanCombo("H ScrollBar Position", "hScrollBarPosition", "Top", "Bottom", table, root);
			addBooleanCombo("V ScrollBar Position", "vScrollBarPosition", "Left", "Right", table, root);
			addBooleanPicker("H Scrolling Disabled", "hScrollDisabled", table, root);
			addBooleanPicker("V Scrolling Disabled", "vScrollDisabled", table, root);
			addBooleanPicker("Force H Scroll", "forceHscroll", table, root);
			addBooleanPicker("Force V Scroll", "forceVscroll", table, root);
			addBooleanPicker("Variable Size Knobs", "variableSizeKnobs", table, root);
			addBooleanPicker("H Overscroll", "hOverscroll", table, root);
			addBooleanPicker("V Overscroll", "vOverscroll", table, root);
			addBooleanPicker("Fade Scroll Bars", "fadeScroll", table, root);
			addBooleanPicker("Smooth Scrolling", "smoothScroll", table, root);
			addBooleanPicker("Flick Scroll", "flickScroll", table, root);
			addBooleanPicker("Clamp", "clamp", table, root);
			addTextArea("Sample text", "text", table, root);
		}
	}
	
	public static void addBooleanPicker(String title, String key, Table table, RootTable root) {
		table.row();
		table.add(new Label(title + ": ", Main.main.getSkin())).right();
         ImageTextButton disabledCheckBox = new ImageTextButton("", Main.main.getSkin(), "switch");
         disabledCheckBox.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                 root.getPreviewProperties().put(key, disabledCheckBox.isChecked());
                 root.refreshPreview();
             }
         });
         disabledCheckBox.addListener(Main.main.getHandListener());
         root.getPreviewProperties().put(key, disabledCheckBox.isChecked());
         table.add(disabledCheckBox).left();
	}
	
	public static void addSizeChooser(Table table, RootTable root) {
		table.row();
		table.add(new Label("Size: ", Main.main.getSkin())).right();

          root.previewSizeSelectBox = new SelectBox<>(Main.main.getSkin());
          root.previewSizeSelectBox.setItems(root.DEFAULT_SIZES);
          root.previewSizeSelectBox.setSelectedIndex(1);
          root.previewSizeSelectBox.addListener(Main.main.getHandListener());
          root.previewSizeSelectBox.getList().addListener(Main.main.getHandListener());
          
          root.previewSizeSelectBox.addListener(new ChangeListener() {
              @Override
              public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                  root.getPreviewProperties().put("size", root.previewSizeSelectBox.getSelectedIndex());
                  if (root.previewSizeSelectBox.getSelectedIndex() != 7) {
                      root.refreshPreview();
                  }
              }
          });
          root.getPreviewProperties().put("size", root.previewSizeSelectBox.getSelectedIndex());
          
          table.add(root.previewSizeSelectBox).growX().minWidth(200.0f);
	}
	
	public static void addColorChooser(String title, String key, Table root) {
		root.add(new Label(title + ": ", Main.main.getSkin()));
        BrowseField textColorField = new BrowseField(null, null, Main.main.getSkin(), "color");
        textColorField.addListener(Main.main.getHandListener());
        root.add(textColorField).growX();
	}
	
	public static void addTextProperty(String title, String key, Table table, RootTable root) {
		 table.row();
		 table.add(new Label(key + ": ", Main.main.getSkin())).right();
         TextField previewTextField = new TextField("", Main.main.getSkin());
         previewTextField.setFocusTraversal(false);
         previewTextField.addListener(Main.main.getIbeamListener());
         previewTextField.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                 root.getPreviewProperties().put(key, previewTextField.getText());
                 root.refreshPreview();
             }
         });
         root.getPreviewProperties().put("title", previewTextField.getText());
         table.add(previewTextField).growX();
	}
	
	public static void populateStyle(Class clazz, CustomStyle customStyle) {
		Field[] fields = clazz.getFields();
		if(customStyle.getProperties().size == 0) {
			for(Field field : fields) {
				
				PropertyType type = PropertyType.TEXT;
				
				if(field.getType().getSimpleName().equals("Drawable")) {
					type = PropertyType.DRAWABLE;
				}
				else if(field.getType().getSimpleName().equals("BitmapFont")) {
					type = PropertyType.FONT;
				}
				else if(field.getType().getSimpleName().equals("Color")) {
					type = PropertyType.COLOR;
				}
				else if(field.getType().getSimpleName().equals("float")) {
					type = PropertyType.NUMBER;
				}
				else {
					if(field.getType().equals(ScrollPaneStyle.class)) {
						type = PropertyType.STYLE;
					}
					if(field.getType().equals(ListStyle.class)) {
						type = PropertyType.STYLE;
					}
				}
				CustomProperty pr = new CustomProperty(field.getName(), type);
				customStyle.getProperties().add(pr);
			}
        }
	}
    
	public static void addSpinnerProperty(String title, String key, float min, float step, Table table, RootTable root) {
		table.row();
		table.add(new Label(title + ": ", Main.main.getSkin())).right();
        Spinner valueSpinner = new Spinner(min, step, false, Spinner.Orientation.HORIZONTAL, Main.main.getSkin());
        valueSpinner.getTextField().setFocusTraversal(false);
        valueSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                root.getPreviewProperties().put(key, valueSpinner.getValue());
                root.refreshPreview();
            }
        });
        valueSpinner.getButtonMinus().addListener(Main.main.getHandListener());
        valueSpinner.getButtonPlus().addListener(Main.main.getHandListener());
        valueSpinner.getTextField().addListener(Main.main.getIbeamListener());
        root.getPreviewProperties().put(key, valueSpinner.getValue());
        table.add(valueSpinner).growX();
	}

	public static void addBooleanCombo(String title, String key, String valueFalse, String valueTrue, Table table, RootTable root) {
		table.row();
        table.add(new Label(title + ": ", Main.main.getSkin())).right();
        SelectBox<String> hScrollPosBox = new SelectBox<>(Main.main.getSkin());
        hScrollPosBox.setItems(new String[]{valueFalse, valueTrue});
        hScrollPosBox.setSelectedIndex(1);
        hScrollPosBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (hScrollPosBox.getSelectedIndex() == 0) {
                    root.getPreviewProperties().put(key, false);
                } else {
                    root.getPreviewProperties().put(key, true);
                }
                root.refreshPreview();
            }
        });
        table.add(hScrollPosBox).growX();
        hScrollPosBox.addListener(Main.main.getHandListener());
        hScrollPosBox.getList().addListener(Main.main.getHandListener());
        root.getPreviewProperties().put(key, true);
	}

	public static void addCombo(String title, String key, String[] values, Table table, RootTable root) {
		table.row();
        table.add(new Label(title + ": ", Main.main.getSkin())).right();
        SelectBox<String> hScrollPosBox = new SelectBox<>(Main.main.getSkin());
        hScrollPosBox.setItems(values);
        hScrollPosBox.setSelectedIndex(0);
        hScrollPosBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                root.getPreviewProperties().put(key, hScrollPosBox.getSelected());
                root.refreshPreview();
            }
        });
        table.add(hScrollPosBox).growX();
        hScrollPosBox.addListener(Main.main.getHandListener());
        hScrollPosBox.getList().addListener(Main.main.getHandListener());
        root.getPreviewProperties().put(key, hScrollPosBox.getSelected());
	}

	public static void addTextArea(String title, String key, Table table, RootTable root) {
		 table.row();
         table.add(new Label(title + ": ", Main.main.getSkin())).right();
         TextArea previewTextArea = new TextArea(RootTable.PARAGRAPH_SAMPLE_EXT, Main.main.getSkin());
         previewTextArea.setFocusTraversal(false);
         previewTextArea.setPrefRows(5);
         previewTextArea.addListener(Main.main.getIbeamListener());
         previewTextArea.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                 root.getPreviewProperties().put(key, previewTextArea.getText());
                 root.refreshPreview();
             }
         });
         root.getPreviewProperties().put(key, previewTextArea.getText());
         table.add(previewTextArea).growX();
	}
}
