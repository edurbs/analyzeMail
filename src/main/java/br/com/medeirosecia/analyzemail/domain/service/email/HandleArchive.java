package br.com.medeirosecia.analyzemail.domain.service.email;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class HandleArchive implements HandleAttachmentType {

    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO, BaseFolders baseFolders) {
        Map<String, HandleAttachmentType> extensionsMap = new HashMap<>();        
            extensionsMap.put("PDF", new HandlePDF());
            extensionsMap.put("XML", new HandleXML());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(emailAttachmentDAO.getData());

        String tempFolderPath = System.getProperty("java.io.tmpdir");
        File tempFolder = new File(tempFolderPath);
        File tempFile;
        try {
            tempFile = File.createTempFile("tempfile", ".tmp", tempFolder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(tempFile, "r");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        IInStream randomAccessFileInStream = new RandomAccessFileInStream(randomAccessFile);
        

        try (IInArchive inArchive = SevenZip.openInArchive(null, randomAccessFileInStream)) {
            for (ISimpleInArchiveItem item : inArchive.getSimpleInterface().getArchiveItems()) {
                if (item.isFolder()){
                   continue;
                }
                String entryName = item.getPath();
                for(Map.Entry<String, HandleAttachmentType> extension : extensionsMap.entrySet()){
                    if (entryName.toUpperCase().endsWith(extension.getKey())) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        item.extractSlow(new ISequentialOutStream() {
                            @Override
                            public int write(byte[] data) throws SevenZipException {
                                try {
                                    byteArrayOutputStream.write(data);
                                    return data.length;
                                } catch (IOException e) {
                                    throw new SevenZipException("Error writing to ByteArrayOutputStream", e);
                                }
                            }
                        });                       
                        HandleAttachmentType handleAttachmentType = extension.getValue();
                        EmailAttachmentDAO extractedEmailAttachmentDAO = new EmailAttachmentDAO(entryName, byteArrayOutputStream.toByteArray());                        
                        handleAttachmentType.analyzeAttachment(extractedEmailAttachmentDAO, baseFolders);
                    }
                }
            } 
        } catch (SevenZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            randomAccessFileInStream.close();
            tempFile.delete();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
    }
    
}
