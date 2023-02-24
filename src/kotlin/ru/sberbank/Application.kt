package ru.sberbank

import com.itextpdf.kernel.pdf.*
import java.lang.Exception
import java.nio.charset.StandardCharsets

private val PdfObject.isMetadata: Boolean
    get() {
        try {
            if (this.type == PdfObject.STREAM) {
                val stream = this as PdfStream

                if (stream[PdfName.Type] == PdfName.Metadata) {
                    return true
                }
            }
        } catch (_: Exception) {

        }
        return false
    }

class Application {
}

fun main(args: Array<String>?) {
    try {
        if (args?.size != 2) {
            throw IllegalArgumentException("incorrect number of arguments")
        } else if (args[0].isEmpty() || args[1].isEmpty()) {
            throw IllegalArgumentException("incorrect arguments")
        } else {
            val source = args[0]
            val target = args[1]

            println("Conversion call with parameters: source $source, target $target")

            Convert(
                source = source,
                target = target
            )
        }

    } catch (error: Exception) {
        println("Process conversion finished with error: ${error.message}")
    }
}

class Convert(private val source: String, private val target: String) {
    init {
        val pdfDocument = PdfDocument(PdfReader(this.source), PdfWriter(this.target))

        for (reference in pdfDocument.listIndirectReferences()) {
            val pdfObject = pdfDocument.getPdfObject(reference.objNumber)

            if (pdfObject.isMetadata) {
                val pdfStream = pdfObject as PdfStream
                val value = String(pdfStream.getBytes(true))
                pdfStream.setData(value.toByteArray(StandardCharsets.UTF_8))
            }
        }
        pdfDocument.close()
    }
}
