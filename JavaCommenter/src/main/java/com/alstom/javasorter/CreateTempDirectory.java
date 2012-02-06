/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alstom.javasorter;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author gmanciet
 */
public class CreateTempDirectory {

    public static File createTempDirectory() throws IOException {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if (!temp.delete()) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!temp.mkdir()) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return temp;
    }
    
    public static File createTempFile() throws IOException {
        final File temp;

        temp = File.createTempFile("temporaryComponent", ".xml");

        return temp;
    }
}
