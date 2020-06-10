package com.ray3k.skincomposer.installer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.io.IOException;

public class Core extends ApplicationAdapter {
    private Stage stage;
    public static Skin skin;
    public static com.ray3k.skincomposer.installer.DesktopWorker desktopWorker;
    private int dragStartX, dragStartY;
    private int windowStartX, windowStartY;
    private static Table root;
    public static ObjectMap<String, String> properties;
    public static FileHandle installationPath;
    public static boolean installationCreateDesktopIcon;
    public static boolean installationCreateStartIcon;
    private static String os;

    @Override
    public void create() {
        properties = new ObjectMap<>();
        try {
            PropertiesUtils.load(properties, Gdx.files.internal("values.properties").reader());
        } catch (IOException e) {
            Gdx.app.error(getClass().getName(), "Error reading installer properties file.", e);
        }
        
        installationPath = Gdx.files.absolute(System.getenv("ProgramFiles") + "\\" + Core.properties.get("product-name"));
        installationCreateDesktopIcon = true;
        installationCreateStartIcon = true;
        
        skin = new Skin(Gdx.files.internal("ui/skin-composer-installer-ui.json"));
        stage = new Stage(new ScreenViewport(), new PolygonSpriteBatch());
        Gdx.input.setInputProcessor(stage);
        
        root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.enabled);
        root.setName("root");
        stage.addActor(root);
        
        MenuTable menuTable = new MenuTable(skin, stage);
        root.add(menuTable).grow();
        
        stage.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (event.getTarget() == root) {
                    desktopWorker.dragWindow(windowStartX + (int) x - dragStartX, windowStartY + dragStartY - (int) y);
                    windowStartX = desktopWorker.getWindowX();
                    windowStartY = desktopWorker.getWindowY();
                }
            }

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                dragStartX = (int) x;
                dragStartY = (int) y;
                windowStartX = desktopWorker.getWindowX();
                windowStartY = desktopWorker.getWindowY();
            }
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(26.0f / 255, 26.0f / 255, 26.0f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    
    public static void transition(Table table1, final Table table2, float transitionTime1, final float transitionTime2) {
        table1.addAction(Actions.sequence(Actions.fadeOut(transitionTime1), new Action() {
            @Override
            public boolean act(float delta) {
                table2.setColor(1, 1, 1, 0);
                table2.setTouchable(Touchable.disabled);
                
                root.clear();
                root.add(table2).grow();
                table2.addAction(Actions.sequence(Actions.fadeIn(transitionTime2), Actions.touchable(Touchable.childrenOnly)));
                return true;
            }
        }));
    }
    
    public static void transition(Table table1, Table table2) {
        transition(table1, table2, .25f, .25f);
    }
    
    public static String readAndReplace(String internalPath) {
        var text = Gdx.files.internal(internalPath).readString();
        for (var key : properties.keys()) {
            text = text.replace("[" + key + "]", properties.get(key));
        }
        return text;
    }
    
    public static boolean isWindows() {
        if (os == null) {
            os = System.getProperty("os.name");
        }
        
        return os.startsWith("Windows");
    }
    
    public static boolean isLinux() {
        if (os == null) {
            os = System.getProperty("os.name");
        }
        return os.startsWith("Linux");
    }
    
    public static boolean isMac() {
        if (os == null) {
            os = System.getProperty("os.name");
        }
        return os.startsWith("Mac");
    }
}
