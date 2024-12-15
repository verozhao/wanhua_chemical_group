import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_videoio.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_highgui.imshow;
import static org.bytedeco.opencv.global.opencv_highgui.waitKey;

public class RealTimeFaceRecognition {

    public static void main(String[] args) {
        // Load the pre-trained Haar Cascade model for face detection
        String classifierPath = "path/to/haarcascade_frontalface_default.xml";
        CascadeClassifier faceDetector = new CascadeClassifier(classifierPath);

        // Open the default camera (ID: 0)
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera not found!");
            return;
        }

        Mat frame = new Mat();
        int frameCounter = 0;

        while (true) {
            // Capture a frame from the video stream
            camera.read(frame);
            if (frame.empty()) {
                System.out.println("Error: Frame is empty!");
                break;
            }

            // Convert to grayscale for better performance
            Mat grayFrame = new Mat();
            org.bytedeco.opencv.global.opencv_imgproc.cvtColor(frame, grayFrame, org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY);

            // Detect faces in the frame
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(grayFrame, faces);

            // Annotate detected faces
            for (int i = 0; i < faces.size(); i++) {
                Rect face = faces.get(i);
                rectangle(frame, face, Scalar.RED, 2, 0, 0);

                // Save the frame when a face is detected
                String snapshotPath = "snapshots/frame_" + frameCounter + ".jpg";
                imwrite(snapshotPath, frame);
                System.out.println("Saved snapshot: " + snapshotPath);
            }

            // Display the video feed
            imshow("Real-Time Face Recognition", frame);

            // Exit if 'q' is pressed
            if (waitKey(1) == 'q') {
                break;
            }

            frameCounter++;
        }

        // Release resources
        camera.release();
        System.out.println("Camera released.");
    }
}
