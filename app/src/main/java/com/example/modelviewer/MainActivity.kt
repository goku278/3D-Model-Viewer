package com.example.modelviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sceneview.SceneView
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.launch

private val ModelCatalog = listOf(
    ModelAsset("Bulb", "models/Bulb.glb"),
    ModelAsset("Fiagena", "models/Fiagena.glb"),
    ModelAsset("Lungs", "models/Lungs.glb"),
    ModelAsset("Microscope", "models/Microscope.glb"),
    ModelAsset("Solarsystem", "models/solarsystem.glb")
)

data class ModelAsset(
    val title: String,
    val assetPath: String
)

data class PlacedModel(
    val id: Long,
    val asset: ModelAsset,
    val x: Float = 0f,
    val y: Float = 0f,
    val size: Float = 220f,
    val rotation: Float = 0f,
    val zoom: Float = 1f,
    val interactionMode: Boolean = false,
    val labelsVisible: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                ModelViewerScreen()
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
private fun ModelViewerScreen() {
    var models by remember { mutableStateOf(listOf<PlacedModel>()) }
    var showPicker by remember { mutableStateOf(false) }
    var nextId by remember { mutableLongStateOf(1L) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val interactingModel = models.firstOrNull { it.interactionMode }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbar)
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "3D Model Viewer",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${models.size} models in gallery",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showPicker = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Add model")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            if (models.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your gallery is empty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Add a bundled GLB model to begin.",
                        color = Color(0xFF667085)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { showPicker = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Add your first model")
                    }
                }
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                        .background(color = Color.White),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 1
                ) {
                    models
                        .filterNot { it.interactionMode }
                        .forEach { placed ->
                            key(placed.id) {
                                ModelCard(
                                    placed = placed,
                                    onUpdate = { updated ->
                                        models = models.map {
                                            if (it.id == updated.id) updated else it
                                        }
                                    },
                                    onClose = {
                                        models = models.filterNot {
                                            it.id == placed.id
                                        }
                                    },
                                    onEnterInteraction = {
                                        models = models.map {
                                            if (it.id == placed.id) {
                                                it.copy(interactionMode = true)
                                            } else if (it.interactionMode) {
                                                it.copy(interactionMode = false)
                                            } else {
                                                it
                                            }
                                        }
                                    }
                                )
                            }
                        }
                }

                interactingModel?.let { placed ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                           // .background(Color(0x66000000))
                            .background(Color.White)
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    androidx.compose.foundation.interaction.MutableInteractionSource()
                                }
                            ) {
                                models = models.map {
                                    if (it.id == placed.id) {
                                        it.copy(interactionMode = false)
                                    } else {
                                        it
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        key(placed.id) {
                            ModelCard(
                                placed = placed,
                                onUpdate = { updated ->
                                    models = models.map {
                                        if (it.id == updated.id) updated else it
                                    }
                                },
                                onClose = {
                                    models = models.filterNot {
                                        it.id == placed.id
                                    }
                                },
                                onEnterInteraction = {
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPicker) {
        ModalBottomSheet(
            onDismissRequest = { showPicker = false }
        ) {
            Text(
                text = "Choose a model",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 28.dp)
            ) {
                items(
                    items = ModelCatalog,
                    key = { it.assetPath }
                ) { asset ->
                    ListItem(
                        headlineContent = { Text(asset.title) },
                        supportingContent = { Text(asset.assetPath) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            if (models.size >= 5) {
                                scope.launch {
                                    snackbar.showSnackbar(
                                        "The performance target is five models at once."
                                    )
                                }
                            } else {
                                models = models + PlacedModel(
                                    id = nextId++,
                                    asset = asset
                                )
                            }
                            showPicker = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelCard(
    placed: PlacedModel,
    onUpdate: (PlacedModel) -> Unit,
    onClose: () -> Unit,
    onEnterInteraction: () -> Unit
) {
    val currentPlaced by rememberUpdatedState(placed)

    var zoom by remember(placed.id) { mutableFloatStateOf(placed.zoom) }
    var rotationDeg by remember(placed.id) { mutableFloatStateOf(placed.rotation) }

    LaunchedEffect(placed.zoom) { zoom = placed.zoom }
    LaunchedEffect(placed.rotation) { rotationDeg = placed.rotation }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    Box(
        modifier = Modifier
            .size(placed.size.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .pointerInput(placed.id, placed.interactionMode) {
                if (placed.interactionMode) {
                    detectTransformGestures(
                        panZoomLock = false
                    ) { _, _, zoomChange, rotationChange ->

                        val newZoom = (zoom * zoomChange).coerceIn(0.4f, 3f)
                        val newRotation = (rotationDeg + rotationChange) % 360f

                        zoom = newZoom
                        rotationDeg = newRotation

                        onUpdate(
                            currentPlaced.copy(
                                zoom = newZoom,
                                rotation = newRotation
                            )
                        )
                    }
                }
            }
    ) {
        SceneView(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 38.dp, bottom = 4.dp)
                .background(Color.White),
            engine = engine,
            modelLoader = modelLoader
        ) {
            val modelInstance = rememberModelInstance(
                modelLoader,
                placed.asset.assetPath
            )

            modelInstance?.let { instance ->
                key(rotationDeg, zoom) {
                    ModelNode(
                        modelInstance = instance,
                        rotation = io.github.sceneview.math.Rotation(
                            y = Math.toRadians(rotationDeg.toDouble()).toFloat()
                        ),
                        scale = io.github.sceneview.math.Scale(zoom)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = placed.asset.title,
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            IconButton(
                onClick = {
                    if (placed.interactionMode) {
                        onUpdate(placed.copy(interactionMode = false))
                    } else {
                        onEnterInteraction()
                    }
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenWith,
                    contentDescription = "Toggle interaction mode",
                    tint = if (placed.interactionMode) {
                        Color(0xFF2563EB)
                    } else {
                        Color.DarkGray
                    },
                    modifier = Modifier.size(17.dp)
                )
            }

            IconButton(
                onClick = {
                    onUpdate(placed.copy(labelsVisible = !placed.labelsVisible))
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = "Toggle labels",
                    tint = if (placed.labelsVisible) {
                        Color(0xFF2563EB)
                    } else {
                        Color.DarkGray
                    },
                    modifier = Modifier.size(17.dp)
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close model",
                    tint = Color(0xFFB42318),
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        if (placed.interactionMode) {
            Text(
                fontSize = 7.sp,
                text = "INTERACT",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(2.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                color = Color(0xFF1D4ED8),
                fontWeight = FontWeight.Bold
            )
        }

        if (placed.labelsVisible) {
            Text(
                text = placed.asset.title,
                color = Color(0xFF1D4ED8),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xEEFFFFFF))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 10.sp,
               // color = Color.DarkGray
            )
        }
    }
}