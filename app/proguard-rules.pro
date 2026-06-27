# Keep ML Kit face detection classes (reflection-based init in Play Services bridge)
-keep class com.google.mlkit.vision.face.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_face_internal.** { *; }
-dontwarn com.google.mlkit.**
