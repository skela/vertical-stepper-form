package ernestoyaquello.com.verticalstepperform;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class VerticalStepperButton
{
    private Button button;
    private ImageButton imageButton;

    public Button getButton()
    {
        return button;
    }

    public ImageButton getImageButton()
    {
        return imageButton;
    }

    public void setButtons(View bottomButton)
    {
        if (bottomButton instanceof Button)
            button = (Button)bottomButton;
        else
            button = null;

        if (bottomButton instanceof ImageButton)
            imageButton = (ImageButton)bottomButton;
        else
            imageButton = null;
    }

    public void setText(@StringRes int resid)
    {
        if (button != null)
            button.setText(resid);
    }

    public void setText(CharSequence text)
    {
        if (button != null)
            button.setText(text);
    }

    public CharSequence getText()
    {
        if (button == null) return null;
        return button.getText();
    }

    public void setEnabled(boolean enabled)
    {
        if (button != null)
            button.setEnabled(enabled);
        if (imageButton != null)
            imageButton.setEnabled(enabled);
    }

    public boolean getEnabled()
    {
        if (button != null)
            return button.isEnabled();
        if (imageButton != null)
            return imageButton.isEnabled();
        return false;
    }

    public void setVisible(boolean visible)
    {
        if (button != null)
        {
            if (visible)
                button.setVisibility(View.VISIBLE);
            else
                button.setVisibility(View.GONE);
        }
        if (imageButton != null)
        {
            if (visible)
                imageButton.setVisibility(View.VISIBLE);
            else
                imageButton.setVisibility(View.GONE);
        }
    }

    public boolean getVisible()
    {
        if (button != null)
            return button.getVisibility() == View.VISIBLE;
        if (imageButton != null)
            return imageButton.getVisibility() == View.VISIBLE;
        return false;
    }

    public void setCompoundDrawablesWithIntrinsicBounds(@DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottomI)
    {
        button.setCompoundDrawablesWithIntrinsicBounds(left,top,right,bottomI);
    }

    public void setOnClickListener(@Nullable View.OnClickListener l)
    {
        if (button != null)
            button.setOnClickListener(l);
        if (imageButton != null)
            imageButton.setOnClickListener(l);
    }

    public void setButtonStyle(VerticalStepperFormLayout.ButtonStyle style)
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
