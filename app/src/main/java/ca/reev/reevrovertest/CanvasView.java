package ca.reev.reevrovertest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import ca.reev.reevrovertest.helper.Utils;
import ca.reev.reevrovertest.model.Point;

public class CanvasView extends View {

    private Context context;
    private Point startPoint;
    private ArrayList<Point> weirs;
    private String command;
    private int numRows = 20, numColumns = 10;
    private int width, height;
    private Bitmap roverBmp;
    private int rotation = 90;
    private int roverX, roverY;
    private int i = 0;
    private boolean validated = false;
    private int roverFX, roverFY;
    private Paint paint;

    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, Point startPoint, ArrayList<Point> weirs, String command, Bitmap roverBmp) {
        super(context);
        this.context = context;
        this.startPoint = startPoint;
        this.weirs = weirs;
        this.command = command;
        this.roverBmp = roverBmp;
        roverFX = startPoint.getX();
        roverFY = startPoint.getY();
        movePlayer0Runnable.run();
    }

    public void setNumberOfRows(int numRows) {
        if (numRows > 0) {
            this.numRows = numRows;
        } else {
            throw new RuntimeException("Cannot have a negative number of rows");
        }
    }

    public void setNumberOfColumns(int numColumns) {
        if (numColumns > 0) {
            this.numColumns = numColumns;
        } else {
            throw new RuntimeException("Cannot have a negative number of columns");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        paint = new Paint();

        DrawLines(canvas);

        SetWeirs(canvas);

        if (i == 0)
            DrawRover(canvas, startPoint.getX(), startPoint.getY());

        MoveRover(canvas, command, startPoint);
    }

    private void DrawLines(Canvas canvas) {
        paint.setColor(Color.BLACK);
        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(width * i / numColumns, 0, width * i / numColumns, height, paint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, height * i / numRows, width, height * i / numRows, paint);
        }
    }

    private void SetWeirs(Canvas canvas) {
        paint.setStrokeWidth(30);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.FILL);

        for (Point weir : weirs)
            SetWeir(canvas, weir.getX(), weir.getY());
    }

    private void SetWeir(Canvas canvas, int x, int y) {
        if (x > numColumns || y > numRows)
            return;
        canvas.drawText("#", width * (x + 1) / numColumns - 3 * width / (4 * numColumns),
                height * (numRows - y) / numRows - height / (4 * numRows), paint);
    }

    private void DrawRover(Canvas canvas, int x, int y) {
        roverX = x;
        roverY = y;
        canvas.drawBitmap(roverBmp, width * (x + 1) / numColumns - 3 * width / (5 * numColumns),
                height * (numRows - (y + 1)) / numRows + height / (4 * numRows), null);
    }

    private void MoveRover(Canvas canvas, String command, final Point startPoint) {
        if (i != 0 && i < command.length()) {
            char c = command.charAt(i - 1);
            if (c == 'L') {
                rotation -= 90;
                roverBmp = Rotate(roverBmp, -90);
                DrawRover(canvas, roverX, roverY);
            } else if (c == 'R') {
                rotation += 90;
                roverBmp = Rotate(roverBmp, 90);
                DrawRover(canvas, roverX, roverY);
            } else if (c == 'M') {
                switch (rotation % 360) {
                    case 0:
                        roverFX = roverX - 1;
                        roverFY = roverY;
                        break;
                    case 90:
                    case -270:
                        roverFY = roverY + 1;
                        roverFX = roverX;
                        break;
                    case -90:
                    case 270:
                        roverFY = roverY - 1;
                        roverFX = roverX;
                        break;
                    case 180:
                        roverFX = roverX + 1;
                        roverFY = roverY;
                        break;
                    case -180:
                        roverFX = roverX + 1;
                        roverFY = roverY;
                        break;
                }

                rotation = rotation % 360;
                validated = Validate();
                if (!validated) {
                    Utils.Vibrate(context);
                    Toast.makeText(context, "block", Toast.LENGTH_SHORT).show();
                    DrawRover(canvas, roverX, roverY);
                } else
                    DrawRover(canvas, roverFX, roverFY);
            }
        }

        i++;
    }

    private boolean Validate() {
        if (weirs.contains(new Point(roverFX, roverFY)) || roverFX < 0 || roverFX > 9 || roverFY < 0 || roverFY > 19) {
            return false;
        } else
            return true;
    }

    private Bitmap Rotate(Bitmap bitmapOrg, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(),
                matrix, true);
    }

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable movePlayer0Runnable = new Runnable() {
        public void run() {
            if (i <= command.length()) {
                invalidate();
                handler.postDelayed(this, 1000);
            }
        }
    };
}
