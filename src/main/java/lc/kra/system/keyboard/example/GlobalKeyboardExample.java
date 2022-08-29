/**
 * Copyright (c) 2016 Kristian Kraljic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package lc.kra.system.keyboard.example;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Map.Entry;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import static java.awt.event.KeyEvent.*;

public class GlobalKeyboardExample {

    private static boolean run = true;
    private static boolean toggled = true;
    private static boolean running = false;
    private static int[] lastPress;
    private static Robot keypresser;

    static {
        try {
            keypresser = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws AWTException {

        // Might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails
        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // Use false here to switch to hook instead of raw input

        System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");

        for (Entry<Long, String> keyboard : GlobalKeyboardHook.listKeyboards().entrySet()) {
            System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());
        }

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {

            @Override
            public void keyPressed(GlobalKeyEvent event) {
                //banned keys
                int[] bans = {20, 9, 13};
                int key = event.getVirtualKeyCode();
                for (int i=0; i<bans.length; i++) {
                    if (key==bans[i]) return;
                }
                //end of banned keys
                if (running) return;
                if (event.getVirtualKeyCode() == 32 && event.isShiftPressed()) {
                    keypresser.keyPress(8); // 8 = backspace
                    keypresser.keyRelease(8); // 8 = backspace

                    toggled = !toggled;
                    System.out.println("Toggled "+ ((toggled) ? "on" : "off"));
                }
                if (!toggled) {
                    return;
                }

//                running = true;

                //left bracket is the repeater key.
                if (event.getVirtualKeyCode() == 219) {
                    keypresser.keyPress(8); // 8 = backspace
                    keypresser.keyRelease(8); // 8 = backspace

//                    System.out.println((char)lastPress[0]);
//                    System.out.println(Arrays.toString(lastPress));

                    int i = ((lastPress[0]>=65 && lastPress[0]<=90) || (lastPress[0]>=97 && lastPress[0]<=122)) ? 1 : 0;

                    try {
                        keypresser.keyPress((char) lastPress[i]);
                        keypresser.keyRelease((char) lastPress[i]);
                    } catch (Exception e) {
                        System.out.println("Invalid key.");
                    }
//                    pressUnicode(keypresser, lastPress);
                } else {
                    setPressed(event.getKeyChar(), event.getVirtualKeyCode());
                }
//                running = false;

            }

            @Override
            public void keyReleased(GlobalKeyEvent event) {
//                System.out.println(event);
            }
        });

//         try {
//             while (run) {
// //                Thread.sleep(128);
//             }
//         } finally {
//             keyboardHook.shutdownHook();
//         }
    }

    public static void setPressed(int key, int code) {
        lastPress = new int[]{key, code};
    }


}
