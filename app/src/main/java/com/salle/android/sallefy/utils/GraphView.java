package com.salle.android.sallefy.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.salle.android.sallefy.R;


public class GraphView extends View{

    private int WIDTH = 0;
    private int HEIGHT = 0;

    private float MARGINS = 0;

    private int maxValue = 0;

    private int type;
    private boolean loading;

    private int[][] timeGraphValues;
    private int[] barGraphValues;
    private String[] labels;
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

    public GraphView(Context context) {
        super(context);

        type = 1;

        timeGraphValues = new int[2][10];
        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < timeGraphValues[0].length; i++) timeGraphValues[0][i] = (i+1)*(i+1);
        for (int i = 0; i < timeGraphValues[1].length; i++) timeGraphValues[1][i] = (10-i)*(10-i);
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        this.x_label = "x_label";
        this.y_label = "y_label";

        loading = true;

        init(null);
    }

    public void setTimeGraphValues(int[][] timeGraphValues) {
        this.timeGraphValues = timeGraphValues;
    }

    public void setBarGraphValues(int[] barGraphValues) {
        this.barGraphValues = barGraphValues;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void resetView() {
        postInvalidate();
    }

    public void setLabels(String x_label, String y_label) {
        this.x_label = x_label;
        this.y_label = y_label;
    }

    public GraphView(Context context, int[][] graph, String[] labels, String x_label, String y_label) {
        super(context);

        type = 1;

        this.timeGraphValues = graph;
        this.labels = labels;
        this.x_label = x_label;
        this.y_label = y_label;

        init(null);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        type = 1;

        timeGraphValues = new int[2][10];
        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < timeGraphValues[0].length; i++) timeGraphValues[0][i] = (i+1)*(i+1);
        for (int i = 0; i < timeGraphValues[1].length; i++) timeGraphValues[1][i] = (10-i)*(10-i);
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        this.x_label = "x_label";
        this.y_label = "y_label";

        loading = true;

        init(attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        type = 1;

        timeGraphValues = new int[2][10];
        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < timeGraphValues[0].length; i++) timeGraphValues[0][i] = (i+1)*(i+1);
        for (int i = 0; i < timeGraphValues[1].length; i++) timeGraphValues[1][i] = (10-i)*(10-i);
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        loading = true;

        this.x_label = "x_label";
        this.y_label = "y_label";

        init(attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        type = 1;

        timeGraphValues = new int[2][10];
        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < timeGraphValues[0].length; i++) timeGraphValues[0][i] = (i+1)*(i+1);
        for (int i = 0; i < timeGraphValues[1].length; i++) timeGraphValues[1][i] = (10-i)*(10-i);
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        this.x_label = "x_label";
        this.y_label = "y_label";

        loading = true;

        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        postInvalidate();
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
        barTextColor.setColor(getContext().getColor(R.color.colorSecondary));
        barTextColor.setTextAlign(Paint.Align.LEFT);
        barTextColor.setTextSize(MARGINS * 1f);

        barTextColor2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        barTextColor2.setColor(Color.WHITE);
        barTextColor2.setTextAlign(Paint.Align.RIGHT);
        barTextColor2.setTextSize(MARGINS * 1f);

        dotsColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotsColor.setColor(getContext().getColor(R.color.colorSecondary));
        dotsColor.setStyle(Paint.Style.STROKE);
        dotsColor.setStrokeWidth((int) (WIDTH * 0.0075));

        linesColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        linesColor.setColor(getContext().getColor(R.color.colorPrimary));
        linesColor.setAlpha(100);
        linesColor.setStyle(Paint.Style.STROKE);
        linesColor.setStrokeWidth((int) (WIDTH * 0.0075));

        barColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        barColor.setColor(getContext().getColor(R.color.colorSecondary));
    }

    private void printDots(Canvas canvas, int[] values) {
        int lastx = 0, lasty = 0;
        double maxv = maxValue;
        double spacing = (WIDTH - 3.0 * MARGINS) / (values.length - 1.0);

        for (int i = 0; i < values.length; i++) {
            int v = values[i];
            int x = (int) (2.0 * MARGINS + spacing * i);
            int y = (int) (HEIGHT - ((v / maxv) * (HEIGHT - 3.0 * MARGINS) + 2.0 * MARGINS));

            if (i != 0) canvas.drawLine(x, y, lastx, lasty, linesColor);

            canvas.drawCircle(x, y, (int) (WIDTH * 0.01), dotsColor);
            lastx = x;
            lasty = y;
        }
    }

    private void printTimeGraphAxis(Canvas canvas) {
        double maxv = maxValue;

        canvas.drawLine(MARGINS*2, MARGINS, MARGINS*2, HEIGHT - MARGINS, axisColor);
        canvas.drawLine(MARGINS, HEIGHT - MARGINS*2, WIDTH - MARGINS, HEIGHT - MARGINS*2, axisColor);

        canvas.drawText(x_label, WIDTH / 2f, HEIGHT - MARGINS * .2f, textColor);

        canvas.rotate(-90, 2*MARGINS - MARGINS * 1.1f, HEIGHT / 2f);
        canvas.drawText(y_label, 2*MARGINS - MARGINS * 1.1f, HEIGHT / 2f, textColor);
        canvas.rotate(90, 2*MARGINS - MARGINS * 1.1f, HEIGHT / 2f);

        for (int i = 1; i <= 4; i++) {
            float vx = 2*MARGINS - MARGINS * .2f;
            float vy = HEIGHT - ((HEIGHT - 3f * MARGINS) / 5f * i + 2f * MARGINS);
            float hx = 2f * MARGINS + (WIDTH - 3f * MARGINS) / 5f * i;
            float hy = HEIGHT - MARGINS * 1.3f;

            canvas.drawText(labels[(int) (labels.length * ((i)/5f))], hx, hy, smallTextColor);

            canvas.rotate(-90, vx, vy);
            canvas.drawText("" + (int) ((i / 5f) * maxv), vx, vy, smallTextColor);
            canvas.rotate(90, vx, vy);
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

    private void printBarGraphAxis(Canvas canvas) {
        double maxv = 0;
        for (int v: barGraphValues) if (maxv < v) maxv = v;

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


    @Override
    protected void onDraw(Canvas canvas) {
        HEIGHT = canvas.getHeight();
        WIDTH = canvas.getWidth();
        MARGINS = WIDTH * 0.06f;

        initColors();

        for (int[] a: timeGraphValues) for (int v: a) if (maxValue < v) maxValue = v;

        if (loading) {
            canvas.drawText("Choose an option", WIDTH/2, HEIGHT/2, textColor);
        } else {
            if (type == 1) {
                printTimeGraphAxis(canvas);
                for (int[] values : timeGraphValues) printDots(canvas, values);
            } else {
                printBarGraphAxis(canvas);
                printBars(canvas, barGraphValues);
            }
        }
        loading = false;
    }
}