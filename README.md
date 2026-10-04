3D Model Viewer — Jetpack Compose


Included
- Single Activity and Compose UI.
- Add-model bottom sheet with five catalog entries.
- Multiple model cards on a shared canvas (limited to five).
- Drag cards in normal mode.
- Pinch to resize cards in normal mode.
- Per-card interaction and label toggles, plus close/remove.
- SceneView/Filament GLB rendering integration.

Required model assets
The five .glb files in this project are generated low-poly illustrative sample models (piston, engine, gearbox, valve, assembly), not the original employer-provided assets. Replace them with the supplied task models if available. They are bundled here at:
'''
app/src/main/assets/models/piston.glb
app/src/main/assets/models/engine.glb
app/src/main/assets/models/gearbox.glb
app/src/main/assets/models/valve.glb
app/src/main/assets/models/assembly.glb
'''
Rename the catalog entries in MainActivity.kt if the supplied filenames differ. The bundled sample files provide basic 3D geometry and node extras.prop labels, but they are simplified stand-ins.

Build
Open this folder in Android Studio (JDK 21), allow Gradle sync, then run the app configuration on a device with OpenGL ES support.

Important implementation notes / remaining acceptance work
This is a project scaffold, not a verified submission APK. The SceneView API can change between releases; sync/build against the selected io.github.sceneview:sceneview:2.3.0 dependency and adjust API imports/signatures if required by that version.

