package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VerticalStepperStepLayout extends LinearLayout
{
    public RelativeLayout stepHeader;
    public ImageView stepDone;
    public TextView stepNumberTextView;
    public LinearLayout buttons;
    public RelativeLayout stepContent;
    public TextView stepTitle;
    public TextView stepSubTitle;
    public LinearLayout circle;

    public Button nextButton;
    public Button alt1Button;
    public Button alt2Button;
    public View alt3Button;

    public LinearLayout stepLeftLine1;
    public LinearLayout stepLeftLine2;
    public LinearLayout stepLeftLine3;

    public LinearLayout errorContainer;
    public TextView errorMessage;
    public ImageView errorIcon;

    public VerticalStepperStepLayout(Context context)
    {
        super(context);
        init(context);
    }

    public VerticalStepperStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public VerticalStepperStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.step_layout, this, true);

        stepHeader = findViewById(R.id.step_header);
        stepDone = stepHeader.findViewById(R.id.step_done);
        circle = stepHeader.findViewById(R.id.circle);
        stepNumberTextView = stepHeader.findViewById(R.id.step_number);
        buttons = findViewById(R.id.next_step_button_container);
        stepContent = findViewById(R.id.step_content);
        stepTitle = findViewById(R.id.step_title);
        stepSubTitle = findViewById(R.id.step_subtitle);

        errorContainer = findViewById(R.id.error_container);
        errorMessage = errorContainer.findViewById(R.id.error_message);
        errorIcon = findViewById(R.id.error_icon);

        nextButton = findViewById(R.id.next_step);
        alt1Button = findViewById(R.id.alt1_step);
        alt2Button = findViewById(R.id.alt2_step);
        alt3Button = findViewById(R.id.alt3_step);

        stepLeftLine1 = findViewById(R.id.vertical_line);
        stepLeftLine2 = findViewById(R.id.vertical_line_subtitle);
        stepLeftLine3 = findViewById(R.id.next_step_button_vertical_line);
    }

    public void addButtons(Context context,int buttonLayout)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout btns = (LinearLayout) inflater.inflate(buttonLayout, buttons, false);
        buttons.addView(btns);

        nextButton = findViewById(R.id.next_step);
        alt1Button = findViewById(R.id.alt1_step);
        alt2Button = findViewById(R.id.alt2_step);
        alt3Button = findViewById(R.id.alt3_step);
    }

    public void setStepCircleBackgroundColor(int color, int resourceId)
    {
        Drawable bg = ContextCompat.getDrawable(getContext(), resourceId);
        bg.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        setStepCircleBackgroundColor(bg);
    }

    public void setStepCircleBackgroundColor(Drawable bg)
    {
        circle.setBackground(bg);
    }

    public void setStepCircleSize(int size)
    {
        RelativeLayout.LayoutParams circleParams = (RelativeLayout.LayoutParams)circle.getLayoutParams();
        circleParams.width = size;
        circleParams.height = size;
        circle.setLayoutParams(circleParams);

        circleParams = (RelativeLayout.LayoutParams)stepDone.getLayoutParams();
        circleParams.width = size;
        circleParams.height = size;
        stepDone.setLayoutParams(circleParams);
    }
}