package com.beanlife.prod;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Vivien on 2017/10/10.
 */

public class ProdAttrView extends View {

    private Paint paint = new Paint();
    private int offset = 200;
    private int body ,acid, after, bal ,aroma;

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public int getAcid() {
        return acid;
    }

    public void setAcid(int acid) {
        this.acid = acid;
    }

    public int getAfter() {
        return after;
    }

    public void setAfter(int after) {
        this.after = after;
    }

    public int getBal() {
        return bal;
    }

    public void setBal(int bal) {
        this.bal = bal;
    }

    public int getAroma() {
        return aroma;
    }

    public void setAroma(int aroma) {
        this.aroma = aroma;
    }

    public ProdAttrView(Context context) {
        super(context);
    }

    public ProdAttrView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        int range = 40;

        for(int i = 1; i <= 5; i++){
            float sin54 = (float) (Math.sin(Math.toRadians(54))*i*range);
            float cos54 = (float) (Math.cos(Math.toRadians(54))*i*range);
            float sin72 = (float) (Math.sin(Math.toRadians(72))*i*range);
            float cos72 = (float) (Math.cos(Math.toRadians(72))*i*range);
            float h = (float)((Math.cos(Math.toRadians(54))*i*range)/2+(Math.sin(Math.toRadians(72))*i*range)/2);

            canvas.drawLine(offset, offset -h, offset + sin54, offset -h + cos54, paint);
            canvas.drawLine(offset + sin54, offset -h + cos54, offset + sin54 - cos72, offset -h + cos54 + sin72,paint);
            canvas.drawLine(offset + sin54 - cos72, offset -h + cos54 + sin72 ,offset + sin54 - cos72 - i*range,offset -h +  cos54 + sin72,paint);
            canvas.drawLine(offset + sin54 - cos72 - i*range,offset -h +  cos54 + sin72,offset - sin54, offset -h + cos54,paint);
            canvas.drawLine(offset - sin54,offset -h +  cos54,offset,offset -h + 0,paint);
        }

        float sin54 = (float) (Math.sin(Math.toRadians(54))*range);
        float cos54 = (float) (Math.cos(Math.toRadians(54))*range);
        float sin72 = (float) (Math.sin(Math.toRadians(72))*range);
        float cos72 = (float) (Math.cos(Math.toRadians(72))*range);
        float h = (float)((Math.cos(Math.toRadians(54))*range)/2+(Math.sin(Math.toRadians(72))*range)/2);

        paint.setColor(Color.parseColor("#84BA15"));
        paint.setStrokeWidth(5);
        Path path = new Path();
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(150);

        path.moveTo(offset, offset - h * body);
        path.lineTo(offset + sin54 * acid, offset - h * acid + cos54 * acid);
        path.lineTo(offset + (sin54 - cos72) * after , offset - h * after + (cos54 + sin72) * after);
        path.lineTo(offset + (sin54 - cos72 - range) * bal, offset - h * bal + (cos54 + sin72)* bal);
        path.lineTo(offset - sin54 * aroma, offset - h * aroma + cos54 * aroma);
        path.close();
        canvas.drawPath(path, paint);

        paint.setStrokeWidth(3);

        paint.setColor(Color.GRAY);
        canvas.drawLine(offset, offset - h*5, offset, offset, paint);
        canvas.drawLine(offset + sin54 * 5, offset - h * 5 + cos54 * 5, offset, offset, paint);
        canvas.drawLine(offset + (sin54 - cos72) * 5 , offset - h * 5 + (cos54 + sin72) * 5 ,offset,offset, paint);
        canvas.drawLine(offset + (sin54 - cos72 - range) * 5, offset - h * 5 + (cos54 + sin72)* 5,offset, offset, paint);
        canvas.drawLine(offset - sin54 * 5, offset - h * 5 + cos54 * 5, offset, offset, paint);

    }
}
