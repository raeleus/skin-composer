package com.ray3k.skincomposer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Custom widget for menu bar lists
 *
 */
public class MenuList extends Table {
    private static final Vector2 temp = new Vector2();
    private final TextButton widget;
    private final Vector2 screenPosition = new Vector2();
    private InputListener hideListener;
    final Table table;

    public MenuList(TextButton widget, Table table) {
        this.widget = widget;

        this.table = table;
        table.setTouchable(Touchable.disabled);
        table.validate();
        add(table);
        validate();

        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        hideListener = new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    hide();
                }
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor target = event.getTarget();
                if (isAscendantOf(target)) {
                    return false;
                }
                hide();
                return false;
            }

        };
    }

    public void show(Stage stage) {
        if (table.isTouchable()) {
            return;
        }

        stage.removeCaptureListener(hideListener);
        stage.addCaptureListener(hideListener);
        stage.addActor(this);

        widget.localToStageCoordinates(screenPosition.set(0, 0));

        setX(screenPosition.x);
        setY(screenPosition.y - table.getHeight());
        align(Align.left);

        setHeight(table.getHeight());
        validate();

        stage.setScrollFocus(this);

        table.setTouchable(Touchable.enabled);
        clearActions();
        getColor().a = 0;
        addAction(fadeIn(0.3f, Interpolation.fade));
    }

    public void hide() {
        if (!table.isTouchable() || !hasParent()) {
            return;
        }
        table.setTouchable(Touchable.disabled);

        Stage stage = getStage();
        if (stage != null) {
            stage.removeCaptureListener(hideListener);
        }

        clearActions();
        getColor().a = 1;
        addAction(sequence(fadeOut(0.15f, Interpolation.fade), com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor()));
    }

    public void draw(Batch batch, float parentAlpha) {
        widget.localToStageCoordinates(temp.set(0, 0));
        if (!temp.equals(screenPosition)) {
            widget.setChecked(false);
            hide();
        }
        super.draw(batch, parentAlpha);
    }

    public void act(float delta) {
        super.act(delta);
        toFront();
    }
}
