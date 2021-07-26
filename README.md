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
If you notice the features listed in the latest update are not yet on your client, chances are the commit has yet to be submitted or is awaiting approval.

### Update 1.1
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
* [ ! ] Anchored tooltips no longer render if the mouse cursor is not over the game window. They will reappear as normal if the cursor enters the game window.

### Release 1.0
After 5 days of development, the plugin has been released to the plugin hub! Now RuneLite users can finally be assured of their actions the same as mobile users.
* [ + ] The plugin is capable of showing the last click action as a configurable tooltip/toast.
* [ + ] Ability to configure tooltip duration, fade, cursor anchoring, background color, and position.
* [ + ] The CTRL key was chosen to be the dedicated hotkey for the plugin. Double-tap CTRL toggles hotkeys, holding CTRL while hotkeys are off shows tooltips normally.
* [ + ] Trivial clicks such as "Walk here" will not receive a tooltip unless configured to do so.
* [ + ] A few useful modes have been added to quickly adapt the behavior of the plugin without having to tweak all the various configurations.
