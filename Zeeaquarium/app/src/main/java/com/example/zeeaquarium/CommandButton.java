package com.example.zeeaquarium;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class CommandButton {
    private Button button;
    private String command;
    private String popupMessage;
    private Activity activity;

    private BehaviorSubject<String> sendCommandSubject = BehaviorSubject.create();

    public CommandButton(Button button, String command, String popupMessage, Activity activity) {
        this.button = button;
        this.command = command;
        this.popupMessage = popupMessage;
        this.activity = activity;

        this.initialize();
    }

    public Observable<String> sendCommand() {
        return this.sendCommandSubject.hide();
    }

    private void initialize() {
        // popup
        PopupWindow popup = new PopupWindow(this.activity);
        popup.setBackgroundDrawable(this.activity.getDrawable(R.drawable.popup_background));
        popup.setElevation(10);

        // layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(20, 30, 20, 30);

        // popup title
        TextView popupTitle = new TextView(this.activity);
        popupTitle.setText("Let op");
        popupTitle.setTextSize(22);

        // popup text
        TextView popupText = new TextView(this.activity);
        popupText.setText(popupMessage);
        popupText.setTextSize(18);

        // layout
        LinearLayout buttonLayout = createButtonLayout(popup);
        LinearLayout layout = new LinearLayout(this.activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(popupTitle, params2);
        layout.addView(popupText, params2);
        layout.addView(buttonLayout, params);
        popup.setContentView(layout);

        this.button.setOnClickListener((View v) -> {
            popup.showAtLocation(layout, Gravity.CENTER, 0 ,0);
        });
    }

    private LinearLayout createButtonLayout(PopupWindow popup) {
        // layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 20, 20, 30);

        // confirm
        Button confirm = new Button(this.activity);
        confirm.setText("Confirm");
        confirm.setBackground(this.activity.getDrawable(R.drawable.confirm_button));
        confirm.setTextColor(Color.WHITE);
        confirm.setOnClickListener((View v) -> {
            sendCommandSubject.onNext(command);
            popup.dismiss();
        });

        // cancel
        Button cancel = new Button(this.activity);
        cancel.setText("Cancel");
        cancel.setBackground(this.activity.getDrawable(R.drawable.cancel_button));
        cancel.setTextColor(Color.WHITE);
        cancel.setOnClickListener((View v) -> popup.dismiss());

        // layout
        LinearLayout layout = new LinearLayout(this.activity);
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(confirm, params);
        layout.addView(cancel, params);

        return layout;
    }
}
