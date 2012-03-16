/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.conformance;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * Base class for black box testing of betterForm Web.
 * Author: Lars Windauer, Tobi Krebs
 * Date: Jan 02, 2012
 * Last Updated:
 */
public class WebDriverTestFunctions extends WebDriverTest {
	protected StringBuffer verificationErrors = new StringBuffer();


    /* Helper Functions */
    public boolean isElementPresent(By by) {
		try {
			webDriver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

    public void waitForTitle(final String title) {
        (new WebDriverWait(webDriver, 30)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return title.equals(d.getTitle());
            }
        });
    }

    public void selectOption(final String id, String option) {
        WebElement webElement = webDriver.findElement(By.id(id));

        WebElement webElementValue = webDriver.findElement(By.id(id+"-value"));
        String controlType = webElementValue.getAttribute("controlType");


        if (hasHTMlClass(webElement, "xfSelect1") && "select1RadioButton".equals(controlType)) {
            Iterator<WebElement> select1OptionsElements = webElement.findElements(By.cssSelector(".xfSelectorItem")).iterator();

            while (select1OptionsElements.hasNext()) {
                WebElement select1Option = select1OptionsElements.next();
                if (option.equals(select1Option.getText())) {
                    select1Option.findElement(By.name(id+"-value")).click();
                    return;
                }
            }
        } else {
            Select select = new Select(webDriver.findElement(By.id(id+ "-value")));

            select.selectByVisibleText(option);

            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    public void deselectOptionByValue(final String id, String value){
        new Select(webDriver.findElement(By.id(id))).deselectByValue(value);
    }

    public void click(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        webElement.click();
    }
    
    public void typeInput(String id, String value){
        typeInput(id, value, false);
    }

    public void typeInput(String id, String value, boolean incremental) {
        WebElement webElement = webDriver.findElement(By.id(id));
        if (!incremental){
            webElement.clear();
            webElement.click();
        }
        webElement.sendKeys(value);

        //Step out of control
        if (!incremental && id.contains("-value")) {
            WebElement parent = webDriver.findElement(By.id(id.substring(0, id.indexOf("-value"))));
            parent.click();
        }
    }

    public boolean checkSelectionOptions(String id, String[] options) {
        WebElement webElement = webDriver.findElement(By.id(id));

        String classes = webElement.getAttribute("class");
        List<String> selectOptions = new Vector<String>();

        if (classes != null) {
            if (classes.contains("xfSelect1")) {
                Iterator<WebElement> select1OptionsElements = webElement.findElements(By.cssSelector(".xfSelectorItem")).iterator();

                while (select1OptionsElements.hasNext()) {
                    selectOptions.add(select1OptionsElements.next().getText());
                }

            } else if (classes.contains("xfSelect")) {
                Select select = new Select(webDriver.findElement(By.id(id + "-value")));
                Iterator<WebElement> selectOptionsElements = select.getOptions().iterator();

                while (selectOptionsElements.hasNext()) {
                    selectOptions.add(selectOptionsElements.next().getText().trim());
                }

            }
        }

        boolean optionsPresent = true;

        for (int i = 0; i < options.length; i++) {
            optionsPresent &= selectOptions.contains(options[i].trim());
        }

        return optionsPresent;
    }

    public boolean isControlPresent(String id, String type) {
        //TODO; Check type?
        try {
            WebElement control = webDriver.findElement(By.id(id));
            if (control.isDisplayed() || control.getAttribute("hidden").equals("true")) {
                return true;
            } else {
                return false;
            }

        } catch (NoSuchElementException nse) {
            //Do nothing.
        }
        return false;
    }

    public boolean isTextPresent(String text) {
        return webDriver.findElement(By.tagName("body")).getText().contains(text);
    }

    public String getControlLabel(String id) {
        return webDriver.findElement(By.id(id)).getText();
    }

    public String getControlValue(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        String classAttribute = webElement.getAttribute("class");

        if (classAttribute.contains("xfOutput")) {
            return webDriver.findElement(By.id(id + "-value")).getText();
        }

        return webDriver.findElement(By.id(id + "-value")).getAttribute("value");
    }

    public String getBooleanControlValue(String id) {
        return webDriver.findElement(By.id(id)).getAttribute("aria-pressed");
    }

    public String getInnerHTML(String id) {
        return webDriver.findElement(By.id(id)).getText();
    }

    public boolean isControlValueValid(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        return hasHTMlClass(webElement, "xfValid");
    }

    public boolean isControlValueInvalid(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        return hasHTMlClass(webElement, "xfInvalid");
    }

    public boolean isControlReadWrite(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));



        if (hasHTMlClass(webElement, "xfReadWrite")) {
            if (hasHTMlClass(webElement,"xfContainer")) {
                return true;
            }
            if (webElement.getAttribute("readonly") != null) {
                   return webElement.getAttribute("readonly").equals("false");
            }

            WebElement webElementValue = webDriver.findElement(By.id(id+ "-value"));

            if ( webElementValue.getAttribute("aria-readonly") != null ) {
                return webElementValue.getAttribute("aria-readonly").equals("false");
            } else {
                //TODO: is this enough??
                return true;
            }
        }


        return false;

    }

    public boolean isControlReadOnly(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));

        if (hasHTMlClass(webElement, "xfReadOnly")) {
            if (hasHTMlClass(webElement, "xfContainer")) {
                return true;
            }
            if (webElement.getAttribute("readonly") != null) {
                return webElement.getAttribute("readonly").equals("true");
            }

            WebElement webElementValue = webDriver.findElement(By.id(id+ "-value"));

            if ( webElementValue.getAttribute("aria-readonly") != null ) {
                return webElementValue.getAttribute("aria-readonly").equals("true");
            } else {
                //TODO: is this enough ??
                return true;
            }
        }


        return false;

    }

    public boolean isControlRequired(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        return hasHTMlClass(webElement, "xfRequired");
    }

    public boolean isControlOptional(String id) {
        boolean optional = !(isControlRequired(id));
        
        if (optional) {
            WebElement label = webDriver.findElement(By.id(id)).findElement(By.tagName("label"));
            String text = label.getText();
            //TODO: FIXME Check that '*' is NOT present!
        }

        return optional;
    }

     public boolean isControlFocused(String id) {
         WebElement webElement = webDriver.switchTo().activeElement();

         if (webElement != null && webElement.getAttribute("id").equals(id)) {
             return true;
         }
        return false;

    }

    public boolean cssSelectorIsValid(String cssSelector) {
        WebElement foundElement = webDriver.findElement(By.cssSelector(cssSelector));
        return foundElement != null;
    }

    public boolean isControlValueInRange(String id, String start, String end) {
        WebElement webElement = webDriver.findElement(By.id(id));

        if (webElement != null) {
            String rangeValue = getControlValue(id);

            try {
                BigDecimal rangeValueAsBigDecimal = new BigDecimal(rangeValue);
                BigDecimal startAsBigDecimal = new BigDecimal(start);
                BigDecimal endAsBigDecimal = new BigDecimal(end);

                if (rangeValueAsBigDecimal.compareTo(startAsBigDecimal) >= 0 && rangeValueAsBigDecimal.compareTo(endAsBigDecimal) <= 0) {
                    return true;
                }
            } catch (NumberFormatException nfe) {
                //carry on.
            }

            //TODO: other types?
        }
        return false;
    }


    public void hasException() {
        (new WebDriverWait(webDriver, 30)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return "Error".equals(d.getTitle());
            }
        });
    }

    public String getExceptionType() {
      return webDriver.findElement(By.className("message2")).getText();
    }


    public Alert getAlert() {
        try {
            (new WebDriverWait(webDriver, 30)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return null != webDriver.switchTo().alert();
                }
            });
            return webDriver.switchTo().alert();
        } catch (NoAlertPresentException nape) {
            return null;
        }
    }


    public boolean checkAlert(String expectedText) {
        Alert alert = getAlert();
        if (alert == null) {
            return false;
        }

        String alertText = alert.getText();
        alert.accept();
        return alertText.startsWith(expectedText);
    }


    public boolean isControlHintPresent(String id, String value) {
        return checkControlHelpHintAlert(id, value, "xfHint");
    }

    public boolean isControlHelpPresent(String id, String value) {
        return checkControlHelpHintAlert(id, value, "xfHelp");
    }

    public boolean isControlAlertPresent(String id, String value, String type) {
        if ("TOOLTIP".equalsIgnoreCase(type)) {
            return checkControlToolTipHelpHintAlert(id,value, "alert");
        }
        
        return checkControlHelpHintAlert(id, value, "xfAlert");
    }

    private boolean checkControlHelpHintAlert(String id, String value, String htmlclass) {
        WebElement webElement = webDriver.findElement(By.id(id));

        if (hasHTMlClass(webElement,htmlclass)) {
            if (! webElement.getCssValue("display").equals("none")) {
                if (! "".equals(value)) {
                    return value.equalsIgnoreCase(webElement.getText());
                }
                return true;
            }
        }

        return false;
    }

    private boolean checkControlToolTipHelpHintAlert(String id, String value, String type) {
        String toolTipId =  id + "-MasterToolTip-" + type;

        WebElement webElement = webDriver.findElement(By.cssSelector("#" + toolTipId + " div"));

        String roleAttribute = webElement.getAttribute("role");

        if (roleAttribute != null && roleAttribute.contains(type)) {
            if (! "".equals(value)) {
                return value.equalsIgnoreCase(webElement.getText());
            }
        }

        return false;
    }

    public boolean isHtmlClassPresentByTagName(String tagName, String value) {
        WebElement webElement = webDriver.findElement(By.tagName(tagName));
        return hasHTMlClass(webElement,value);
    }
    public boolean isHtmlClassPresentByCssSelector(String cssSelector, String value) {
        WebElement webElement = webDriver.findElement(By.cssSelector(cssSelector));
        return hasHTMlClass(webElement,value);
    }

    private boolean hasHTMlClass(WebElement webElement, String htmlClass) {
        String classes = webElement.getAttribute("class");

        if (classes != null) {
            return classes.contains(htmlClass);
        }

        return false;
    }

    public boolean hasControlAppearance(String locator, String type, String appearance) {
        String htmlClass;
        if (WebDriverTestInterface.appearanceDefault.equals(appearance) ) {
            htmlClass = "xf" + type.substring(0,1).toUpperCase() + type.substring(1);
        } else if(appearance.contains("bf:")) {
            String[] split = appearance.split(":");
            htmlClass = split[0] + split[1].substring(0,1).toUpperCase() + split[1].substring(1);
        } else {
            htmlClass = "xf" + appearance.substring(0,1).toUpperCase() + appearance.substring(1) + type.substring(0,1).toUpperCase() + type.substring(1);
        }

        WebElement control = webDriver.findElement(By.id(locator));

        if (hasHTMlClass(control, htmlClass)) {
            if ("group".equals(type)) {
                return hasGrouplayout(control, appearance);
            }
        }
        return false;
    }


    private boolean hasGrouplayout(WebElement group, String apperance) {
        boolean validLayout = true;

        if (WebDriverTestInterface.appearanceFull.equals(apperance)) {
             if (WebDriverTestInterface.spanTagName.equals(group.getTagName())) {
                 Iterator<WebElement> children = group.findElements(By.className(WebDriverTestInterface.xfControlClass)).iterator();

                 while (children.hasNext()) {
                     WebElement child = children.next();
                     if (child.getAttribute("widgetid") != null) {
                         WebElement widget = child.findElement(By.id("widget_" + child.getAttribute("widgetid") + "-value"));

                         validLayout &= "200px".equals(widget.getCssValue("left"));
                         validLayout &= "left".equals(widget.getCssValue("text-align"));
                     }
                     validLayout &= WebDriverTestInterface.spanTagName.equals(child.getTagName());
                 }

                 return validLayout;
             }
        } else  if (WebDriverTestInterface.appearanceCompact.equals(apperance) || WebDriverTestInterface.appearanceDefault.equals(apperance)) {
            if (WebDriverTestInterface.spanTagName.equals(group.getTagName())) {
                Iterator<WebElement> children = group.findElements(By.className(WebDriverTestInterface.xfControlClass)).iterator();

                while (children.hasNext()) {
                    WebElement child = children.next();
                    if (child.getAttribute("widgetid") != null) {
                        WebElement widget = child.findElement(By.id("widget_" + child.getAttribute("widgetid") + "-value"));

                        validLayout &= "start".equals(widget.getCssValue("text-align"));

                    }
                    validLayout &= WebDriverTestInterface.spanTagName.equals(child.getTagName());
                }

                return validLayout;
            }
        } else  if (WebDriverTestInterface.appearanceHorizontalTable.equals(apperance)) {
            if (WebDriverTestInterface.tableTagName.equals(group.getTagName())) {
                final List<WebElement> children = group.findElements(By.tagName(WebDriverTestInterface.tableRowTagName));

                if ( children.size() == 3 ) {
                    WebElement labelRow = children.get(1);
                    WebElement valueRow = children.get(2);

                    final List<WebElement> labels = labelRow.findElements(By.tagName(WebDriverTestInterface.tableColTagName));
                    final List<WebElement> values = valueRow.findElements(By.tagName(WebDriverTestInterface.tableColTagName));

                    if (labels.size() == values.size()) {
                        for (int i = 0; i < labels.size(); i++) {
                            validLayout &= hasHTMlClass(labels.get(i), "bfHorizontalTableLabel)");
                            List<WebElement> htmlLabels = labels.get(i).findElements(By.tagName(WebDriverTestInterface.labelTagName));

                            validLayout &= (htmlLabels.size() == 1);
                            validLayout &= (hasHTMlClass(htmlLabels.get(0), "bfTableLabel"));
                            //TODO: !!!!
                        }

                        for (int i = 0; i < labels.size(); i++) {
                            validLayout &= hasHTMlClass(values.get(i), "bfHorizontalTableValue)");
                            //TODO: !!!!
                        }
                    }
                }
            }
        }

        return false;
    }
}
