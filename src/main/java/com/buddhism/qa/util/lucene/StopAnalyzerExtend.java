package com.buddhism.qa.util.lucene;

/**
 * Created by TT. Wu on 2017/5/6.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * �Զ���Analyzerʵ����չͣ�ô�
 * 1. �̳���Analyzer����дcreateComponents(String)����
 * 2. ά���Լ���ͣ�ôʴʵ�
 * 3. ��дTokenStreamComponents��ѡ����ʵĹ��˲���
 */
class StopAnalyzerExtend extends Analyzer {
    private CharArraySet stopWordSet;//ֹͣ�ʴʵ�

    public CharArraySet getStopWordSet() {
        return this.stopWordSet;
    }

    public void setStopWordSet(CharArraySet stopWordSet) {
        this.stopWordSet = stopWordSet;
    }

    public StopAnalyzerExtend() {
        super();
        setStopWordSet(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    /**
     * @param stops ��Ҫ��չ��ֹͣ��
     */
    public StopAnalyzerExtend(List<String> stops) {
        this();
        /**���ֱ��ΪstopWordSet��ֵ�Ļ����ᱨ�����쳣��������Ϊ��StopAnalyzer����ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
         * ENGLISH_STOP_WORDS_SET ������Ϊ���ɸ��ĵ�set����
         * Exception in thread "main" java.lang.UnsupportedOperationException
         * at org.apache.lucene.analysis.util.CharArrayMap$UnmodifiableCharArrayMap.put(CharArrayMap.java:592)
         * at org.apache.lucene.analysis.util.CharArraySet.add(CharArraySet.java:105)
         * at java.util.AbstractCollection.addAll(AbstractCollection.java:344)
         * at MyAnalyzer.<init>(AnalyzerDemo.java:146)
         * at MyAnalyzer.main(AnalyzerDemo.java:162)
         */
        //stopWordSet = getStopWordSet();
        stopWordSet = CharArraySet.copy(getStopWordSet());
        stopWordSet.addAll(StopFilter.makeStopSet(stops));
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new LowerCaseTokenizer();
        return new TokenStreamComponents(source, new StopFilter(source, stopWordSet));
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> strings = new ArrayList<String>() {{
            add("С����");
            add("������");
        }};
        Analyzer analyzer = new StopAnalyzerExtend(strings);
        String content = "С���� and ������ are playing together!";
        TokenStream tokenStream = analyzer.tokenStream("myfield", content);
        tokenStream.reset();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            // �Ѿ����˵��Զ���ͣ�ô�
            // �����playing   together
            System.out.println(charTermAttribute.toString());
        }
        tokenStream.end();
        tokenStream.close();
    }
}