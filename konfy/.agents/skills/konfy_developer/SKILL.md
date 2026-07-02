---
name: konfy-developer
description: Skill for developer AI agents to maintain the Konfy config library.
---

# Developer Skill for KonfyLib

This guide specifies codebase layout, rendering limits, and UI development coordinates.

## Codebase Layout

* **Main Screen**: `KonfyLibConfigScreen.java` handles layout, tab routing, and bottom bar buttons.
* **Base Screen**: `BaseScreen.java` handles general rendering, customized tooltips, and background blurs.
* **Option Widgets**: Found in `widgets/`. Subclasses represent different options (e.g. `BooleanWidget`, `ColorWidget`, `EnumOptionWidget`).
* **Warning Popups**: Positioned in `popup/impl/`. The 3-button save dialog is `SaveWarningPopUp.java`.

## Sizing and Layout Constraints

### 1. Vertical Layout System
* **Tab Bar**: Starts at `Y = 0` with a height of `24` pixels.
* **Dynamic Connecting Lines**: Rendered at `y + 1` (where `y = getY() + 19` of the tab box).
* **Scroll Area Starting Offset**: Option groups start rendering at `Y = 34`.
* **Viewport Clipping (Scissor)**: Vertical viewport scissoring starts at `Y = 24` and ends at `Y = height - 28`.
* **Click Guard**: Custom clickable settings must only handle clicks inside `Y >= 24` and `Y < height - 28`. This prevents background click sounds when clicking top/bottom bars.
* **Bottom Bar**: Separation lines are at `height - 28` and `height - 27`. Buttons are `50x16` positioned at Y = `height - 21`.
* **Search Bar**: Dimensions must be `102x18` positioned at Y = `height - 22`.

### 2. Option Row Alignments
* **Row Width**: `ScreenGlobals.OPTION_WIDTH` is set to `320`.
* **Interactive Elements Alignment**: Setting switches, dropdowns, and color preview boxes must end exactly `6px` or `7px` before the right edge.
  * Toggles: outline starts at `getX() + getWidth() - 31` (width `25`).
  * Dropdowns: box starts at `getX() + getWidth() - 45 - maxWidth` (width `maxWidth + 38`).
  * Color Preview: outline starts at `getX() + getWidth() - 31` (width `25`).
* **Reset Button**: Positioned at `getX() + getWidth() + 2` (floating outside the border).

### 3. Popups & Dialogue Dialogs
* **SaveWarningPopUp**: Size must be `220x70`. It contains a single centered warning text `"Do you wanna save the changes?"` and three buttons: Cancel (left), No (center), and Yes (right).
* **WarningPopUp**: Size is `300x100`. It contains a large title, line separator, description text, and Yes/No buttons.

### 4. APIScreen Mod Hub & Single-Screen Tab Navigation
* `APIScreen` extends `KonfyLibConfigScreen` and sets the `showModsTab` flag to `true` in its constructor.
* When `showModsTab` is enabled:
  * An additional `"Mods"` tab category is prepended to the category tab list (at index 0).
  * When `"Mods"` tab is active: standard options rendering and settings buttons are hidden, and the mod list grid elements start rendering at Y = 34.
  * The single `"Back"` button is rendered at the bottom-right of the mods grid view to exit the hub.
  * Clicking between the `"Mods"` and `"Showcase"` category tabs performs a seamless, local tab selection on the same screen (no transition/mouse reset).
