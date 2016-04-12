package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.HandListener;

public class DialogAbout extends Dialog {
    public DialogAbout(Skin skin, String windowStyleName) {
        super("About", skin, windowStyleName);
        key(Keys.ENTER, true);
        key(Keys.ESCAPE, false);
        getTitleLabel().setAlignment(Align.center);
        Table table = getContentTable();
        table.defaults().pad(10.0f);
        Label label = new Label("Skin Composer is developed by Raeleus for the LibGDX community\nVersion 1\nCopyright © Raymond \"Raeleus\" Buckley 2016", skin);
        label.setAlignment(Align.center);
        table.add(label).padBottom(0);
        table.row();
        TextButton button = new TextButton("ray3k.com/skincomposer", skin, "link");
        button.addListener(HandListener.get());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.net.openURI("http://www.ray3k.com/skincomposer");
            }
        });
        table.add(button).padTop(0);
        button("Close");
        table.setWidth(200);
    }
}
