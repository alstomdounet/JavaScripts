/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alstom.javasorter;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import org.apache.log4j.Logger;

/**
 *
 * @author gmanciet
 */
public class FileDropHandler extends TransferHandler {

    protected static Logger logger = Logger.getLogger(FileDropHandler.class.getName());

    @Override
    public boolean canImport(TransferSupport support) {
        /* for the demo, we'll only support drops (not clipboard paste) */
        if (!support.isDrop()) {
            return false;
        }

        /* return false if the drop doesn't contain a list of files */
        if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }

        /* check to see if the source actions (a bitwise-OR of supported
         * actions) contains the COPY action
         */
        boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;

        /* if COPY is supported, choose COPY and accept the transfer */
        if (copySupported) {
            support.setDropAction(COPY);
            return true;
        }

        /* COPY isn't supported, so reject the transfer.
         *
         * Note: If you want to accept the transfer with the default
         *       action anyway, you could instead return true.
         */
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        /* fetch the Transferable */
        Transferable t = support.getTransferable();

        try {
            /* fetch the data from the Transferable */
            Object data = t.getTransferData(DataFlavor.javaFileListFlavor);

            /* data of type javaFileListFlavor is a list of files */
            List<File> fileList = (java.util.List) data;

            /* loop through the files in the file list */
            for (File file : fileList) {

                logger.info("Processing file : " + file);
                if (file.isFile() && file.getAbsolutePath().endsWith(".xml")) {
                    String filename = file.getName();

                    File newFile;
                    if (Interface.replaceRequested) {
                        newFile = CreateTempDirectory.createTempFile();

                    } else {
                        String newFilePath = file.getParent() + File.separator + filename.substring(0, filename.length() - 4) + "-new.xml";
                        newFile = new File(newFilePath);
                        if (newFile.exists()) {
                            logger.error("Output file already exists : " + newFile);
                            javax.swing.JOptionPane.showMessageDialog(null, "le fichier suivant existe déjà : " + newFile, "Fichier existant", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }
                    }

                    logger.info("Ordering variables in file : " + file);
                    ControlBuildProcessor.orderVariables(file.toString(), newFile.toString(), Interface.changeCoordinates);

                    if (Interface.replaceRequested) {
                        if (file.canWrite()) {
                            logger.debug("File can be written.");
                        } else {
                            logger.error("original file cannot be written : " + file);
                            javax.swing.JOptionPane.showMessageDialog(null, "le fichier suivant ne peut être écrasé : " + file, "Fichier verrouillé", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }

                        FileOutputStream out = new FileOutputStream(file);
                        FileInputStream in = new FileInputStream(newFile);

                        byte[] buf = new byte[1024];

                        int len;

                        while ((len = in.read(buf)) > 0) {

                            out.write(buf, 0, len);

                        }
                        in.close();
                        out.close();

                        logger.info("Writing output file : " + file);  
                        javax.swing.JOptionPane.showMessageDialog(null, "Fichier modifié : " + file, "Information", JOptionPane.INFORMATION_MESSAGE);

                    }
                    else {
                     logger.info("Writing output file : " + newFile);    
                     javax.swing.JOptionPane.showMessageDialog(null, "Fichier crée : " + newFile, "Information", JOptionPane.INFORMATION_MESSAGE);
                    }

                    
                } else if (file.isFile()) {
                    logger.error("Non-supported file : " + file);
                    javax.swing.JOptionPane.showMessageDialog(null, "Le fichier suivant n'est pas supporté : " + file.getName(), "Erreur de type", JOptionPane.ERROR_MESSAGE);
                } else {
                    logger.error("Non-supported directory : " + file);
                    javax.swing.JOptionPane.showMessageDialog(null, "Le dossier suivant n'est pas supporté : " + file.getName(), "Erreur de type", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        return true;
    }
}