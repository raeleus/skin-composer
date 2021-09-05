package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.*;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class DialogSceneComposerEvents {
    public enum WidgetType {
        BUTTON, CHECK_BOX, IMAGE, IMAGE_BUTTON, IMAGE_TEXT_BUTTON, LABEL, LIST, PROGRESS_BAR, SELECT_BOX, SLIDER,
        TEXT_BUTTON, TEXT_FIELD, TEXT_AREA, TOUCH_PAD, CONTAINER, HORIZONTAL_GROUP, SCROLL_PANE, STACK, SPLIT_PANE,
        TABLE, TREE, VERTICAL_GROUP
    }
    
    private DialogSceneComposer dialog;
    
    public DialogSceneComposerEvents() {
        this.dialog = DialogSceneComposer.dialog;
    }
    
    public void menuQuit() {
        dialog.hide();
    }
    
    public void menuFind() {
        dialog.showFindDialog();
    }
    
    public void menuClear() {
        processUndoable(new MenuClearUndoable());
    }
    
    public void menuUndo() {
        dialog.model.undo();
        dialog.updateMenuUndoRedo();
    }
    
    public void menuRedo() {
        dialog.model.redo();
        dialog.updateMenuUndoRedo();
    }
    
    public void menuView(DialogSceneComposer.View view) {
        dialog.view = view;
        dialog.updateMenuView();
        dialog.model.updatePreview();
    }
    
    public void menuHelp() {
        dialog.showHelpDialog();
    }
    
    public void importTemplate(FileHandle loadFile) {
        Main.projectData.setLastSceneComposerJson(loadFile.path());
        DialogSceneComposerModel.loadFromJson(loadFile);
        dialog.simActor = rootActor;
        dialog.model.updatePreview();
        dialog.populatePath();
        dialog.populateProperties();
    }
    
    public void exportTemplate(FileHandle saveFile) {
        Main.projectData.setLastSceneComposerJson(saveFile.path());
        DialogSceneComposerModel.saveToJson(saveFile);
    }
    
    public void exportJava(FileHandle saveFile) {
        Main.projectData.setLastSceneComposerJson(saveFile.path());
        String java = DialogSceneComposerJavaBuilder.generateJavaFile();
        saveFile.writeString(java, false);
    }
    
    public void exportClipboard() {
        String clipBoard = DialogSceneComposerJavaBuilder.generateClipBoard();
        Gdx.app.getClipboard().setContents(clipBoard);
    }
    
    public void rootPackage(String packageName) {
        processUndoable(new RootPackageUndoable(packageName));
    }
    
    public void rootClass(String className) {
        processUndoable(new RootClassUndoable(className));
    }
    
    public void rootSkinPath(String skinPath) {
        processUndoable(new RootSkinPathUndoable(skinPath));
    }
    
    private void processUndoable(SceneComposerUndoable undoable) {
        dialog.model.undoables.add(undoable);
        dialog.model.redoables.clear();
        undoable.redo();
        dialog.updateMenuUndoRedo();
        Main.projectData.setChangesSaved(false);
    }
    
    public void rootAddTable(int columns, int rows) {
        processUndoable(new RootAddTableUndoable(columns, rows));
    }
    
    public void rootBackgroundColor(ColorData color) {
        processUndoable(new RootBackgroundColorUndoable(color));
    }
    
    public void tableName(String name) {
        processUndoable(new TableNameUndoable(name));
    }
    
    public void tableTouchable(Touchable touchable) {
        processUndoable(new TableTouchableUndoable(touchable));
    }
    
    public void tableVisible(boolean visible) {
        processUndoable(new TableVisibleUndoable(visible));
    }
    
    public void tableBackground(DrawableData background) {
        processUndoable(new TableBackgroundUndoable(background));
    }
    
    public void tableColor(ColorData color) {
        processUndoable(new TableColorUndoable(color));
    }
    
    public void tablePadding(boolean enabled, float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new TablePaddingUndoable(enabled, paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void tableAlignment(int alignment) {
        processUndoable(new TableAlignmentUndoable(alignment));
    }
    
    public void tableSetCells(int columns, int rows) {
        processUndoable(new TableSetCellsUndoable(columns, rows));
    }
    
    public void tableReset() {
        processUndoable(new TableResetUndoable());
    }
    
    public void tableDelete() {
        processUndoable(new TableDeleteUndoable());
    }
    
    public void cellSetWidget(WidgetType widgetType) {
        processUndoable(new CellSetWidgetUndoable(widgetType));
    }
    
    public void cellAddCellToLeft() {
        processUndoable(new CellAddCellToLeftUndoable());
    }
    
    public void cellAddCellToRight() {
        processUndoable(new CellAddCellToRightUndoable());
    }
    
    public void cellAddCellAbove() {
        processUndoable(new CellAddCellAboveUndoable());
    }
    
    public void cellAddCellBelow() {
        processUndoable(new CellAddCellBelowUndoable());
    }
    
    public void cellAddRowAbove() {
        processUndoable(new CellAddRowAboveUndoable());
    }
    
    public void cellAddRowBelow() {
        processUndoable(new CellAddRowBelowUndoable());
    }
    
    public void cellDuplicateCellLeft() {
        processUndoable(new CellDuplicateCellLeftUndoable());
    }
    
    public void cellDuplicateCellRight() {
        processUndoable(new CellDuplicateCellRightUndoable());
    }
    
    public void cellDuplicateCellAbove() {
        processUndoable(new CellDuplicateCellAboveUndoable());
    }
    
    public void cellDuplicateCellBelow() {
        processUndoable(new CellDuplicateCellBelowUndoable());
    }
    
    public void cellDuplicateCellNewRowAbove() {
        processUndoable(new CellDuplicateCellNewRowAboveUndoable());
    }
    
    public void cellDuplicateCellNewRowBelow() {
        processUndoable(new CellDuplicateCellNewRowBelowUndoable());
    }
    
    public void cellMoveCellLeft() {
        processUndoable(new CellMoveCellLeftUndoable());
    }
    
    public void cellMoveCellRight() {
        processUndoable(new CellMoveCellRightUndoable());
    }
    
    public void cellMoveCellUp() {
        processUndoable(new CellMoveCellUpUndoable());
    }
    
    public void cellMoveCellDown() {
        processUndoable(new CellMoveCellDownUndoable());
    }
    
    public void cellMoveToNewRowBelow() {
        processUndoable(new CellMoveToNewRowBelowUndoable());
    }
    
    public void cellMoveToNewRowAbove() {
        processUndoable(new CellMoveToNewRowAboveUndoable());
    }
    
    public void cellPaddingSpacing(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom, float spaceLeft, float spaceRight, float spaceTop, float spaceBottom) {
        processUndoable(new CellPaddingSpacingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom, spaceLeft, spaceRight, spaceTop, spaceBottom));
    }
    
    public void cellExpandFillGrow(boolean expandX, boolean expandY, boolean fillX, boolean fillY, boolean growX, boolean growY) {
        processUndoable(new CellExpandFillGrowUndoable(expandX, expandY, fillX, fillY, growX, growY));
    }
    
    public void cellAlignment(int alignment) {
        processUndoable(new CellAlignmentUndoable(alignment));
    }
    
    public void cellSize(float minWidth, float minHeight, float maxWidth, float maxHeight, float preferredWidth, float preferredHeight) {
        processUndoable(new CellSizeUndoable(minWidth, minHeight, maxWidth, maxHeight, preferredWidth, preferredHeight));
    }
    
    public void cellUniform(boolean uniformX, boolean uniformY) {
        processUndoable(new CellUniformUndoable(uniformX, uniformY));
    }
    
    public void cellColSpan(int colSpan) {
        processUndoable(new CellColSpanUndoable(colSpan));
    }
    
    public void cellReset() {
        processUndoable(new CellResetUndoable());
    }
    
    public void cellDelete() {
        processUndoable(new CellDeleteUndoable());
    }
    
    public void buttonName(String name) {
        processUndoable(new ButtonNameUndoable(name));
    }
    
    public void buttonTouchable(Touchable touchable) {
        processUndoable(new ButtonTouchableUndoable(touchable));
    }
    
    public void buttonVisible(boolean visible) {
        processUndoable(new ButtonVisibleUndoable(visible));
    }
    
    public void buttonStyle(StyleData style) {
        processUndoable(new ButtonStyleUndoable(style));
    }
    
    public void buttonChecked(boolean checked) {
        processUndoable(new ButtonCheckedUndoable(checked));
    }
    
    public void buttonDisabled(boolean disabled) {
        processUndoable(new ButtonDisabledUndoable(disabled));
    }
    
    public void buttonColor(ColorData colorData) {
        processUndoable(new ButtonColorUndoable(colorData));
    }
    
    public void buttonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new ButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void buttonReset() {
        processUndoable(new ButtonResetUndoable());
    }
    
    public void buttonDelete() {
        processUndoable(new ButtonDeleteUndoable());
    }
    
    public void imageButtonName(String name) {
        processUndoable(new ImageButtonNameUndoable(name));
    }
    
    public void imageButtonTouchable(Touchable touchable) {
        processUndoable(new ImageButtonTouchableUndoable(touchable));
    }
    
    public void imageButtonVisible(boolean visible) {
        processUndoable(new ImageButtonVisibleUndoable(visible));
    }
    
    public void imageButtonStyle(StyleData style) {
        processUndoable(new ImageButtonStyleUndoable(style));
    }
    
    public void imageButtonChecked(boolean checked) {
        processUndoable(new ImageButtonCheckedUndoable(checked));
    }
    
    public void imageButtonDisabled(boolean disabled) {
        processUndoable(new ImageButtonDisabledUndoable(disabled));
    }
    
    public void imageButtonColor(ColorData colorData) {
        processUndoable(new ImageButtonColorUndoable(colorData));
    }
    
    public void imageButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new ImageButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void imageButtonReset() {
        processUndoable(new ImageButtonResetUndoable());
    }
    
    public void imageButtonDelete() {
        processUndoable(new ImageButtonDeleteUndoable());
    }
    
    public void imageTextButtonName(String name) {
        processUndoable(new ImageTextButtonNameUndoable(name));
    }
    
    public void imageTextButtonTouchable(Touchable touchable) {
        processUndoable(new ImageTextButtonTouchableUndoable(touchable));
    }
    
    public void imageTextButtonVisible(boolean visible) {
        processUndoable(new ImageTextButtonVisibleUndoable(visible));
    }
    
    public void imageTextButtonText(String text) {
        processUndoable(new ImageTextButtonTextUndoable(text));
    }
    
    public void imageTextButtonStyle(StyleData style) {
        processUndoable(new ImageTextButtonStyleUndoable(style));
    }
    
    public void imageTextButtonChecked(boolean checked) {
        processUndoable(new ImageTextButtonCheckedUndoable(checked));
    }
    
    public void imageTextButtonDisabled(boolean disabled) {
        processUndoable(new ImageTextButtonDisabledUndoable(disabled));
    }
    
    public void imageTextButtonColor(ColorData colorData) {
        processUndoable(new ImageTextButtonColorUndoable(colorData));
    }
    
    public void imageTextButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new ImageTextButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void imageTextButtonReset() {
        processUndoable(new ImageTextButtonResetUndoable());
    }
    
    public void imageTextButtonDelete() {
        processUndoable(new ImageTextButtonDeleteUndoable());
    }
    
    public void textButtonName(String name) {
        processUndoable(new TextButtonNameUndoable(name));
    }
    
    public void textButtonTouchable(Touchable touchable) {
        processUndoable(new TextButtonTouchableUndoable(touchable));
    }
    
    public void textButtonVisible(boolean visible) {
        processUndoable(new TextButtonVisibleUndoable(visible));
    }
    
    public void textButtonText(String text) {
        processUndoable(new TextButtonTextUndoable(text));
    }
    
    public void textButtonStyle(StyleData style) {
        processUndoable(new TextButtonStyleUndoable(style));
    }
    
    public void textButtonChecked(boolean checked) {
        processUndoable(new TextButtonCheckedUndoable(checked));
    }
    
    public void textButtonDisabled(boolean disabled) {
        processUndoable(new TextButtonDisabledUndoable(disabled));
    }
    
    public void textButtonColor(ColorData colorData) {
        processUndoable(new TextButtonColorUndoable(colorData));
    }
    
    public void textButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new TextButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void textButtonReset() {
        processUndoable(new TextButtonResetUndoable());
    }
    
    public void textButtonDelete() {
        processUndoable(new TextButtonDeleteUndoable());
    }
    
    public void checkBoxTouchable(Touchable touchable) {
        processUndoable(new CheckBoxTouchableUndoable(touchable));
    }
    
    public void checkBoxVisible(boolean visible) {
        processUndoable(new CheckBoxVisibleUndoable(visible));
    }
    
    public void checkBoxChecked(boolean checked) {
        processUndoable(new CheckBoxCheckedUndoable(checked));
    }
    
    public void checkBoxColor(ColorData color) {
        processUndoable(new CheckBoxColorUndoable(color));
    }
    
    public void checkBoxDelete() {
        processUndoable(new CheckBoxDeleteUndoable());
    }
    
    public void checkBoxDisabled(boolean disabled) {
        processUndoable(new CheckBoxDisabledUndoable(disabled));
    }
    
    public void checkBoxName(String name) {
        processUndoable(new CheckBoxNameUndoable(name));
    }
    
    public void checkBoxPadding(float padLeft, float padRight, float padTop, float padBottom) {
        processUndoable(new CheckBoxPaddingUndoable(padLeft, padRight, padTop, padBottom));
    }
    
    public void checkBoxReset() {
        processUndoable(new CheckBoxResetUndoable());
    }
    
    public void checkBoxStyle(StyleData style) {
        processUndoable(new CheckBoxStyleUndoable(style));
    }
    
    public void checkBoxText(String text) {
        processUndoable(new CheckBoxTextUndoable(text));
    }
    
    public void containerAlignment(int alignment) {
        processUndoable(new ContainerAlignmentUndoable(alignment));
    }
    
    public void containerBackground(DrawableData background) {
        processUndoable(new ContainerBackgroundUndoable(background));
    }
    
    public void containerDelete() {
        processUndoable(new ContainerDeleteUndoable());
    }
    
    public void containerFill(boolean fillX, boolean fillY) {
        processUndoable(new ContainerFillUndoable(fillX, fillY));
    }
    
    public void containerName(String name) {
        processUndoable(new ContainerNameUndoable(name));
    }
    
    public void containerTouchable(Touchable touchable) {
        processUndoable(new ContainerTouchableUndoable(touchable));
    }
    
    public void containerVisible(boolean visible) {
        processUndoable(new ContainerVisibleUndoable(visible));
    }
    
    public void containerPadding(float padLeft, float padRight, float padTop, float padBottom) {
        processUndoable(new ContainerPaddingUndoable(padLeft,  padRight, padTop, padBottom));
    }
    
    public void containerReset() {
        processUndoable(new ContainerResetUndoable());
    }
    
    public void containerSetWidget(WidgetType widgetType) {
        processUndoable(new ContainerSetWidgetUndoable(widgetType));
    }
    
    public void containerSize(float minWidth, float minHeight, float maxWidth, float maxHeight, float preferredWidth, float preferredHeight) {
        processUndoable(new ContainerSizeUndoable(minWidth, minHeight, maxWidth, maxHeight, preferredWidth, preferredHeight));
    }
    
    public void horizontalGroupAlignment(int alignment) {
        processUndoable(new HorizontalGroupAlignmentUndoable(alignment));
    }
    
    public void horizontalGroupAddChild(WidgetType widgetType) {
        processUndoable(new HorizontalGroupAddChildUndoable(widgetType));
    }
    
    public void horizontalGroupDelete() {
        processUndoable(new HorizontalGroupDeleteUndoable());
    }
    
    public void horizontalGroupExpand(boolean expand) {
        processUndoable(new HorizontalGroupExpandUndoable(expand));
    }
    
    public void horizontalGroupFill(boolean fill) {
        processUndoable(new HorizontalGroupFillUndoable(fill));
    }
    
    public void horizontalGroupName(String name) {
        processUndoable(new HorizontalGroupNameUndoable(name));
    }
    
    public void horizontalGroupTouchable(Touchable touchable) {
        processUndoable(new HorizontalGroupTouchableUndoable(touchable));
    }
    
    public void horizontalGroupVisible(boolean visible) {
        processUndoable(new HorizontalGroupVisibleUndoable(visible));
    }
    
    public void horizontalGroupPadBottom(float padBottom) {
        processUndoable(new HorizontalGroupPadBottomUndoable(padBottom));
    }
    
    public void horizontalGroupPadLeft(float padLeft) {
        processUndoable(new HorizontalGroupPadLeftUndoable(padLeft));
    }
    
    public void horizontalGroupPadRight(float padRight) {
        processUndoable(new HorizontalGroupPadRightUndoable(padRight));
    }
    
    public void horizontalGroupPadTop(float padTop) {
        processUndoable(new HorizontalGroupPadTopUndoable(padTop));
    }
    
   public void horizontalGroupReset() {
        processUndoable(new HorizontalGroupResetUndoable());
   }
   
   public void horizontalGroupReverse(boolean reverse) {
        processUndoable(new HorizontalGroupReverseUndoable(reverse));
   }
   
   public void horizontalGroupRowAlignment(int rowAlignment) {
        processUndoable(new HorizontalGroupRowAlignmentUndoable(rowAlignment));
   }
   
   public void horizontalGroupSpace(float space) {
        processUndoable(new HorizontalGroupSpaceUndoable(space));
   }
   
   public void horizontalGroupWrapSpace(float wrapSpace) {
        processUndoable(new HorizontalGroupWrapSpaceUndoable(wrapSpace));
   }
   
   public void horizontalGroupWrap(boolean wrap) {
        processUndoable(new HorizontalGroupWrapUndoable(wrap));
   }
   
   public void imageDrawable(DrawableData drawable) {
        processUndoable(new ImageDrawableUndoable(drawable));
   }
   
   public void imageDelete() {
        processUndoable(new ImageDeleteUndoable());
   }
   
   public void imageName(String name) {
        processUndoable(new ImageNameUndoable(name));
   }
    
    public void imageTouchable(Touchable touchable) {
        processUndoable(new ImageTouchableUndoable(touchable));
    }
    
    public void imageVisible(boolean visible) {
        processUndoable(new ImageVisibleUndoable(visible));
    }
   
   public void imageReset() {
        processUndoable(new ImageResetUndoable());
   }
   
   public void imageScaling(String scaling) {
        processUndoable(new ImageScalingUndoable(scaling));
   }
   
   public void labelAlignment(int alignment) {
        processUndoable(new LabelAlignmentUndoable(alignment));
   }
   
   public void labelColor(ColorData color) {
        processUndoable(new LabelColorUndoable(color));
   }
   
   public void labelDelete() {
        processUndoable(new LabelDeleteUndoable());
   }
   
   public void labelEllipsis(boolean ellipsis, String ellipsisString) {
        processUndoable(new LabelEllipsisUndoable(ellipsis, ellipsisString));
   }
   
   public void labelName(String name) {
        processUndoable(new LabelNameUndoable(name));
   }
    
    public void labelTouchable(Touchable touchable) {
        processUndoable(new LabelTouchableUndoable(touchable));
    }
    
    public void labelVisible(boolean visible) {
        processUndoable(new LabelVisibleUndoable(visible));
    }
   
   public void labelReset() {
        processUndoable(new LabelResetUndoable());
   }
   
   public void labelStyle(StyleData style) {
        processUndoable(new LabelStyleUndoable(style));
   }
   
   public void labelText(String text) {
        processUndoable(new LabelTextUndoable(text));
   }
   
   public void labelWrap(boolean wrap) {
        processUndoable(new LabelWrapUndoable(wrap));
   }
   
   public void listDelete() {
        processUndoable(new ListDeleteUndoable());
   }
   
   public void listList(Array<String> textList) {
        processUndoable(new ListListUndoable(textList));
   }
   
   public void listName(String name) {
        processUndoable(new ListNameUndoable(name));
   }
    
    public void listTouchable(Touchable touchable) {
        processUndoable(new ListTouchableUndoable(touchable));
    }
    
    public void listVisible(boolean visible) {
        processUndoable(new ListVisibleUndoable(visible));
    }
   
   public void listReset() {
        processUndoable(new ListResetUndoable());
   }
   
   public void listStyle(StyleData style) {
        processUndoable(new ListStyleUndoable(style));
   }
   
   public void nodeDelete() {
        processUndoable(new NodeDeleteUndoable());
   }
   
   public void nodeExpanded(boolean expanded) {
        processUndoable(new NodeExpandedUndoable(expanded));
   }
   
   public void nodeIcon(DrawableData icon) {
        processUndoable(new NodeIconUndoable(icon));
   }
   
   public void nodeReset() {
        processUndoable(new NodeResetUndoable());
   }
   
   public void nodeSelectable(boolean selectable) {
        processUndoable(new NodeSelectableUndoable(selectable));
   }
   
   public void nodeAddNode() {
        processUndoable(new NodeAddNodeUndoable());
   }
   
   public void nodeSetWidget(WidgetType widgetType) {
        processUndoable(new NodeSetWidgetUndoable(widgetType));
   }
   
   public void progressBarAnimateInterpolation(DialogSceneComposerModel.Interpol interpol) {
        processUndoable(new ProgressBarAnimateInterpolationUndoable(interpol));
   }
   
   public void progressBarAnimationDuration(float animationDuration) {
        processUndoable(new ProgressBarAnimationDurationUndoable(animationDuration));
   }
   
   public void progressBarDelete() {
        processUndoable(new ProgressBarDeleteUndoable());
   }
   
   public void progressBarDisabled(boolean disabled) {
        processUndoable(new ProgressBarDisabledUndoable(disabled));
   }
   
   public void progressBarIncrement(float increment) {
        processUndoable(new ProgressBarIncrementUndoable(increment));
   }
   
   public void progressBarMaximum(float maximum) {
        processUndoable(new ProgressBarMaximumUndoable(maximum));
   }
   
   public void progressBarMinimum(float minimum) {
        processUndoable(new ProgressBarMinimumUndoable(minimum));
   }
   
   public void progressBarName(String name) {
        processUndoable(new ProgressBarNameUndoable(name));
   }
    
    public void progressBarTouchable(Touchable touchable) {
        processUndoable(new ProgressBarTouchableUndoable(touchable));
    }
    
    public void progressBarVisible(boolean visible) {
        processUndoable(new ProgressBarVisibleUndoable(visible));
    }
   
   public void progressBarReset() {
        processUndoable(new ProgressBarResetUndoable());
   }
   
   public void progressBarRound(boolean round) {
        processUndoable(new ProgressBarRoundUndoable(round));
   }
   
   public void progressBarStyle(StyleData style) {
        processUndoable(new ProgressBarStyleUndoable(style));
   }
   
   public void progressBarValue(float value) {
        processUndoable(new ProgressBarValueUndoable(value));
   }
   
   public void progressBarVertical(boolean vertical) {
        processUndoable(new ProgressBarVerticalUndoable(vertical));
   }
   
   public void progressBarVisualInterpolation(DialogSceneComposerModel.Interpol visualInterpolation) {
        processUndoable(new ProgressBarVisualInterpolationUndoable(visualInterpolation));
   }
   
   public void scrollPaneClamp(boolean clamp) {
        processUndoable(new ScrollPaneClampUndoable(clamp));
   }
   
   public void scrollPaneDelete() {
        processUndoable(new ScrollPaneDeleteUndoable());
   }
   
   public void scrollPaneFadeScrollBars(boolean fadeScrollBars) {
        processUndoable(new ScrollPaneFadeScrollBarsUndoable(fadeScrollBars));
   }
   
   public void scrollPaneFlickScroll(boolean flickScroll) {
        processUndoable(new ScrollPaneFlickScrollUndoable(flickScroll));
   }
   
   public void scrollPaneFlingTime(float flingTime) {
        processUndoable(new ScrollPaneFlingTimeUndoable(flingTime));
   }
   
   public void scrollPaneOverScrollX(boolean overScrollX) {
        processUndoable(new ScrollPaneOverScrollXUndoable(overScrollX));
   }
   
   public void scrollPaneOverScrollY(boolean overScrollY) {
        processUndoable(new ScrollPaneOverScrollYUndoable(overScrollY));
   }
   
   public void scrollPaneForceScrollX(boolean forceScrollX) {
        processUndoable(new ScrollPaneForceScrollXUndoable(forceScrollX));
   }
   
   public void scrollPaneForceScrollY(boolean forceScrollY) {
        processUndoable(new ScrollPaneForceScrollYUndoable(forceScrollY));
   }
   
   public void scrollPaneName(String name) {
        processUndoable(new ScrollPaneNameUndoable(name));
   }
    
    public void scrollPaneTouchable(Touchable touchable) {
        processUndoable(new ScrollPaneTouchableUndoable(touchable));
    }
    
    public void scrollPaneVisible(boolean visible) {
        processUndoable(new ScrollPaneVisibleUndoable(visible));
    }
   
   public void scrollPaneOverScrollDistance(float overScrollDistance) {
        processUndoable(new ScrollPaneOverScrollDistanceUndoable(overScrollDistance));
   }
   
   public void scrollPaneOverScrollSpeedMax(float overScrollSpeedMax) {
        processUndoable(new ScrollPaneOverScrollSpeedMaxUndoable(overScrollSpeedMax));
   }
   
   public void scrollPaneOverScrollSpeedMin(float overScrollSpeedMin) {
        processUndoable(new ScrollPaneOverScrollSpeedMinUndoable(overScrollSpeedMin));
   }
   
   public void scrollPaneReset() {
        processUndoable(new ScrollPaneResetUndoable());
   }
   
   public void scrollPaneScrollBarBottom(boolean scrollBarBottom) {
        processUndoable(new ScrollPaneScrollBarBottomUndoable(scrollBarBottom));
   }
   
   public void scrollPaneScrollBarRight(boolean scrollBarRight) {
        processUndoable(new ScrollPaneScrollBarRightUndoable(scrollBarRight));
   }
   
   public void scrollPaneScrollBarsOnTop(boolean scrollBarsOnTop) {
        processUndoable(new ScrollPaneScrollBarsOnTopUndoable(scrollBarsOnTop));
   }
   
   public void scrollPaneScrollBarsVisible(boolean scrollBarsVisible) {
        processUndoable(new ScrollPaneScrollBarsVisibleUndoable(scrollBarsVisible));
   }
   
   public void scrollPaneScrollBarTouch(boolean scrollBarTouch) {
        processUndoable(new ScrollPaneScrollBarTouchUndoable(scrollBarTouch));
   }
   
   public void scrollPaneScrollingDisabledX(boolean scrollingDisabledX) {
        processUndoable(new ScrollPaneScrollingDisabledXUndoable(scrollingDisabledX));
   }
   
   public void scrollPaneScrollingDisabledY(boolean scrollingDisabledY) {
        processUndoable(new ScrollPaneScrollingDisabledYUndoable(scrollingDisabledY));
   }
   
   public void scrollPaneSetWidget(WidgetType widgetType) {
        processUndoable(new ScrollPaneSetWidgetUndoable(widgetType));
   }
   
   public void scrollPaneSmoothScrolling(boolean smoothScrolling) {
        processUndoable(new ScrollPaneSmoothScrollingUndoable(smoothScrolling));
   }
   
   public void scrollPaneStyle(StyleData style) {
        processUndoable(new ScrollPaneStyleUndoable(style));
   }
   
   public void scrollPaneVariableSizeKnobs(boolean variableSizeKnobs) {
        processUndoable(new ScrollPaneVariableSizeKnobsUndoable(variableSizeKnobs));
   }
   
   public void selectBoxAlignment(int alignment) {
        processUndoable(new SelectBoxAlignmentUndoable(alignment));
   }
   
   public void selectBoxDelete() {
        processUndoable(new SelectBoxDeleteUndoable());
   }
   
   public void selectBoxDisabled(boolean disabled) {
        processUndoable(new SelectBoxDisabledUndoable(disabled));
   }
   
   public void selectBoxList(Array<String> textList) {
        processUndoable(new SelectBoxListUndoable(textList));
   }
   
   public void selectBoxMaxListCount(int maxListCount) {
        processUndoable(new SelectBoxMaxListCountUndoable(maxListCount));
   }
   
   public void selectBoxName(String name) {
        processUndoable(new SelectBoxNameUndoable(name));
   }
    
    public void selectBoxTouchable(Touchable touchable) {
        processUndoable(new SelectBoxTouchableUndoable(touchable));
    }
    
    public void selectBoxVisible(boolean visible) {
        processUndoable(new SelectBoxVisibleUndoable(visible));
    }
   
   public void selectBoxReset() {
        processUndoable(new SelectBoxResetUndoable());
   }
   
   public void selectBoxScrollingDisabled(boolean scrollingDisabled) {
        processUndoable(new SelectBoxScrollingDisabledUndoable(scrollingDisabled));
   }
   
   public void selectBoxSelected(int selected) {
        processUndoable(new SelectBoxSelectedUndoable(selected));
   }
   
   public void selectBoxStyle(StyleData style) {
        processUndoable(new SelectBoxStyleUndoable(style));
   }
    
    public void sliderAnimateInterpolation(DialogSceneComposerModel.Interpol interpol) {
        processUndoable(new SliderAnimateInterpolationUndoable(interpol));
    }
    
    public void sliderAnimationDuration(float animationDuration) {
        processUndoable(new SliderAnimationDurationUndoable(animationDuration));
    }
    
    public void sliderDelete() {
        processUndoable(new SliderDeleteUndoable());
    }
    
    public void sliderDisabled(boolean disabled) {
        processUndoable(new SliderDisabledUndoable(disabled));
    }
    
    public void sliderIncrement(float increment) {
        processUndoable(new SliderIncrementUndoable(increment));
    }
    
    public void sliderMaximum(float maximum) {
        processUndoable(new SliderMaximumUndoable(maximum));
    }
    
    public void sliderMinimum(float minimum) {
        processUndoable(new SliderMinimumUndoable(minimum));
    }
    
    public void sliderName(String name) {
        processUndoable(new SliderNameUndoable(name));
    }
    
    public void sliderTouchable(Touchable touchable) {
        processUndoable(new SliderTouchableUndoable(touchable));
    }
    
    public void sliderVisible(boolean visible) {
        processUndoable(new SliderVisibleUndoable(visible));
    }
    
    public void sliderReset() {
        processUndoable(new SliderResetUndoable());
    }
    
    public void sliderRound(boolean round) {
        processUndoable(new SliderRoundUndoable(round));
    }
    
    public void sliderStyle(StyleData style) {
        processUndoable(new SliderStyleUndoable(style));
    }
    
    public void sliderValue(float value) {
        processUndoable(new SliderValueUndoable(value));
    }
    
    public void sliderVertical(boolean vertical) {
        processUndoable(new SliderVerticalUndoable(vertical));
    }
    
    public void sliderVisualInterpolation(DialogSceneComposerModel.Interpol visualInterpolation) {
        processUndoable(new SliderVisualInterpolationUndoable(visualInterpolation));
    }
    
    public void splitPaneChildFirst(WidgetType widgetType) {
        processUndoable(new SplitPaneChildFirstUndoable(widgetType));
    }
    
    public void splitPaneChildSecond(WidgetType widgetType) {
        processUndoable(new SplitPaneChildSecondUndoable(widgetType));
    }
    
    public void splitPaneDelete() {
        processUndoable(new SplitPaneDeleteUndoable());
    }
    
    public void splitPaneName(String name) {
        processUndoable(new SplitPaneNameUndoable(name));
    }
    
    public void splitPaneTouchable(Touchable touchable) {
        processUndoable(new SplitPaneTouchableUndoable(touchable));
    }
    
    public void splitPaneVisible(boolean visible) {
        processUndoable(new SplitPaneVisibleUndoable(visible));
    }
    
    public void splitPaneReset() {
        processUndoable(new SplitPaneResetUndoable());
    }
    
    public void splitPaneSplitMax(float splitMax) {
        processUndoable(new SplitPaneSplitMaxUndoable(splitMax));
    }
    
    public void splitPaneSplitMin(float splitMin) {
        processUndoable(new SplitPaneSplitMinUndoable(splitMin));
    }
    
    public void splitPaneSplit(float split) {
        processUndoable(new SplitPaneSplitUndoable(split));
    }
    
    public void splitPaneStyle(StyleData style) {
        processUndoable(new SplitPaneStyleUndoable(style));
    }
    
    public void splitPaneVertical(boolean vertical) {
        processUndoable(new SplitPaneVerticalUndoable(vertical));
    }
    
    public void stackAddChild(WidgetType widgetType) {
        processUndoable(new StackAddChildUndoable(widgetType));
    }
    
    public void stackDelete() {
        processUndoable(new StackDeleteUndoable());
    }
    
    public void stackName(String name) {
        processUndoable(new StackNameUndoable(name));
    }
    
    public void stackTouchable(Touchable touchable) {
        processUndoable(new StackTouchableUndoable(touchable));
    }
    
    public void stackVisible(boolean visible) {
        processUndoable(new StackVisibleUndoable(visible));
    }
    
    public void stackReset() {
        processUndoable(new StackResetUndoable());
    }
    
    public void textAreaAlignment(int alignment) {
        processUndoable(new TextAreaAlignmentUndoable(alignment));
    }
    
    public void textAreaCursorPosition(int cursorPosition) {
        processUndoable(new TextAreaCursorPositionUndoable(cursorPosition));
    }
    
    public void textAreaDelete() {
        processUndoable(new TextAreaDeleteUndoable());
    }
    
    public void textAreaDisabled(boolean disabled) {
        processUndoable(new TextAreaDisabledUndoable(disabled));
    }
    
    public void textAreaFocusTraversal(boolean focusTraversal) {
        processUndoable(new TextAreaFocusTraversalUndoable(focusTraversal));
    }
    
    public void textAreaMaxLength(int maxLength) {
        processUndoable(new TextAreaMaxLengthUndoable(maxLength));
    }
    
    public void textAreaMessageText(String messageText) {
        processUndoable(new TextAreaMessageTextUndoable(messageText));
    }
    
    public void textAreaName(String name) {
        processUndoable(new TextAreaNameUndoable(name));
    }
    
    public void textAreaTouchable(Touchable touchable) {
        processUndoable(new TextAreaTouchableUndoable(touchable));
    }
    
    public void textAreaVisible(boolean visible) {
        processUndoable(new TextAreaVisibleUndoable(visible));
    }
    
    public void textAreaPasswordCharacter(char character) {
        processUndoable(new TextAreaPasswordCharacterUndoable(character));
    }
    
    public void textAreaPasswordMode(boolean passwordMode) {
        processUndoable(new TextAreaPasswordModeUndoable(passwordMode));
    }
    
    public void textAreaPreferredRows(int preferredRow) {
        processUndoable(new TextAreaPreferredRowUndoable(preferredRow));
    }
    
    public void textAreaReset() {
        processUndoable(new TextAreaResetUndoable());
    }
    
    public void textAreaSelectAll(boolean selectAll) {
        processUndoable(new TextAreaSelectAllUndoable(selectAll));
    }
    
    public void textAreaSelectionEnd(int selectionEnd) {
        processUndoable(new TextAreaSelectionEndUndoable(selectionEnd));
    }
    
    public void textAreaSelectionStart(int selectionStart) {
        processUndoable(new TextAreaSelectionStartUndoable(selectionStart));
    }
    
    public void textAreaStyle(StyleData style) {
        processUndoable(new TextAreaStyleUndoable(style));
    }
    
    public void textAreaText(String text) {
        processUndoable(new TextAreaTextUndoable(text));
    }
    
    public void textFieldAlignment(int alignment) {
        processUndoable(new TextFieldAlignmentUndoable(alignment));
    }
    
    public void textFieldCursorPosition(int cursorPosition) {
        processUndoable(new TextFieldCursorPositionUndoable(cursorPosition));
    }
    
    public void textFieldDelete() {
        processUndoable(new TextFieldDeleteUndoable());
    }
    
    public void textFieldDisabled(boolean disabled) {
        processUndoable(new TextFieldDisabledUndoable(disabled));
    }
    
    public void textFieldFocusTraversal(boolean focusTraversal) {
        processUndoable(new TextFieldFocusTraversalUndoable(focusTraversal));
    }
    
    public void textFieldMaxLength(int maxLength) {
        processUndoable(new TextFieldMaxLengthUndoable(maxLength));
    }
    
    public void textFieldMessageText(String messageText) {
        processUndoable(new TextFieldMessageTextUndoable(messageText));
    }
    
    public void textFieldName(String name) {
        processUndoable(new TextFieldNameUndoable(name));
    }
    
    public void textFieldTouchable(Touchable touchable) {
        processUndoable(new TextFieldTouchableUndoable(touchable));
    }
    
    public void textFieldVisible(boolean visible) {
        processUndoable(new TextFieldVisibleUndoable(visible));
    }
    
    public void textFieldPasswordCharacter(char character) {
        processUndoable(new TextFieldPasswordCharacterUndoable(character));
    }
    
    public void textFieldPasswordMode(boolean passwordMode) {
        processUndoable(new TextFieldPasswordModeUndoable(passwordMode));
    }
    
    public void textFieldReset() {
        processUndoable(new TextFieldResetUndoable());
    }
    
    public void textFieldSelectAll(boolean selectAll) {
        processUndoable(new TextFieldSelectAllUndoable(selectAll));
    }
    
    public void textFieldSelectionEnd(int selectionEnd) {
        processUndoable(new TextFieldSelectionEndUndoable(selectionEnd));
    }
    
    public void textFieldSelectionStart(int selectionStart) {
        processUndoable(new TextFieldSelectionStartUndoable(selectionStart));
    }
    
    public void textFieldStyle(StyleData style) {
        processUndoable(new TextFieldStyleUndoable(style));
    }
    
    public void textFieldText(String text) {
        processUndoable(new TextFieldTextUndoable(text));
    }
    
    public void touchPadDelete() {
        processUndoable(new TouchPadDeleteUndoable());
    }
    
    public void touchPadName(String name) {
        processUndoable(new TouchPadNameUndoable(name));
    }
    
    public void touchPadTouchable(Touchable touchable) {
        processUndoable(new TouchPadTouchableUndoable(touchable));
    }
    
    public void touchPadVisible(boolean visible) {
        processUndoable(new TouchPadVisibleUndoable(visible));
    }
    
    public void touchPadDeadZone(float deadZone) {
        processUndoable(new TouchPadDeadZoneUndoable(deadZone));
    }
    
    public void touchPadResetOnTouchUp(boolean resetOnTouchUp) {
        processUndoable(new TouchPadResetOnTouchUpUndoable(resetOnTouchUp));
    }
    
    public void touchPadReset() {
        processUndoable(new TouchPadResetUndoable());
    }
    
    public void touchPadStyle(StyleData style) {
        processUndoable(new TouchPadStyleUndoable(style));
    }
    
    public void treeAddNode() {
        processUndoable(new TreeAddNodeUndoable());
    }
    
    public void treeDelete() {
        processUndoable(new TreeDeleteUndoable());
    }
    
    public void treeIconSpaceLeft(float iconSpaceLeft) {
        processUndoable(new TreeIconSpaceLeftUndoable(iconSpaceLeft));
    }
    
    public void treeIconSpaceRight(float iconSpaceRight) {
        processUndoable(new TreeIconSpaceRightUndoable(iconSpaceRight));
    }
    
    public void treeIndentSpacing(float indentSpacing) {
        processUndoable(new TreeIndentSpacingUndoable(indentSpacing));
    }
    
    public void treeName(String name) {
        processUndoable(new TreeNameUndoable(name));
    }
    
    public void treeTouchable(Touchable touchable) {
        processUndoable(new TreeTouchableUndoable(touchable));
    }
    
    public void treeVisible(boolean visible) {
        processUndoable(new TreeVisibleUndoable(visible));
    }
    
    public void treePadLeft(float padLeft) {
        processUndoable(new TreePadLeftUndoable(padLeft));
    }
    
    public void treePadRight(float padRight) {
        processUndoable(new TreePadRightUndoable(padRight));
    }
    
    public void treeReset() {
        processUndoable(new TreeResetUndoable());
    }
    
    public void treeStyle(StyleData style) {
        processUndoable(new TreeStyleUndoable(style));
    }
    
    public void treeYSpacing(float ySpacing) {
        processUndoable(new TreeYSpacingUndoable(ySpacing));
    }
    
    public void verticalGroupAlignment(int alignment) {
        processUndoable(new VerticalGroupAlignmentUndoable(alignment));
    }
    
    public void verticalGroupAddChild(WidgetType widgetType) {
        processUndoable(new VerticalGroupAddChildUndoable(widgetType));
    }
    
    public void verticalGroupDelete() {
        processUndoable(new VerticalGroupDeleteUndoable());
    }
    
    public void verticalGroupExpand(boolean expand) {
        processUndoable(new VerticalGroupExpandUndoable(expand));
    }
    
    public void verticalGroupFill(boolean fill) {
        processUndoable(new VerticalGroupFillUndoable(fill));
    }
    
    public void verticalGroupName(String name) {
        processUndoable(new VerticalGroupNameUndoable(name));
    }
    
    public void verticalGroupTouchable(Touchable touchable) {
        processUndoable(new VerticalGroupTouchableUndoable(touchable));
    }
    
    public void verticalGroupVisible(boolean visible) {
        processUndoable(new VerticalGroupVisibleUndoable(visible));
    }
    
    public void verticalGroupPadBottom(float padBottom) {
        processUndoable(new VerticalGroupPadBottomUndoable(padBottom));
    }
    
    public void verticalGroupPadLeft(float padLeft) {
        processUndoable(new VerticalGroupPadLeftUndoable(padLeft));
    }
    
    public void verticalGroupPadRight(float padRight) {
        processUndoable(new VerticalGroupPadRightUndoable(padRight));
    }
    
    public void verticalGroupPadTop(float padTop) {
        processUndoable(new VerticalGroupPadTopUndoable(padTop));
    }
    
    public void verticalGroupReset() {
        processUndoable(new VerticalGroupResetUndoable());
    }
    
    public void verticalGroupReverse(boolean reverse) {
        processUndoable(new VerticalGroupReverseUndoable(reverse));
    }
    
    public void verticalGroupColumnAlignment(int columnAlignment) {
        processUndoable(new VerticalGroupColumnAlignmentUndoable(columnAlignment));
    }
    
    public void verticalGroupSpace(float space) {
        processUndoable(new VerticalGroupSpaceUndoable(space));
    }
    
    public void verticalGroupWrapSpace(float wrapSpace) {
        processUndoable(new VerticalGroupWrapSpaceUndoable(wrapSpace));
    }
    
    public void verticalGroupWrap(boolean wrap) {
        processUndoable(new VerticalGroupWrapUndoable(wrap));
    }
}