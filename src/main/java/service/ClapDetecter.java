package service;

import com.musicg.api.ClapApi;
import com.musicg.api.WhistleApi;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Toan on 20.01.2015.
 * WaveType Detector for clapping modified
 */
public class ClapDetecter {
    private Wave wave;

    public ClapDetecter(Wave wave){
        this.wave = wave;
    }

    public double getClapProbability(){
        double probability = 0.0D;
        WaveHeader wavHeader = this.wave.getWaveHeader();
        short fftSampleSize = 1024;
        int fftSignalByteLength = fftSampleSize * wavHeader.getBitsPerSample() / 8;
        byte[] audioBytes = this.wave.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
        ClapApi clapApi = new ClapApi(wavHeader);

        try {
            int e = inputStream.available() / fftSignalByteLength;
            byte[] bytes = new byte[fftSignalByteLength];
            byte checkLength = 1;
            byte passScore = 1;
            ArrayList bufferList = new ArrayList();
            int numWhistles = 0;
            int numPasses = 0;

            int frameNumber;
            boolean isWhistle;
            for(frameNumber = 0; frameNumber < checkLength; ++frameNumber) {
                inputStream.read(bytes);
                isWhistle = clapApi.isClap(bytes);
                bufferList.add(Boolean.valueOf(isWhistle));
                if(isWhistle) {
                    ++numWhistles;
                }

                if(numWhistles >= passScore) {
                    ++numPasses;
                }
            }

            for(frameNumber = checkLength; frameNumber < e; ++frameNumber) {
                inputStream.read(bytes);
                isWhistle = clapApi.isClap(bytes);
                if(((Boolean)bufferList.get(0)).booleanValue()) {
                    --numWhistles;
                }

                bufferList.remove(0);
                bufferList.add(Boolean.valueOf(isWhistle));
                if(isWhistle) {
                    ++numWhistles;
                }

                if(numWhistles >= passScore) {
                    ++numPasses;
                }
            }

            probability = (double)numPasses / (double)e;
        } catch (IOException var18) {
            var18.printStackTrace();
        }

        return probability;
    }
}
