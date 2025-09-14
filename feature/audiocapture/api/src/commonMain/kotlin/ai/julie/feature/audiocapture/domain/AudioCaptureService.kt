package ai.julie.feature.audiocapture.domain

import ai.julie.feature.audiocapture.model.AudioApplication
import ai.julie.feature.audiocapture.model.AudioBuffer
import ai.julie.feature.audiocapture.model.AudioFormat
import kotlinx.coroutines.flow.Flow

interface AudioCaptureService {
    val isCapturing: Boolean
    val audioFormat: AudioFormat
    
    suspend fun startCapture(): Result<Unit>
    suspend fun stopCapture(): Result<Unit>
    suspend fun getAvailableApplications(): List<AudioApplication>
    
    fun audioDataFlow(): Flow<AudioBuffer>
}