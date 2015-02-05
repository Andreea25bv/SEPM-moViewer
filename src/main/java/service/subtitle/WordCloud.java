package service.subtitle;

/**
 * Created by powerowle on 09.01.2015.
 */
public class WordCloud implements Comparable<WordCloud> {

    private String word;
    private Integer count;

    public WordCloud(String word, Integer count) {
        this.word = word;
        this.count = count;

    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(WordCloud entry) {
        if (this.count > entry.getCount()) {
            return 1;
        } else if (this.count == entry.getCount()) {
            return 0;
        }
        else return -1;
    }
}