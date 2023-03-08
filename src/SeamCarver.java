
import java.awt.image.BufferedImage;

public class SeamCarver
   {
    private BufferedImage original;
    private BufferedImage highlighted;
    private BufferedImage image;

    private RGB[][] imageRGB;
    private RGB[][] testImage = {
            {new RGB(78, 209, 79), new RGB(63, 118, 247), new RGB(92, 175, 95), new RGB(243, 73, 183), new RGB(210, 109, 104), new RGB(252, 101, 119)},
            {new RGB(224, 191, 182), new RGB(108, 89, 82), new RGB(80, 196, 230), new RGB(112, 156, 180), new RGB(176, 178, 120), new RGB(142, 151, 142)},
            {new RGB(117, 189, 149), new RGB(171, 231, 153), new RGB(149, 164, 168), new RGB(107, 119, 71), new RGB(120, 105, 138), new RGB(163, 174, 196)},
            {new RGB(163, 222, 132), new RGB(187, 117, 183), new RGB(92, 145, 69), new RGB(158, 143, 79), new RGB(220, 75, 222), new RGB(189, 73, 214)},
            {new RGB(211, 120, 173), new RGB(188, 218, 244), new RGB(214, 103, 68), new RGB(163, 166, 246), new RGB(79, 125, 246), new RGB(211, 201, 98)}
        };

    public SeamCarver(BufferedImage image)
       {
        this.original = copy(image);
        this.highlighted = copy(image);
        this.image = copy(image);
        this.imageRGB = new RGB[image.getWidth()][image.getHeight()];

        updateRGB();
       }

    public void reset()
       {
        if(original != null)
           {
            image = copy(original);
            highlighted = copy(original);

            updateRGB();
           }
       }

    public BufferedImage copy(BufferedImage image)
       {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < image.getWidth(); i++)
           {
            for(int j = 0; j < image.getHeight(); j++)
               {
                copy.setRGB(i, j, image.getRGB(i, j));
               }
           }
        return copy;
       }

    public void removeHorizontalSeam()
       {
        int[] seam = findHorizontalSeam();
        removeHorizontalSeam(seam);
       }

    public void removeVerticalSeam()
       {
        int[] seam = findVerticalSeam();
        removeVerticalSeam(seam);
       }

    public void highlightHorizontalSeam()
       {
        BufferedImage newImage = copy(image);

        int[] seam = findHorizontalSeam();
        for(int i = 0; i < seam.length; i++)
           {
            newImage.setRGB(i, seam[i], getIRGB(255, 255, 0));
           }

        highlighted = newImage;
       }

    public void highlightVerticalSeam()
       {
        BufferedImage newImage = copy(image);

        int[] seam = findVerticalSeam();
        for(int i = 0; i < seam.length; i++)
           {
            newImage.setRGB(seam[i], i, getIRGB(255, 255, 0));
           }

        highlighted = newImage;
       }

    public BufferedImage original()
       {
        return original;
       }

    public BufferedImage highlighted()
       {
        return highlighted;
       }

    public BufferedImage image()
       {
        return image;
       }

    public int width()
       {
        return image.getWidth();
       }

    public int height()
       {
        return image.getHeight();
       }

    public double energy(int x, int y)
       {
        if(x < 0 || x >= width())
           {
            return -1.0;
           }
        if(y < 0 || y >= height())
           {
            return -1.0;
           }

        return deltaX(x, y) + deltaY(x, y);
       }

    public double deltaX(int x, int y)
       {
        int xLeft = x - 1;
        int xRight = x + 1;

        if(xLeft < 0)
           {
            xLeft = width() - 1;
           }

        if(xRight >= width())
           {
            xRight = 0;
           }

        RGB left = getRGB(xLeft, y);
        RGB right = getRGB(xRight, y);

        double rx = Math.pow(Math.abs(right.red() - left.red()), 2);
        double gx = Math.pow(Math.abs(right.green() - left.green()), 2);
        double bx = Math.pow(Math.abs(right.blue() - left.blue()), 2);

        return rx + gx + bx;
       }

    public double deltaY(int x, int y)
       {
        int yDown = y - 1;
        int yUp = y + 1;

        if(yDown < 0)
           {
            yDown = height() - 1;
           }

        if(yUp >= height())
           {
            yUp = 0;
           }

        RGB down = getRGB(x, yDown);
        RGB up = getRGB(x, yUp);

        double ry = Math.pow(Math.abs(down.red() - up.red()), 2);
        double gy = Math.pow(Math.abs(down.green() - up.green()), 2);
        double by = Math.pow(Math.abs(down.blue() - up.blue()), 2);

        return ry + gy + by;
       }

    public int[] findHorizontalSeam()
       {
        int[] lowestEnergySeam = null;
        double lowestEnergy = -1.0;

        for(int y = 0; y < height(); y++)
           {
            int[] seam = new int[width()];
            seam[0] = y;
            for(int x = 1; x < width(); x++)
               {
                double middle = energy(x, y);
                double down = energy(x, y - 1);
                double up = energy(x, y + 1);

                if(down <= up && down <= middle && down != -1)
                   {
                    seam[x] = y - 1;
                   }
                else if(up <= middle && up <= down && up != -1)
                   {
                    seam[x] = y + 1;
                   }
                else
                   {
                    seam[x] = y;
                   }
               }
            double seamEnergy = 0.0;
            for(int i = 0; i < width(); i++)
               {
                seamEnergy = seamEnergy + energy(i, seam[i]);
               }

            if(seamEnergy < lowestEnergy || lowestEnergy == -1.0)
               {
                lowestEnergySeam = seam;
                lowestEnergy = seamEnergy;
               }
           }
        return lowestEnergySeam;
       }

    public int[] findVerticalSeam()
       {
        int[] lowestEnergySeam = null;
        double lowestEnergy = -1.0;

        for(int x = 0; x < width(); x++)
           {
            int[] seam = new int[height()];
            seam[0] = x;
            for(int y = 0; y < height(); y++)
               {
                double middle = energy(x, y);
                double left = energy(x - 1, y);
                double right = energy(x + 1, y);

                if(left <= right && left <= middle && left != -1)
                   {
                    seam[y] = x - 1;
                   }
                else if(right <= middle && right <= left && right != -1)
                   {
                    seam[y] = x + 1;
                   }
                else
                   {
                    seam[y] = x;
                   }
               }

            double seamEnergy = 0.0;
            for(int i = 0; i < height(); i++)
               {
                seamEnergy = seamEnergy + energy(seam[i], i);
               }

            if(seamEnergy < lowestEnergy || lowestEnergy == -1.0)
               {
                lowestEnergySeam = seam;
                lowestEnergy = seamEnergy;
               }
           }
        return lowestEnergySeam;
       }

    public void removeHorizontalSeam(int[] seam)
       {
        BufferedImage newImage = new BufferedImage(width(), height() - 1, BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < width(); i++)
           {
            int flag = 0;
            for(int j = 0; j < height(); j++)
               {
                if(j == seam[i])
                   {
                    flag = 1;
                    continue;
                   }
                newImage.setRGB(i, j - flag, getIRGB(i, j));
               }
           }
        image = newImage;
        updateRGB();
       }

    public void removeVerticalSeam(int[] seam)
       {
        BufferedImage newImage = new BufferedImage(width() - 1, height(), BufferedImage.TYPE_INT_RGB);

        for(int j = 0; j < height(); j++)
           {
            int flag = 0;
            for(int i = 0; i < width(); i++)
               {
                if(i == seam[j])
                   {
                    flag = 1;
                    continue;
                   }
                newImage.setRGB(i - flag, j, getIRGB(i, j));
               }
           }
        image = newImage;
        updateRGB();
       }

    public void setRGB(int x, int y, RGB rgb)
       {
        imageRGB[x][y] = rgb;
       }

    public void updateRGB()
       {
        imageRGB = new RGB[width()][height()];

        for(int i = 0; i < width(); i++)
           {
            for(int j = 0; j < height(); j++)
               {
                int rgb = image.getRGB(i, j);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb >> 0) & 0xFF;

                setRGB(i, j, new RGB(red, green, blue));
               }
           }
       }

    public RGB getRGB(int x, int y)
       {
        return imageRGB[x][y];
       }

    public int getIRGB(int x, int y)
       {
        RGB rgb = getRGB(x, y);
        int irgb = getIRGB(rgb.red(), rgb.green(), rgb.blue());

        return irgb;
       }

    public int getIRGB(int red, int green, int blue)
       {
        RGB rgb = new RGB(red, green, blue);
        int irgb = (65536 * red) + (256 * green) + (blue);

        return irgb;
       }
   }
