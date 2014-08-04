/*
 * 
 * Copyright 2007-2012 Audrius Valunas
 * 
 * This file is part of OpenACS.

 * OpenACS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenACS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenACS.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.openacs.web;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.sql.Timestamp;

public class Graph {

    protected void normalize(int[] a, int f, int l, int[] minmax, int top, int height) {
        minmax[0] = Integer.MAX_VALUE;
        minmax[1] = Integer.MIN_VALUE;

        for (int i = f; i < l; i++) {
            if (a[i] != Integer.MIN_VALUE && a[i] < minmax[0]) {
                minmax[0] = a[i];
            }
            if (a[i] != Integer.MIN_VALUE && a[i] > minmax[1]) {
                minmax[1] = a[i];
            }
        }
        if (minmax[0] == minmax[1]) {
            if (minmax[0] != 0) {
                minmax[0] *= 0.75;
                minmax[1] *= 1.25;
            } else {
                minmax[0] = -10;
                minmax[1] = 10;
            }
        }
        for (int i = f; i < l; i++) {
            if (a[i] != Integer.MIN_VALUE) {
                a[i] = top + height - (a[i] - minmax[0]) * height / (minmax[1] - minmax[0]);
            } else {
                a[i] = top + height;
            }
        }
    }

    protected void drawTimeTicks(Graphics2D g2, int LEFT, int TOP, int WDTH, int HGHT, Timestamp tleft, Timestamp tright) {
        long milis = tleft.getTime();
        long deltamilis = (tright.getTime() - tleft.getTime()) / 5;

        for (int nx = LEFT + 100; nx < LEFT + WDTH; nx += 100) {
            Timestamp t = new Timestamp(milis);
            String ts = t.toString();
            int w = g2.getFontMetrics().stringWidth(ts);
            int ho = (((nx - LEFT) % 200 == 0) ? g2.getFontMetrics().getHeight() : 0);
            g2.drawString(ts, nx - w / 2, TOP + HGHT + 10 + g2.getFontMetrics().getHeight() + ho);
            milis += deltamilis;
        }
    }

    protected void graph(Graphics2D g2, int[] y1, int[] y2, Timestamp tleft, Timestamp tright, double scale1, double scale2) {

        int LEFT = 75;
        int TOP = 30;

        int HGHT = 150;
        int WDTH = 500;


        Rectangle bounds = g2.getDeviceConfiguration().getBounds();
//        int HGHT = bounds.height;
//        int WDTH = bounds.width;

        g2.setColor(new Color(245, 245, 245));
        g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        /*        AffineTransform t = g2.getTransform();
        t.translate(75, 30);
        //g2.setTransform(t);*/

        int[] minmax1 = new int[2];
        int[] minmax2 = new int[2];
        normalize(y1, 1, 501, minmax1, TOP, HGHT);
        normalize(y2, 1, 501, minmax2, TOP, HGHT);

        int y1min = minmax1[0], y1max = minmax1[1], y2min = minmax2[0], y2max = minmax2[1];

        int[] x = new int[y1.length];
        for (int ix = 1; ix < x.length - 1; ix++) {
            x[ix] = ix - 1 + LEFT;
        }
        x[0] = LEFT;
        y1[0] = y1[WDTH + 1] = HGHT + TOP;
        x[WDTH + 1] = WDTH + LEFT;

        y2[0] = y2[1];

        Color color1 = new Color(0, 207, 0);   // Green
        Color color2 = new Color(0, 42, 151);   // Blue

        g2.setColor(color1);
        g2.setStroke(new BasicStroke(1));
        g2.fillPolygon(x, y1, x.length);
        g2.setColor(color2);
        g2.drawPolyline(x, y2, x.length - 2);

        g2.setColor(new Color(140, 140, 140));
        g2.drawRect(LEFT, TOP, WDTH, HGHT);
        g2.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1}, 0));
        for (int ny = TOP + 10; ny < TOP + HGHT; ny += 10) {
            g2.drawLine(LEFT, ny, WDTH + LEFT, ny);
        }
        for (int nx = LEFT + 10; nx < WDTH + LEFT; nx += 10) {
            g2.drawLine(nx, TOP, nx, TOP + HGHT);
        }

        g2.setColor(new Color(130, 30, 30));
        for (int ny = TOP + 50; ny < TOP + HGHT; ny += 50) {
            g2.drawLine(LEFT, ny, WDTH + LEFT, ny);
        }
        for (int nx = LEFT + 50; nx < WDTH + LEFT; nx += 50) {
            g2.drawLine(nx, TOP, nx, TOP + HGHT);
        }
        /*
        Timestamp t = new Timestamp(Calendar.getInstance().getTimeInMillis());
        String ts = t.toString();
         */
        drawTimeTicks(g2, LEFT, TOP, WDTH, HGHT, tleft, tright);
        double y1d = (y1max - y1min) / 3.0;
        double y1c = y1max;

        double y2d = (y2max - y2min) / 3.0;
        double y2c = y2max;

        String fmt1 = (scale1 == 1) ? "%.0f" : "%.1f";
        String fmt2 = (scale2 == 1) ? "%.0f" : "%.1f";

        int fh = g2.getFontMetrics().getHeight();
        for (int ny = TOP; ny <= TOP + HGHT; ny += HGHT / 3) {
            String y1s = String.format(fmt1, y1c * scale1);
            int w1 = g2.getFontMetrics().stringWidth(y1s);
            //int w2 = g2.getFontMetrics().stringWidth(y2c.toString());
            g2.setColor(color1);
            g2.drawString(y1s, LEFT - w1 - 10, ny + fh / 2);
            g2.setColor(color2);
            g2.drawString(String.format(fmt2, y2c * scale2), LEFT + WDTH + 10, ny + fh / 2);
            y1c -= y1d;
            y2c -= y2d;
        }

        // Draw Text
        //g2.drawString("This is my custom Panel!",10,20);
        //g2.getFontMetrics().stringWidth(TOOL_TIP_TEXT_KEY);
    }
}
