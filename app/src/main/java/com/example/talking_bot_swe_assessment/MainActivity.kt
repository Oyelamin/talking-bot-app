package com.example.robotspeaker

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.talking_bot_swe_assessment.R
import com.example.talking_bot_swe_assessment.ui.theme.TalkingbotsweassessmentTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TalkingbotsweassessmentTheme {
                var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    
                ) {
                    RobotSpeakerApp(textToSpeech = textToSpeech)
                }
            }
        }
    }
}

@Composable
fun RobotSpeakerApp(
    textToSpeech: TextToSpeech?,
) {
    val context = LocalContext.current
    //var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsInitialized by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Initializing TTS...") }
    var isSpeaking by remember { mutableStateOf(false) }
    val robotPhrase = "Greetings, human! I am your friendly robot companion."
    val imageResId = R.drawable.robot_image

    // Initialize TTS once and clean up on dispose
    /*DisposableEffect(Unit) {
        var tts: TextToSpeech? = null

        tts = TextToSpeech(context.applicationContext) { status ->
            Log.d("TTS", "TTS initialization status: $status")

            if (status == TextToSpeech.SUCCESS) {
                // Set language
                val result = tts?.setLanguage(Locale.US)
                Log.d("TTS", "Language setting result: $result")

                when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.e("TTS", "Language data is missing!")
                        Handler(Looper.getMainLooper()).post {
                            statusMessage = "TTS Error: Language data missing"
                        }
                    }
                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.e("TTS", "Language not supported!")
                        Handler(Looper.getMainLooper()).post {
                            statusMessage = "TTS Error: Language not supported"
                        }
                    }
                    else -> {
                        // Set audio attributes for TTS
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            val audioAttributes = android.media.AudioAttributes.Builder()
                                .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                            tts?.setAudioAttributes(audioAttributes)
                        }

                        // Set up utterance progress listener for debugging
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                Log.d("TTS", "Speech started: $utteranceId")
                                Handler(Looper.getMainLooper()).post {
                                    isSpeaking = true
                                    statusMessage = "Speaking..."
                                }
                            }

                            override fun onDone(utteranceId: String?) {
                                Log.d("TTS", "Speech completed: $utteranceId")
                                Handler(Looper.getMainLooper()).post {
                                    isSpeaking = false
                                    statusMessage = "Ready to speak! Tap the robot."
                                }
                            }

                            override fun onError(utteranceId: String?) {
                                Log.e("TTS", "Speech error: $utteranceId")
                                Handler(Looper.getMainLooper()).post {
                                    isSpeaking = false
                                    statusMessage = "Speech error occurred"
                                }
                            }
                        })

                        Handler(Looper.getMainLooper()).post {
                            ttsInitialized = true
                            statusMessage = "Ready to speak! Tap the robot."
                        }
                        Log.i("TTS", "TextToSpeech Initialized successfully.")
                    }
                }
            } else {
                Log.e("TTS", "TextToSpeech Initialization Failed! Status: $status")
                Handler(Looper.getMainLooper()).post {
                    statusMessage = "TTS initialization failed"
                }
            }
        }

        textToSpeech = tts

        onDispose {
            Log.i("TTS", "Disposing TextToSpeech engine.")
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
            ttsInitialized = false
        }
    }*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "IFT 310 Assessment",
            modifier = Modifier.padding(bottom = 54.dp),
            color = Color.Black,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Robot Image",
            modifier = Modifier
                .size(200.dp)
                .clickable {
                    Log.d("RobotApp", "Robot image clicked")
                    Log.d("RobotApp", "TTS initialized: $ttsInitialized")
                    Log.d("RobotApp", "TTS object: ${textToSpeech != null}")

                    if (ttsInitialized && textToSpeech != null && !isSpeaking) {
                        Log.i("RobotApp", "Attempting to speak: $robotPhrase")

                        // Check and log audio volumes
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        val mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                        val maxMediaVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                        Log.d("Audio", "Media volume: $mediaVolume/$maxMediaVolume")

                        if (mediaVolume == 0) {
                            Log.w("Audio", "Media volume is 0 - this might be why there's no sound!")
                            statusMessage = "Warning: Media volume is 0"
                        }

                        val utteranceId = "robot_utterance_${System.currentTimeMillis()}"

                        // Check if TTS engine is available
                        val engines = textToSpeech?.engines
                        Log.d("TTS", "Available TTS engines: ${engines?.size ?: 0}")

                        val speakResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Log.i("TTS", "Using modern TTS API")
                            // Create bundle with audio stream type
                            val params = Bundle()
                            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
                            textToSpeech?.speak(robotPhrase, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                        } else {
                            Log.i("TTS", "Using legacy TTS API")
                            @Suppress("DEPRECATION")
                            val params = HashMap<String, String>()
                            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
                            params[TextToSpeech.Engine.KEY_PARAM_STREAM] = AudioManager.STREAM_MUSIC.toString()
                            textToSpeech?.speak(robotPhrase, TextToSpeech.QUEUE_FLUSH, params)
                        }

                        Log.d("TTS", "Speak result: $speakResult")
                        if (speakResult == TextToSpeech.ERROR) {
                            Log.e("TTS", "Error occurred while trying to speak")
                            statusMessage = "Error: Could not speak"
                        }
                    } else {
                        when {
                            !ttsInitialized -> {
                                Log.w("RobotApp", "TTS not initialized yet")
                                statusMessage = "TTS not ready yet, please wait..."
                            }
                            textToSpeech == null -> {
                                Log.w("RobotApp", "TTS object is null")
                                statusMessage = "TTS engine not available"
                            }
                            isSpeaking -> {
                                Log.w("RobotApp", "Already speaking")
                                statusMessage = "Already speaking..."
                            }
                        }
                    }
                }
        )

        Text(
            text = statusMessage,
            modifier = Modifier.padding(top = 24.dp),
            color = if (ttsInitialized) Color.Black else Color.Red,
            style = MaterialTheme.typography.bodySmall,
            fontSize = MaterialTheme.typography.titleLarge.fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TalkingbotsweassessmentTheme {
        RobotSpeakerApp(textToSpeech = null)
        //itText("Blessing")
    }
}