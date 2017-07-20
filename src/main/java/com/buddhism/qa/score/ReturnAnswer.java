package com.buddhism.qa.score;

import com.buddhism.qa.model.Answer;
import com.buddhism.qa.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ȷ�����յĴ�
 * Created by TT. Wu on 2017/5/14.
 */
public class ReturnAnswer {
    private static final Logger LOG = LoggerFactory.getLogger(ReturnAnswer.class);

    public static Question getAnswer(Question question){
        List<Answer> options = question.getOptions();
        int choose = 0;
        double max = 0;
        double score = 0;


        for(int i = 0; i < options.size(); i++){
            score = options.get(i).getScore();
            if(max < score){
                max = score;
                choose = i;
            }
        }

        //*********************************************
        //��ʱ���Դ���:�񶨼��

        double min = 10000;
        String questionStr = question.getQuestionStr();
        boolean negativeTest = questionStr.contains("����") || questionStr.contains("����")
                || questionStr.contains("������") || questionStr.contains("����")
                || questionStr.contains("������") || questionStr.contains("�������");
        LOG.info("negative test: "+negativeTest);
        if(negativeTest){
            for(int i = 0; i < options.size(); i++){
                score = options.get(i).getScore();
                if(min > score){
                    min = score;
                    choose = i;
                }
            }
        }

        //����
        //*********************************************
        question.setReturnAnswer(options.get(choose));
        return question;
    }
}
