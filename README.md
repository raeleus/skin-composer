# README #

This project is programmed in Java and depends on LibGDX and the LWJGL3 backend.

### Skin Composer ###

* Use Skin Composer to create skins for scene2d.ui
* Version 24

### Changes ###

* Updated to LibGDX 1.9.9.
* Resolved issue with Create Bitmap Font dialog creating fonts with incorrect baseline values.
* Resolved drag and drop broken after using Create Bitmap Font dialog.
* Added drag and drop to Create Bitmap Font dialog.
* Fixed SplitPane preview not allowing for adjustments.
* Added indent spacing and tied missing properties to the Tree preview.
* Prioritized nine patches over standard graphics when images of the same name are dragged into the Drawables dialog.
* Auto zoom and recenter in Nine Patch Editor now considers width in addition to height.
* Resolved auto patches not functioning correctly for fully opaque, single-colored images in Nine Patch Editor.
* Added save/load settings for bitmap font and freetype font dialogs.
* Added check for preview fonts upon project load.
* Added option to change preview background color in Nine Patch Editor.
* Added option to change preview background to bitmap font and FreeType font dialogs.
* Improved appearance of handles in Nine Patch Editor when zoomed out.

### Contact ###

* This project is maintained by Raymond "Raeleus" Buckley
* http://ray3k.wordpress.com
* raymond.ray3k (at) gmail.com

### Notes ###

To run Skin Composer on OSX, use the executable Mac release. If that fails, download the skin_composer_mac.jar version of the app and run it via terminal with the following command: **java -XstartOnFirstThread -jar skin_composer_mac.jar**

To run/build the project in Idea/Android Studio, do the following:

* In 'File/Project Structure...' set the project language level to 10
* Update run configuration with the following VM Option: -XstartOnFirstThread

Thanks to contributer RaimundWege for this information.

### License ###
MIT License

Copyright (c) 2018 Raymond Buckley

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
