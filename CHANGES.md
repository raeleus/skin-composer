### Skin Composer Version 57 ###
* Refactored Scene Composer and fixed various errors. Resolves #135. Thanks deviodesign!

### Skin Composer Version 56 ###
* Fixed font scaling issues.
* Fixed unable to import a skin because of markupEnabled and flip flags in font.

### Skin Composer Version 55a ###
* Fixed build error preventing Skin Composer from starting.

### Skin Composer Version 55 ###
* Added emoji menu to TextraTypist Playground.
* Fixed not saving flip, markupEnabled, and scaling font values.
* Various UI tweaks and bugfixes. Thanks TEttinger!

### Skin Composer Version 54 ###
* Fixed errors in TextraTypist Playground effect options.
* Fixed blurry text on TextraTypist Playground's About page.

### Skin Composer Version 53 ###
* Updated to Stripe 1.4.1 and TextraTypist 0.7.5
* Added new effects from TextraTypist
* Added fields for font scaling, markup, and flip in Bitmap Fonts
* Pressing Escape in the Drawables dialog clears the name filter. Pressing Escape when there is no name filter closes the dialog.
* Fixed file dialog crashing on M1 Macs. Thanks Lyze!
* Fixed floating point number parsing. Thanks H0k493!
* Fixed new font names not being correctly validated
* Added arm64 support to work on macOS.
* JSON import no longer requires an associated Atlas file.

### Skin Composer Version 52 ###
* Added option to skip animation in TextraTypist Playground.
* Updated TextraTypist Playground to use TextraTypist 0.6.2.
* Updated color picker with new swatches.
* Fixed text not persisting when inserting a token in TextraTypist Playground.
* Fixed TenPatch settings dialog not keeping Color selection when pressing enter.
* Fixed -swingfd option crashing when cancel is pressed in a file dialog.

### Skin Composer Version 51 ###
* Changed appearance and improved functionality of the color picker.
* Updated TextraTypist Playground to use the TextraTypist 0.5.3.
* Fixed inability to scroll the styles list when the list exceeds the height of the window.
* Fixed crash with sorting pixel drawables by newest.
* Minor UI changes and bug fixes.

### Skin Composer Version 50 ###
* Added TextraTypist Playground to markup text with formatting and effects.
* Updated to libGDX 1.11.0
* Added more hints to the font creation dialogs.
* Fixed crash when naming a duplicate drawable.
* Fixed "Copy tvg files to destination" checkmark always being checked in Export dialog.
* Fixed TenPatch settings editor not changing the correct corner color in the preview.
* Minor UI changes and bug fixes.

### Skin Composer Version 49 ###
* Prompt user to approve changes to UI scale or revert automatically to the last UI scale that was set.
* Fixed crash when exporting pixel drawables and unnecessary copying of files.
* Fixed crash when the source region for a TenPatch is decreased in size.

### Skin Composer Version 48 ###
* Fixed inability to open TinyVG files with many points.
* Fixed clicking "do not show again" on tips opens the webpage anyway.

### Skin Composer Version 47 ###
* Added TinyVGDrawable as a drawable option. A custom serializer must be implemented to read TinyVG from a skin JSON.
* TenPatch editor background color can be changed by adjusting the preview background color.
* TenPatch settings editor can also set the background color.
* Refresh button and F5 keyboard shortcut is added to the Drawables dialog to reload all images.
* Custom preview sizes are persisted from session to session.
* Fixed background color of FreeType font not being set correctly when the font is edited.
* Fixed file dialogs not opening on various platforms.
* Fixed SceneComposer exporting SplitPane incorrectly.
* Fixed crash when pressing enter or escape in the FreeType dialog.
* Fixed color of TenPatch preview being changed even when user chooses cancel.
* Fixed scroll focus not being set correctly with long lists of styles.
* Fixed app centering and resizing on the wrong monitor on startup.
* Minor UI changes and bug fixes.

### Skin Composer Version 46 ###
* Added CrushMode options to TenPatch.
* Added resizable preview to the "More Settings" dialog of TenPatch.
* Added Free Transform size option to the preview properties of widgets.
* Bitmap Font and FreeType Font dialogs now allow for OTF files to be loaded.
* Fixed Tinted NinePatches not being exported.
* Fixed Tinted NinePatches being displayed in the preview as TextureRegions.
* Grabbing handles in the 9patch TenPatch dialog is easier when zoomed out completely.
* Minor UI changes and bug fixes.

### Skin Composer Version 45 ###
* Added commandline option -swingfd to use swing.JFileChooser for platforms that can't use Tiny File Dialogs.
* Fixed code path that bypasses null check in ProjectData when loading a skin with placeholder fonts.
* Fixed null pointer exception when opening a project with relative resources and Pixel Drawables

### Skin Composer Version 44 ###
* Fixed TintedDrawables created from NinePatches not working.
* Fixed Color Picker to correctly show numeric values for imported colors. Resolves #95

### Skin Composer Version 43 ###
* Preview text adapts to available glyphs in Fonts Dialog.
* Prevent users from selecting a parent listed further down the styles list which would cause an error when loaded in game. Resolves #90
* Added setting to allow showing the full path in the Recent Files menu (Thanks Grisgram). Resolves #91
* Fixed exported skins overwriting content padding when minWidth/minHeight is specified. Resolves #93
* Fixed filenames in exported JSON's being forced to be lowercase when minWidth/minHeight of NinePatch's are specified. Resolves #94

### Skin Composer Version 42 ###
* Fixed NPE when loading a project with a place holder FreeType font.

### Skin Composer Version 41 ###
* Added UI Scaling options and default HiDPI support. Thanks to Hangman and MGSX. Resolves #85.
* Fixed being unable to open huge files in the TenPatch Dialog.
* Fixed Cell Reset causing the cell to go to 0,0 in SceneComposer.

### Skin Composer Version 40 ###
* Allow reordering of styles by drag and drop in the styles menu.
* Added an option to create a single pixel texture region that will be added to the drawables.
* Added Auto Patches functionality to Ten Patch dialog.
* The cursor and selection in the preview of the BitmapFont/FreeTypeFont dialogs now changes color based on font color.
* The auto generated FNT name for BitmapFont dialog no longer defaults to a name that overwrites an existing file.
* Fixed improper behavior of colors in FreeTypeFont dialog when changing a color value. (Thanks piotr-j)
* Fixed crash when using a FreeTypeFont placeholder.
* Fixed crash when pressing escape in BitmapFont dialog.
* Fixed crash when adding a Slider to Scene Composer.
* Fixed defaults being exported unnecessarily in Scene Composer.
* Fixed incorrect classes and defaults for Scene Composer widgets.
* Fixed SceneComposer Export to Java not converting escaped text.
* Minor UI tweaks and bug fixes.

### Skin Composer Version 39 ###
* Fixed Scene Composer exporting redundant Label#setEllipsis() if using default values.
* Fixed casting error with LeadingTruncateLabel in Bitmap Font dialog.

### Skin Composer Version 38 ###

* Added shortcut "Ctrl+e" for export in Scene Composer.
* Added MoveCellToNewRowAbove and MoveCellToNewRowBelow in Scene Composer.
* Improved file menu. Recent Files is now a submenu.
* Updated instructions for Custom Serializer in FreeType Font dialog to include link to FreeTypeSkin.
* JSON exports are guaranteed to be UTF-8 formatted to prevent issues with loading unicode characters.
* Scene Composer now exports to clipboard without fully qualified names.
* Scene Composer now tracks its own last path to avoid accidentally overwriting your Skin export.
* Clicking on a disabled class in SetWidget for SceneComposer takes you to the class that requires the style in Skin Composer. 
* Background color of font is automatically adjusted based on font color in Bitmap Font Dialog.
* An appropriate background color is generated for FreeType font previews in the Fonts dialog.
* Prevent colors with the same name from being generated.
* Fixed crash if setting increment on Sliders/ProgressBars to a value less than 1
* Fixed crash when renaming a Color and there is a TenPatch that does not have a color defined.
* Fixed Lwjgl3FileHandle issue when reading/writing SCMP files.
* Fixed parent not copying when duplicating a style.
* Fixed unable to export to clipboard with a tree widget in Scene Composer.
* Fixed background color not being persisted in Scene Composer.
* Fixed background color not being reset when Scene >> Clear is selected in Scene Composer.
* Replaced incorrect quote character in Scene Composer font.
* Fixed error with incorrect cell alignment when exporting from Scene Composer.
* Fixed selected drawable not being highlighted in detail view of Drawables dialog.
* Fixed crash when using FreeTypeFonts that are unusually small and decreasing their size until they are not visible.
* Fixed incorrect types for VisTextButtonStyle in the VisUI template. 
* Minor UI tweaks and bug fixes

### Skin Composer Version 37 ###

* Fixed Window EXE version incapable of exporting Java files from Scene Composer.
* Prevent crash if setting cell spacing to less than 0 in Scene Composer.
* Minor UI tweaks and bug fixes.

### Skin Composer Version 36 ###

* Added Touchable and Visible options to all widgets in Scene Composer.
* The preview for a Custom Drawable is now tiled instead of looking nasty at larger scales.
* Fixed missing icons for uninstall and scmp files in Windows installer.
* Fixed Move Cell Up and Down being broken in Scene Composer.
* Disabled ScrollPane if there are no valid ScrollPane styles in Scene Composer
* Fixed adding an empty image to Stack causes a crash.
* Fixed null styles resulting in an unclickable preview for the Edit mode in Scene Composer. 
* Fixed unable to delete an ImageTextButton in Scene Composer
* Fixed unable to change ScrollPanesVisible in Scene Composer
* Added missing control for ScrollBarTouch in Scene Composer.
* Added missing Command symbol for mac users in file menus.

### Skin Composer Version 35 ###

* Added Scene Composer to generate basic GUI's with a visual editor.
* Added submenu controls in dialogs to clean up interface.
* Added duplicate option for Tinted and Tiled Drawables.
* Cleaned up Drawables Dialog. Popups for Drawable settings and labels describing what type of Drawable it is.
* Drawables can be hidden in the Drawables dialog to prevent them from being shown with the default filter. They can be unhidden by deactivating the filter and deselcting "hidden".
* Drawables filter is persisted between every use. Remember to click the filter button and choose "reset" to disable the filter.
* When selecting a Drawable for a property, the currently selected Drawable is highlighted in the dialog.
* Textures associated with Bitmap Fonts are now viewable in the Drawables Dialog to help reduce confusion from overwriting with another Drawable. Enable via the Drawables filter.
* Background color is automatically set in FreeTypeFont dialog.
* Red marquees make it more obvious that required values are missing in various dialogs.
* Preview background color is saved with the project for convenience.
* Running on Mac OSX no longer requires start with -XstartOnFirstThread option.
* Fixed being unable to choose "Open" or "Save As" after accessing a demo project on Mac.
* Fixed crash when adding a custom placeholder drawable to a project that is set to keep resources relative.
* Fixed Tenpatch dialog not displaying correct region in preview when removing all animation frames.
* Fixed crash with FreeTypeFonts when they are missing from the project.
* Default maximum for ProgressBar previews is 10 instead of 100.
* Corrected terminology in 9patch editor.
* Fixed crash when typing "[]" into text fields.
* Fixed exporting with Simple Names breaking TenPatchDrawables.
* Fixed importing Skin with multiple refrences to the same TTF file failing.
* Set "NO_MAXIMUM" on FreeTypeFontGenerators to allow FreeType fonts of any size.
* Updated to Gradle 6.4.1. Building Skin Composer project with JDK 14 suggested. Running Skin Composer JAR requires JRE 11 or later. 
* Minor UI Tweaks and bug fixes. 

### Skin Composer Version 34 ###
**Please use TenPatch 5.0.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Fixed save files with Ten Patches accidentally saving TextureRegions, causing a crash on load.
* Fixed content padding not showing in main preview for Ten Patches.
* Fixed export of PlayMode for animated Ten Patches.

### Skin Composer Version 33 ###
**Please use TenPatch 4.2.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Added PlayMode option to Ten Patch animations. Use this to single play, loop, reverse, and randomize your animations.
* Added option to duplicate an existing Ten Patch in the Drawables dialog.
* Allow copy/paste of Ten Patch data by pressing Ctrl+C/Ctrl+V respectively in the TenPatchDialog.
* Added option for preview background color in Ten Patch Animation dialog.
* Changed keyboard shortcut for moving frames to left/right arrow. No longer necessary to hold control.
* Improved performance of TenPatches when rebuilding the TextureAtlas in the Drawable dialogs and in the main menu.
* Fixed dark preview of texture and staggered scrolling at slow speeds in Ten Patch Dialog.
* Fixed keyboard shortcuts not working in Ten Patch Animation dialog.
* Fixed Ten Patch export not setting default stretch regions if they are not specified in the editor.
* Fixed renaming colors causing Ten Patches to break.
* Fixed deleting drawables causing Ten Patches to break.
* Minor UI Tweaks and bug fixes.

### Skin Composer Version 32 ###
**Please use TenPatch 4.1.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Fixed keyboard focus issue in Ten Patch Animation dialog.
* Minor UI Tweaks and bug fixes.

### Skin Composer Version 31 ###
**Please use TenPatch 4.1.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Added gradient and offset options for scrolling background effects to TenPatch. See "More settings..." in the editor.
* Added animation editor for TenPatch.
* Added number labels for the TenPatch editor handles.
* Added right click panning and double click content/padding shortcut to NinePatch editor for parity with the TenPatch editor.
* Fixed TenPatchDrawable content padding not being displayed in the preview.
* Added filter option for TenPatches in the drawables dialog.
* TenPatches with no stretch areas now preview correctly outside of the editor.
* Pressing escape in drawables dialog disables the filter.
* Prevent small fonts from crashing FreeType and Bitmap Font dialogs.
* Prevent crash when creating a font that requires more than one texture page in Bitmap Font dialog.
* Fixed being unable to set content if handles are squished to the far right or bottom in TenPatch editor.
* Fixed being able to make a new stretch area while in content mode in Ten Patch.
* Minor UI Tweaks and bug fixes.

### Skin Composer Version 30 ###

* Integrated TenPatch functionality as an alternative to 9patch. See https://github.com/raeleus/TenPatch
* The last zoom level of the drawables dialog will be persisted to the next time you open it.
* Allow for drag and drop of JSON files into import dialog.
* minWidth and minHeight values are now correctly exported for Tinted drawables
* minWidth and minHeight values are now imported from JSON files.
* Added dialog to fix the minWidth and minHeight settings of drawables when opening old projects.
* Added tooltips when hovering over items in the preview for custom classes.
* Added option to change preview texture packer settings. See Project >> Settings.
* Fixed nine patch editor not showing drawables when selected from Content in Preview area.
* Added wrapping to Style Property entries to prevent overflow.
* Fixed character filtering in Spinner so that negative values can be entered properly.
* Fixed losing focus when typing a new value for number fields in Style Properties.
* Various UI tweaks and bug fixes.
* Source updated to JDK 11

### Skin Composer Version 29 ###

* Improved Windows distribution with single file EXE
* Added option to set minWidth and minHeight of any drawable.
* Fixed issues with pressed, unpressed, and checked offsets in buttons.

### Skin Composer Version 28 ###
* BREAKING CHANGE: Bitmap/Freetype Font settings files may not be compatible with this version of Skin Composer unless converted to UTF-8.

* Skin Composer now has a wiki: https://github.com/raeleus/skin-composer/wiki
* Resolved release issues with Mac and Windows.
* Dialogs now highlight which field needs to be completed in order to continue.
* Added option to not show warnings when exporting a skin.
* Added option to import/export colors in a skin JSON as hexadecimal.
* Default export path is now a per project setting to prevent unfortunate accidents.
* Allow loading characters from a text file (UTF-8) in Font dialogs. This allows for any Unicode characters to be included in your fonts.
* Added detail view to Drawables dialog.
* Improved Image Font dialog's calculation of default baseline.
* Fixed pressing enter while typing in the preview dismisses the Image Font dialog.
* Fixed Create Bitmap Font dialog not closing file stream.
* Fixed errors when creating new font that overrides existing drawables.
* Fixed errors when opening a project with missing assets on Mac.
* Fixed unable to import FreeType fonts when importing a skin.
* Fixed filter error in native dialogs on Mac.
* Changes to texture packer settings no longer influence internal rendering of assets. This prevents "useIndexes" from crashing skin composer.
* Minor bug fixes and interface improvements.

### Skin Composer Version 27 ###
* Added filter option for Drawables dialog. Click the filter button or type text directly in the dialog.
* Allow drag and drop of folders into Drawables and Fonts dialogs.
* Fixed crash bug when editing the preview in Nine Patch Editor without an image loaded.
* Fixed crash when erasing a value in the Nine Patch Editor.
* Fixed preview properties for SelectBox: alignment and max items count.
* Fixed issue with TextField messageFontColor not working.
* Various formatting fixes.

### Skin Composer Version 26 ###
* Fixed export creating JSON with null values for Styles that have parents
* Allow drag and drop of SCMP files into main screen to load project.
* Created export dialog and moved export settings to this window.
* Created import dialog with options to import into a new project or the current one.
* Added options to not generate a texture atlas and to not copy font FNT/TTF files on export.
* Improved appearance of Settings dialog.
* Updated Tiny File Dialogs to 3.2.0. Resolves #45
* Added Style property type to Custom Classes. Resolves #46
* Fixed crashes when changing a custom style property's type.

### Skin Composer Version 25 ###
* Fixed exception when opening an old project.
* Improved error message when trying to add a font that is too large for the texture settings.

### Skin Composer Version 24 ###
* Updated to libGDX 1.9.9.
* Added setting for export with simple names per 1.9.9. See Project > Settings.
* Allows import of 1.9.9 skins with simple names.
* Added a parent setting to each style to support 1.9.9 cascading styles. Selectable parent options respects class inheritance.
* Resolved issue with Create Bitmap Font dialog creating fonts with incorrect baseline values.
* Resolved drag and drop broken after using Create Bitmap Font dialog.
* Added drag and drop to Create Bitmap Font and Freetype Font dialogs.
* Fixed SplitPane preview not allowing for adjustments.
* Added indent spacing and tied missing properties to the Tree preview.
* Prioritized nine patches over standard graphics when images of the same name are dragged into the Drawables dialog.
* Auto zoom and recenter in Nine Patch Editor now considers width in addition to height.
* Resolved auto patches not functioning correctly for fully opaque, single-colored images in Nine Patch Editor.
* Added save/load settings for bitmap font and Freetype Font dialogs.
* Added check for preview fonts upon project load.
* Added option to change preview background color in Nine Patch Editor.
* Added option to change preview background to bitmap font and Freetype font dialogs.
* Improved appearance of handles in Nine Patch Editor when zoomed out.
* Fixed crash when trying to move handles in Nine Patch Editor when no image is loaded.
* Resolved update button disappearing when a new project is started.
* Replacing a drawable with a new drawable no longer breaks the link with styles using the original.
* Proper replacement of custom drawables with a standard drawable.
* Replacing a drawable no longer deletes tinted drawables based on that file.
* Fixed crash when viewing fonts with multiple images after importing skin on an unsaved, new project.
* Added UI support for ISO-8859-1: Western European and ISO-8859-2: Central European.
* Fixed characters select box value incorrectly set when editing a Freetype font.
* Fixed "duplicate custom class" creating an extra default style.
* Minor UI fixes.

### Skin Composer Version 23 ###
* Added 9-Patch Editor. See Project >> Drawables >> Create 9-Patch.
* Fixed crash bug when adding a drawable after creating a custom drawable.
* Fixed crash bug when saving a project with relative paths and a FreeType font with no custom serializer.

### Skin Composer Version 22 ###
* Added setting to check for updates. Update button appears on top right of main screen.
* Added dialog to create Bitmap Fonts as an alternative to Hiero.
* Fixed imported skin files having darkened transparent images.
* Added preview bg color setting for Image Font dialog.
* Temporary files and program settings are now saved in folder ".skincomposer" of the user's home directory.
* Added Windows installer and file associations for ".scmp" files.
* Improved layout and applied fixes to FreeType dialog.

### Skin Composer Version 21 ###
* Fixed blending issue causing white fonts to have dark outlines in Image Font.
* Automatically suggest target file name in Image Font.
* Fixed SplashScreen.

### Skin Composer Version 20 ###
* Upgraded to JDK 10
* Added ImageFont dialog to create Bitmap Fonts from images designed in an image editing software.
* Updated UI to use FreeType font.
* Fixed FreeType fonts not loading correctly from SCMP file.
* Fixed FreeType font selections in custom classes not saving with SCMP file.
* Fixed editing FreeType fonts removes them from custom classes.
* New executable releases are packaged by JavaPackager and JLink

### Skin Composer Version 19 ###
* Fixed locale error causing crash bug in the FreeType dialog.
* Fixed crash with font in custom class when rendering table.
* Fixed export not acknowledging custom class with font set to a FreeType font.

### Skin Composer Version 18 ###
* Added FreeType font option. Users can generate their own fonts or build their fonts in the editor.
* Added option to export custom classes before or after the standard UI classes are defined.

### Skin Composer Version 17 ###
* Added a hexadecimal field to the color picker.
* Added custom drawable button to drawables dialog. Use this to refer to your classes that inherit from Drawable specified in the new custom class dialog.
* Fixed alpha slider being initialized with the incorrect color in color picker.
* Fixed keyboard shortcuts being incorrectly configured on alternative keyboard layouts.
* Fixed crash bug on Mac.
* Fixed spinner not accepting numbers starting with a decimal point.
* Fixed removing a custom property does not remove it from new custom styles.
* Reordered JSON export so that custom classes can be implemented by styles.

### Skin Composer Version 16 ###
* Added Ctrl+E shortcut for export.
* Added F5 shortcut for refreshing the texture atlas.
* Moved refresh texture atlas option to project menu.
* Added TiledDrawable functionality. See Project >> Drawables.

### Skin Composer Version 15 ###
* Added Export Format option to allow the user to select between Minimal, JavaScript, and JSON output types when exporting a skin JSON. See Project >> Settings.

### Skin Composer Version 14 ###
* Added "Raw Text" type for custom class properties to allow manual typing of arrays and objects in JSON format. Ex. ["banana", "strawberry", "grape"]
* Allow import of objects and arrays in custom classes as "Raw Text" types.
* Added a splash screen to show while Java is loading.
* Added support for Spanish characters: ñ, ¿, ¡, á, é, í, ó, ú, ü.
* All JSON exports are now encoded in UTF-8.

### Skin Composer Version 13 ###
* Fixed crashes in Progress Bar and Slider when changing spinner values to invalid values.
* Added prompt for user to browse for missing files when opening a project.

### Skin Composer Version 12 ###
* Fixed font dialog not allowing import of fonts larger than 1024x1024
* Possible fix for Mac version

### Skin Composer Version 11 ###
* Fixed unable to hide welcome screen setting
* Enforce using "_data" folder when "Keep resources relative" is selected. Solves #20
* Fixed alignment property of the TextField preview having no effect.
* Fixed renaming a style selects a different style afterwards
* Fixed wrong warnings when exporting imported skins

### Skin Composer Version 10 ###
* Added Welcome Screen with templates and sample files to load.
* Added a warnings dialog to alert the user with what failed to load in import/export. Can be exported to text file.
* stageBackground for Window class is now properly previewed in the preview pane.
* Various bug fixes.

### Skin Composer Version 9 ###
* Added a pseudo preview for custom classes. Drawables, fonts, colors, etc. are laid out one after the other.
* Solved dialog file filter issue on OSX affecting Open and Import.
* Can import existing VisUI skins without crashing.
* Sort selection is persistent in Drawables dialog.
* Various bug fixes.

### Skin Composer Version 8 ###
* Added "Custom Class" option to include styles for classes not part of Scene2D.ui. Press the "plus" button next to the class select box.
* Defaults for exported texture atlasses are now editable via a defaults.json file. See "Project >> Settings >> Open texture packer settings file".
* File resources (Drawables and Fonts) can be kept relative to the save file. See "Project >> Settings >> Keep resources relative?".
* Fixed errors with default paths for drawables and fonts.
* Added tooltips to buttons as necessary.

### Skin Composer Version 7 ###
* Switched to TinyFileDialogs for all file dialogs across all platforms.
* Updated user interface to Skin Composer UI.
* Recent files menu option.
* Refactored all of the interface code to allow for new features.
* Multiple bug fixes and improvements.