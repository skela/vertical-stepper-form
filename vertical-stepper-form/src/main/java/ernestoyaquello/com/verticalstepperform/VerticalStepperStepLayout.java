package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VerticalStepperStepLayout extends LinearLayout
{
    public int stepNumber;

    public RelativeLayout stepHeader;
    public ImageView stepDone;
    public TextView stepNumberTextView;
    public LinearLayout buttons;
    public RelativeLayout stepContent;
    public TextView stepTitle;
    public TextView stepSubTitle;
    public LinearLayout circle;
    public LinearLayout accessoryView;

    public LinearLayout titleHeader;

    public VerticalStepperButton nextButton = new VerticalStepperButton();
    public VerticalStepperButton alt1Button = new VerticalStepperButton();
    public VerticalStepperButton alt2Button = new VerticalStepperButton();
    public VerticalStepperButton alt3Button = new VerticalStepperButton();

    public View stepLeftLine0;
    public View stepLeftLine1;
    public View stepLeftLine2;
    public View stepLeftLine3;

    public LinearLayout errorContainer;
    public TextView errorMessage;
    public ImageView errorIcon;

    public RelativeLayout stepTitleHeaderContainer;

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

        stepTitleHeaderContainer = findViewById(R.id.step_title_header_container);
        stepHeader = findViewById(R.id.step_header);
        stepDone = stepHeader.findViewById(R.id.step_done);
        circle = stepHeader.findViewById(R.id.circle);
        stepNumberTextView = stepHeader.findViewById(R.id.step_number);
        buttons = findViewById(R.id.next_step_button_container);
        stepContent = findViewById(R.id.step_content);
        stepTitle = findViewById(R.id.step_title);
        stepSubTitle = findViewById(R.id.step_subtitle);
        titleHeader = findViewById(R.id.step_title_header);

        errorContainer = findViewById(R.id.error_container);
        errorMessage = errorContainer.findViewById(R.id.error_message);
        errorIcon = findViewById(R.id.error_icon);

        nextButton.setButtons(findViewById(R.id.next_step));
        alt1Button.setButtons(findViewById(R.id.alt1_step));
        alt2Button.setButtons(findViewById(R.id.alt2_step));
        alt3Button.setButtons(findViewById(R.id.alt3_step));

        stepLeftLine0 = findViewById(R.id.step_title_vertical_line);
        stepLeftLine1 = findViewById(R.id.vertical_line);
        stepLeftLine2 = findViewById(R.id.vertical_line_subtitle);
        stepLeftLine3 = findViewById(R.id.next_step_button_vertical_line);

        accessoryView = findViewById(R.id.step_accessory);
    }

    public void addButtons(int buttonLayout)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout btns = (LinearLayout) inflater.inflate(buttonLayout, buttons, false);
        buttons.addView(btns);

        nextButton.setButtons(findViewById(R.id.next_step));
        alt1Button.setButtons(findViewById(R.id.alt1_step));
        alt2Button.setButtons(findViewById(R.id.alt2_step));
        alt3Button.setButtons(findViewById(R.id.alt3_step));
    }

    public void addAccessoryViews(int accessoryLayout)
    {
        accessoryView.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout accessories = (LinearLayout) inflater.inflate(accessoryLayout, accessoryView, false);
        List<View> children = new ArrayList<View>();
        for (int i = 0; i < accessories.getChildCount(); i++)
        {
            View v = accessories.getChildAt(i);
            children.add(v);
        }

        while (children.size() > 0)
        {
            View v = children.remove(0);
            accessories.removeView(v);
            accessoryView.addView(v);
        }
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

    public boolean hasVisibleBottomButtons(boolean nextButtonIsOnStep)
    {
        boolean altButtonsVisible = alt1Button.getVisible() || alt2Button.getVisible() || alt3Button.getVisible();
        if (nextButtonIsOnStep)
            return altButtonsVisible;
        return altButtonsVisible || nextButton.getVisible();
    }
}
