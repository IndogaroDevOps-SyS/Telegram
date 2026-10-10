package id.indogaro.ui.Charts;

import android.animation.Animator;

import id.indogaro.ui.Charts.data.ChartData;
import id.indogaro.ui.Charts.view_data.StackLinearViewData;

public class PieChartViewData extends StackLinearViewData {

    float selectionA;
    float drawingPart;
    Animator animator;

    public PieChartViewData(ChartData.Line line) {
        super(line);
    }
}
