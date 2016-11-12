package org.httpeter.ic;

import com.galenframework.rainbow4j.ComparisonOptions;
import com.galenframework.rainbow4j.ImageCompareResult;
import com.galenframework.rainbow4j.Rainbow4J;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Peter Hendriks, httpeter@gmail.com
 */
public class ImageCompare
{

    private final ComparisonOptions cOptions = new ComparisonOptions();

    public static void main(String[] args)
    {
        int thresholdPercentage = 70;
        if (args.length != 0)
        {
            new ImageCompare(args[0], thresholdPercentage);
        } else
        {
            thresholdPercentage = 100 - Integer.parseInt(
                    JOptionPane.showInputDialog(null,
                            "Minimum similarity percentage"));

            new ImageCompare(JOptionPane.showInputDialog(null,
                    "Please provide the path to a folder with .jpg images...."),
                    thresholdPercentage);
        }
    }
    
    public ImageCompare()
    {
        
    }

    public ImageCompare(String path, int thresholdPercentage)
    {
        cOptions.setStretchToFit(true);
        cOptions.setTolerance(10);

        StringBuilder ignoreList = new StringBuilder();

        File dir = new File(path);

        File[] iFiles = dir.listFiles();

        List<String> images = new ArrayList(iFiles.length);

        for (int i = 0; i < iFiles.length; i++)
        {
            File currentFile = iFiles[i];
            if (currentFile.toString().toLowerCase().endsWith(".jpg"))
            {
                images.add(currentFile.getAbsolutePath());
            }

        }

        System.out.println("Found " + images.size()
                + " images.");

        int ncomparisons = images.size() * images.size();

        System.out.println("Making "
                + ncomparisons
                + " comparisons...\n");

        images.parallelStream().forEach(image ->
        {
            for (int currentImageNr = 0;
                    currentImageNr < images.size(); currentImageNr++)
            {
                System.out.println("Comparing '"
                        + images.get(currentImageNr)
                        + "' with '"
                        + image + "'");

                if (!ignoreList.toString().contains(images.get(currentImageNr)
                        .concat(image)))
                {
                    int diffPercentage = compareImages(images.get(currentImageNr),
                            image);

                    if (diffPercentage <= thresholdPercentage
                            && !image.equals(images.get(currentImageNr)))
                    {

                        showEqualImages(images.get(currentImageNr),
                                image, diffPercentage);

                        ignoreList.append(image.concat(images.get(currentImageNr)));
                    }
                } else
                {
                    /*
                    System.out.println("Ignoring: "
                            + images.get(currentImageNr).concat(image)
                            + "\n");
                     */
                }
            }
        });

    }

    private int compareImages(String inputImagePath, String compareWithImagePath)
    {
        try
        {
            BufferedImage inputImage = Rainbow4J.loadImage(inputImagePath);

            BufferedImage compareWithImage = Rainbow4J.loadImage(compareWithImagePath);

            ImageCompareResult diff = Rainbow4J.compare(inputImage,
                    compareWithImage,
                    cOptions);

            return (int) Math.round(diff.getPercentage());
        } catch (IOException ioe)
        {
            ioe.printStackTrace(System.out);
            return 0;
        }
    }

    private void showEqualImages(String picAPath,
            String picBPath,
            int diffPercentage)
    {

        try
        {
            Image picA = ImageIO.read(new File(picAPath))
                    .getScaledInstance(600, 320, Image.SCALE_SMOOTH);

            Image picB = ImageIO.read(new File(picBPath))
                    .getScaledInstance(600, 320, Image.SCALE_SMOOTH);

            JPanel panel = new JPanel();
            panel.add(new JLabel(picAPath));
            panel.add(new JLabel(new ImageIcon(picA)));
            panel.add(new JLabel(picBPath));
            panel.add(new JLabel(new ImageIcon(picB)));

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(2);
            frame.setSize(600, 900);
            frame.setLocationRelativeTo(null);
            frame.add(panel);
            frame.setTitle("Similarity "
                    + (100 - diffPercentage)
                    + "%");
            frame.setVisible(true);
        } catch (Exception e)
        {
            e.printStackTrace(System.out);
        }

    }

}
