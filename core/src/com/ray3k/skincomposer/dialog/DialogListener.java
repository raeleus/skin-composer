/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.scenes.scene2d.*;

/**
 *
 * @author Raymond
 */
public interface DialogListener extends EventListener{
    public void opened();
    
    public void closed();
    
    @Override
    default boolean handle(Event event) {
        if (event instanceof DialogEvent) {
            var dialogEvent = (DialogEvent) event;
            if (dialogEvent.type == DialogEvent.Type.OPEN) {
                opened();
            } else if (dialogEvent.type == DialogEvent.Type.CLOSE) {
                closed();
            }
            
            return true;
        }
        
        return false;
    }
}
