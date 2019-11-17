package com.ray3k.skincomposer.dialog.scenecomposer;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class DialogSceneComposerEvents {
    public enum WidgetType {
        BUTTON, CHECK_BOX, IMAGE, IMAGE_BUTTON, IMAGE_TEXT_BUTTON, LABEL, LIST, PROGRESS_BAR, SELECT_BOX, SLIDER,
        TEXT_BUTTON, TEXT_FIELD, TEXT_AREA, TOUCH_PAD, CONTAINER, HORIZONTAL_GROUP, SCROLL_PANE, STACK, SPLIT_PANE,
        TABLE, TREE, VERTICAL_GROUP
    }
    
    private DialogSceneComposer dialog;
    
    public DialogSceneComposerEvents(DialogSceneComposer dialog) {
        this.dialog = dialog;
    }
    
    public void menuImport() {
        dialog.showImportDialog();
    }
    
    public void menuExport() {
        dialog.showExportDialog();
    }
    
    public void menuSettings() {
        dialog.showSettingsDialog();
    }
    
    public void menuQuit() {
        dialog.hide();
    }
    
    public void menuRefresh() {
    }
    
    public void menuClear() {
        dialog.model.clear();
    }
    
    public void menuUndo() {
        dialog.model.undo();
    }
    
    public void menuRedo() {
        dialog.model.redo();
    }
    
    public void menuMode() {
    
    }
    
    public void menuHelp() {
        dialog.showHelpDialog();
    }
    
    public void rootAddTable(int columns, int rows) {
    
    }
    
    public void tableName(String name) {
    
    }
    
    public void tableBackground(DrawableData background) {
    
    }
    
    public void tableColor(ColorData color) {
    
    }
    
    public void tablePadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
    
    }
    
    public void tableAlignment(int alignment) {
    
    }
    
    public void tableReset() {
    
    }
    
    public void cellAddWidget(WidgetType widgetType) {
    
    }
    
    public void cellAddCellToLeft() {
    
    }
    
    public void cellAddCellToRight() {
    
    }
    
    public void cellAddRowAbove() {
    
    }
    
    public void cellAddRowBelow() {
    
    }
    
    public void cellPaddingSpacing(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom, float spacingLeft, float spacingRight, float spacingTop, float spacingBottom) {
    
    }
    
    public void cellExpandFillGrow(boolean expandX, boolean expandY, boolean fillX, boolean fillY, boolean growX, boolean growY) {
    
    }
    
    public void cellAlignment(int alignment) {
    
    }
    
    public void cellSize(float minWidth, float minHeight, float maxWidth, float maxHeight, float preferredWidth, float preferredHeight) {
    
    }
    
    public void cellUniform(boolean uniformX, boolean uniformY) {
    
    }
    
    public void cellReset() {
    
    }
    
    public void cellDelete() {
    
    }
    
    public void textButtonName(String name) {
    
    }
    
    public void textButtonText(String text) {
    
    }
    
    public void textButtonStyle(StyleData styleData) {
    
    }
    
    public void textButtonChecked(boolean checked) {
    
    }
    
    public void textButtonDisabled(boolean disabled) {
    
    }
    
    public void textButtonColor(ColorData colorData) {
    
    }
    
    public void textButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
    
    }
    
    public void textButtonAlign(int alignment) {
    
    }
    
    public void textButtonReset() {
    
    }
}
