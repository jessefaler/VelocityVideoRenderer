package com.protoxon.display;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;

public class DisplayPipeline {

    // Holds frames that have been processed but have not yet been displayed
    private static final BlockingQueue<Component> frameBuffer = new LinkedBlockingQueue<>(50);
    // Define A thread executor for the frame grabber
    ExecutorService grabberExecutor = Executors.newSingleThreadExecutor();
    // Get available processor cores
    int cores = Runtime.getRuntime().availableProcessors();
    // Define a thread pool for frame processing
    ExecutorService processingExecutor = Executors.newFixedThreadPool(cores * 2);
    ScheduledExecutorService displayExecutor = Executors.newScheduledThreadPool(1);
    private FFmpegFrameGrabber grabber;
    Java2DFrameConverter converter = new Java2DFrameConverter();
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    int maxPixels = 15000;
    private final DisplayInstance displayInstance;

    public DisplayPipeline(DisplayInstance displayInstance) {
        this.displayInstance = displayInstance;
    }

    public void initializePipeline(String videoFile) {
        grabber = new FFmpegFrameGrabber(videoFile);
        try {
            grabber.start();
            grabberExecutor.submit(this::grabFrames);
        } catch (FFmpegFrameGrabber.Exception e) {
            Display.logger.error("An error occurred while initializing the frame grabbing pipeline!\n{}", e.getMessage());
        }
    }

    public void play(String videoFile) {
        frameBuffer.clear();
        initializePipeline(videoFile);
        initializeDisplayRenderer();
    }

    public void initializeDisplayRenderer() {
        displayExecutor.scheduleAtFixedRate(this::renderFrame, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void renderFrame() {
        try {
            Display.logger.error("Buffer Size: {}", frameBuffer.size());
            displayFrame(frameBuffer.take());
        } catch (InterruptedException e) {
            Display.logger.error("An error occurred in the display rendering pipeline\n{}", e.getMessage());
        }
    }

    public void displayFrame(Component frame) {
        displayInstance.setText(frame);
    }

    public void grabFrames() {
        Frame frame;
        try {
            while ((frame = grabber.grabImage()) != null) {
                if (frame.image == null) {
                    Display.logger.error("Skipped null frame.");
                    continue;
                }
                // Convert the frame to a buffered image
                BufferedImage bufferedImage = frameToImage(frame);
                // Submit frame to the processing thread pool
                processingExecutor.submit(() -> processFrame(bufferedImage));
            }
            grabber.stop();
        } catch (FFmpegFrameGrabber.Exception e) {
            Display.logger.error("An error occurred while grabbing a frame in the frame grabbing pipeline\n{}", e.getMessage());
        }
    }

    public void processFrame(BufferedImage bufferedImage) {
        if(bufferedImage == null) {
            Display.logger.error("An error occurred in the frame processing pipeline. A frame conversion resulted in a null buffered image. Skipping Frame.");
            return;
        }
        bufferedImage = resizeImage(bufferedImage, maxPixels); // Resize the image
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
        Component frameComponent = convertToComponent(pixels, width, height);
        try {
            frameBuffer.put(frameComponent);
        } catch (InterruptedException e) {
            Display.logger.error("An error occurred in the frame processing pipeline. A component frame was interrupted while being added to the frame buffer\n{}", e.getMessage());
        }
    }

    private BufferedImage frameToImage(Frame frame) {
        return converter.convert(frame);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int maxPixels) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int totalPixels = width * height;
        // Calculate the scale factor to ensure total pixels do not exceed maxPixels
        double scaleFactor = Math.sqrt((double) maxPixels / totalPixels);
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);
        // Ensure at least 1x1 size
        newWidth = Math.max(newWidth, 1);
        newHeight = Math.max(newHeight, 1);
        // Resize the image
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resizedImage;
    }

    /*
    public Component convertToComponent(int[] pixels, int width, int height) {
        TextComponent.Builder frameBuilder = net.kyori.adventure.text.Component.text();
        StringBuilder lineBreak = new StringBuilder("\n"); // Pre-created for efficiency
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];

                // Extract RGB values from the pixel
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                // Append the colored block character to the builder
                frameBuilder.append(net.kyori.adventure.text.Component.text("█").color(TextColor.color(red, green, blue)));
            }
            frameBuilder.append(net.kyori.adventure.text.Component.text(lineBreak.toString())); // Append new line efficiently
        }
        return frameBuilder.build();
    }
     */

    public Component convertToComponent(int[] pixels, int width, int height) {
        // Create the overall component builder for the frame
        TextComponent.Builder frameBuilder = net.kyori.adventure.text.Component.text();

        for (int y = 0; y < height; y++) {
            int x = 0;
            // Builder for one row of pixels
            TextComponent.Builder rowBuilder = net.kyori.adventure.text.Component.text();

            while (x < width) {
                int pixel = pixels[y * width + x];
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                TextColor color = TextColor.color(red, green, blue);

                // Count how many consecutive pixels in this row share the same color.
                int count = 1;
                while (x + count < width && pixels[y * width + (x + count)] == pixel) {
                    count++;
                }
                // Build a string that repeats the block character count times.
                String blockStr = "█".repeat(count); // Java 11+ method
                // Append a single component for the grouped pixels.
                rowBuilder.append(net.kyori.adventure.text.Component.text(blockStr).color(color));
                x += count;
            }
            // Append the completed row to the frame builder, then add a newline.
            frameBuilder.append(rowBuilder.build());
            frameBuilder.append(net.kyori.adventure.text.Component.text("\n"));
        }
        return frameBuilder.build();
    }

}
