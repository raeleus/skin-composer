package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.utils.Utils;
import java.io.IOException;

public class DialogError extends Dialog {
    public static void showError(String title, String message) {
        DialogError dialog = new DialogError(title, message, Main.instance().getSkin());
        dialog.show(Main.instance().getStage());
    }
    
    public DialogError(String title, String message, Skin skin) {
        super(title, skin);
        text(message);
        
        button("OK", true).key(Keys.ENTER, true);
        button("Cancel", false).key(Keys.ESCAPE, false);
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        if ((boolean) object) {
            try {
                Utils.openFileExplorer(Gdx.files.local("temp/log.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
