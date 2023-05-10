package com.example.weparentjr.views

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.weparentjr.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Hashtable

class QRActivity : AppCompatActivity() {


    fun generateQRCodeImage(text: String, width: Int = 250, height: Int = 250): Bitmap {
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints)
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)



        val contextView = findViewById<View>(R.id.view)
        val button = findViewById<Button>(R.id.button)
        val qrImage = findViewById<ImageView>(R.id.qr)
        val qrCodeImage = generateQRCodeImage(Build.ID.toString())
        qrImage.setImageBitmap(qrCodeImage)
        button.setOnClickListener {
             finish()
        }

    }
}