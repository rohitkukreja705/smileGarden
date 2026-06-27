package com.example.smilegarden

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.smilegarden.ui.DesertScene
import com.example.smilegarden.ui.PermissionRequest
import com.example.smilegarden.ui.StatusBadge
import com.example.smilegarden.ui.SunflowerScene
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            MaterialTheme {
                SmileGardenApp(cameraExecutor)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@Composable
fun SmileGardenApp(cameraExecutor: ExecutorService) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    // Exponential-moving-average smoothed smile score, plus hysteresis
    // thresholds so the scene doesn't flicker between desert/garden on
    // noisy single-frame readings.
    var smoothedSmile by remember { mutableStateOf(0f) }
    var isSmiling by remember { mutableStateOf(false) }

    val bloomProgress by animateFloatAsState(
        targetValue = if (isSmiling) 1f else 0f,
        animationSpec = tween(durationMillis = 1400),
        label = "bloomProgress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        DesertScene(alpha = 1f - bloomProgress, modifier = Modifier.fillMaxSize())
        SunflowerScene(bloomProgress = bloomProgress, modifier = Modifier.fillMaxSize())

        if (hasCameraPermission) {
            CameraPreviewOverlay(
                cameraExecutor = cameraExecutor,
                onSmileValue = { value ->
                    val target = value ?: 0f
                    smoothedSmile = smoothedSmile * 0.7f + target * 0.3f
                    isSmiling = when {
                        smoothedSmile > 0.6f -> true
                        smoothedSmile < 0.3f -> false
                        else -> isSmiling
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(width = 110.dp, height = 150.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            StatusBadge(
                isSmiling = isSmiling,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        } else {
            PermissionRequest(
                modifier = Modifier.align(Alignment.Center),
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            )
        }
    }
}
