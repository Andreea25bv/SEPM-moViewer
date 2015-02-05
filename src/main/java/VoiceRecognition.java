import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;

/**
 * Created by Toan on 09.12.2014.
 */
public class VoiceRecognition {

    public static void main(String [ ] args)
    {
        Configuration configuration = new Configuration();

        // Set path to acoustic model.
        //configuration.setAcousticModelPath("file:///C:/Users/Toan/IdeaProjects/qse-sepm-ws14-01/src/main/resources/HUB4_8gau_13dCep_16k_40mel_133Hz_6855Hz/cd_continuous_8gau");
        //configuration.setAcousticModelPath("resource:/voxforge-en-r0_1_3/model_parameters/voxforge_en_sphinx.cd_cont_3000");
        configuration.setAcousticModelPath("resource:/cmusphinx-5prealpha-en-us-2.0adapt");
        // Set path to dictionary.
        //configuration.setDictionaryPath("file:///C:/Users/Toan/IdeaProjects/qse-sepm-ws14-01/src/main/resources/HUB4_8gau_13dCep_16k_40mel_133Hz_6855Hz/cmudict.06d");
        //configuration.setDictionaryPath("resource:/voxforge-en-r0_1_3/etc/cmudict.0.7a");
        configuration.setDictionaryPath("resource:/cmudict.0.7a");
        // Set language model.
        //configuration.setLanguageModelPath("file:///C:/Users/Toan/IdeaProjects/qse-sepm-ws14-01/src/main/resources/HUB4_trigram_lm/language_model.arpaformat.dmp");
        //configuration.setLanguageModelPath("resource:/voxforge-en-r0_1_3/etc/voxforge_en_sphinx.lm");

        configuration.setUseGrammar(true);
        configuration.setGrammarPath("resource:/");
        configuration.setGrammarName("moviewer");




        //configuration.

        //configuration.setAcousticModelPath("resource:/voxforge-de-r20140216/model_parameters/voxforge.cd_cont_4000");
        // Set path to dictionary.
        //configuration.setDictionaryPath("file:///C:/Users/Toan/IdeaProjects/qse-sepm-ws14-01/src/main/resources/HUB4_8gau_13dCep_16k_40mel_133Hz_6855Hz/cmudict.06d");
        //configuration.setDictionaryPath("resource:/voxforge-de-r20140216/etc/voxforge.dic");
        // Set language model.
        //configuration.setLanguageModelPath("file:///C:/Users/Toan/IdeaProjects/qse-sepm-ws14-01/src/main/resources/HUB4_trigram_lm/language_model.arpaformat.dmp");
        //configuration.setLanguageModelPath("resource:/voxforge-de-r20140216/etc/voxforge.lm.dmp");
        LiveSpeechRecognizer recognizer = null;
        try {
            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Start recognition process pruning previously cached data.
        recognizer.startRecognition(true);

        Boolean yes = false;

        while(true) {
            SpeechResult result = recognizer.getResult();



            System.out.println(result.getHypothesis());


        }

        // Pause recognition process. It can be resumed then with startRecognition(false).
        //recognizer.stopRecognition();

        // Print utterance string without filler words.

    }
}