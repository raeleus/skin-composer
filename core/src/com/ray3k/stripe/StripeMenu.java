package com.ray3k.stripe;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.ray3k.stripe.StripeMenuBar.KeyboardShortcut;

public interface StripeMenu {
    public com.ray3k.stripe.StripeMenu menu(String name, EventListener... listeners);
    public com.ray3k.stripe.StripeMenu item(String name, EventListener... listeners);
    public com.ray3k.stripe.StripeMenu item(String name, KeyboardShortcut keyboardShortcut, EventListener... listeners);
    public com.ray3k.stripe.StripeMenu parent();
    public TextButton getParentButton();
    public TextButton findButton(String name);
    public StripeMenu findMenu(String name);
    public Cell findCell(String name);
}
