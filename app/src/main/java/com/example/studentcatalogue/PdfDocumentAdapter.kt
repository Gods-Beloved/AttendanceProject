package com.example.studentcatalogue

import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.*

class PdfDocumentAdapter(var path: String, var filename: String) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal!!.isCanceled) callback!!.onLayoutCancelled()
        else {
            val builder = PrintDocumentInfo.Builder(filename)

            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()
            callback!!.onLayoutFinished(builder.build(), oldAttributes != newAttributes)
        }
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        var `in`: InputStream? = null
        var `out`: OutputStream? = null

        try {
            val file = File(path)
            `in` = FileInputStream(file)
            out = FileOutputStream(destination!!.fileDescriptor)

            val buff = ByteArray(16384)
            var size: Int
            while (`in`.read(buff)
                    .also { size = it } >= 0 && !cancellationSignal!!.isCanceled
            ) out.write(buff, 0, size)
            if (cancellationSignal!!.isCanceled) callback!!.onWriteCancelled() else
                callback!!.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `in`!!.close()
                out!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }
}