package net.betzel.bytedeco.javacv.bioinspired;

import org.bytedeco.javacpp.tools.Slf4jLogger;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_bioinspired.*;


/**
 * Created by mbetzel on 04.09.2016.
 */
public class RetinaExample {

    static {
        System.setProperty("org.bytedeco.javacpp.logger", "slf4jlogger");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    private static final Slf4jLogger logger = (Slf4jLogger) org.bytedeco.javacpp.tools.Logger.create(RetinaExample.class);

    public static void main(String[] args) {
        try {
            logger.info(String.valueOf(logger.isDebugEnabled()));
            logger.info("Start");
            new RetinaExample().execute(args);
            logger.info("Stop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute(String[] args) throws Exception {
        BufferedImage bufferedImage = args.length >= 1 ? ImageIO.read(new File(args[0])) : ImageIO.read(this.getClass().getResourceAsStream("/images/retina_TreeHdr_small.jpg"));
        System.out.println("Image type: " + bufferedImage.getType());
        Mat matrix = new OpenCVFrameConverter.ToMat().convert(new Java2DFrameConverter().convert(bufferedImage));
        normalize(matrix, matrix, 0, 255, NORM_MINMAX, -1, noArray());
        showImage(matrix);
        matrix.convertTo(matrix, CV_32F);
        printMat(matrix);
        Mat gammaTransformedImage = new Mat(matrix.size(), CV_32F);
        pow(matrix, 1. / 5, gammaTransformedImage);
        Retina retina = createRetina(gammaTransformedImage.size());
        Mat retinaOutput_parvo = new Mat();
        Mat retinaOutput_magno = new Mat();
        retina.clearBuffers();
        retina.run(gammaTransformedImage);
        retina.getParvo(retinaOutput_parvo);
        retina.getMagno(retinaOutput_magno);
        showImage(retinaOutput_parvo);
        showImage(retinaOutput_magno);
    }


    private void showImage(Mat matrix) {
        CanvasFrame canvasFrame = new CanvasFrame("RetinaExample", 1);
        canvasFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvasFrame.setCanvasSize(640, 480);
        Canvas canvas = canvasFrame.getCanvas();
        canvasFrame.getContentPane().removeAll();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(canvas);
        canvasFrame.add(scrollPane);
        canvasFrame.showImage(new OpenCVFrameConverter.ToMat().convert(matrix));
    }

    private void printMat(Mat mat) {
        System.out.println("Channels: " + mat.channels());
        System.out.println("Rows: " + mat.rows());
        System.out.println("Cols: " + mat.cols());
        System.out.println("Type: " + mat.type());
        System.out.println("Dims: " + mat.dims());
        System.out.println("Depth: " + mat.depth());
    }

}