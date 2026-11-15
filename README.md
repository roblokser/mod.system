# System Client — Fabric 1.21.11

Client-side starter project based on the supplied UI reference.

## Included
- Right Shift opens/closes the menu.
- Dark System-style screen with a left navigation bar and Combat / Configs pages.
- Aim Assist and Trigger Bot module cards (enable/disable UI).
- Middle-click a module card, then press a key to assign a toggle bind.
- Click a module normally to select it and show its settings.
- Click settings rows to cycle target type, aim speed, prediction, FPS Adaptive, delay and trigger prediction.
- Automatic `default.json` persistence and named JSON configs under `.minecraft/config/systemclient/`.
- Bound keys toggle the corresponding module state.

## Build
Requires JDK 21 and internet access for Gradle/Fabric dependencies.

1. Open this folder in IntelliJ IDEA with the Gradle plugin.
2. Allow Gradle to sync the project.
3. Run the Gradle task `build`.
4. The remapped mod jar will be in `build/libs/`.

Minecraft 1.21.11 uses Java 21. The build uses Fabric Loader 0.18.1, Fabric API 0.141.1+1.21.11 and Loom 1.14.1.

## Important status
This is a menu/configuration starter, not a finished combat implementation: Aim Assist and Trigger Bot currently have settings and toggle states, but do not yet rotate the camera or send attack actions. Add and test that server-specific gameplay logic separately.
