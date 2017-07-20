package com.buddhism.qa.score.answer;

import com.buddhism.qa.model.Answer;
import com.buddhism.qa.model.Question;
import com.buddhism.qa.model.TextEvidence;
import com.buddhism.qa.model.Word;

import com.buddhism.qa.system.ScoreWeight;
import com.buddhism.qa.util.segmentation.Segmentor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ͨ���ı�����ķ����Դ𰸽��д��
 * Created by TT. Wu on 2017/5/18.
 */
public class TextualAlignmentCandidateAnswerScore implements AnswerScore {
    private static final Logger LOG  = LoggerFactory.getLogger(TextualAlignmentCandidateAnswerScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public Question score(Question question) {
        LOG.debug("***************");
        LOG.debug("�ı���������");

        //���ػ�����Ϣ�����⣬ѡ���Լ�����֤��
        List<Word> wordList = question.getWords();
        int questionTermsSize = wordList.size();

        List<TextEvidence> evidenceList = question.getEvidences();
        List<Answer> options = question.getOptions();

        //��ÿ��ѡ��ĵ÷ֽ��г�ʼ��
        double[] scores = new double[options.size()];
        for(int i = 0; i < scores.length; i++){
            scores[i] = 0.0;
        }

        for(TextEvidence textEvidence: evidenceList){
            String evidenceText = textEvidence.getTitle() + " " + textEvidence.getSnippet();

            for(int i = 0; i < options.size(); i++){
                String answerStr = options.get(i).getAnswerStr();

                for(int j = 0; j < questionTermsSize; j++){
                    StringBuilder textAlignment = new StringBuilder();
                    for(int k = 0; k < questionTermsSize; k++){
                        if(j == k){
                            textAlignment.append(answerStr);
                        } else{
                            textAlignment.append(wordList.get(k).getWordStr());
                        }
                    }

                    String textualAlignmentPattern = textAlignment.toString();

                    //�ݻ�Ϊ���ģʽ��֧��ģ��ƥ��
                    List<Word> textualAlignmentPatternTerms = Segmentor.getWordList(textualAlignmentPattern);
                    List<String> patterns = new ArrayList<>();
                    patterns.add(textualAlignmentPattern);
                    StringBuilder str = new StringBuilder();
                    int len = textualAlignmentPatternTerms.size();

                    for(int t = 0; t < len; t++){
                        str.append(textualAlignmentPatternTerms.get(t).getWordStr());
                        if(t < len-1){
                            str.append(".{0,5}");
                        }
                    }
                    patterns.add(str.toString());

                    //�ж��ı��Ƿ����
                    int count = 0;
                    int length = 0;

                    for(String pattern: patterns){
//                        LOG.info(pattern);
                        Pattern p = Pattern.compile(pattern);
                        Matcher matcher = p.matcher(evidenceText);
                        while (matcher.find()){
                            String text = matcher.group();
                            LOG.debug("������ı���"+text);
                            LOG.debug("�����ģʽ��"+ pattern);
                            count++;
                            length += text.length();
                        }
                    }

                    //���
                    if(count>0){
                        double avgLen = (double) length/count;
                        //���ⳤ��question����Ϊ������
                        //ƥ�䳤��avg����Ϊ������
                        int questionLen = question.getQuestionStr().length();
                        scores[i] += scoreWeight.getTextualAlignmentCandidateAnswerScoreWeight() * questionLen / avgLen;
                    }
                }
            }
        }

        for(int x = 0; x < scores.length; x++){
            LOG.info("ѡ��"+x+": "+scores[x]);
        }

        //���·�װ��question����
        for(int i = 0; i < options.size(); i++){
            Answer option = options.get(i);
            double score = option.getScore() + scores[i] * scoreWeight.getTermFrequencyAnswerScoreWeight();
            option.setScore(score);
            System.out.println(option.getScore());
            options.set(i, option);
        }
        question.setOptions(options);

        LOG.debug("�ı��������ֽ���");
        LOG.debug("***********************");
        return question;
    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }
}
