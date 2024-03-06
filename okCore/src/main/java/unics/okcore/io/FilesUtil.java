package unics.okcore.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FilesUtil {

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    public static String md5(String filePath, int bufferSize) throws IOException, NoSuchAlgorithmException {
        InputStream fis = new FileInputStream(filePath);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[bufferSize];
        int length = -1;
        while ((length = fis.read(buffer, 0, bufferSize)) != -1) {
            md.update(buffer, 0, length);
        }
        fis.close();
        //转换并返回包含16个元素字节数组,返回数值范围为-128到127
        byte[] md5Bytes = md.digest();
        BigInteger bigInt = new BigInteger(1, md5Bytes);
        return bigInt.toString(16);
    }

    /**
     * 获取文件md5值
     */
    public static String md5(String filePath) throws IOException, NoSuchAlgorithmException {
        return md5(filePath, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(InputStream input, OutputStream out) throws IOException {
        return copy(input, out, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(InputStream input, OutputStream out, int bufferSize) throws IOException {
        long bytesCopied = 0;
        byte[] buffer = new byte[bufferSize];
        int bytes = 0;
        while ((bytes = input.read(buffer)) >= 0) {
            out.write(buffer, 0, bytes);
            bytesCopied += bytes;
        }
        return bytesCopied;
    }

    public static File copy(File input, File target) throws IOException {
        return copy(input, target, false, DEFAULT_BUFFER_SIZE);
    }

    public static File copy(File input, File target, boolean overwrite) throws IOException {
        return copy(input, target, overwrite, DEFAULT_BUFFER_SIZE);
    }

    public static File copy(File input, File target, boolean overwrite, int bufferSize) throws IOException {
        if (!input.exists()) {
            throw new FileNotFoundException("The source file [" + input.getAbsolutePath() + "] doesn't exist.");
        }

        if (target.exists()) {
            if (!overwrite)
                throw new FileAlreadyExistsException(input.getAbsolutePath(), target.getAbsolutePath(), "The destination file already exists.");
            else if (!target.delete())
                throw new FileAlreadyExistsException(input.getAbsolutePath(), target.getAbsolutePath(), "Tried to overwrite the destination, but failed to delete it.");
        }

        if (input.isDirectory()) {
            if (!target.mkdirs())
                throw new FileSystemException(input.getAbsolutePath(), target.getAbsolutePath(), "Failed to create target directory.");
        } else {
            File parent = target.getParentFile();
            if (parent != null)
                parent.mkdirs();
            try (FileInputStream fis = new FileInputStream(input);
                 FileOutputStream fos = new FileOutputStream(target)) {
                copy(fis, fos, bufferSize);
            }
        }
        return target;
    }


    public static File zip(File input) throws IOException {
        File zip = new File(input.getParent(), input.getName() + ".zip");
        return zip(input, zip.getAbsolutePath(), new byte[8 * 1024]);
    }

    /**
     * 压缩文件夹
     *
     * @param input  文件夹路径
     * @param output 输出得zip文件路径
     */
    public static File zip(File input, String output) throws IOException {
        return zip(input, output, new byte[8 * 1024]);
    }

    /**
     * 压缩文件夹
     *
     * @param input  文件夹路径
     * @param output 输出得zip文件路径
     */
    public static File zip(File input, String output, byte[] buffer) throws IOException {
        if (!input.exists() || !input.isDirectory()) {
            throw new IOException("Folder " + input + " does't exist or isn't a directory");
        }
        File outputFile = new File(output);
        if (!outputFile.exists()) {
            File zipFolder = outputFile.getParentFile();
            if (zipFolder != null && !zipFolder.exists()) {
                if (!zipFolder.mkdirs()) {
                    throw new IOException("Zip folder " + zipFolder.getAbsolutePath() + " not created");
                }
            }
            if (!outputFile.createNewFile()) {
                throw new IOException("Zip file " + output + " not created");
            }
        }

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputFile))) {
            // 压缩文件夹
            compressFolder(input, buffer, zipOutputStream);
            System.out.println("Folder compressed successfully!");
        }
        return outputFile;
    }

    private static void compressFolder(File input, byte[] buffer, ZipOutputStream zipOutputStream) throws IOException {
        File[] files = input.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                // 压缩子文件夹
                compressChildFolder(file, file.getName(), buffer, zipOutputStream);
            } else {
                // 压缩文件
                addToZipFile(file, file.getName(), buffer, zipOutputStream);
            }
        }
    }

    private static void compressChildFolder(File input, String folderName, byte[] buffer, ZipOutputStream zipOutputStream) throws IOException {
        File[] files = input.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                // 压缩子文件夹
                compressChildFolder(file, folderName + File.separator + file.getName(), buffer, zipOutputStream);
            } else {
                // 压缩文件
                addToZipFile(file, folderName + File.separator + file.getName(), buffer, zipOutputStream);
            }
        }
    }


    private static void addToZipFile(File file, String zipFileName, byte[] buffer, ZipOutputStream zipOutputStream) throws IOException {
        // 创建ZipEntry对象并设置文件名
        ZipEntry entry = new ZipEntry(zipFileName);
        zipOutputStream.putNextEntry(entry);

        // 读取文件内容并写入Zip文件
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, bytesRead);
            }
        }
        // 完成当前文件的压缩
        zipOutputStream.closeEntry();
    }

    /**
     * 同级目录下创建一个与zip文件同名的文件夹并解压
     */
    public static void unzip(File zipFile) throws IOException {
        String outputPath = zipFile.getParent();
        String folderName = zipFile.getName();
        if (folderName.contains(".")) {
            folderName = folderName.substring(0, folderName.lastIndexOf("."));
        }
        if (outputPath == null || outputPath.isEmpty()) {
            outputPath = folderName;
        } else {
            outputPath = outputPath + File.separator + folderName;
        }
        System.out.println("outputPath = " + outputPath);
        unzip(zipFile, outputPath);
    }

    /**
     * 解压到指定路径
     *
     * @param zipFile      zip文件
     * @param outputFolder 解压路径
     */
    public static void unzip(File zipFile, String outputFolder) throws IOException {
        unzip(zipFile, outputFolder, new byte[8 * 1024]);
    }

    /**
     * @param zipFile      zip文件
     * @param outputFolder 输出的路径
     * @param buffer       缓冲区大小
     * @throws IOException
     */
    public static void unzip(File zipFile, String outputFolder, byte[] buffer) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                File outputFile = new File(outputFolder, fileName);
                // 创建文件夹
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    // 创建文件并写入内容
                    outputFile.getParentFile().mkdirs();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                        int bytesRead;
                        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
            System.out.println("Files unzipped successfully!");
        }
    }
}

