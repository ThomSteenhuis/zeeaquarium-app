package com.example.zeeaquarium;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.widget.ImageView;
import android.widget.LinearLayout;

import io.reactivex.subjects.BehaviorSubject;

public class Screenshot {
    private ImageView imageView;
    private Activity activity;

    private BehaviorSubject<byte[]> screenshotSubject = BehaviorSubject.create();

    public Screenshot(ImageView imageView, Activity activity){
        this.imageView = imageView;
        this.activity = activity;

        Point size = new Point();
        this.activity.getWindowManager().getDefaultDisplay().getSize(size);
        this.imageView.setLayoutParams(new LinearLayout.LayoutParams(size.x, size.x * 3 / 4));

        this.screenshotSubject.subscribe(
                value -> {
                    Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length);
                    this.activity.runOnUiThread(() -> this.imageView.setImageBitmap(bmp));
                },
                error -> System.out.println("Error: " + error.getMessage())
        );
    }

    public void setScreenshot(byte[] screenshot) {
        this.screenshotSubject.onNext(screenshot);
    }
}
