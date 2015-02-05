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
import edu.cmu.sphinx.util.TimeFrame;

import java.io.IOException;
import java.io.InputStream;

/**
 * Speech recognizer that works with audio resources.
 *
 * @see sphinx.api.myLiveSpeechRecognizer live speech recognizer
 */
public class StreamSpeechRecognizer extends AbstractSpeechRecognizer {

    /**
     * Constructs new stream recognizer.
     *
     * @param configuration configuration
     */
    public StreamSpeechRecognizer(Configuration configuration)
        throws IOException
    {
        super(configuration);
    }

    public void startRecognition(InputStream stream) {
        startRecognition(stream, TimeFrame.INFINITE);
    }

    /**
     * Starts recognition process.
     *
     * Starts recognition process and optionally clears previous data.
     *
     * @see         sphinx.api.StreamSpeechRecognizer#stopRecognition()
     */
    public void startRecognition(InputStream stream, TimeFrame timeFrame) {
        recognizer.allocate();
        context.setSpeechSource(stream, timeFrame);
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see edu.cmu.sphinx.api.StreamSpeechRecognizer#stopRecognition()
     */
    public void stopRecognition() {
        recognizer.deallocate();
    }
}
