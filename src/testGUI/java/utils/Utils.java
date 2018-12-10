package utils;

import controler.Controler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertNotNull;


public class Utils {
    private Controler controler;
    private Point origin;
    private Robot robot;
    public String XML_FOLDER =
            "C:\\Users\\laure\\Documents\\INSA\\4IF\\PLD\\AGILE\\Code\\ressources\\fichiersXML2018\\";

    public Utils(Controler controler) throws AWTException {
        this.controler = controler;
        this.robot = new Robot();
    }

    public void setOrigin(Component component) {
        this.origin = new Point(0, 0);
        SwingUtilities.convertPointToScreen(this.origin, component);
    }

    public Component getButton(String buttonName) {
        for (Component cpt : controler.getMainWindow().getInputView().getComponents()) {
            if (buttonName.equals(cpt.getName()))
                return cpt;
        }
        return null;
    }

    public void seeButton() {
        for (Component cpt : controler.getMainWindow().getInputView().getComponents()) {
            System.out.println(cpt.getName() + ": " + cpt);
        }
    }

    public void clickOnComponent(Component component) throws InterruptedException {
        robot.mouseMove(
                (int) origin.getX() + component.getX() + component.getWidth() / 2,
                (int) origin.getY() + component.getY() + component.getHeight() / 2);
        Thread.sleep(1000);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void loadXML(String buttonName, String fileName) throws InterruptedException {
        Component planButton = getButton(buttonName);
        assertNotNull(planButton);
        clickOnComponent(planButton);
        Thread.sleep(2000);
        writeTextEnglishKeyboard(fileName);
        Thread.sleep(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    private void writeTextEnglishKeyboard(String textToWrite) {
        for (char c : textToWrite.toCharArray()) {
            boolean upperChar = (Character.isAlphabetic(c) && Character.toUpperCase(c) == c) || c == ':';
            if (upperChar)
               robot.keyPress(KeyEvent.VK_SHIFT);

            if (c == '\\') {
               robot.keyPress(KeyEvent.VK_BACK_SLASH);
               robot.keyRelease(KeyEvent.VK_BACK_SLASH);
            } else if (c == '/') {
               robot.keyPress(KeyEvent.VK_SLASH);
               robot.keyRelease(KeyEvent.VK_SLASH);
            } else if (c == ':' || c == ';') {
               robot.keyPress(KeyEvent.VK_SEMICOLON);
               robot.keyRelease(KeyEvent.VK_SEMICOLON);
            } else {
               robot.keyPress(Character.toUpperCase(c));
               robot.keyRelease(Character.toUpperCase(c));
            }

            if (upperChar)
               robot.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    private void writeText(String textToWrite) {
        for (char c : textToWrite.toCharArray()) {
            boolean upperChar = (Character.isAlphabetic(c) && Character.toUpperCase(c) == c) ||
                    Character.isDigit(c) || c == '/' || c == '.';
            if (upperChar)
               robot.keyPress(KeyEvent.VK_SHIFT);

            if (c == '\\') {
               robot.keyPress(KeyEvent.VK_ALT_GRAPH);
               robot.keyPress('8');
               robot.keyRelease('8');
               robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
            } else if (c == '/' || c == ':') {
               robot.keyPress(KeyEvent.VK_COLON);
               robot.keyRelease(KeyEvent.VK_COLON);
            } else if (c == '.' || c == ';') {
               robot.keyPress(KeyEvent.VK_SEMICOLON);
               robot.keyRelease(KeyEvent.VK_SEMICOLON);
            } else {
               robot.keyPress(Character.toUpperCase(c));
               robot.keyRelease(Character.toUpperCase(c));
            }
            if (upperChar)
               robot.keyRelease(KeyEvent.VK_SHIFT);
        }
    }
}