package com.salle.android.sallefy.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.salle.android.sallefy.R;

public class GraphAnimator extends Thread {

    public static final float TOTAL_DURATION = 2f;
    public static final float FPS = 60f;
    public static final float TIME_PER_FRAME = 1/FPS;

    private Canvas canvas;
    private Context context;
    private int[] values;
    private String[] labels;
    private GraphView view;

    private int WIDTH = 0;
    private int HEIGHT = 0;

    private float MARGINS = 0;

    private String x_label;
    private String y_label;

    private Paint axisColor;
    private Paint textColor;
    private Paint smallTextColor;
    private Paint dotsColor;
    private Paint linesColor;
    private Paint barTextColor;
    private Paint barTextColor2;
    private Paint barColor;

    public GraphAnimator (Canvas canvas, GraphView view, Context context, int[] values, String[] labels, String x_label, String y_label) {
        this.canvas = canvas;
        this.values = values;
        this.labels = labels;
        this.view = view;
        this.context = context;
        this.x_label = x_label;
        this.y_label = y_label;
    }

    public void run() {
        //initColors();

        while (true) {
            //printBarGraph();

            view.postInvalidateOnAnimation();
            break;
        }

        Log.d("TAGG", "Eii");
    }

    private void printBarGraphAxis(Canvas canvas) {
        double maxv = 0;
        for (int v: values) if (maxv < v) maxv = v;

        canvas.drawLine(MARGINS*2, MARGINS, MARGINS*2, HEIGHT - MARGINS, axisColor);
        canvas.drawLine(MARGINS, HEIGHT - MARGINS*2, WIDTH - MARGINS, HEIGHT - MARGINS*2, axisColor);

        canvas.drawText(x_label, WIDTH / 2f, HEIGHT - MARGINS * .2f, textColor);

        canvas.rotate(-90, 2*MARGINS - MARGINS * 1.1f, HEIGHT / 2f);
        canvas.drawText(y_label, 2*MARGINS - MARGINS * 1.1f, HEIGHT / 2f, textColor);
        canvas.rotate(90, 2*MARGINS - MARGINS * 1.1f, HEIGHT / 2f);

        for (int i = 1; i <= 4; i++) {
            float hx = 2f * MARGINS + (WIDTH - 3f * MARGINS) / 5f * i;
            float hy = HEIGHT - MARGINS * 1.3f;

            canvas.drawText("" + (int) ((i / 5f) * maxv), hx, hy, smallTextColor);
        }

    }

    private String adaptString(String text, Paint paint, float size) {
        String adapted = text;

        if (paint.measureText(adapted) < size) return adapted;

        while (paint.measureText(adapted) > size) adapted = adapted.substring(0, adapted.length() - 4) + "...";
        return adapted;
    }

    private void printBars(Canvas canvas, int[] values) {
        double maxv = 0;
        float textSize = MARGINS;

        float sep = MARGINS / 2;
        float H = ((HEIGHT - 3f * MARGINS - 1f * sep) / values.length) - sep;

        for (int v: values) if (maxv < v) maxv = v;

        if (MARGINS > (H - sep)) {
            barTextColor.setTextSize(H - sep);
            barTextColor2.setTextSize(H - sep);
            textSize = H - sep;
        }

        for (int i = 0; i < values.length; i++) {
            int v = values[i];

            float top = 1f * MARGINS + 1f * sep + (H + sep) * i;
            float left = 2f * MARGINS + 1f * sep;
            float bottom = top + H;
            float right = 2f * MARGINS + 1f * sep + (WIDTH - 3f * MARGINS - 2f * sep) * (float) (v / maxv);

            canvas.drawRect(left, top, right, bottom, barColor);

            if ((v / maxv) < .5) {
                String l = adaptString(labels[i], barTextColor, (WIDTH - MARGINS - sep) - (right + sep));
                canvas.drawText(l, right + sep, top + H/2f + textSize/2f, barTextColor);
            } else {
                String l = adaptString(labels[i], barTextColor2, (right - sep) - (left + sep));
                canvas.drawText(l, right - sep, bottom - H/2f + textSize/2f, barTextColor2);
            }
        }
    }

    private void printBarGraph() {
        HEIGHT = canvas.getHeight();
        WIDTH = canvas.getWidth();
        MARGINS = WIDTH * 0.06f;

        printBarGraphAxis(canvas);
        printBars(canvas, values);
    }


    private void initColors() {
        axisColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisColor.setColor(Color.GRAY);
        axisColor.setStyle(Paint.Style.STROKE);
        axisColor.setStrokeWidth((int) (WIDTH * 0.0075));

        textColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        textColor.setColor(Color.GRAY);
        textColor.setTextAlign(Paint.Align.CENTER);
        textColor.setTextSize(MARGINS * 1f);

        smallTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallTextColor.setColor(Color.GRAY);
        smallTextColor.setTextAlign(Paint.Align.CENTER);
        smallTextColor.setTextSize(MARGINS * .5f);

        barTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        barTextColor.setColor(context.getColor(R.color.colorSecondary));
        barTextColor.setTextAlign(Paint.Align.LEFT);
        barTextColor.setTextSize(MARGINS * 1f);

        barTextColor2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        barTextColor2.setColor(Color.WHITE);
        barTextColor2.setTextAlign(Paint.Align.RIGHT);
        barTextColor2.setTextSize(MARGINS * 1f);

        dotsColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotsColor.setColor(context.getColor(R.color.colorSecondary));
        dotsColor.setStyle(Paint.Style.STROKE);
        dotsColor.setStrokeWidth((int) (WIDTH * 0.0075));

        linesColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        linesColor.setColor(context.getColor(R.color.colorPrimary));
        linesColor.setAlpha(100);
        linesColor.setStyle(Paint.Style.STROKE);
        linesColor.setStrokeWidth((int) (WIDTH * 0.0075));

        barColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        barColor.setColor(context.getColor(R.color.colorSecondary));
    }
}
