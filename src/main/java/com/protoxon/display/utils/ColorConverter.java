package com.protoxon.display.utils;

public class ColorConverter {

    /**
     * Converts an ARGB string (e.g., "rgba(195, 87, 143, 0.8)") to its long representation. <p>
     * The ARGB string can be generated using the tool at <a href="https://rgbacolorpicker.com/">RGBA Color Picker</a>.
     * @param rgba the ARGB string
     * @return the long value representation of the ARGB color
     */
    public static long rgbaToArgb(String rgba) {
        rgba = rgba.substring(5, rgba.length() - 1);
        // Split by commas
        String[] components = rgba.split(",");
        // Parse components
        int red = Integer.parseInt(components[0].trim());
        int green = Integer.parseInt(components[1].trim());
        int blue = Integer.parseInt(components[2].trim());
        float alpha = Float.parseFloat(components[3].trim());
        // Convert alpha to 0-255 range
        int alphaInt = Math.round(alpha * 255);
        // Combine into ARGB value
        return ((long) alphaInt << 24) | ((long) red << 16) | ((long) green << 8) | blue;
    }

    public static void main(String[] args) {
        String rgba = "rgba(28, 155, 86, 0.19)";
        String rgba_black = "rgba(0, 0, 0, 1)";
        long argb = rgbaToArgb(rgba);
        long argb_black = rgbaToArgb(rgba_black);
        System.out.println("ARGB: " + argb);
        System.out.println("BLACK ARGB: " + argb_black);
    }
}
