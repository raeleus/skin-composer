/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.scenes.scene2d.Event;

/**
 *
 * @author Raymond
 */
public class DialogEvent extends Event {
    public static enum Type {
        OPEN, CLOSE
    }
    
    Type type;

    public DialogEvent(Type type) {
        this.type = type;
    }
}
