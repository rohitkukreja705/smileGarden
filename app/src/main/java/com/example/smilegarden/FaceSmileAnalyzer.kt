package com.example.smilegarden

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * Runs each camera frame through ML Kit's on-device face detector with
 * classification enabled, and reports the highest smiling-probability found
 * (or null if no face / no usable frame). Detection is fast + tiny since we
 * only need bounding boxes + the smile classifier, not landmarks or contours.
 */
class FaceSmileAnalyzer(
    private val onSmileValue: (Float?) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        detector.process(image)
            .addOnSuccessListener { faces ->
                val bestSmile = faces.mapNotNull { it.smilingProbability }.maxOrNull()
                onSmileValue(bestSmile)
            }
            .addOnFailureListener {
                onSmileValue(null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
