package battleSystemApp.utils;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.UserFacingIcon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JRadioButton;
import javax.swing.Timer;

/**
 * Crea un icono de alarma alrededor de un punto
 * 
 * @author vgonllo
 *
 */
public class AlarmIcon {

	private BufferedImage image;
	private UserFacingIcon icon;
	private WorldWindow wwd;
	
	public AlarmIcon(WorldWindow wwd, UserFacingIcon icon) {
		this.wwd = wwd;
		this.icon = icon;
		createBitmap(PatternFactory.PATTERN_CIRCLE, Color.RED);
	}
	// Create a blurred pattern bitmap
    private BufferedImage createBitmap(String pattern, Color color)
    {
        // Create bitmap with pattern
        BufferedImage image = PatternFactory.createPattern(pattern, new Dimension(128, 128), 0.7f,
            color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
        // Blur a lot to get a fuzzy edge
        image = PatternFactory.blur(image, 13);
        image = PatternFactory.blur(image, 13);
        image = PatternFactory.blur(image, 13);
        image = PatternFactory.blur(image, 13);
        return image;
    }

    private class StaticAlarmAction extends AbstractAction
    {
        private Object bgIconPath;
        private double bgScale;

        private StaticAlarmAction(String name, Object bgIconPath, double bgScale)
        {
            super(name);
            this.bgIconPath = bgIconPath;
            this.bgScale = bgScale;
        }

        public void actionPerformed(ActionEvent e)
        {
            icon.setBackgroundImage(bgIconPath);
            icon.setBackgroundScale(bgScale);
            wwd.redraw();
        }
    }
    
    private class PulsingAlarmAction extends AbstractAction
    {
        protected final Object bgIconPath;
        protected int frequency;
        protected int scaleIndex = 0;
        protected double[] scales = new double[] {1.25, 1.5, 1.75, 2, 2.25, 2.5, 2.75, 3, 3.25, 3.5, 3.25, 3,
            2.75, 2.5, 2.25, 2, 1.75, 1.5};
        protected Timer timer;

        private PulsingAlarmAction(String name, Object bgp, int frequency)
        {
            super(name);
            this.bgIconPath = bgp;
            this.frequency = frequency;
        }

        private PulsingAlarmAction(String name, Object bgp, int frequency, double[] scales)
        {
            this(name, bgp, frequency);
            this.scales = scales;
        }

        public void actionPerformed(ActionEvent e)
        {
            if (timer == null)
            {
                timer = new Timer(frequency, new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        icon.setBackgroundScale(scales[++scaleIndex % scales.length]);
                        wwd.redraw();
                    }
                });

                ((JRadioButton) e.getSource()).addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED)
                            timer.stop();
                    }
                });
            }
            icon.setBackgroundImage(bgIconPath);
            scaleIndex = 0;
            timer.start();
        }
    }

    private class FlashingAlarmAction extends PulsingAlarmAction
    {
        private FlashingAlarmAction(String name, Object bgp, int frequency)
        {
            super(name, bgp, frequency, new double[] {2, 0.5});
        }
    }
}
