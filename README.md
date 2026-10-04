# 3D Model Viewer — Jetpack Compose

Single-activity Kotlin + Jetpack Compose implementation for the Infusory Future Tech Labs screening task.

## Included
- Single Activity and Compose UI.
- Add-model bottom sheet with five catalog entries.
- Multiple model cards on a shared canvas (limited to five).
- Drag cards in normal mode.
- Pinch to resize cards in normal mode.
- Per-card interaction and label toggles, plus close/remove.
- SceneView/Filament GLB rendering integration.

## Required model assets
The five `.glb` files in this project are generated low-poly illustrative sample models (piston, engine, gearbox, valve, assembly), not the original employer-provided assets. Replace them with the supplied task models if available. They are bundled here at:
```
app/src/main/assets/models/piston.glb
app/src/main/assets/models/engine.glb
app/src/main/assets/models/gearbox.glb
app/src/main/assets/models/valve.glb
app/src/main/assets/models/assembly.glb
```
Rename the catalog entries in `MainActivity.kt` if the supplied filenames differ. The bundled sample files provide basic 3D geometry and node `extras.prop` labels, but they are simplified stand-ins.

## Build
Open this folder in Android Studio (JDK 17), allow Gradle sync, then run the `app` configuration on a device with OpenGL ES support.

## Important implementation notes / remaining acceptance work
This is a project scaffold, not a verified submission APK. The SceneView API can change between releases; sync/build against the selected `io.github.sceneview:sceneview:2.3.0` dependency and adjust API imports/signatures if required by that version.

The screening task also requires part labels from each GLB's JSON `nodes[].extras.prop`, anchored to each node's projected screen coordinates and updated every frame. That feature needs the five actual GLB assets and a renderer-level node/world-position projection pass; the current UI includes the label toggle but does not yet draw projected labels. Likewise, interaction-mode rotation should be wired to a dedicated gesture handler and the model's own camera/content zoom; normal-mode and interaction-mode gestures must remain mutually exclusive.

## Performance checklist before submission
- Profile with Android Studio Profiler and Perfetto on a 2–3 GB RAM device.
- Measure frame time with five models, not just one.
- Avoid recreating model instances during recomposition; cache instances and destroy them when a card closes.
- Prefer a shared Filament engine with independently managed model instances/scenes where supported by the chosen SceneView API.
- Verify model resource destruction on close and lifecycle cleanup.
- Record device, Android version, measured FPS, and known limitations in this README before submitting.
