package com.example.pdf

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var generateButton: Button

    private val pageHeight = 1120
    private val pageWidth = 792

    private lateinit var bitmap: Bitmap
    private lateinit var scaledBitmap: Bitmap

    private val permissionRequestCode = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        generateButton = findViewById(R.id.generate_button)

        if (!checkPermission()) {
            requestPermission()
        }

        generateButton.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty()) {

                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            // checking of permissions.
            val permission1 = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
            val permission2 = ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
            permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
            val uri = Uri.parse("package:${applicationContext.packageName}")
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        } else {
            val permissions = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, permissionRequestCode)
        }
    }

    private fun generatePdf() {
        val pdfDocument = PdfDocument()

        val paint = Paint()
        val title = Paint()

        // adding page info
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

        // set start page
        val startPage = pdfDocument.startPage(pageInfo)

        // create canvas from PDF
        val canvas = startPage.canvas

//        canvas.drawBitmap(scaledBitmap, 56f, 40f, paint)

        // declare typeface for text
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
        title.textSize = 15f
        title.color = ContextCompat.getColor(this, R.color.black)

        // draw text
        canvas.drawText("This is PDF Document", 209f, 100f, title)
        canvas.drawText("Hello from PDF", 209f, 80f, title)

        // generate another style
        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        title.color = ContextCompat.getColor(this, R.color.black);
        title.textSize = 15f;
        title.textAlign = Paint.Align.CENTER

        canvas.drawText("Example document content", 396f, 560f, title)

        pdfDocument.finishPage(startPage)

        val file = File(Environment.getExternalStorageDirectory(), "hello.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF file generated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed generate PDF file", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }

    private fun printedView() {
        val printAttrs = PrintAttributes.Builder()
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("zooey", PRINT_SERVICE, 450, 700))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build();
        val document: PdfDocument = PrintedPdfDocument(this@MainActivity, printAttrs)
        val pageInfo = PageInfo.Builder(450, 700, 1).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)

        val content = findViewById<Button>(R.id.generate_button)
        content.draw(page.canvas)

        document.finishPage(page)

        val file = File(Environment.getExternalStorageDirectory(), "hello.pdf")

        try {
            document.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF file generated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed generate PDF file", Toast.LENGTH_SHORT).show()
        }

        document.close()
    }

    private fun parentView(): LinearLayout {
        val parentView = LinearLayout(applicationContext, null, R.style.Theme_AndroidPDF)
        val params: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        parentView.layoutParams = params
        parentView.orientation = LinearLayout.VERTICAL
        return parentView
    }

}