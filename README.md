# Lingering Click Tooltips

Generates tooltips (or toasts) for left click actions, just like tapping the screen on the mobile client. But more powerful.

![lingering-click-tooltips-kitten](https://user-images.githubusercontent.com/29218387/126550301-9b674927-d215-40b0-8264-24c917435304.gif)

Double tap the CTRL key to toggle tooltips. While tooltips are hidden, hold down the CTRL key to show tooltips normally. <br>
Choose whether to filter certain trivial actions such as walking or eating.

![lingering-click-tooltips-kitten-colored-hide-hotkey-walk](https://user-images.githubusercontent.com/29218387/126551486-60e6178b-731f-4f83-b4d1-535949d94001.gif)

Offers lots of options to configure tooltip appearance, including duration, fade, cursor anchoring, color, and position.

![lingering-click-tooltips-kitten-anchored-colored-longer](https://user-images.githubusercontent.com/29218387/126550321-8baad828-8e27-4c09-8065-27f7cf837931.gif)

There are many ways to combine the different settings. Hopefully everyone can make use of this feature!<br><br>
If you would like to request/enhance a feature or if you notice a bug, remember to submit an issue. Read below for update notes!<br>

####Patch 1.3.1 (Aug 11, 2021)
* [ + ] New config "SHIFT blocks" added to "Hotkeys": Choose whether clicks should be consumed while holding SHIFT, must have filter mode set to blacklist/whitelist.
  * This allows users to peek a filter list action without the native client processing clicks.
  * Note that this activates the moment SHIFT is pressed, rather than waiting for the double-tap delay period like peeking.
* [ + ] New trivial click "Special attack": Choose whether using special attack should be hidden.
<br><br>
* [ ! ] Config "CTRL bypasses block" has been moved from "Filter lists" to "Hotkeys".
* [ ! ] Trivial clicks "SHIFT drop", "Use initiate", "Eat", and "Puzzles" have been moved to blocked by default.
<br><br>
* [ * ] Certain inputs were evaluating incorrectly on startup, causing unexpected behavior until the inputs were used.
  * User inputs controlling plugin behavior, such as CTRL or SHIFT, now initialize to corrected values.
* [ * ] Filter lists used insufficient logic for checking texts. This resulted in text being filtered despite the list not containing a perfect match.
  * The filter lists are now converted to collections of unique text before checking for matches, which allows the code to correctly identify perfect matches.
* [ * ] After updating a filter list, data tracking filter list tooltips cleared, which caused certain functions like peeking to no longer work until a new click is input by the user.
  * To prevent this, relevant data no longer clears after a filter list is updated.

### Update 1.3 (Aug 7, 2021)
This update is a mixed bag of new useful features, a small redesign, and a few bug fixes.
* [ + ] A tooltip fade-in has been added. It is recommended to keep this value relatively low (<25%) as it may cause a noticeable visual delay, which could be undesirable.
* [ + ] A new "Tick sync mode" has been added under the "Modes" config section. Tooltips will wait until the next game tick to begin showing.
  * Note that this will decrease your tooltip throughput, as only the most recent click within a game tick gets processed.
* [ + ] The blacklist/whitelist filter modes can now consume filtered clicks. Consumed clicks will not be passed on to the native client for processing. Config has been added under the "Filter lists" section.
  * [ + ] New config "Block filtered clicks": Choose whether filtered clicks should be consumed.
  * [ + ] New config "Show blocked clicks": Choose whether tooltips should appear for consumed clicks.
  * [ + ] New config "CTRL bypasses block": Choose whether holding CTRL will allow blocked clicks to process.
  * Users may find this feature awkward to use in conjunction with the whitelist, except for specific cases. When used properly, it can serve to prevent misclicks.
  * In response to this change, users are now able to blacklist or whitelist any clicks. Previously, trivial clicks were excluded from filter lists.
    * Trivial clicks will still be barred from showing regardless of their presence in either filter list as long as the corresponding checkbox is still enabled in "Trivial clicks".
<br><br>
* [ ! ] Some config names/descriptions have been updated to be more accurate or less wordy.
  * Notably, "Tooltip start opacity" updated to "Max opacity", in response to the addition of fade-in and the fade redesign.
* [ ! ] Tooltip fade has been redesigned to prevent tooltip duration from being drowned. Both fadeout and the new fade-in generate additional duration instead of consuming the configured tooltip duration.
  * For example, if tooltip duration = 500ms, fade-in = 10%, and fadeout = 20%:
    * A fade-in period equal to 50ms will be added at the beginning of the tooltip duration.
    * A fadeout period equal to 100ms will be added at the end of the tooltip duration.
    * This would give a total tooltip duration of 650ms. But, 150ms of that duration are fade periods.
    * The end result is that the 500ms tooltip duration setting defines the period the tooltip will render at the user-specified max opacity.
  * Users are recommended to reconfigure their tooltip duration, fade-in, and fadeout to feel more comfortable under the new design.
    * A good starting point would be tooltip duration = ~600ms, fade-in = ~20%, and fade-out = ~40%.
* [ ! ] Light mode now reduces tooltip start opacity by 25% in response to the changes to fade.
  * In other words, if you have tooltip opacity set to 80%, tooltips render as if it were set to 60%.
<br><br>
* [ * ] Certain trivial clicks were still being filtered despite having "Hide trivial clicks" set to false.
  * The config setting "Hide trivial clicks" was being evaluated at the wrong location in the code.
* [ * ] Tooltips remained hidden even when holding CTRL to toggle hide.
  * Experimental code was erroneously left in the overlay rendering. It has been removed.


### Update 1.2 (Aug 2, 2021)
This update bolsters tooltip filtering, among massive improvements to code and other areas.
* [ + ] The main feature of this update is the familiar blacklist/whitelist system. For this feature, the SHIFT key was adopted as the main operator.
  * [ + ] A new selector "Filter mode" was added to the "Modes" config section.
  * [ + ] A new config section "Filter lists" has been added above "Location" and below "Modes". Note that you _may_ modify the lists manually.
    * Any tooltip matching text in the blacklist will **NOT** show when "Filter mode" is set to "Blacklist".
    * Any tooltip **NOT** matching text in the whitelist will **NOT** show when "Filter mode" is set to "Whitelist".
  * [ + ] A new SHIFT double-tap delay has been added. Like the CTRL double-tap delay, it sets the delay required for activating useful functions.
    * Double-tapping SHIFT will add or remove the most recent tooltip (shown or not) to the relevant filter list.
    * Holding SHIFT for longer than the double-tap delay will peek the action, allowing users to visually confirm actions. If no filter mode is enabled, you can still peek your last click.
    * For both SHIFT key functions, the CTRL key must also be held.
    * Setting the double-tap delay to 0 disables both functions.
  * The usage of SHIFT with CTRL is implemented without SHIFT-drop conflicts. Feel free to enable both!
* [ + ] A few more actions have been added to trivial clicks. Please review them to ensure they are configured as desired.
  * [ + ] Added: "Walk here" with a target, "Toggle Run", "Quick-prayers" (Activate/Deactivate), toggling prayers from the panel, and most panel clicks (like "Inventory") as "Panels (group)".
  * Among those added to trivial clicks, some have been blocked by default. They will **never** appear. This **cannot** be configured.
    * [ + ] Default added: "Continue" (which was moved from configurable), "Close", "World Switcher", and "Select".
    * Blocked by default actions already existed, for those unaware.
  * If you think there are any other actions which should be added to trivial clicks, please submit an issue!
* [ + ] Clamp padding has been added under the "Location" config section. Larger values force tooltips to render further away from the canvas border.
* [ + ] An opacity value has been added to the custom background color! Users can set a more transparent background if desired.
* [ + ] A new selector "Tooltip location" has been added under the "Location" config section. Use it to specify where the tooltips will show.
  * "Lingering" specifies the familiar lingering tooltips, which remain at the click target.
  * "Anchored" specifies the same anchoring behavior as before where tooltips follow the mouse cursor.
  * "Fixed" is a new location where users can specify a static location for click text.
    * For now, only the most recent action will show in the fixed location. Multiple tooltips support may come in a future update.
    * Fixed tooltips will continue to render as transparent even after fully fading. This is to allow the user to adjust the fixed location at any time.
<br><br>
* [ - ] The "Anchored" checkbox under "Location" has been replaced by the "Tooltip location" selector.
<br><br>
* [ ! ] Double-tap features have been strengthened by timing out key presses that are too long.
<br><br>
* [ * ] The recently added clamping code restricted non-anchored tooltips to the viewport, which excludes the panel/minimap/chat areas in Fixed mode.
  * The code now uses canvas dimensions instead of viewport dimensions so that Fixed mode users can see non-anchored tooltips rendered everywhere. Nothing changes for resizable users.

### Update 1.1 (Jul 26, 2021)
As a reminder to all, please remember to use the double-tap CTRL feature, instead of the RuneLite plugin panel, to quickly toggle tooltips. Read over all the other features this plugin has to offer as well!
* [ + ] Text now preserves the color of the menu option (e.g., NPC names are yellow).
* [ + ] Personalize your text by applying a custom text color! This custom color can, if desired, override the menu option color.
* [ + ] Ability to show more than one concurrent tooltip. If multiple permanent tooltips are showing, opacity decreases as tooltips "age".
* [ + ] Specify the opacity tooltips will render at when they first appear (see tooltip start opacity).
* [ + ] "Wear" and "Wield" (not from menu) have been added to trivial clicks.
* [ + ] The plugin has a new icon for the plugin hub!
<br><br>
* [ - ] Permanent tooltip opacity has been replaced by tooltip start opacity.
<br><br>
* [ ! ] Tooltips now clamp to the viewport. This means that they will automatically adjust their position if they were to render offscreen or cut off.
* [ ! ] The "Modes" config section has been moved above "Location" and below "Appearance" for easier access.
* [ ! ] The "Location" and "Hotkey" config sections closed by default to reduce config clutter.
<br><br>
* [ * ] Anchored tooltips no longer render if the mouse cursor is not over the game window. They will reappear as normal if the cursor enters the game window.

### Release 1.0 (Jul 21, 2021)
After 5 days of development, the plugin has been released to the plugin hub! Now RuneLite users can finally be assured of their actions the same as mobile users.
* [ + ] The plugin is capable of showing the last click action as a configurable tooltip/toast.
* [ + ] Ability to configure tooltip duration, fade, cursor anchoring, background color, and position.
* [ + ] The CTRL key was chosen to be the dedicated hotkey for the plugin. Double-tap CTRL toggles hotkeys, holding CTRL while hotkeys are off shows tooltips normally.
* [ + ] Trivial clicks such as "Walk here" will not receive a tooltip unless configured to do so.
* [ + ] A few useful modes have been added to quickly adapt the behavior of the plugin without having to tweak all the various configurations.

Markers and what they indicate:<br>
[ + ] Addition of a new feature.<br>
[ - ] Removal of an existing feature.<br>
[ ! ] A change to a feature, or an important notice to users.<br>
[ * ] A bug, followed by what caused it and how it was resolved.<br>
