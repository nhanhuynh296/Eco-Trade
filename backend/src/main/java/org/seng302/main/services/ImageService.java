package org.seng302.main.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class ImageService {

    /**
     * Try and save an image to specified path within the images rootdir,
     * if image already exists with filename, replaces it.
     *
     * @param path Image path within root directory
     * @param filename Name of the file to be saved
     * @param image Image to be saved
     * @throws IOException if cannot be saved
     */
    void saveImage(Path path, String filename, MultipartFile image) throws IOException {
        // Ensure folder is present
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        InputStream inputStream = image.getInputStream();
        Path filePath = path.resolve(filename);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Save an input image as a thumbnail
     *
     * @param inputFilePath  Filepath to the input image
     * @param outputFilePath Filepath to the output image
     * @param fileExtension  File extension of the input/output file
     * @param scale_x X Axis size
     * @param scale_y Y Axis size
     * @throws IOException If input/output file is not found/errors
     */
    void saveImageThumbnail(Path inputFilePath, Path outputFilePath, String fileExtension, int scale_x, int scale_y) throws IOException {
        BufferedImage imageBuffer = new BufferedImage(scale_x, scale_y, BufferedImage.TYPE_INT_RGB);
        java.awt.Image scaledImage = ImageIO.read(new File(inputFilePath.toString())).getScaledInstance(scale_x, scale_y, BufferedImage.SCALE_SMOOTH);
        imageBuffer.createGraphics().drawImage(scaledImage, 0, 0, null);
        ImageIO.write(imageBuffer, fileExtension, new File(outputFilePath.toString()));
    }

}
