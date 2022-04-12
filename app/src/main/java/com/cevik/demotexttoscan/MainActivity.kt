package com.cevik.demotexttoscan

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {
    private val REQUEST_CAMERA_CODE=100
    lateinit var bitmap:Bitmap
    lateinit var  textData:TextView
    lateinit var buttonCopy:Button
    lateinit var buttonCapture:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


         buttonCapture=findViewById(R.id.buttonCapture)
         buttonCopy=findViewById(R.id.buttonCopy)

         textData=findViewById(R.id.textData)

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),REQUEST_CAMERA_CODE)
        }

        buttonCapture.setOnClickListener {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this@MainActivity)
        }

        buttonCopy.setOnClickListener {
            var scannedText=textData.text.toString()
            copyToClipBoard(scannedText)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
             val result:CropImage.ActivityResult =CropImage.getActivityResult(data)
            if (resultCode== RESULT_OK){
                val resultUri=result.uri
                bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,resultUri)
                getTextFromImage(bitmap)
            }

        }
    }

    private fun getTextFromImage(bitmap:Bitmap){
        var recognizer=TextRecognizer.Builder(this@MainActivity).build()
        if (!recognizer.isOperational()){
            Toast.makeText(this@MainActivity,"Error Occured",Toast.LENGTH_LONG).show()
        }else{
            var frame:Frame=Frame.Builder().setBitmap(bitmap).build()
            var textBlock:SparseArray<TextBlock> =recognizer.detect(frame)
            var stringBuilder:StringBuilder= StringBuilder()
            for (i in 0 until textBlock.size()) {
               var textBlock=textBlock.valueAt(i)
                stringBuilder.append(textBlock.value)
                stringBuilder.append("\n")
            }
            textData.text=stringBuilder.toString()
            buttonCapture.text="Retake"
            buttonCopy.visibility= View.VISIBLE

        }
    }

    private fun copyToClipBoard(text:String){
        var clipBoard:ClipboardManager= getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var clip=ClipData.newPlainText("Copied Data",text)
        clipBoard.setPrimaryClip(clip)
        Toast.makeText(this@MainActivity,"copied clipboard",Toast.LENGTH_SHORT).show()
    }

}