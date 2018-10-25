# Changelog

Changelog format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Time text (shows when carrying pocket watch) position can be changed to LEFT, RIGHT, TOP, BOTTOM. Default is AUTO, which places the text either left or right as appropriate (old behavior).
### Changed
- HUD textures (Z-Tunic). Positioning of elements is slightly different, if anyone has made a resource pack, it will need to be updated.
### Fixed
- Mod breaking time in other dimensions, should completely ignore everything but the overworld now [#25, #29]
- Clock bar displaying incorrect time in other dimensions
- Clock bar time text being in wrong position when left of bar and using 24-hour clock
- Extended periods now respect the doDaylightCycle gamerule
- Pocket watch recipe now has a JSON file, instead of direct register (oops)
- Miscellaneous small fixes (removes some warnings from log)

## [0.2.7]
- Updated for Silent Lib 3.0.x

## [0.2.6]
- Updated for Silent Lib 2.3.18

## [0.2.5]
### Changed
- Clock HUD and debug info hides when the debug screen (F3) is open.
### Fixed
- World time being set incorrectly to some time in the first day... hopefully.
- Sky renderer being replaced in dimension Extended Days does not affect [#20]

## [0.2.4]
### Fixed
- World time occasionally resetting on world load.
- A crash during world load [#16]
- A crash when used with Bloodmoon 1.5.3 [#18]

## [0.2.3]
### Changed
- Removes use of the doDaylightCycle gamerule. You may need to re-enable it if you were on an extended period when you logged off. This should fix compatibility with most if not all mods that affect sleep.

## [0.2.2]
### Added
- Compatibility with the Bloodmoon mod. Mimics Bloodmoon's sky rendering and adds a red nighttime clock overlay.
- Config to disable clock overlay entirely.

## [0.2.1]
### Fixed
- A crash some users were experiencing [#9]

## [0.2.0]
### Added
- The pocket watch. With a pocket watch in your inventory, you can tell time accurately, no matter where you are! Craft a vanilla clock with some type of quartz to get one.
- Pocket watch displays an actual time next to the clock bar. It's a 24-hour clock by default but you can change it to a 12-hour clock (with AM/PM) if that's more your style. There's also a config to disable it entirely. The text is to the right of the clock bar normally, or to the left if the clock bar is anchored to the right of the screen (tldr: you won't need to tweak your clock bar's position)
- You can now see the clock bar whenever there's a vanilla clock in your inventory, but it will not display the exact time.
- A new command, "edtime". Allows opped users to get or set the current time. Currently, the set command requires two values and is not very intuitive. I'm working on it.
### Fixed
- The sun/moon no longer freeze during extended periods. The mod completely takes over sky rendering, so it may not working correctly if other mods try to do the same.

## [0.1.2]
### Added
- Support for Morpheus [#7]
### Fixed
- Sleeping during extended periods not working [#7]

## [0.1.1]
### Added
- The ability to shorten days/nights with negative extended periods (this advances time, so the sun/moon will jump ahead. I'm still working on a fix for sky rendering) [#4]
### Fixed
Clock and actual time not syncing when playing on a dedicated server [#3]
Removed some unneeded debug text [#5]

## [0.1.0]
- First alpha release! Extended time periods and clock HUD.
