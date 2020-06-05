package com.salle.android.sallefy.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.salle.android.sallefy.R;

public class BarGraphView extends View {

    private int WIDTH = 0;
    private int HEIGHT = 0;

    private float MARGINS = 0;

    private int[] barGraphValues;
    private String[] labels;
    private String x_label;
    private String y_label;

    private Paint axisColor;
    private Paint textColor;
    private Paint smallTextColor;
    private Paint barTextColor;
    private Paint barTextColor2;
    private Paint barColor;

    public BarGraphView(Context context) {
        super(context);

        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        this.x_label = "x_label";
        this.y_label = "y_label";

        init(null);
    }


    public BarGraphView(Context context, int[] graph, String[] labels, String x_label, String y_label) {
        super(context);

        this.barGraphValues = graph;
        this.labels = labels;
        this.x_label = x_label;
        this.y_label = y_label;

        init(null);
    }

    public BarGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        this.x_label = "x_label";
        this.y_label = "y_label";

        init(attrs);
    }

    public BarGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        this.x_label = "x_label";
        this.y_label = "y_label";

        init(attrs);
    }

    public BarGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        barGraphValues = new int[10];
        labels = new String[10];
        for (int i = 0; i < barGraphValues.length; i++) barGraphValues[i] = (i+1)*(i+1);
        for (int i = 0; i < labels.length; i++) labels[i] = "0" + i + "-20";

        this.x_label = "x_label";
        this.y_label = "y_label";

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
        textColor.setTextAlign(Paint.Align.LEFT);
        textColor.setTextSize(MARGINS * 1f);

        barTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        barTextColor.setColor(getContext().getColor(R.color.colorSecondary));
        barTextColor.setTextAlign(Paint.Align.LEFT);
        barTextColor.setTextSize(MARGINS * 1f);

        barTextColor2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        barTextColor2.setColor(Color.WHITE);
        barTextColor2.setTextAlign(Paint.Align.RIGHT);
        barTextColor2.setTextSize(MARGINS * 1f);

        smallTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallTextColor.setColor(Color.GRAY);
        smallTextColor.setTextAlign(Paint.Align.CENTER);
        smallTextColor.setTextSize(MARGINS * .5f);

        barColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        barColor.setColor(getContext().getColor(R.color.colorSecondary));
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
                canvas.drawText(labels[i], right + sep, top + H/2f + textSize/2f, barTextColor);
            } else {
                canvas.drawText(labels[i], right - sep, bottom - H/2f + textSize/2f, barTextColor2);
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
        printBarGraphAxis(canvas);
        printBars(canvas, barGraphValues);
    }

    public void setBarGraphValues(int[][] barGraphValues) {}

    public void setGraphValues(int[] graphValues) {
        this.barGraphValues = graphValues;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
