package soundanalyzer.gui.graph;

import java.awt.Color;

public class ColorPalettes {
    private static Color[] viridisPalette = new Color[] {
            new Color(0x440154),
            new Color(0x440154),
            new Color(0x481567),
            new Color(0x482677),
            new Color(0x453781),
            new Color(0x404788),
            new Color(0x39568C),
            new Color(0x33638D),
            new Color(0x2D708E),
            new Color(0x287D8E),
            new Color(0x238A8D),
            new Color(0x1F968B),
            new Color(0x20A387),
            new Color(0x29AF7F),
            new Color(0x3CBB75),
            new Color(0x55C667),
            new Color(0x73D055),
            new Color(0x95D840),
            new Color(0xB8DE29),
            new Color(0xDCE319),
            new Color(0xFDE725),
    };
    
    private static Color[] magmaPalette = new Color[] {
            new Color(0xFCFFB2),
            new Color(0xFCDF96),
            new Color(0xFBC17D),
            new Color(0xFBA368),
            new Color(0xFA8657),
            new Color(0xF66B4D),
            new Color(0xED504A),
            new Color(0xE03B50),
            new Color(0xC92D59),
            new Color(0xB02363),
            new Color(0x981D69),
            new Color(0x81176D),
            new Color(0x6B116F),
            new Color(0x57096E),
            new Color(0x43006A),
            new Color(0x300060),
            new Color(0x1E0848),
            new Color(0x110B2D),
            new Color(0x080616),
            new Color(0x000005),
    };
    
    public static Color viridis(double val) {
        val = Math.max(Math.min(val, 1), 0);

        return pickColor(viridisPalette, val);        
    }
    
    public static Color magma(double val) {
        val = Math.max(Math.min(val, 1), 0);

        return pickColor(magmaPalette, val);        
    }
    
    private static Color pickColor(Color[] palette, double val) {
        val *= palette.length;
        int index = Math.min((int)val, palette.length - 1);
        double leftover = val - index;
        
        Color color1 = palette[index];
        if (index + 1 >= palette.length) {
            return color1;
        } else {
            Color color2 = palette[index + 1];
            return new Color(
                    (int)(color1.getRed() * (1 - leftover) + color2.getRed() * leftover),
                    (int)(color1.getGreen() * (1 - leftover) + color2.getGreen() * leftover),
                    (int)(color1.getBlue() * (1 - leftover) + color2.getBlue() * leftover));
        }        
    }
}
