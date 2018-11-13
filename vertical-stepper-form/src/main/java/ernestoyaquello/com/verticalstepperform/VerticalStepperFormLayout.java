package ernestoyaquello.com.verticalstepperform;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import ernestoyaquello.com.verticalstepperform.utils.Animations;

/**
 * Custom layout that implements a vertical stepper form
 */

public class VerticalStepperFormLayout extends RelativeLayout implements View.OnClickListener {

    public class VerticalStepperStyle
    {
        public int enabled;
        public int disabled;
        public int completed;

        public VerticalStepperStyle(int enabled)
        {
            this.enabled = enabled;
            this.disabled = enabled;
            this.completed = enabled;
        }

        public VerticalStepperStyle(int enabled,int disabled)
        {
            this.enabled = enabled;
            this.disabled = disabled;
            this.completed = enabled;
        }

        public VerticalStepperStyle(int enabled,int disabled,int completed)
        {
            this.enabled = enabled;
            this.disabled = disabled;
            this.completed = completed;
        }

        public int value(boolean isEnabled,boolean isComplete)
        {
            if (isEnabled) return enabled;
            if (isComplete) return completed;
            return disabled;
        }
    }

    public class ButtonColors
    {
        public ColorStateList colors;

        public void setColor(int normal,int pressed)
        {
            setColor(normal,normal,pressed);
        }

        public void setColor(int normal,int disabled,int pressed)
        {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_pressed},
                    new int[]{android.R.attr.state_focused},
                    new int[]{-android.R.attr.state_enabled},
                    new int[]{}
            };
            colors = new ColorStateList(
                    states,
                    new int[]{
                            pressed,
                            pressed,
                            disabled,
                            normal
                    });
        }
    }

    public class ButtonBackground extends ButtonColors
    {
        public int resource;
    }

    public class ButtonTextAppearance extends ButtonColors
    {
        public int appearance;
    }

    public class ButtonStyle
    {
        public ButtonTextAppearance text = new ButtonTextAppearance();
        public ButtonBackground background = new ButtonBackground();
        public Padding padding;
    }

    public class Padding
    {
        public int left;
        public int top;
        public int right;
        public int bottom;

        public Padding(int left,int right)
        {
            this.left = left;
            this.right = right;
            this.top = 0;
            this.bottom = 0;
        }

        public Padding(int left,int top,int right,int bottom)
        {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }
    }

    // Style
    protected float alphaOfDisabledElements;
    protected int stepNumberBackgroundColor;
    protected int stepTitleTextColor;
    protected int stepSubtitleTextColor;
    protected int errorMessageTextColor;
    protected boolean displayBottomNavigation;
    protected boolean materialDesignInDisabledSteps;
    protected boolean hideKeyboard;
    protected boolean showVerticalLineWhenStepsAreCollapsed;

    // Extended Style
    protected VerticalStepperStyle circleSize;
    protected VerticalStepperStyle stepNumberTextColor = new VerticalStepperStyle(Color.rgb(255, 255, 255),Color.rgb(255, 255, 255));
    protected VerticalStepperStyle circleResourceId = new VerticalStepperStyle(R.drawable.circle_step_done,R.drawable.circle_step_done);
    protected VerticalStepperStyle circleBackgroundColor = new VerticalStepperStyle(0,Color.rgb(176, 176, 176));
    protected ButtonStyle buttonStyle = new ButtonStyle();
    protected ButtonStyle alt1ButtonStyle;
    protected ButtonStyle alt2ButtonStyle;
    protected ButtonStyle alt3ButtonStyle;
    protected VerticalStepperStyle stepTitleAppearance = new VerticalStepperStyle(0,0,0);
    protected VerticalStepperStyle stepSubtitleAppearance = new VerticalStepperStyle(0,0,0);
    protected int verticalLineColor = 0;
    protected int doneIcon = 0;

    protected int getButtonLayoutId()
    {
        return R.layout.step_layout_buttons;
    }

    // Views
    protected LayoutInflater mInflater;
    protected LinearLayout content;
    protected ScrollView stepsScrollView;
    protected List<LinearLayout> stepLayouts;
    protected List<View> stepContentViews;
    protected List<TextView> stepsTitlesViews;
    protected List<TextView> stepsSubtitlesViews;
    protected Button confirmationButton;
    protected ProgressBar progressBar;
    protected AppCompatImageButton previousStepButton, nextStepButton;
    protected RelativeLayout bottomNavigation;

    // Data
    protected List<String> steps;
    protected List<String> stepsSubtitles;

    // Logic
    protected int activeStep = -1;
    protected int numberOfSteps;
    protected boolean[] completedSteps;

    public boolean shouldAddConfirmationStep = true;
    public boolean startOnStep = true;
    public boolean explorable = false;

    // Listeners and callbacks
    protected VerticalStepperForm verticalStepperFormImplementation;

    // Context
    protected Context context;
    protected Activity activity;

    public VerticalStepperFormLayout(Context context) {
        super(context);
        init(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context)
    {
        this.context = context;
        circleSize = new VerticalStepperStyle(context.getResources().getDimensionPixelSize(R.dimen.vertical_stepper_circle_size));
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.vertical_stepper_form_layout, this, true);
    }

    public void addHeader(View header)
    {
        content.addView(header);
    }

    public void addFooter(View header)
    {
        content.addView(header);
    }

    public void clear()
    {
        content.removeAllViews();
    }

    /**
     * Returns the title of a step
     * @param stepNumber The step number (counting from 0)
     * @return the title string
     */
    public String getStepTitle(int stepNumber) {
        return steps.get(stepNumber);
    }

    /**
     * Returns the subtitle of a step
     * @param stepNumber The step number (counting from 0)
     * @return the subtitle string
     */
    public String getStepsSubtitles(int stepNumber) {
        if(stepsSubtitles != null) {
            return stepsSubtitles.get(stepNumber);
        }
        return null;
    }

    /**
     * Returns the active step number
     * @return the active step number (counting from 0)
     */
    public int getActiveStepNumber() {
        return activeStep;
    }

    /**
     * Set the title of certain step
     * @param stepNumber The step number (counting from 0)
     * @param title New title of the step
     */
    public void setStepTitle(int stepNumber, String title) {
        if(title != null && !title.equals("")) {
            steps.set(stepNumber, title);
            TextView titleView = stepsTitlesViews.get(stepNumber);
            if (titleView != null) {
                titleView.setText(title);
            }
        }
    }

    /**
     * Set the subtitle of certain step
     * @param stepNumber The step number (counting from 0)
     * @param subtitle New subtitle of the step
     */
    public void setStepSubtitle(int stepNumber, String subtitle) {
        if(stepsSubtitles != null && subtitle != null && !subtitle.equals("")) {
            stepsSubtitles.set(stepNumber, subtitle);
            TextView subtitleView = stepsSubtitlesViews.get(stepNumber);
            if (subtitleView != null) {
                subtitleView.setText(subtitle);
            }
        }
    }

    /**
     * Set the active step as completed
     */
    public void setActiveStepAsCompleted() {
        setStepAsCompleted(activeStep);
    }

    /**
     * Set the active step as not completed
     * @param errorMessage Error message that will be displayed (null for no message)
     */
    public void setActiveStepAsUncompleted(String errorMessage) {
        setStepAsUncompleted(activeStep, errorMessage);
    }

    /**
     * Set the step as completed
     * @param stepNumber the step number (counting from 0)
     */
    public void setStepAsCompleted(int stepNumber) {
        completedSteps[stepNumber] = true;

        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        LinearLayout errorContainer = (LinearLayout) stepLayout.findViewById(R.id.error_container);
        TextView errorTextView = (TextView) errorContainer.findViewById(R.id.error_message);
        Button nextButton = (Button) stepLayout.findViewById(R.id.next_step);

        enableStepHeader(stepLayout);

        updateButton(stepNumber);
        nextButton.setEnabled(true);

        if (stepNumber != activeStep) {
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.INVISIBLE);
        } else {
            if (stepNumber != numberOfSteps) {
                enableNextButtonInBottomNavigationLayout();
            } else {
                disableNextButtonInBottomNavigationLayout();
            }
        }

        errorTextView.setText("");
        //errorContainer.setVisibility(View.GONE);
        Animations.slideUp(errorContainer);

        displayCurrentProgress();
    }

    /**
     * Set the step as not completed
     * @param stepNumber the step number (counting from 0)
     * @param errorMessage Error message that will be displayed (null for no message)
     */
    public void setStepAsUncompleted(int stepNumber, String errorMessage)
    {
        if (stepNumber >= completedSteps.length - 1)
            return;

        completedSteps[stepNumber] = false;

        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        Button nextButton = (Button) stepLayout.findViewById(R.id.next_step);

        stepDone.setVisibility(View.INVISIBLE);
        stepNumberTextView.setVisibility(View.VISIBLE);

        updateButton(stepNumber);
        nextButton.setEnabled(false);

        if (stepNumber == activeStep) {
            disableNextButtonInBottomNavigationLayout();
        } else {
            disableStepHeader(stepLayout);
        }

        if (stepNumber < numberOfSteps) {
            setStepAsUncompleted(numberOfSteps, null);
        }

        if (errorMessage != null && !errorMessage.equals("")) {
            LinearLayout errorContainer = (LinearLayout) stepLayout.findViewById(R.id.error_container);
            TextView errorTextView = (TextView) errorContainer.findViewById(R.id.error_message);

            errorTextView.setText(errorMessage);
            //errorContainer.setVisibility(View.VISIBLE);
            Animations.slideDown(errorContainer);
        }

        displayCurrentProgress();
    }

    /**
     * Determines whether the active step is completed or not
     * @return true if the active step is completed; false otherwise
     */
    public boolean isActiveStepCompleted() {
        return isStepCompleted(activeStep);
    }

    /**
     * Determines whether the given step is completed or not
     * @param stepNumber the step number (counting from 0)
     * @return true if the step is completed, false otherwise
     */
    public boolean isStepCompleted(int stepNumber) {
        return completedSteps[stepNumber];
    }

    /**
     * Determines if any step has been completed
     * @return true if at least 1 step has been completed; false otherwise
     */
    public boolean isAnyStepCompleted() {
        for (boolean completedStep : completedSteps) {
            if (completedStep) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the steps that are previous to the given one are completed
     * @param stepNumber the selected step number (counting from 0)
     * @return true if all the previous steps have been completed; false otherwise
     */
    public boolean arePreviousStepsCompleted(int stepNumber) {
        boolean previousStepsAreCompleted = true;
        for (int i = (stepNumber - 1); i >= 0 && previousStepsAreCompleted; i--) {
            previousStepsAreCompleted = completedSteps[i];
        }
        return previousStepsAreCompleted;
    }

    /**
     * Go to the next step
     */
    public void goToNextStep() {
        goToStep(activeStep + 1, false);
    }

    /**
     * Go to the previous step
     */
    public void goToPreviousStep() {
        goToStep(activeStep - 1, false);
    }

    /**
     * Go to the selected step
     * @param stepNumber the selected step number (counting from 0)
     * @param restoration true if the method has been called to restore the form; false otherwise
     */
    public boolean goToStep(int stepNumber, boolean restoration)
    {
        if (activeStep != stepNumber || restoration) {
            if(hideKeyboard) {
                hideSoftKeyboard();
            }
            boolean previousStepsAreCompleted =
                    arePreviousStepsCompleted(stepNumber);
            if (stepNumber == 0 || previousStepsAreCompleted || explorable) {
                return openStep(stepNumber, restoration);
            }
        }
        else if (activeStep == stepNumber)
        {
            closeStep(stepNumber);
        }
        return false;
    }

    /**
     * Set the active step as not completed
     * @deprecated use {@link #setActiveStepAsUncompleted(String)} instead
     */
    @Deprecated
    public void setActiveStepAsUncompleted() {
        setStepAsUncompleted(activeStep, null);
    }

    /**
     * Set the selected step as not completed
     * @param stepNumber the step number (counting from 0)
     * @deprecated use {@link #setStepAsUncompleted(int, String)} instead
     */
    @Deprecated
    public void setStepAsUncompleted(int stepNumber) {
        setStepAsUncompleted(stepNumber, null);
    }

    /**
     * Set up and initialize the form
     * @param stepsTitles names of the steps
     * @param colorPrimary primary color
     * @param colorPrimaryDark primary color (dark)
     * @param verticalStepperForm instance that implements the interface "VerticalStepperForm"
     * @param activity activity where the form is
     *
     * @deprecated use {@link Builder#newInstance(VerticalStepperFormLayout, String[], VerticalStepperForm, Activity)} instead like this:
     * <blockquote><pre>
     * VerticalStepperFormLayout.Builder.newInstance(verticalStepperFormLayout, stepsTitles, verticalStepperForm, activity)<br>
     *     .primaryColor(colorPrimary)<br>
     *     .primaryDarkColor(colorPrimaryDark)<br>
     *     .init();
     * </pre></blockquote>
     */
    @Deprecated
    public void initialiseVerticalStepperForm(String[] stepsTitles,
                                              int colorPrimary, int colorPrimaryDark,
                                              VerticalStepperForm verticalStepperForm,
                                              Activity activity) {

        this.alphaOfDisabledElements = 0.25f;
        this.buttonStyle.text.setColor(Color.rgb(255, 255, 255),Color.rgb(255, 255, 255));
        this.stepTitleTextColor = Color.rgb(33, 33, 33);
        this.stepSubtitleTextColor = Color.rgb(162, 162, 162);
        this.stepNumberBackgroundColor = colorPrimary;
        this.circleBackgroundColor.enabled = colorPrimary;
        this.buttonStyle.background.setColor(colorPrimary,colorPrimaryDark);
        this.errorMessageTextColor = Color.rgb(175, 18, 18);
        this.displayBottomNavigation = true;
        this.materialDesignInDisabledSteps = false;
        this.hideKeyboard = true;
        this.showVerticalLineWhenStepsAreCollapsed = false;

        this.verticalStepperFormImplementation = verticalStepperForm;
        this.activity = activity;

        initStepperForm(stepsTitles, null,null);
    }

    /**
     * Set up and initialize the form
     * @param stepsTitles names of the steps
     * @param buttonBackgroundColor background colour of the buttons
     * @param buttonTextColor text color of the buttons
     * @param buttonPressedBackgroundColor background color of the buttons when clicked
     * @param buttonPressedTextColor text color of the buttons when clicked
     * @param stepNumberBackgroundColor background color of the left circles
     * @param stepNumberTextColor text color of the left circles
     * @param verticalStepperForm instance that implements the interface "VerticalStepperForm"
     * @param activity activity where the form is
     *
     * @deprecated use {@link Builder#newInstance(VerticalStepperFormLayout, String[], VerticalStepperForm, Activity)} instead like this:
     * <blockquote><pre>
     * VerticalStepperFormLayout.Builder.newInstance(verticalStepperFormLayout, stepsTitles, verticalStepperForm, activity)<br>
     *     .buttonBackgroundColor(buttonBackgroundColor)<br>
     *     .buttonTextColor(buttonTextColor)<br>
     *     .buttonPressedBackgroundColor(buttonPressedBackgroundColor)<br>
     *     .buttonPressedTextColor(buttonPressedTextColor)<br>
     *     .stepNumberBackgroundColor(stepNumberBackgroundColor)<br>
     *     .stepNumberTextColor(stepNumberTextColor)<br>
     *     .init();
     * </pre></blockquote>
     */
    @Deprecated
    public void initialiseVerticalStepperForm(String[] stepsTitles,
                                              int buttonBackgroundColor, int buttonTextColor,
                                              int buttonPressedBackgroundColor, int buttonPressedTextColor,
                                              int stepNumberBackgroundColor, int stepNumberTextColor,
                                              VerticalStepperForm verticalStepperForm,
                                              Activity activity) {

        this.alphaOfDisabledElements = 0.25f;
        this.circleBackgroundColor.enabled = buttonBackgroundColor;
        this.buttonStyle.text.setColor(buttonTextColor,buttonPressedTextColor);
        this.buttonStyle.background.setColor(buttonBackgroundColor,buttonPressedBackgroundColor);
        this.stepNumberBackgroundColor = stepNumberBackgroundColor;
        this.stepTitleTextColor = Color.rgb(33, 33, 33);
        this.stepSubtitleTextColor = Color.rgb(162, 162, 162);
        this.stepNumberTextColor.enabled = stepNumberTextColor;
        this.stepNumberTextColor.disabled = stepNumberTextColor;
        this.errorMessageTextColor = Color.rgb(175, 18, 18);
        this.displayBottomNavigation = true;
        this.materialDesignInDisabledSteps = false;
        this.hideKeyboard = true;
        this.showVerticalLineWhenStepsAreCollapsed = false;

        this.verticalStepperFormImplementation = verticalStepperForm;
        this.activity = activity;

        initStepperForm(stepsTitles, null,null);
    }

    protected void initialiseVerticalStepperForm(Builder builder) {

        this.verticalStepperFormImplementation = builder.verticalStepperFormImplementation;
        this.activity = builder.activity;

        this.alphaOfDisabledElements = builder.alphaOfDisabledElements;
        this.stepNumberBackgroundColor = builder.stepNumberBackgroundColor;
        if (this.buttonStyle.background.colors == null)
            this.buttonStyle.background.setColor(builder.buttonBackgroundColor,builder.buttonPressedBackgroundColor);
        this.stepTitleTextColor = builder.stepTitleTextColor;
        this.stepSubtitleTextColor = builder.stepSubtitleTextColor;
        this.buttonStyle.text.setColor(builder.buttonTextColor,builder.buttonPressedTextColor);
        this.errorMessageTextColor = builder.errorMessageTextColor;
        this.displayBottomNavigation = builder.displayBottomNavigation;
        this.materialDesignInDisabledSteps = builder.materialDesignInDisabledSteps;
        this.hideKeyboard = builder.hideKeyboard;
        this.showVerticalLineWhenStepsAreCollapsed = builder.showVerticalLineWhenStepsAreCollapsed;

        initStepperForm(builder.steps, builder.stepsSubtitles,builder.stepsCompleted);
    }

    public View getStepView(int step)
    {
        if (step >= stepLayouts.size())
            return null;
        return stepContentViews.get(step);
    }

    protected void initStepperForm(String[] stepsTitles, String[] stepsSubtitles,Boolean[] stepsCompleted)
    {
        setSteps(stepsTitles, stepsSubtitles);
        setCompletedSteps(stepsCompleted);

        List<View> stepContentLayouts = new ArrayList<>();
        for (int i = 0; i < numberOfSteps; i++) {
            View stepLayout = verticalStepperFormImplementation.createStepContentView(i);
            stepContentLayouts.add(stepLayout);
        }
        stepContentViews = stepContentLayouts;

        initializeForm();

        if (startOnStep)
        {
            verticalStepperFormImplementation.onStepOpening(activeStep);

            updateButton(activeStep);
        }
        else
        {
            for (int i = 0; i < numberOfSteps; i++)
            {
                disableStepLayout(i,false);
                LinearLayout stepLayout = stepLayouts.get(i);
                if (arePreviousStepsCompleted(i))
                {
                    if (activeStep == i)
                        enableStepHeader(stepLayout);
                    else if (isStepCompleted(i))
                        completeStepHeader(stepLayout);
                    else
                        disableStepHeader(stepLayout);
                }
            }
        }
    }

    private void updateButton(int step)
    {
        if (step >= stepLayouts.size())
            return;
        LinearLayout stepLayout = stepLayouts.get(step);
        updateNextButton((Button)stepLayout.findViewById(R.id.next_step),step);
        updateAlt1Button((Button)stepLayout.findViewById(R.id.alt1_step),step);
        updateAlt2Button((Button)stepLayout.findViewById(R.id.alt2_step),step);
        updateAlt3Button(stepLayout.findViewById(R.id.alt3_step),step);
    }

    protected void updateNextButton(Button button,int step)
    {
        button.setEnabled(arePreviousStepsCompleted(step));
    }

    protected void updateAlt1Button(Button button,int step)
    {

    }

    protected void updateAlt2Button(Button button,int step)
    {

    }

    protected void updateAlt3Button(View button,int step)
    {

    }

    protected void setSteps(String[] steps, String[] stepsSubtitles) {
        this.steps = new ArrayList<>(Arrays.asList(steps));
        if(stepsSubtitles != null) {
            this.stepsSubtitles = new ArrayList<>(Arrays.asList(stepsSubtitles));
        } else {
            this.stepsSubtitles = null;
        }
        numberOfSteps = steps.length;
        setAuxVars();
        addConfirmationStepToStepsList();
    }

    protected void setCompletedSteps(Boolean[] steps)
    {
        int active = activeStep;
        int i = 0;
        for (boolean step : steps)
        {
            completedSteps[i] = step;
            i++;
            if (step)
                active = i;
        }
        if (startOnStep)
            activeStep = active;
    }

    protected void registerListeners() {
        previousStepButton.setOnClickListener(this);
        nextStepButton.setOnClickListener(this);
    }

    protected void initializeForm() {
        stepsTitlesViews = new ArrayList<>();
        stepsSubtitlesViews = new ArrayList<>();
        setUpSteps();
        if (!displayBottomNavigation) {
            hideBottomNavigation();
        }
        if (startOnStep)
            goToStep(activeStep, true);

        setObserverForKeyboard();
    }

    // http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
    protected void setObserverForKeyboard() {
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                content.getWindowVisibleDisplayFrame(r);

                int heightDiff = content.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) { // if more than 100 pixels, it is probably a keyboard...
                    scrollToActiveStep(true);
                }
            }
        });
    }

    protected void hideBottomNavigation() {
        bottomNavigation.setVisibility(View.GONE);
    }

    protected void setUpSteps() {
        stepLayouts = new ArrayList<>();
        // Set up normal steps
        for (int i = 0; i < numberOfSteps; i++) {
            setUpStep(i);
        }

        if (shouldAddConfirmationStep)
        {
            // Set up confirmation step
            setUpStep(numberOfSteps);
        }
    }

    protected void setUpStep(int stepNumber) {
        LinearLayout stepLayout = createStepLayout(stepNumber);
        if (stepNumber < numberOfSteps) {
            // The content of the step is the corresponding custom view previously created
            RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
            stepContent.addView(stepContentViews.get(stepNumber));
        } else {
            setUpStepLayoutAsConfirmationStepLayout(stepLayout);
        }
        addStepToContent(stepLayout);
    }

    protected void addStepToContent(LinearLayout stepLayout) {
        content.addView(stepLayout);
    }

    protected void setUpStepLayoutAsConfirmationStepLayout(LinearLayout stepLayout)
    {
        if (!shouldAddConfirmationStep) return;

        LinearLayout stepLeftLine = (LinearLayout) stepLayout.findViewById(R.id.vertical_line);
        LinearLayout stepLeftLine2 = (LinearLayout) stepLayout.findViewById(R.id.vertical_line_subtitle);
        confirmationButton = (Button) stepLayout.findViewById(R.id.next_step);

        stepLeftLine.setVisibility(View.INVISIBLE);
        stepLeftLine2.setVisibility(View.INVISIBLE);

        disableConfirmationButton();

        confirmationButton.setText(R.string.vertical_form_stepper_form_confirm_button);
        confirmationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareSendingAndSend();
            }
        });
        updateNextButton(confirmationButton,numberOfSteps);
        updateAlt1Button((Button) stepLayout.findViewById(R.id.alt1_step),numberOfSteps);
        updateAlt2Button((Button) stepLayout.findViewById(R.id.alt2_step),numberOfSteps);

        // Some content could be added to the final step inside stepContent layout
        // RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
    }

    protected LinearLayout createStepLayout(final int stepNumber)
    {
        LinearLayout stepLayout = generateStepLayout();

        LinearLayout circle = (LinearLayout) stepLayout.findViewById(R.id.circle);
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.circle_step_done);
        bg.setColorFilter(new PorterDuffColorFilter(
                stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        circle.setBackground(bg);

        TextView stepTitle = (TextView) stepLayout.findViewById(R.id.step_title);
        stepTitle.setText(steps.get(stepNumber));
        stepTitle.setTextColor(stepTitleTextColor);
        if (stepTitleAppearance.disabled!=0)
            setTextAppearance(stepTitle,stepTitleAppearance.disabled);
        stepsTitlesViews.add(stepNumber, stepTitle);

        TextView stepSubtitle = null;
        if(stepsSubtitles != null && stepNumber < stepsSubtitles.size()) {
            String subtitle = stepsSubtitles.get(stepNumber);
            if(subtitle != null && !subtitle.equals("")) {
                stepSubtitle = (TextView) stepLayout.findViewById(R.id.step_subtitle);
                stepSubtitle.setText(subtitle);
                stepSubtitle.setTextColor(stepSubtitleTextColor);
                if (stepSubtitleAppearance.disabled!=0)
                    setTextAppearance(stepSubtitle,stepSubtitleAppearance.disabled);
                stepSubtitle.setVisibility(View.VISIBLE);
            }
        }
        stepsSubtitlesViews.add(stepNumber, stepSubtitle);

        TextView stepNumberTextView = (TextView) stepLayout.findViewById(R.id.step_number);
        stepNumberTextView.setText(String.valueOf(stepNumber + 1));
        stepNumberTextView.setTextColor(stepNumberTextColor.enabled);

        ImageView stepDoneImageView = (ImageView) stepLayout.findViewById(R.id.step_done);
        if (doneIcon != 0)
            stepDoneImageView.setImageResource(doneIcon);
        stepDoneImageView.setColorFilter(stepNumberTextColor.enabled);

        TextView errorMessage = (TextView) stepLayout.findViewById(R.id.error_message);
        ImageView errorIcon = (ImageView) stepLayout.findViewById(R.id.error_icon);
        errorMessage.setTextColor(errorMessageTextColor);
        errorIcon.setColorFilter(errorMessageTextColor);

        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (!goToStep(stepNumber, false))
                    closeStep(stepNumber);
            }
        });

        LinearLayout btnsContainer = stepLayout.findViewById(R.id.next_step_button_container);

        int layoutButtons = getButtonLayoutId();

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout btns = (LinearLayout) inflater.inflate(layoutButtons, btnsContainer, false);
        btnsContainer.addView(btns);

        Button nextButton = (Button) stepLayout.findViewById(R.id.next_step);
        Button alt1Button = (Button) stepLayout.findViewById(R.id.alt1_step);
        Button alt2Button = (Button) stepLayout.findViewById(R.id.alt2_step);
        View alt3Button = stepLayout.findViewById(R.id.alt3_step);

        if (layoutButtons == R.layout.step_layout_buttons)
        {
            setButtonStyle(nextButton, buttonStyle);
            setButtonStyle(alt1Button, alt1ButtonStyle);
            setButtonStyle(alt2Button, alt2ButtonStyle);
            if (alt3Button instanceof Button)
                setButtonStyle((Button)alt3Button, alt3ButtonStyle);
        }

        nextButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedNext(stepNumber);
            }
        });

        alt1Button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedAlt1(stepNumber);
            }
        });

        alt2Button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedAlt2(stepNumber);
            }
        });

        alt3Button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedAlt3(stepNumber);
            }
        });

        int lineColor = verticalLineColor;
        boolean lastStep = !shouldAddConfirmationStep && stepNumber == numberOfSteps - 1;
        if (lastStep)
            lineColor = Color.TRANSPARENT;

        if (lineColor!=0 || lastStep)
        {
            LinearLayout stepLeftLine = (LinearLayout) stepLayout.findViewById(R.id.vertical_line);
            LinearLayout stepLeftLine2 = (LinearLayout) stepLayout.findViewById(R.id.vertical_line_subtitle);
            LinearLayout stepLeftLine3 = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_vertical_line);

            stepLeftLine.setBackgroundColor(lineColor);
            stepLeftLine2.setBackgroundColor(lineColor);
            stepLeftLine3.setBackgroundColor(lineColor);
        }

        stepLayouts.add(stepLayout);

        return stepLayout;
    }

    protected void clickedNext(int step)
    {
        verticalStepperFormImplementation.clickedNext(step);
    }

    protected void clickedAlt1(int step)
    {
        verticalStepperFormImplementation.clickedAlt1(step);
    }

    protected void clickedAlt2(int step)
    {
        verticalStepperFormImplementation.clickedAlt2(step);
    }

    protected void clickedAlt3(int step)
    {
        verticalStepperFormImplementation.clickedAlt3(step);
    }

    static private void setTextAppearance(TextView tv,int appearanceResourceId)
    {
        if (appearanceResourceId == 0) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            tv.setTextAppearance(appearanceResourceId);
        else
            tv.setTextAppearance(tv.getContext(), appearanceResourceId);
    }

    protected LinearLayout generateStepLayout() {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (LinearLayout) inflater.inflate(R.layout.step_layout, content, false);
    }

    protected boolean openStep(int stepNumber, boolean restoration) {
        if (stepNumber >= 0 && stepNumber <= numberOfSteps) {
            activeStep = stepNumber;

            if (stepNumber == 0) {
                disablePreviousButtonInBottomNavigationLayout();
            } else {
                enablePreviousButtonInBottomNavigationLayout();
            }

            if (completedSteps[stepNumber] && activeStep != numberOfSteps) {
                enableNextButtonInBottomNavigationLayout();
            } else {
                disableNextButtonInBottomNavigationLayout();
            }

            int stepCount = numberOfSteps;
            if (!shouldAddConfirmationStep)
                stepCount -= 1;

            for(int i = 0; i <= stepCount; i++) {
                if(i != stepNumber) {
                    disableStepLayout(i, !restoration);
                } else {
                    enableStepLayout(i, !restoration);
                }
            }

            scrollToActiveStep(!restoration);

            if (stepNumber == numberOfSteps && shouldAddConfirmationStep) {
                setStepAsCompleted(stepNumber);
            }

            verticalStepperFormImplementation.onStepOpening(stepNumber);
            updateButton(stepNumber);
            return true;
        }
        return false;
    }

    public void closeStep(int stepNumber)
    {
        if (stepNumber >= 0 && stepNumber <= numberOfSteps)
        {
            boolean canClose = stepNumber == 0 || arePreviousStepsCompleted(stepNumber) || explorable;
            if (!canClose) return;

            disableStepLayout(stepNumber, true);
            LinearLayout stepLayout = stepLayouts.get(stepNumber);
            if (isStepCompleted(stepNumber))
                completeStepHeader(stepLayout);
            else
                enableStepHeader(stepLayout);
        }
        activeStep = -1;
    }

    protected void scrollToStep(final int stepNumber, boolean smoothScroll) {

        if (stepNumber >= stepLayouts.size())
            return;
        if (stepNumber == -1)
            return;

        if (smoothScroll) {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.smoothScrollTo(0, stepLayouts.get(stepNumber).getTop());
                }
            });
        } else {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.scrollTo(0, stepLayouts.get(stepNumber).getTop());
                }
            });
        }
    }

    protected void scrollToActiveStep(boolean smoothScroll) {
        scrollToStep(activeStep, smoothScroll);
    }

    protected void findViews() {
        content = (LinearLayout) findViewById(R.id.content);
        stepsScrollView = (ScrollView) findViewById(R.id.steps_scroll);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        previousStepButton = (AppCompatImageButton) findViewById(R.id.down_previous);
        nextStepButton = (AppCompatImageButton) findViewById(R.id.down_next);
        bottomNavigation = (RelativeLayout) findViewById(R.id.bottom_navigation);
    }

    protected void disableStepLayout(int stepNumber, boolean smoothieDisabling) {
        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        TextView stepTitle = (TextView) stepLayout.findViewById(R.id.step_title);
        TextView stepSubTitle = (TextView) stepLayout.findViewById(R.id.step_subtitle);

        if (smoothieDisabling) {
            Animations.slideUp(button);
            Animations.slideUp(stepContent);
        } else {
            button.setVisibility(View.GONE);
            stepContent.setVisibility(View.GONE);
        }

        if (!completedSteps[stepNumber])
        {
            disableStepHeader(stepLayout);
            stepDone.setVisibility(View.INVISIBLE);
            stepNumberTextView.setVisibility(View.VISIBLE);
            setTextAppearance(stepTitle,stepTitleAppearance.disabled);
            setTextAppearance(stepSubTitle,stepSubtitleAppearance.disabled);
        }
        else
        {
            completeStepHeader(stepLayout);
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.INVISIBLE);
            setTextAppearance(stepTitle,stepTitleAppearance.completed);
            setTextAppearance(stepSubTitle,stepSubtitleAppearance.completed);
        }

        showVerticalLineInCollapsedStepIfNecessary(stepLayout);

    }

    protected void enableStepLayout(int stepNumber, boolean smoothieEnabling) {
        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        TextView stepTitle = (TextView) stepLayout.findViewById(R.id.step_title);
        TextView stepSubTitle = (TextView) stepLayout.findViewById(R.id.step_subtitle);

        enableStepHeader(stepLayout);

        if (smoothieEnabling) {
            Animations.slideDown(stepContent);
            Animations.slideDown(button);
        } else {
            stepContent.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }

        if (completedSteps[stepNumber] && activeStep != stepNumber)
        {
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.INVISIBLE);
            setTextAppearance(stepTitle,stepTitleAppearance.completed);
            setTextAppearance(stepSubTitle,stepSubtitleAppearance.completed);
        }
        else
        {
            stepDone.setVisibility(View.INVISIBLE);
            stepNumberTextView.setVisibility(View.VISIBLE);
            setTextAppearance(stepTitle,stepTitleAppearance.enabled);
            setTextAppearance(stepSubTitle,stepSubtitleAppearance.enabled);
        }

        hideVerticalLineInCollapsedStepIfNecessary(stepLayout);
    }

    protected void completeStepHeader(LinearLayout stepLayout)
    {
        setHeaderAppearance(stepLayout, 1, circleBackgroundColor.completed,circleResourceId.completed,false,true);
    }

    protected void enableStepHeader(LinearLayout stepLayout)
    {
        setHeaderAppearance(stepLayout, 1, circleBackgroundColor.enabled,circleResourceId.enabled,true,false);
    }

    protected void disableStepHeader(LinearLayout stepLayout)
    {
        setHeaderAppearance(stepLayout, alphaOfDisabledElements, circleBackgroundColor.disabled,circleResourceId.disabled,false,false);
    }

    protected void showVerticalLineInCollapsedStepIfNecessary(LinearLayout stepLayout) {
        // The height of the line will be 16dp when the subtitle textview is gone
        if(showVerticalLineWhenStepsAreCollapsed) {
            setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(stepLayout, 16);
        }
    }

    protected void hideVerticalLineInCollapsedStepIfNecessary(LinearLayout stepLayout) {
        // The height of the line will be 0 when the subtitle text is being shown
        if(showVerticalLineWhenStepsAreCollapsed) {
            setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(stepLayout, 0);
        }
    }

    protected void displayCurrentProgress() {
        int progress = 0;
        for (int i = 0; i < (completedSteps.length - 1); i++) {
            if (completedSteps[i]) {
                ++progress;
            }
        }
        progressBar.setProgress(progress);
    }

    protected void displayMaxProgress() {
        setProgress(numberOfSteps + 1);
    }

    protected void setAuxVars() {
        completedSteps = new boolean[numberOfSteps + 1];
        for (int i = 0; i < (numberOfSteps + 1); i++) {
            completedSteps[i] = false;
        }
        progressBar.setMax(numberOfSteps + 1);
    }

    protected void addConfirmationStepToStepsList()
    {
        if (!shouldAddConfirmationStep) return;
        String confirmationStepText = context.getString(R.string.vertical_form_stepper_form_last_step);
        steps.add(confirmationStepText);
    }

    protected void disablePreviousButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(previousStepButton);
    }

    protected void enablePreviousButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(previousStepButton);
    }

    protected void disableNextButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(nextStepButton);
    }

    protected void enableNextButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(nextStepButton);
    }

    protected void enableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(1f);
        button.setEnabled(true);
    }

    protected void disableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(alphaOfDisabledElements);
        button.setEnabled(false);
    }

    protected void setProgress(int progress) {
        if (progress > 0 && progress <= (numberOfSteps + 1)) {
            progressBar.setProgress(progress);
        }
    }

    protected void disableConfirmationButton() {
        confirmationButton.setEnabled(false);
    }

    protected void hideSoftKeyboard() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void prepareSendingAndSend() {
        displayDoneIconInConfirmationStep();
        disableConfirmationButton();
        displayMaxProgress();
        verticalStepperFormImplementation.sendData();
    }

    protected void displayDoneIconInConfirmationStep() {
        LinearLayout confirmationStepLayout = stepLayouts.get(stepLayouts.size() - 1);
        ImageView stepDone = (ImageView) confirmationStepLayout.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) confirmationStepLayout.findViewById(R.id.step_number);
        stepDone.setVisibility(View.VISIBLE);
        stepNumberTextView.setVisibility(View.INVISIBLE);
    }

    protected void restoreFormState() {
        goToStep(activeStep, true);
        displayCurrentProgress();
    }

    protected int convertDpToPixel(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }

    protected void setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(LinearLayout stepLayout, int height) {
        TextView stepSubtitle = (TextView) stepLayout.findViewById(R.id.step_subtitle);
        if (stepSubtitle.getVisibility() == View.GONE) {
            LinearLayout stepLeftLine = (LinearLayout) stepLayout.findViewById(R.id.vertical_line_subtitle);
            LayoutParams params = (LayoutParams) stepLeftLine.getLayoutParams();
            params.height = convertDpToPixel(height);
            stepLeftLine.setLayoutParams(params);
        }
    }

    protected void setHeaderAppearance(LinearLayout stepLayout, float alpha,int stepCircleBackgroundColor,int stepCircleBackgroundResource,boolean enabled,boolean completed)
    {
        if(!materialDesignInDisabledSteps)
        {
            RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
            TextView title = (TextView) stepHeader.findViewById(R.id.step_title);
            TextView subtitle = (TextView) stepHeader.findViewById(R.id.step_subtitle);
            LinearLayout circle = (LinearLayout) stepHeader.findViewById(R.id.circle);
            ImageView done = (ImageView) stepHeader.findViewById(R.id.step_done);

            title.setAlpha(alpha);
            circle.setAlpha(alpha);
            done.setAlpha(alpha);

            if(subtitle.getText() != null && !subtitle.getText().equals("")) {
                if(alpha == 1) {
                    subtitle.setVisibility(View.VISIBLE);
                } else {
                    subtitle.setVisibility(View.GONE);
                }
            }
        }
        else
        {
            setStepCircleSize(stepLayout,enabled,completed);
            setStepCircleBackgroundColor(stepLayout, stepCircleBackgroundColor,stepCircleBackgroundResource);
            TextView stepNumberTextView = (TextView) stepLayout.findViewById(R.id.step_number);
            stepNumberTextView.setTextColor(stepNumberTextColor.value(enabled,completed));
        }
    }

    protected void setStepCircleSize(LinearLayout stepLayout,boolean enabled,boolean completed)
    {
        int csize = circleSize.value(enabled,completed);

        LinearLayout circle = (LinearLayout) stepLayout.findViewById(R.id.circle);
        RelativeLayout.LayoutParams circleParams = (RelativeLayout.LayoutParams)circle.getLayoutParams();
        circleParams.width = csize;
        circleParams.height = csize;
        circle.setLayoutParams(circleParams);

        ImageView imageView = (ImageView) stepLayout.findViewById(R.id.step_done);
        circleParams = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
        circleParams.width = csize;
        circleParams.height = csize;
        imageView.setLayoutParams(circleParams);
    }

    protected void setStepCircleBackgroundColor(LinearLayout stepLayout, int color, int resourceId) {

        Drawable bg = ContextCompat.getDrawable(context, resourceId);
        bg.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        setStepCircleBackgroundColor(stepLayout,bg);
    }

    protected void setStepCircleBackgroundColor(LinearLayout stepLayout, Drawable bg)
    {
        LinearLayout circle = (LinearLayout) stepLayout.findViewById(R.id.circle);
        circle.setBackground(bg);
    }

    static protected void setButtonStyle(Button button,ButtonStyle style)
    {
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
            setTextAppearance(button,style.text.appearance);

        if (style.background.resource!=0)
            button.setBackgroundResource(style.background.resource);

        if (style.background.colors!=null)
            button.setBackgroundTintList(style.background.colors);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViews();
        registerListeners();
    }

    @Override
    public void onClick(View v) {
        String previousNavigationButtonTag =
                context.getString(R.string.vertical_form_stepper_form_down_previous);
        if (v.getTag().equals(previousNavigationButtonTag)) {
            goToPreviousStep();
        } else {
            if (isActiveStepCompleted()) {
                goToNextStep();
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("activeStep", this.activeStep);
        bundle.putBooleanArray("completedSteps", this.completedSteps);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            this.activeStep = bundle.getInt("activeStep");
            this.completedSteps = bundle.getBooleanArray("completedSteps");
            state = bundle.getParcelable("superState");
            restoreFormState();
        }
        super.onRestoreInstanceState(state);
    }

    public static class Builder {

        // Required parameters
        protected VerticalStepperFormLayout verticalStepperFormLayout;
        protected String[] steps;
        protected VerticalStepperForm verticalStepperFormImplementation;
        protected Activity activity;

        // Optional parameters
        protected String[] stepsSubtitles = null;
        protected Boolean[] stepsCompleted = null;
        protected float alphaOfDisabledElements = 0.25f;
        protected int stepNumberBackgroundColor = Color.rgb(63, 81, 181);
        protected int buttonBackgroundColor = Color.rgb(63, 81, 181);
        protected int buttonPressedBackgroundColor = Color.rgb(48, 63, 159);
        protected int stepTitleTextColor = Color.rgb(33, 33, 33);
        protected int stepSubtitleTextColor = Color.rgb(162, 162, 162);
        protected int buttonTextColor = Color.rgb(255, 255, 255);
        protected int buttonPressedTextColor = Color.rgb(255, 255, 255);
        protected int errorMessageTextColor = Color.rgb(175, 18, 18);
        protected boolean displayBottomNavigation = true;
        protected boolean materialDesignInDisabledSteps = false;
        protected boolean hideKeyboard = true;
        protected boolean showVerticalLineWhenStepsAreCollapsed = false;

        protected Builder(VerticalStepperFormLayout stepperLayout,
                          String[] steps,
                          VerticalStepperForm stepperImplementation,
                          Activity activity) {

            this.verticalStepperFormLayout = stepperLayout;
            this.steps = steps;
            this.verticalStepperFormImplementation = stepperImplementation;
            this.activity = activity;
        }

        /**
         * Generates an instance of the builder that will set up and initialize the form (after
         * setting up the form it is mandatory to initialize it calling init())
         * @param stepperLayout the form layout
         * @param stepTitles a String array with the names of the steps
         * @param stepperImplementation The instance that implements "VerticalStepperForm" interface
         * @param activity The activity where the form is
         * @return an instance of the builder
         */
        public static Builder newInstance(VerticalStepperFormLayout stepperLayout,
                                          String[] stepTitles,
                                          VerticalStepperForm stepperImplementation,
                                          Activity activity) {

            return new Builder(stepperLayout, stepTitles, stepperImplementation, activity);
        }

        /**
         * Set the subtitles of the steps
         * @param stepsSubtitles a String array with the subtitles of the steps
         * @return the builder instance
         */
        public Builder stepsSubtitles(String[] stepsSubtitles) {
            this.stepsSubtitles = stepsSubtitles;
            return this;
        }

        public Builder stepsCompleted(Boolean[] stepsCompleted)
        {
            this.stepsCompleted = stepsCompleted;
            return this;
        }

        /**
         * Set the primary color (background color of the left circles and buttons)
         * @param colorPrimary primary color
         * @return the builder instance
         */
        public Builder primaryColor(int colorPrimary) {
            this.stepNumberBackgroundColor = colorPrimary;
            this.buttonBackgroundColor = colorPrimary;
            return this;
        }

        /**
         * Set the dark primary color (background color of the buttons when clicked)
         * @param colorPrimaryDark primary color (dark)
         * @return the builder instance
         */
        public Builder primaryDarkColor(int colorPrimaryDark) {
            this.buttonPressedBackgroundColor = colorPrimaryDark;
            return this;
        }

        /**
         * Set the background color of the left circles
         * @param stepNumberBackgroundColor background color of the left circles
         * @return the builder instance
         */
        public Builder stepNumberBackgroundColor(int stepNumberBackgroundColor) {
            this.stepNumberBackgroundColor = stepNumberBackgroundColor;
            return this;
        }

        /**
         * Set the background colour of the buttons
         * @param buttonBackgroundColor background color of the buttons
         * @return the builder instance
         */
        public Builder buttonBackgroundColor(int buttonBackgroundColor) {
            this.buttonBackgroundColor = buttonBackgroundColor;
            return this;
        }

        /**
         * Set the background color of the buttons when clicked
         * @param buttonPressedBackgroundColor background color of the buttons when clicked
         * @return the builder instance
         */
        public Builder buttonPressedBackgroundColor(int buttonPressedBackgroundColor) {
            this.buttonPressedBackgroundColor = buttonPressedBackgroundColor;
            return this;
        }

        /**
         * Set the text color of the step title
         * @param stepTitleTextColor the color of the step title
         * @return this builder instance
         */
        public Builder stepTitleTextColor(int stepTitleTextColor) {
            this.stepTitleTextColor = stepTitleTextColor;
            return this;
        }

        /**
         * Set the text color of the step subtitle
         * @param stepSubtitleTextColor the color of the step title
         * @return this builder instance
         */
        public Builder stepSubtitleTextColor(int stepSubtitleTextColor) {
            this.stepSubtitleTextColor = stepSubtitleTextColor;
            return this;
        }

        /**
         * Set the text color of the buttons
         * @param buttonTextColor text color of the buttons
         * @return the builder instance
         */
        public Builder buttonTextColor(int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        /**
         * Set the text color of the buttons when clicked
         * @param buttonPressedTextColor text color of the buttons when clicked
         * @return the builder instance
         */
        public Builder buttonPressedTextColor(int buttonPressedTextColor) {
            this.buttonPressedTextColor = buttonPressedTextColor;
            return this;
        }

        /**
         * Set the error message color
         * @param errorMessageTextColor error message color
         * @return the builder instance
         */
        public Builder errorMessageTextColor(int errorMessageTextColor) {
            this.errorMessageTextColor = errorMessageTextColor;
            return this;
        }

        /**
         * Set whether or not the bottom navigation bar will be displayed
         * @param displayBottomNavigationBar true to display it; false otherwise
         * @return the builder instance
         */
        public Builder displayBottomNavigation(boolean displayBottomNavigationBar) {
            this.displayBottomNavigation = displayBottomNavigationBar;
            return this;
        }

        /**
         * Set whether or not the disabled steps will have a Material Design look
         * @param materialDesignInDisabledSteps true to use Material Design for disabled steps; false otherwise
         * @return the builder instance
         */
        public Builder materialDesignInDisabledSteps(boolean materialDesignInDisabledSteps) {
            this.materialDesignInDisabledSteps = materialDesignInDisabledSteps;
            return this;
        }

        /**
         * Specify whether or not the keyboard should be hidden at the beginning
         * @param hideKeyboard true to hide the keyboard; false to not hide it
         * @return the builder instance
         */
        public Builder hideKeyboard(boolean hideKeyboard) {
            this.hideKeyboard = hideKeyboard;
            return this;
        }

        /**
         * Specify whether or not the vertical lines should be displayed when steps are collapsed
         * @param showVerticalLineWhenStepsAreCollapsed true to show the lines; false to not
         * @return the builder instance
         */
        public Builder showVerticalLineWhenStepsAreCollapsed(boolean showVerticalLineWhenStepsAreCollapsed) {
            this.showVerticalLineWhenStepsAreCollapsed = showVerticalLineWhenStepsAreCollapsed;
            return this;
        }

        /**
         * Set the alpha level of disabled elements
         * @param alpha alpha level of disabled elements (between 0 and 1)
         * @return the builder instance
         */
        public Builder alphaOfDisabledElements(float alpha) {
            this.alphaOfDisabledElements = alpha;
            return this;
        }

        /**
         * Set up the form and initialize it
         */
        public void init() {
            verticalStepperFormLayout.initialiseVerticalStepperForm(this);
        }

    }

}