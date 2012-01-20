package de.betterform.conformance;

import org.openqa.selenium.By;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 28.10.11
 * Time: 11:27
 */
public interface WebDriverTestInterface {

    public static final String xfControlClass = "xfControl";

    public static final String appearanceCompact = "compact";
    public static final String appearanceDefault = "default";
    public static final String appearanceFull = "full";

    public static final String appearanceHorizontalTable = "bf:horizontalTable";

    public static final String spanTagName = "span";
    public static final String labelTagName = "label";
    public static final String tableTagName = "table";
    public static final String tableRowTagName = "tr";
    public static final String tableColTagName = "td";

    /* Helper Functions */
    public boolean isElementPresent(By by);
    public void waitForTitle(String title);
    public void selectOption(String id, String option);
    public void click(String id);
    public void typeInput(String id, String value);

    public boolean checkSelectionOptions(String id, String[] options);
    public boolean isControlPresent(String id, String type);
    public boolean isTextPresent(String text);
    public String getControlValue(String id);
    public boolean isControlValueValid(String id);
    public boolean isControlValueInvalid(String id);
    public boolean isControlReadWrite(String id);
    public boolean isControlReadOnly(String id);
    public boolean isControlRequired(String id);
    public boolean isControlOptional(String id);
    public boolean isControlFocused(String id);
    public boolean isControlValueInRange(String id, String start, String end);

    public boolean isControlHintPresent(String id, String value);
    public boolean isControlHelpPresent(String id, String value);
    public boolean isControlAlertPresent(String id, String value, String type);
    public boolean isHtmlClassPresentByTagName(String tagName, String value);
    public boolean isHtmlClassPresentByCssSelector(String cssSelector, String value);

    public boolean hasControlAppearance(String locator, String type, String appearance);

    public void hasException();
    public String getExceptionType();
}
