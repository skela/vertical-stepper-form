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
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean nextButtonIsOnStep = false;
    public boolean alt1ButtonIsOnStep = false;
    public boolean alt2ButtonIsOnStep = false;
    public boolean alt3ButtonIsOnStep = false;

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
    protected int stepBackgroundColor=0;
    protected int stepHeaderBackgroundColor=0;

    protected int getLayoutId() { return R.layout.vertical_stepper_form_layout; }
    protected int getButtonLayoutId()
    {
        return R.layout.step_layout_buttons;
    }
    protected int getAccessoryLayout() { return 0; }

    // Views
    protected LayoutInflater mInflater;
    protected LinearLayout content;
    protected ScrollView stepsScrollView;
    protected List<VerticalStepperStepLayout> stepLayouts;
    protected List<View> stepContentViews;
    protected List<TextView> stepsTitlesViews;
    protected List<TextView> stepsSubtitlesViews;
    protected Map<String,View> stepTitleHeaderViews = new HashMap<>();
    protected VerticalStepperButton confirmationButton;
    protected ProgressBar progressBar;
    protected AppCompatImageButton previousStepButton, nextStepButton;
    protected RelativeLayout bottomNavigation;

    // Data
    protected List<CharSequence> steps;
    protected List<CharSequence> stepsSubtitles;

    // Logic
    protected int activeStep = -1;
    protected int numberOfSteps;
    protected boolean[] completedSteps;

    public boolean shouldAddConfirmationStep = true;
    public boolean startOnStep = true;
    public boolean explorable = false;

    public String nextButtonTag="next";
    public String alt1ButtonTag="alt1";
    public String alt2ButtonTag="alt2";
    public String alt3ButtonTag="alt3";

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
        mInflater.inflate(getLayoutId(), this, true);
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
    public CharSequence getStepTitle(int stepNumber) {
        return steps.get(stepNumber);
    }

    /**
     * Returns the subtitle of a step
     * @param stepNumber The step number (counting from 0)
     * @return the subtitle string
     */
    public CharSequence getStepsSubtitles(int stepNumber) {
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
    public void setStepSubtitle(int stepNumber, CharSequence subtitle) {
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
    public void setStepAsCompleted(int stepNumber)
    {
        completedSteps[stepNumber] = true;

        VerticalStepperStepLayout stepLayout = stepLayouts.get(stepNumber);

        if (isStepActive(stepNumber))
            enableStepHeader(stepLayout);
        else
            completeStepHeader(stepLayout);

        updateButtons(stepNumber);

        if (stepNumber != activeStep)
        {
            stepLayout.stepDone.setVisibility(View.VISIBLE);
            stepLayout.stepNumberTextView.setVisibility(View.INVISIBLE);
        }
        else
        {
            if (stepNumber != numberOfSteps)
            {
                enableNextButtonInBottomNavigationLayout();
            }
            else
            {
                disableNextButtonInBottomNavigationLayout();
            }
        }

        stepLayout.errorMessage.setText("");
        //errorContainer.setVisibility(View.GONE);
        Animations.slideUp(stepLayout.errorContainer);

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

        VerticalStepperStepLayout stepLayout = stepLayouts.get(stepNumber);

        stepLayout.stepDone.setVisibility(View.INVISIBLE);
        stepLayout.stepNumberTextView.setVisibility(View.VISIBLE);

        updateButtons(stepNumber);

        if (stepNumber == activeStep)
        {
            disableNextButtonInBottomNavigationLayout();
        }
        else
        {
            disableStepHeader(stepLayout);
        }

        if (stepNumber < numberOfSteps)
        {
            setStepAsUncompleted(numberOfSteps, null);
        }

        if (errorMessage != null && !errorMessage.equals(""))
        {
            stepLayout.errorMessage.setText(errorMessage);
            //errorContainer.setVisibility(View.VISIBLE);
            Animations.slideDown(stepLayout.errorContainer);
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

    private Map<Integer,Boolean> activeSteps = new HashMap<Integer,Boolean>();

    public boolean isStepActive(int step)
    {
        if (canOpenMultipleSteps)
        {
            if (activeSteps.containsKey(step))
                return activeSteps.get(step).booleanValue();
            return false;
        }
        else
        {
            return activeStep == step;
        }
    }

    void addActiveStep(int step)
    {
        activeStep = step;
        if (canOpenMultipleSteps)
        {
            activeSteps.put(step,true);
        }
    }

    void removeActiveStep(int step)
    {
        activeStep = -1;
        activeSteps.remove(step);
    }

    public boolean goToStep(int stepNumber, boolean restoration)
    {
        boolean isActive = isStepActive(stepNumber);
        if (!isActive || restoration)
        {
            if(hideKeyboard)
            {
                hideSoftKeyboard();
            }
            boolean previousStepsAreCompleted = arePreviousStepsCompleted(stepNumber);
            if (stepNumber == 0 || previousStepsAreCompleted || explorable)
            {
                return openStep(stepNumber, restoration);
            }
        }
        else if (isActive)
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

    protected void initStepperForm(CharSequence[] stepsTitles, CharSequence[] stepsSubtitles,Boolean[] stepsCompleted)
    {
        setSteps(stepsTitles, stepsSubtitles);
        setCompletedSteps(stepsCompleted);

        List<View> stepContentLayouts = new ArrayList<>();
        for (int i = 0; i < numberOfSteps; i++)
        {
            View stepLayout = verticalStepperFormImplementation.createStepContentView(i);
            stepContentLayouts.add(stepLayout);

            View stepHeader = verticalStepperFormImplementation.createStepHeaderView(i);
            if (stepHeader!=null)
                stepTitleHeaderViews.put(""+i,stepHeader);
        }
        stepContentViews = stepContentLayouts;

        initializeForm();

        if (startOnStep)
        {
            verticalStepperFormImplementation.onStepOpening(activeStep);

            updateButtons(activeStep);
        }
        else
        {
            for (int i = 0; i < numberOfSteps; i++)
            {
                disableStepLayout(i,false);
                VerticalStepperStepLayout stepLayout = stepLayouts.get(i);
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

    private void updateButtons(int step)
    {
        if (stepLayouts == null)
            return;
        if (step >= stepLayouts.size())
            return;
        VerticalStepperStepLayout stepLayout = stepLayouts.get(step);
        updateNextButton(stepLayout.nextButton,step);
        updateAlt1Button(stepLayout.alt1Button,step);
        updateAlt2Button(stepLayout.alt2Button,step);
        updateAlt3Button(stepLayout.alt3Button,step);
    }

    void updateNextButton(VerticalStepperButton button,int step)
    {
        updateStepButton(step, button, nextButtonTag);
    }

    void updateAlt1Button(VerticalStepperButton button,int step)
    {
        updateStepButton(step, button, alt1ButtonTag);
    }

    void updateAlt2Button(VerticalStepperButton button,int step)
    {
        updateStepButton(step, button, alt2ButtonTag);
    }

    void updateAlt3Button(VerticalStepperButton button,int step)
    {
        updateStepButton(step, button, alt3ButtonTag);
    }

    protected void updateStepButton(int step, @NonNull VerticalStepperButton button, @NonNull String tag)
    {

    }

    protected void setSteps(CharSequence[] steps, CharSequence[] stepsSubtitles) {
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

    protected void setUpStep(int stepNumber)
    {
        VerticalStepperStepLayout stepLayout = createStepLayout(stepNumber);
        if (stepNumber < numberOfSteps)
        {
            // The content of the step is the corresponding custom view previously created
            stepLayout.stepContent.addView(stepContentViews.get(stepNumber));
            View v = stepTitleHeaderViews.get(""+stepNumber);
            if (v != null)
                stepLayout.titleHeader.addView(v);
        }
        else
        {
            setUpStepLayoutAsConfirmationStepLayout(stepLayout);
        }
        addStepToContent(stepLayout);
    }

    protected void addStepToContent(LinearLayout stepLayout) {
        content.addView(stepLayout);
    }

    protected void setUpStepLayoutAsConfirmationStepLayout(VerticalStepperStepLayout stepLayout)
    {
        if (!shouldAddConfirmationStep) return;

        confirmationButton = stepLayout.nextButton;

        stepLayout.stepLeftLine1.setVisibility(View.INVISIBLE);
        stepLayout.stepLeftLine2.setVisibility(View.INVISIBLE);

        disableConfirmationButton();

        confirmationButton.setText(R.string.vertical_form_stepper_form_confirm_button);
        confirmationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareSendingAndSend();
            }
        });

        updateButtons(numberOfSteps);

        // Some content could be added to the final step inside stepContent layout
        // RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
    }

    protected VerticalStepperStepLayout createStepLayout(final int stepNumber)
    {
        VerticalStepperStepLayout stepLayout = generateStepLayout();

        if (stepBackgroundColor != 0)
            stepLayout.setBackgroundColor(stepBackgroundColor);
        if (stepHeaderBackgroundColor != 0)
        {
            stepLayout.stepTitleHeaderContainer.setBackgroundColor(stepHeaderBackgroundColor);
            stepLayout.stepHeader.setBackgroundColor(stepHeaderBackgroundColor);
        }

        Drawable bg = ContextCompat.getDrawable(context, R.drawable.circle_step_done);
        bg.setColorFilter(new PorterDuffColorFilter(
                stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        stepLayout.circle.setBackground(bg);

        stepLayout.stepTitle.setText(steps.get(stepNumber));
        stepLayout.stepTitle.setTextColor(stepTitleTextColor);
        if (stepTitleAppearance.disabled!=0)
            setTextAppearance(stepLayout.stepTitle,stepTitleAppearance.disabled);
        stepsTitlesViews.add(stepNumber,stepLayout.stepTitle);

        TextView stepSubtitle = null;
        if(stepsSubtitles != null && stepNumber < stepsSubtitles.size())
        {
            CharSequence subtitle = stepsSubtitles.get(stepNumber);
            if(subtitle != null && !subtitle.equals("")) {
                stepSubtitle = stepLayout.stepSubTitle;
                stepSubtitle.setText(subtitle);
                stepSubtitle.setTextColor(stepSubtitleTextColor);
                if (stepSubtitleAppearance.disabled!=0)
                    setTextAppearance(stepSubtitle,stepSubtitleAppearance.disabled);
                stepSubtitle.setVisibility(View.VISIBLE);
            }
        }
        stepsSubtitlesViews.add(stepNumber, stepSubtitle);

        stepLayout.stepNumberTextView.setText(String.valueOf(stepNumber + 1));
        stepLayout.stepNumberTextView.setTextColor(stepNumberTextColor.enabled);

        if (doneIcon != 0)
            stepLayout.stepDone.setImageResource(doneIcon);
        stepLayout.stepDone.setColorFilter(stepNumberTextColor.enabled);

        stepLayout.errorMessage.setTextColor(errorMessageTextColor);
        stepLayout.errorIcon.setColorFilter(errorMessageTextColor);

        stepLayout.stepHeader.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedHeader(stepNumber);
            }
        });

        int accessoryLayout = getAccessoryLayout();
        if (accessoryLayout != 0)
        {
            stepLayout.addAccessoryViews(accessoryLayout);
        }

        int layoutButtons = getButtonLayoutId();

        stepLayout.addButtons(layoutButtons);

        setupButton(stepLayout,stepLayout.nextButton,nextButtonIsOnStep,R.id.next_step_accessory_button_next);
        setupButton(stepLayout,stepLayout.alt1Button,alt1ButtonIsOnStep,R.id.next_step_accessory_button_alt1);
        setupButton(stepLayout,stepLayout.alt2Button,alt2ButtonIsOnStep,R.id.next_step_accessory_button_alt2);
        setupButton(stepLayout,stepLayout.alt3Button,alt3ButtonIsOnStep,R.id.next_step_accessory_button_alt3);

        if (layoutButtons == R.layout.step_layout_buttons)
        {
            stepLayout.nextButton.setButtonStyle(buttonStyle);
            stepLayout.alt1Button.setButtonStyle(alt1ButtonStyle);
            stepLayout.alt2Button.setButtonStyle(alt2ButtonStyle);
            stepLayout.alt3Button.setButtonStyle(alt3ButtonStyle);
        }

        stepLayout.nextButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedNext(stepNumber);
            }
        });

        stepLayout.alt1Button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedAlt1(stepNumber);
            }
        });

        stepLayout.alt2Button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickedAlt2(stepNumber);
            }
        });

        stepLayout.alt3Button.setOnClickListener(new OnClickListener()
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
            if (stepNumber == 0)
                stepLayout.stepLeftLine0.setBackgroundColor(Color.TRANSPARENT);
            else
                stepLayout.stepLeftLine0.setBackgroundColor(lineColor);
            stepLayout.stepLeftLine1.setBackgroundColor(lineColor);
            stepLayout.stepLeftLine2.setBackgroundColor(lineColor);
            stepLayout.stepLeftLine3.setBackgroundColor(lineColor);
        }

        stepLayouts.add(stepLayout);

        return stepLayout;
    }

    private void setupButton(VerticalStepperStepLayout layout,VerticalStepperButton button,boolean isOnStep,int id)
    {
        if (isOnStep)
        {
            button.setVisible(false);
            button.setButtons(layout.findViewById(id));
            button.isOnStep = isOnStep;
            button.setVisible(false);
        }
        else
        {
            View v = layout.findViewById(id);
            if (v != null)
                v.setVisibility(View.GONE);
        }
    }

    protected boolean canOpenMultipleSteps = false;

    public void clickedHeader(int step)
    {
        if (!goToStep(step, false))
            closeStep(step);
    }

    protected void clickedNext(int step)
    {
        verticalStepperFormImplementation.clickedStepButton(step,nextButtonTag);
    }

    protected void clickedAlt1(int step)
    {
        verticalStepperFormImplementation.clickedStepButton(step,alt1ButtonTag);
    }

    protected void clickedAlt2(int step)
    {
        verticalStepperFormImplementation.clickedStepButton(step,alt2ButtonTag);
    }

    protected void clickedAlt3(int step)
    {
        verticalStepperFormImplementation.clickedStepButton(step,alt3ButtonTag);
    }

    public static void setTextAppearance(TextView tv,int appearanceResourceId)
    {
        if (appearanceResourceId == 0) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            tv.setTextAppearance(appearanceResourceId);
        else
            tv.setTextAppearance(tv.getContext(), appearanceResourceId);
    }

    protected VerticalStepperStepLayout generateStepLayout()
    {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        return (LinearLayout) inflater.inflate(R.layout.step_layout, content, false);
        return new VerticalStepperStepLayout(context);
    }

    protected boolean openStep(int stepNumber, boolean restoration)
    {
        if (stepNumber >= 0 && stepNumber <= numberOfSteps)
        {
            int oldActiveStep = activeStep;

            addActiveStep(stepNumber);

            if (oldActiveStep!=-1)
                updateButtons(oldActiveStep);

            if (stepNumber == 0) {
                disablePreviousButtonInBottomNavigationLayout();
            } else {
                enablePreviousButtonInBottomNavigationLayout();
            }

            if (completedSteps[stepNumber] && stepNumber != numberOfSteps) {
                enableNextButtonInBottomNavigationLayout();
            } else {
                disableNextButtonInBottomNavigationLayout();
            }

            int stepCount = numberOfSteps;
            if (!shouldAddConfirmationStep)
                stepCount -= 1;

            for(int i = 0; i <= stepCount; i++)
            {
                if(i != stepNumber)
                {
                    if (!canOpenMultipleSteps)
                        disableStepLayout(i, !restoration);
                }
                else
                {
                    enableStepLayout(i, !restoration);
                }
            }

            scrollToActiveStep(!restoration);

            if (stepNumber == numberOfSteps && shouldAddConfirmationStep) {
                setStepAsCompleted(stepNumber);
            }

            if (verticalStepperFormImplementation != null)
                verticalStepperFormImplementation.onStepOpening(stepNumber);
            updateButtons(stepNumber);
            return true;
        }
        return false;
    }

    public void reloadActiveStep()
    {
        reloadStep(activeStep);
    }

    public void reloadStep(int step)
    {
        if (step == -1)
            return;
        if (verticalStepperFormImplementation != null)
            verticalStepperFormImplementation.onStepOpening(step);
        updateButtons(step);
    }

    public void closeStep(int stepNumber)
    {
        if (stepNumber >= 0 && stepNumber <= numberOfSteps)
        {
            boolean canClose = stepNumber == 0 || arePreviousStepsCompleted(stepNumber) || explorable;
            if (!canClose) return;

            disableStepLayout(stepNumber, true);
            VerticalStepperStepLayout stepLayout = stepLayouts.get(stepNumber);
            if (isStepCompleted(stepNumber))
                completeStepHeader(stepLayout);
            else
                disableStepHeader(stepLayout);
        }
        removeActiveStep(stepNumber);
        updateButtons(stepNumber);
    }

    protected void scrollToStep(final int stepNumber, boolean smoothScroll)
    {
        if (stepLayouts == null)
            return;
        if (stepNumber >= stepLayouts.size())
            return;
        if (stepNumber == -1)
            return;

        if (smoothScroll)
        {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.smoothScrollTo(0, stepLayouts.get(stepNumber).getTop());
                }
            });
        }
        else
            {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.scrollTo(0, stepLayouts.get(stepNumber).getTop());
                }
            });
        }
    }

    protected void scrollToActiveStep(boolean smoothScroll) {
        //scrollToStep(activeStep, smoothScroll);
    }

    protected void findViews()
    {
        content = (LinearLayout) findViewById(R.id.content);
        stepsScrollView = (ScrollView) findViewById(R.id.steps_scroll);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        previousStepButton = (AppCompatImageButton) findViewById(R.id.down_previous);
        nextStepButton = (AppCompatImageButton) findViewById(R.id.down_next);
        bottomNavigation = (RelativeLayout) findViewById(R.id.bottom_navigation);
    }

    protected void animateStep(VerticalStepperStepLayout stepLayout,boolean up)
    {
        if (up)
        {
            Animations.slideUp(stepLayout.buttons);
            Animations.slideUp(stepLayout.stepContent);
        }
        else
        {
            Animations.slideDown(stepLayout.stepContent);
            if (stepLayout.hasVisibleBottomButtons(nextButtonIsOnStep))
                Animations.slideDown(stepLayout.buttons);
        }
    }

    protected void disableStepLayout(int stepNumber, boolean smoothieDisabling)
    {
        VerticalStepperStepLayout stepLayout = stepLayouts.get(stepNumber);

        if (smoothieDisabling)
        {
            animateStep(stepLayout,true);
        }
        else
        {
            stepLayout.buttons.setVisibility(View.GONE);
            stepLayout.stepContent.setVisibility(View.GONE);
        }

        if (!completedSteps[stepNumber])
        {
            disableStepHeader(stepLayout);
            stepLayout.stepDone.setVisibility(View.INVISIBLE);
            stepLayout.stepNumberTextView.setVisibility(View.VISIBLE);
            setTextAppearance(stepLayout.stepTitle,stepTitleAppearance.disabled);
            setTextAppearance(stepLayout.stepSubTitle,stepSubtitleAppearance.disabled);
        }
        else
        {
            completeStepHeader(stepLayout);
            stepLayout.stepDone.setVisibility(View.VISIBLE);
            stepLayout.stepNumberTextView.setVisibility(View.INVISIBLE);
            setTextAppearance(stepLayout.stepTitle,stepTitleAppearance.completed);
            setTextAppearance(stepLayout.stepSubTitle,stepSubtitleAppearance.completed);
        }

        showVerticalLineInCollapsedStepIfNecessary(stepLayout);
    }

    protected void enableStepLayout(int stepNumber, boolean smoothieEnabling)
    {
        VerticalStepperStepLayout stepLayout = stepLayouts.get(stepNumber);

        enableStepHeader(stepLayout);

        if (smoothieEnabling)
        {
            animateStep(stepLayout,false);
        }
        else
        {
            stepLayout.stepContent.setVisibility(View.VISIBLE);
            stepLayout.buttons.setVisibility(View.VISIBLE);
        }

        if (completedSteps[stepNumber] && activeStep != stepNumber)
        {
            stepLayout.stepDone.setVisibility(View.VISIBLE);
            stepLayout.stepNumberTextView.setVisibility(View.INVISIBLE);
            setTextAppearance(stepLayout.stepTitle,stepTitleAppearance.completed);
            setTextAppearance(stepLayout.stepSubTitle,stepSubtitleAppearance.completed);
        }
        else
        {
            stepLayout.stepDone.setVisibility(View.INVISIBLE);
            stepLayout.stepNumberTextView.setVisibility(View.VISIBLE);
            setTextAppearance(stepLayout.stepTitle,stepTitleAppearance.enabled);
            setTextAppearance(stepLayout.stepSubTitle,stepSubtitleAppearance.enabled);
        }

        hideVerticalLineInCollapsedStepIfNecessary(stepLayout);
    }

    protected void completeStepHeader(VerticalStepperStepLayout stepLayout)
    {
        setHeaderAppearance(stepLayout, 1, circleBackgroundColor.completed,circleResourceId.completed,false,true);
    }

    protected void enableStepHeader(VerticalStepperStepLayout stepLayout)
    {
        setHeaderAppearance(stepLayout, 1, circleBackgroundColor.enabled,circleResourceId.enabled,true,false);
    }

    protected void disableStepHeader(VerticalStepperStepLayout stepLayout)
    {
        setHeaderAppearance(stepLayout, alphaOfDisabledElements, circleBackgroundColor.disabled,circleResourceId.disabled,false,false);
    }

    protected void showVerticalLineInCollapsedStepIfNecessary(VerticalStepperStepLayout stepLayout) {
        // The height of the line will be 16dp when the subtitle textview is gone
        if(showVerticalLineWhenStepsAreCollapsed) {
            setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(stepLayout, 16);
        }
    }

    protected void hideVerticalLineInCollapsedStepIfNecessary(VerticalStepperStepLayout stepLayout) {
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

    protected void displayDoneIconInConfirmationStep()
    {
        VerticalStepperStepLayout confirmationStepLayout = stepLayouts.get(stepLayouts.size() - 1);
        confirmationStepLayout.stepDone.setVisibility(View.VISIBLE);
        confirmationStepLayout.stepNumberTextView.setVisibility(View.INVISIBLE);
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

    protected void setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(VerticalStepperStepLayout stepLayout, int height)
    {
        if (stepLayout.stepSubTitle.getVisibility() == View.GONE)
        {
            LayoutParams params = (LayoutParams) stepLayout.stepLeftLine2.getLayoutParams();
            params.height = convertDpToPixel(height);
            stepLayout.stepLeftLine2.setLayoutParams(params);
        }
    }

    protected void setHeaderAppearance(VerticalStepperStepLayout stepLayout, float alpha,int stepCircleBackgroundColor,int stepCircleBackgroundResource,boolean enabled,boolean completed)
    {
        if(!materialDesignInDisabledSteps)
        {
            TextView title = stepLayout.stepTitle;
            TextView subtitle = stepLayout.stepSubTitle;
            LinearLayout circle = stepLayout.circle;
            ImageView done = stepLayout.stepDone;

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
            stepLayout.setStepCircleBackgroundColor(stepCircleBackgroundColor,stepCircleBackgroundResource);
            stepLayout.stepNumberTextView.setTextColor(stepNumberTextColor.value(enabled,completed));
        }
    }

    protected void setStepCircleSize(VerticalStepperStepLayout stepLayout,boolean enabled,boolean completed)
    {
        int csize = circleSize.value(enabled,completed);
        stepLayout.setStepCircleSize(csize);
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
        protected CharSequence[] steps;
        protected VerticalStepperForm verticalStepperFormImplementation;
        protected Activity activity;

        // Optional parameters
        protected CharSequence[] stepsSubtitles = null;
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
                          CharSequence[] steps,
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
                                          CharSequence[] stepTitles,
                                          VerticalStepperForm stepperImplementation,
                                          Activity activity) {

            return new Builder(stepperLayout, stepTitles, stepperImplementation, activity);
        }

        /**
         * Set the subtitles of the steps
         * @param stepsSubtitles a String array with the subtitles of the steps
         * @return the builder instance
         */
        public Builder stepsSubtitles(CharSequence[] stepsSubtitles) {
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