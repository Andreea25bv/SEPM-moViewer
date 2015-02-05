/*
 * Copyright 2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package sphinx.api;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.frontend.util.StreamDataSource;

import java.io.IOException;


/**
 * High-level class for live speech recognition. Modified by Toan to suit needs.
 * allocation is different and line will be closed, so it will be available again.
 */
public class myLiveSpeechRecognizer extends AbstractSpeechRecognizer {

    private Microphone microphone;

    /**
     * Constructs new live recognition object.
     *
     * @param configuration common configuration
     */
    public myLiveSpeechRecognizer(Configuration configuration) throws IOException
    {
        super(configuration);
        recognizer.allocate();
    }

    public void openLine(){
        microphone = speechSourceProvider.getMicrophone();
        context.getInstance(StreamDataSource.class)
                .setInputStream(microphone.getStream());
    }

    /**
     * Starts recognition process.
     *
     * @param clear clear cached microphone data
     * @see         sphinx.api.myLiveSpeechRecognizer#stopRecognition()
     */
    public void startRecognition(boolean clear) {
        openLine();
        microphone.startRecording();
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see sphinx.api.myLiveSpeechRecognizer#startRecognition(boolean)
     */
    public void stopRecognition() {
        microphone.stopRecording();
        //recognizer.deallocate();
    }

    public void deallocate(){
        recognizer.deallocate();
    }
}
