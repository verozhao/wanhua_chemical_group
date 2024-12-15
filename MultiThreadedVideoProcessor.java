import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_videoio.*;
import org.bytedeco.opencv.opencv_imgproc.*;

import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY;

public class MultiThreadedVideoProcessor {

    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        String videoPath = "path/to/your_video.mp4";
        VideoCapture videoCapture = new VideoCapture(videoPath);

        if (!videoCapture.isOpened()) {
            System.out.println("Error: Unable to open video file!");
            return;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        Mat frame = new Mat();
        int frameIndex = 0;

        while (true) {
            if (!videoCapture.read(frame)) {
                break;
            }

            Mat currentFrame = frame.clone();
            int currentFrameIndex = frameIndex;

            executorService.submit(() -> processFrame(currentFrame, currentFrameIndex));

            frameIndex++;
        }

        // Shut down the executor service
        videoCapture.release();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println("Timeout waiting for frame processing to complete!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Video processing completed.");
    }

    private static void processFrame(Mat frame, int frameIndex) {
        // Convert the frame to grayscale
        Mat grayFrame = new Mat();
        cvtColor(frame, grayFrame, COLOR_BGR2GRAY);

        // Simulate processing time
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Processed frame: " + frameIndex);
    }
}
