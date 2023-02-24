package ru.sberbank.itext;

import com.itextpdf.kernel.pdf.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ITextExampleReplaceStream {
    public static final String DEST = "./target/sandbox/stamper/pdf1a.pdf";
    public static final String SRC = "/resources/pdfs/1edc6710_03061.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        new ITextExampleReplaceStream().manipulatePdf3(DEST);
    }

    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));

        PdfPage page = pdfDoc.getFirstPage();
        PdfDictionary dict = page.getPdfObject();

        PdfObject object = dict.get(PdfName.Contents);
        if (object instanceof PdfStream) {
            PdfStream stream = (PdfStream) object;
            byte[] data = stream.getBytes();
            String stringData = new String(data);
            String replacedData = stringData.replace("Hello World", "HELLO WORLD");
            stream.setData(replacedData.getBytes(StandardCharsets.UTF_8));
        }

        pdfDoc.close();
    }

    protected void manipulatePdf1(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));

        ArrayList<Integer> listObject = new ArrayList<Integer>();
        listObject.add(61);
        listObject.add(58);
        listObject.add(52);
        listObject.add(44);

        for (Integer objectNum : listObject) {
            PdfObject pdfObject = pdfDoc.getPdfObject(objectNum);
            if (objectNum == null) {
                continue;
            }

            if (pdfObject instanceof PdfStream) {
                PdfStream pdfStream = (PdfStream) pdfObject;
                String value = new String(pdfStream.getBytes(true));
                pdfStream.setData(value.getBytes(StandardCharsets.UTF_8));
            }
        }

        pdfDoc.close();
    }

    protected void manipulatePdf2(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));

        ArrayList<Integer> listObject = new ArrayList<Integer>();
        listObject.add(61);
        listObject.add(58);
        listObject.add(52);
        listObject.add(44);

        List<PdfIndirectReference> pdfIndirectReferencesList = pdfDoc.listIndirectReferences();
        for (PdfIndirectReference ref : pdfIndirectReferencesList) {

            if (listObject.contains(ref.getObjNumber())) {
                System.out.println(ref.getObjNumber());

                PdfObject pdfObject = pdfDoc.getPdfObject(ref.getObjNumber());

                if (pdfObject instanceof PdfStream) {


                    PdfStream pdfStream = (PdfStream) pdfObject;

                    byte type = pdfStream.getType();
                    boolean isMetadata = pdfStream.containsKey(new PdfName("Metadata"));


                    String value = new String(pdfStream.getBytes(true));
                    pdfStream.setData(value.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        pdfDoc.close();
    }

    protected void manipulatePdf3(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(dest));

        List<PdfIndirectReference> pdfIndirectReferencesList = pdfDoc.listIndirectReferences();
        for (PdfIndirectReference ref : pdfIndirectReferencesList) {

            PdfObject pdfObject = pdfDoc.getPdfObject(ref.getObjNumber());
            if (pdfObject != null && pdfObject.getType() == PdfObject.STREAM) {
                System.out.println(ref.getObjNumber());

                boolean isMetadata = false;
                PdfStream pdfStream = (PdfStream) pdfObject;

                PdfObject objectType = pdfStream.get(new PdfName("Type"));
                if (objectType != null) {
                    if (objectType.getType() == PdfObject.NAME) {
                        PdfName valueObjectType = (PdfName) objectType;

                        String value = valueObjectType.getValue();

                        if (Objects.equals(value, "Metadata")) {
                            isMetadata = true;
                        }
                    }
                }
                if (isMetadata) {
                    String value = new String(pdfStream.getBytes(true));
                    pdfStream.setData(value.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        pdfDoc.close();
    }
}
