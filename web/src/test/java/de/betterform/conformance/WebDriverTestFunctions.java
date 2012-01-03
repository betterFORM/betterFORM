/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
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
        Select select = new Select(webDriver.findElement(By.id(id)));

        //deselect first();
        /*
        if (select.isMultiple()) {
            select.deselectAll();
        } else {
            List <WebElement> webElements = select.getAllSelectedOptions();
            Iterator<WebElement> webElementIterator = webElements.iterator();

            while (webElementIterator.hasNext()) {
                select.deselectByVisibleText(webElementIterator.next().getText());
            }
        }
        */

        select.selectByVisibleText(option);

        try {
         Thread.sleep(1000);
        } catch (Exception e) {

        }
    }

    public void click(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        webElement.click();
    }

    public void typeInput(String id, String value) {
        WebElement webElement = webDriver.findElement(By.id(id));
        webElement.clear();
        webElement.click();
        webElement.sendKeys(value);

        //Step out of control
        if (id.contains("-value")) {
            WebElement parent = webDriver.findElement(By.id(id.substring(0, id.indexOf("-value"))));
            parent.click();
        }
    }

    public boolean checkSelectionOptions(String id, String[] options) {
        Select select = new Select(webDriver.findElement(By.id(id)));
        Iterator<WebElement> selectOptionsElements = select.getOptions().iterator();


        List<String> selectOptions = new Vector<String>();

        while (selectOptionsElements.hasNext()) {
            selectOptions.add(selectOptionsElements.next().getText());
        }


        boolean optionsPresent = true;

        for (int i = 0; i < options.length; i++) {
            optionsPresent &= selectOptions.contains(options[i]);
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

    public boolean isControlValueValid(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        String classAttribute = webElement.getAttribute("class");

        if (classAttribute != null) {
            return classAttribute.contains("xfValid");
        }

        return false;

    }

    public boolean isControlValueInvalid(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        String classAttribute = webElement.getAttribute("class");

        if (classAttribute != null) {
            return classAttribute.contains("xfInvalid");
        }

        return false;

    }

    public boolean isControlReadWrite(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        String classAttribute = webElement.getAttribute("class");

        if (classAttribute != null) {
            if (classAttribute.contains("xfReadWrite")) {
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
        }

        return false;

    }

    public boolean isControlReadOnly(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        String classAttribute = webElement.getAttribute("class");

        if (classAttribute != null) {
            if (classAttribute.contains("xfReadOnly")) {
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
        }

        return false;

    }

    public boolean isControlRequired(String id) {
        WebElement webElement = webDriver.findElement(By.id(id));
        String classAttribute = webElement.getAttribute("class");

        if (classAttribute != null) {
            return classAttribute.contains("xfRequired");
        }

        return false;
    }

     public boolean isControlFocused(String id) {
         WebElement webElement = webDriver.switchTo().activeElement();

         if (webElement != null && webElement.getAttribute("id").equals(id)) {
             return true;
         }
        return false;

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
                return "Error Page".equals(d.getTitle());
            }
        });
    }

    public String getExceptionType() {
      return webDriver.findElement(By.id("msg")).getText();
    }


    public Alert getAlert() {
        try {
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

    public boolean isControlAlertPresent(String id, String value) {
        return checkControlHelpHintAlert(id, value, "xfAlert");
    }

    private boolean checkControlHelpHintAlert(String id, String value, String htmlclass) {
        WebElement webElement = webDriver.findElement(By.id(id));

        String classAttribute = webElement.getAttribute("class");

        if (classAttribute != null && classAttribute.contains(htmlclass)) {
            if (! webElement.getCssValue("display").equals("none")) {
                if (! "".equals(value)) {
                    return value.equalsIgnoreCase(webElement.getText());
                }
                return true;
            }
        }

        return false;
    }
}
