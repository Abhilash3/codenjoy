package com.globallogic.training.oleksii.morozov.sapperthehero.controller.input.impl;

import com.globallogic.training.oleksii.morozov.sapperthehero.controller.input.Reader;
import com.globallogic.training.oleksii.morozov.sapperthehero.controller.output.Printer;

import java.util.Scanner;

/**
 * User: oleksii.morozov
 * Date: 10/18/12
 * Time: 3:06 PM
 */
public class ConsoleReader implements Reader {
    private Printer printer;

    @Override
    public int read(String message) {
        while (true) {
            try {
                printer.print(message);
                int input = Integer.parseInt(new Scanner(System.in).nextLine());
                if (input < 1) {
                    throw new Exception();
                }
                return input;
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    @Override
    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

}
