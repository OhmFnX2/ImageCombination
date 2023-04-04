package net.ohmfnx2.Imageombination;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageCombination {
    public static void main(String[] args) {
        // Define the folder path where the images are located
        String image_name = "testa";

        String folderPath = "./img";
        // Get a list of all the image files in the folder
        File[] imageFiles = new File(folderPath).listFiles((dir, name) -> name.endsWith(".png"));
        assert imageFiles != null;
        int newLine = 12;
        int outputWidth = 1280 * newLine;
        int outputHeight = 720 * ((imageFiles.length / newLine)+1);
        int imageDuration = 200;
        JsonObject jsonObject = null;
        try {
            jsonObject = JsonParser.parseReader(new FileReader("./tem.json")).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        jsonObject.getAsJsonObject("meta").addProperty("image", image_name + ".png");
        JsonObject size = new JsonObject();
        size.addProperty("w", outputWidth);
        size.addProperty("h", outputHeight);
        jsonObject.getAsJsonObject("meta").add("size", size);

        // Create a new BufferedImage to hold the combined images
        BufferedImage outputImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_BYTE_INDEXED);

        // Loop through each image file and combine it into the output image
        int y = 0;
        int x = 0;
        for (File imageFile : imageFiles) {
            try {
                // Read in the current image file
                BufferedImage inputImage = ImageIO.read(imageFile);
                // Copy the current input image into the output image
                outputImage.createGraphics().drawImage(inputImage, x, y, null);

                //	"filename": "Symbol 10000",
                //	"frame": {"x":0,"y":0,"w":512,"h":288},
                //	"rotated": false,
                //	"trimmed": false,
                //	"spriteSourceSize": {"x":0,"y":0,"w":512,"h":288},
                //	"sourceSize": {"w":512,"h":288},
                //  "duration": 100
                JsonObject frame = new JsonObject();
                frame.addProperty("filename", imageFile.getName().replace(".png", ""));
                JsonObject frame1 = new JsonObject();
                frame1.addProperty("x", x);
                frame1.addProperty("y", y);
                frame1.addProperty("w", inputImage.getWidth());
                frame1.addProperty("h", inputImage.getHeight());
                frame.add("frame", frame1);
                frame.addProperty("rotated", false);
                frame.addProperty("trimmed", false);
                JsonObject spriteSourceSize = new JsonObject();
                spriteSourceSize.addProperty("x", 0);
                spriteSourceSize.addProperty("y", 0);
                spriteSourceSize.addProperty("w", inputImage.getWidth());
                spriteSourceSize.addProperty("h", inputImage.getHeight());
                frame.add("spriteSourceSize", spriteSourceSize);
                JsonObject sourceSize = new JsonObject();
                sourceSize.addProperty("w", inputImage.getWidth());
                sourceSize.addProperty("h", inputImage.getHeight());
                frame.add("sourceSize", sourceSize);
                frame.addProperty("duration", imageDuration);
                jsonObject.getAsJsonArray("frames").add(frame);

                if(x >= (newLine-1) * inputImage.getWidth()) {
                    x = 0;
                    y += inputImage.getHeight();
                } else {
                    x += inputImage.getWidth();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Write the output image to a file
        try {
            ImageIO.write(outputImage, "png", new File("./" + image_name + ".png"));
            FileWriter writer = new FileWriter("./" + image_name + ".json");
            writer.write(jsonObject.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}