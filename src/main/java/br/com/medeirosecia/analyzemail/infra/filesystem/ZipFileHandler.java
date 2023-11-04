package br.com.medeirosecia.analyzemail.infra.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class ZipFileHandler {

    private ByteArrayInputStream archive;

    /**
     * @param archive a zip file to handle
     */
    public ZipFileHandler(ByteArrayInputStream archive) {
        this.archive = archive;
    }

    /**
     * Extracts files with specified extensions from the archive.
     *
     * @param extensions a list of file extensions to extract
     * @return a map containing the String filename and the byte array of the
     *         extracted files
     */
    public Map<String, byte[]> extractTheseFileExtentions(List<String> extensions) {
        String tempFolderPath = System.getProperty("java.io.tmpdir");
        File tempFolder = new File(tempFolderPath);

        File tempFile;
        try {
            tempFile = File.createTempFile("tempfile", ".tmp", tempFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }

        try (OutputStream outputStream = new FileOutputStream(tempFile);
                RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "r");
                IInStream iInStream = new RandomAccessFileInStream(randomAccessFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = archive.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            Map<String, byte[]> extractedFilesMap = openArchive(extensions, iInStream);

            // to avoid errors when deleting the file
            iInStream.close();
            randomAccessFile.close();
            outputStream.close();

            Files.delete(tempFile.toPath());

            return extractedFilesMap;

        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private Map<String, byte[]> openArchive(List<String> extensions, IInStream randomAccessFileInStream) {
        try (IInArchive inArchive = SevenZip.openInArchive(null, randomAccessFileInStream)) {
            Map<String, byte[]> fileMap = new HashMap<>();
            for (ISimpleInArchiveItem item : inArchive.getSimpleInterface().getArchiveItems()) {
                if (!item.isFolder()) {
                    fileMap.put(item.getPath(), extractFileExtension(item, extensions));
                }
            }

        } catch (SevenZipException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private byte[] extractFileExtension(ISimpleInArchiveItem item, List<String> extensions) {
        try {
            String fileNameItem = item.getPath();
            String fileNameItemExtension = fileNameItem.substring(fileNameItem.lastIndexOf(".") + 1).toUpperCase();

            for (String extension : extensions) {
                if (fileNameItemExtension.endsWith(extension)) {
                    return extractFile(item);
                }
            }

        } catch (SevenZipException e) {
            e.printStackTrace();
        }
        return new byte[0];

    }

    private byte[] extractFile(ISimpleInArchiveItem item) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
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
        } catch (SevenZipException e) {
            e.printStackTrace();
            return new byte[0];
        }
        return byteArrayOutputStream.toByteArray();

    }

}
