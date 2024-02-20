package ir.seniorcode.voltmeter

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var audioRecord: AudioRecord
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.voltage)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        )

        // Start recording
        audioRecord.startRecording()

        Thread {
            while (true) {
                // Read the audio data
                val buffer = ByteArray(1024)
                val bytesRead = audioRecord.read(buffer, 0, buffer.size)

                // Calculate the voltage
                val voltage = (buffer[0] + buffer[1]) / 2.0

                // Update the TextView

               /* val voltageFormatted = String.format("%.5f", voltage)
                runOnUiThread {
                    textView.text = "Voltage: $voltageFormatted"
                }
*/


                val voltageMicro = voltage / 1000000 // تبدیل ولت به میکرو ولت
                val formattedVoltageMicro = String.format("%.5f", voltageMicro) // قالب‌بندی با دقت پنج رقم اعشار
                runOnUiThread {
                    textView.text = "Voltage: $formattedVoltageMicro μV"
                }

                /*
                runOnUiThread {
                    textView.text = "Voltage: ${voltage/1000000}"
                }*/
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop recording
        audioRecord.stop()

        // Release the AudioRecord
        audioRecord.release()
    }
}