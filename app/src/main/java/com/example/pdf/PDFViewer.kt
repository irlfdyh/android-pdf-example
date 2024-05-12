package com.example.pdf

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.FileProvider
import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity
import java.net.URLConnection


class PDFViewer : PDFViewerActivity() {

    companion object {
        const val PDF_VIEWER_URI = "test"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Pdf Viewer"
            supportActionBar!!.setBackgroundDrawable(
                ColorDrawable(
                    getResources()
                        .getColor(R.color.white)
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_pdf_viewer, menu)
        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSharePdf) {
            val fileToShare = pdfFile
            if (fileToShare == null || !fileToShare.exists()) {
                Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show()
                return super.onOptionsItemSelected(item)
            }
            val intentShareFile = Intent(Intent.ACTION_SEND)
            val apkURI = FileProvider.getUriForFile(
                applicationContext,
                applicationContext
                    .packageName + ".provider", fileToShare
            )
            intentShareFile.setDataAndType(
                apkURI,
                URLConnection.guessContentTypeFromName(fileToShare.name)
            )
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intentShareFile.putExtra(
                Intent.EXTRA_STREAM,
                apkURI
            )
            startActivity(Intent.createChooser(intentShareFile, "Share File"))
        }
        return super.onOptionsItemSelected(item)
    }

}