package ernestoyaquello.com.verticalstepperform;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class VerticalStepperButton
{
    private Button bottom;
    private ImageButton bottomImageButton;

    public Button getBottomButton()
    {
        return bottom;
    }

    public ImageButton getBottomImageButton()
    {
        return bottomImageButton;
    }

    public void setButtons(View bottomButton)
    {
        if (bottomButton instanceof Button)
            bottom = (Button)bottomButton;
        else
            bottom = null;

        if (bottomButton instanceof ImageButton)
            bottomImageButton = (ImageButton)bottomButton;
        else
            bottomImageButton = null;
    }

    public void setText(@StringRes int resid)
    {
        if (bottom != null)
            bottom.setText(resid);
    }

    public void setText(CharSequence text)
    {
        if (bottom != null)
            bottom.setText(text);
    }

    public CharSequence getText()
    {
        if (bottom == null) return null;
        return bottom.getText();
    }

    public void setEnabled(boolean enabled)
    {
        if (bottom != null)
            bottom.setEnabled(enabled);
        if (bottomImageButton != null)
            bottomImageButton.setEnabled(enabled);
    }

    public boolean getEnabled()
    {
        if (bottom != null)
            return bottom.isEnabled();
        if (bottomImageButton != null)
            return bottomImageButton.isEnabled();
        return false;
    }

    public void setVisible(boolean visible)
    {
        if (bottom != null)
        {
            if (visible)
                bottom.setVisibility(View.VISIBLE);
            else
                bottom.setVisibility(View.GONE);
        }
        if (bottomImageButton != null)
        {
            if (visible)
                bottomImageButton.setVisibility(View.VISIBLE);
            else
                bottomImageButton.setVisibility(View.GONE);
        }
    }

    public boolean getVisible()
    {
        if (bottom != null)
            return bottom.getVisibility() == View.VISIBLE;
        if (bottomImageButton != null)
            return bottomImageButton.getVisibility() == View.VISIBLE;
        return false;
    }

    public void setCompoundDrawablesWithIntrinsicBounds(@DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottomI)
    {
        bottom.setCompoundDrawablesWithIntrinsicBounds(left,top,right,bottomI);
    }

    public void setOnClickListener(@Nullable View.OnClickListener l)
    {
        if (bottom != null)
            bottom.setOnClickListener(l);
        if (bottomImageButton != null)
            bottomImageButton.setOnClickListener(l);
    }

    public void setButtonStyle(VerticalStepperFormLayout.ButtonStyle style)
    {
        setButtonStyle(bottom,style);
    }

    private static void setButtonStyle(Button button,VerticalStepperFormLayout.ButtonStyle style)
    {
        if (button == null) return;

        if (style == null)
        {
            button.setVisibility(View.GONE);
            return;
        }

        if (style.padding != null)
        {
            button.setPadding(style.padding.left,style.padding.top,style.padding.right,style.padding.bottom);
        }

        if (style.text.appearance == 0)
            button.setTextColor(style.text.colors);
        else
            VerticalStepperFormLayout.setTextAppearance(button,style.text.appearance);

        if (style.background.resource!=0)
            button.setBackgroundResource(style.background.resource);

        if (style.background.colors!=null)
            button.setBackgroundTintList(style.background.colors);
    }
}
