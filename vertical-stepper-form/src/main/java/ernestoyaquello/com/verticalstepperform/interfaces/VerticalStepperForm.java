package ernestoyaquello.com.verticalstepperform.interfaces;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

public interface VerticalStepperForm
{
    /**
     * The content of the layout of the corresponding step must be generated here. The system will
     * automatically call this method for every step
     * @param stepNumber the number of the step
     * @return The view that will be automatically added as the content of the step
     */
    View createStepContentView(int stepNumber);

    /**
     * An optional step header of the corresponding step can be generated here. The system will
     * automatically call this method for every step
     * @param stepNumber the number of the step
     * @return The view that will be automatically added as the header of the step, or null if no header.
     */
    View createStepHeaderView(int stepNumber);

    void clickedStepButton(int stepNumber, @NonNull String tag);

    /**
     * This method will be called every time a certain step is open
     * @param stepNumber the number of the step
     */
    void onStepOpening(int stepNumber);

    /**
     * This method will be called when the user press the confirmation button
     */
    void sendData();

}
