package com.protoxon.display;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class DisplayPipeline {

    // Holds frames that have been processed but have not yet been displayed
    //private final BlockingQueue<Component> frameBuffer = new LinkedBlockingQueue<>();
    int frameBufferCapacity = 175;
    private final ArrayBlockingQueue<Component> frameBuffer = new ArrayBlockingQueue<>(frameBufferCapacity);
    // Define A thread executor for the frame grabber
    ExecutorService grabberExecutor;
    // Define a thread pool for frame processing
    ExecutorService processingExecutor;
    ScheduledExecutorService displayExecutor;
    private FFmpegFrameGrabber grabber;
    Java2DFrameConverter converter = new Java2DFrameConverter();
    int maxFrameRate = 20; // Frames Per Second
    int maxPixels = 15000;
    private final DisplayInstance displayInstance;
    double frameRatio;
    double inputFrameRate;
    int skippedFrames;
    int frames;
    boolean debugLogging;
    Player player;
    String videoFile;

    public DisplayPipeline(DisplayInstance displayInstance) {
        this.displayInstance = displayInstance;
    }

    public void initializePipeline(String videoFile) {;
        grabber = new FFmpegFrameGrabber(videoFile);
        try {
            grabber.start();
            grabberExecutor.submit(this::grabFrames);
        } catch (FFmpegFrameGrabber.Exception e) {
            Display.logger.error("An error occurred while initializing the frame grabbing pipeline!\n{}", e.getMessage());
        }
    }

    public void play(String videoFile) {
        this.videoFile = videoFile;
        initializeExecutors();
        initializePipeline(videoFile);
        initializeDisplayRenderer();
    }

    public void initializeExecutors() {
        shutdown(); //
        int cores = Runtime.getRuntime().availableProcessors();
        grabberExecutor = Executors.newSingleThreadExecutor();
        processingExecutor = Executors.newFixedThreadPool(cores * 2);
        displayExecutor = Executors.newScheduledThreadPool(1);
    }

    public void shutdown() {
        frameBuffer.clear();
        if(grabberExecutor != null) {
            grabberExecutor.shutdown();
        }
        if(processingExecutor != null) {
            processingExecutor.shutdown();
        }
        if(displayExecutor != null) {
            displayExecutor.shutdown();
        }
    }

    public void logMessage(Component component) {
        if(debugLogging) {
            player.sendMessage(component);
        }
    }

    public void logActionBarMessage(Component component) {
        if(debugLogging) {
            player.sendActionBar(component);
        }
    }

    public void pause() {
        displayExecutor.shutdown();
    }

    public void resume() {
        displayExecutor = Executors.newScheduledThreadPool(1);
        initializeDisplayRenderer();
    }

    public void initializeDisplayRenderer() {
        int renderUpdateInterval = 1000 / maxFrameRate;
        displayExecutor.scheduleAtFixedRate(this::renderFrame, 0, renderUpdateInterval, TimeUnit.MILLISECONDS);
    }

    public void clearFrameBuffer() {
        frameBuffer.clear();
    }

    public void renderFrame() {
        try {
            if(frameBuffer.size() == 1) {
                play(videoFile);
            }
            logActionBarMessage(Component.text("Frame buffer: " + frameBuffer.size() + "/" + frameBufferCapacity
                    + " | Processed frames: " + frames + " | Skipped frames: " + skippedFrames, NamedTextColor.GOLD).compact());
            Display.logger.error("Frames/Buffer: {}/{}", frames, frameBuffer.size());
            displayFrame(frameBuffer.take());
        } catch (InterruptedException e) {
            Display.logger.error("An error occurred in the display rendering pipeline\n{}", e.getMessage());
        }
    }

    public void displayFrame(Component frame) {
        displayInstance.setText(frame);
    }

    public void updateMaxFrameRate(int maxFrameRate) {
        frameRatio = inputFrameRate / maxFrameRate;
        this.maxFrameRate = maxFrameRate;
        // Restart the display executor service
        pause();
        resume();
    }

    public void grabFrames() {
        inputFrameRate = grabber.getFrameRate();
        // Calculate the ratio of input to output frames.
        // For 24fps -> 15fps, ratio is 24 / 15 = 1.6
        frameRatio = inputFrameRate / maxFrameRate;
        // Accumulator to track how many frames have passed
        double accumulator = 0.0;
        Frame frame;
        try {
            while ((frame = grabber.grabImage()) != null) {
                frames++;
                if (frame.image == null) {
                    Display.logger.error("Skipped null frame.");
                    continue;
                }
                // Increment the accumulator each time a frame is grabbed.
                accumulator += 1.0;
                // If we haven't reached the threshold to display a frame, drop this one.
                if (accumulator < frameRatio) {
                    skippedFrames++;
                    Display.logger.warn("accumulator skipped a frame. " + skippedFrames + " frames skipped");
                    continue;
                }

                // When the accumulator exceeds the ratio, we process the frame.
                accumulator -= frameRatio;

                // Convert the frame to a buffered image
                BufferedImage bufferedImage = frameToImage(frame);
                // Submit frame to the processing thread pool
                processingExecutor.submit(() -> processFrame(bufferedImage));
                while (frameBuffer.remainingCapacity() == 0) {
                    Thread.sleep(10); // Small sleep to prevent busy-waiting
                }
            }
            grabber.stop();
        } catch (FFmpegFrameGrabber.Exception e) {
            Display.logger.error("An error occurred while grabbing a frame in the frame grabbing pipeline\n{}", e.getMessage());
        } catch (InterruptedException ignored) {}
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

    public String getStreamUrl(String videoUrl) {
        // Command to run yt-dlp to get the stream URL
        String[] command = {
                "yt-dlp",           // yt-dlp command
                "-f", "worst",       // Select the best quality format
                "--get-url",        // Option to get the direct URL
                videoUrl            // URL passed as an argument
        };

        // Create and start the process
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            Display.logger.error("Error while fetching youtube stream url: {}", e.getMessage());
            return null;
        }

        // Capture the output of the yt-dlp command
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String streamUrl = reader.readLine();  // Read the URL from yt-dlp's output
            if (streamUrl != null) {
                return streamUrl;  // Return the stream URL
            } else {
                Display.logger.error("No URL found in yt-dlp output.");
            }
        } catch (Exception e) {
            Display.logger.error("Error extracting stream URL: {}", e.getMessage());
        }
        return null;
    }

}
