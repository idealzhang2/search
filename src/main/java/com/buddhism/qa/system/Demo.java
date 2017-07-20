package com.buddhism.qa.system;

import com.buddhism.qa.analysis.KeyWords;
import com.buddhism.qa.analysis.Words;
import com.buddhism.qa.analysis.answer.AnswerRewriteFilter;

import com.buddhism.qa.datasource.document.IndexFile;
import com.buddhism.qa.datasource.question.InputQuestionFromFile;
import com.buddhism.qa.files.FileConfig;
import com.buddhism.qa.model.*;
import com.buddhism.qa.retrieval.AnswerEvidence;
import com.buddhism.qa.retrieval.GenerateQuery;
import com.buddhism.qa.retrieval.QuestionEvidence;
import com.buddhism.qa.score.ReturnAnswer;
import com.buddhism.qa.score.answer.TermFrequencyCandidateScore;
import com.buddhism.qa.score.answer.TextualAlignmentCandidateAnswerScore;
import com.buddhism.qa.score.evidence.TermMatchEvidenceScore;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TT. Wu on 2017/5/14.
 */
public class Demo {
    private static final Logger LOG = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) throws IOException {
        //�������⼰ѡ��
        //����Դ���ļ�
        List<Question> questionList = new InputQuestionFromFile().getQuestionList(FileConfig.questionFile2);

        //�������ص��ĵ�����
        IndexFile indexFile = new IndexFile();
//        Directory directory = indexFile.indexData(FileConfig.wikiData);

        List<Directory> directoryList = indexFile.indexData(FileConfig.frequentlyAskedQuestions);
        List<Directory> directoryList1 = indexFile.indexData(FileConfig.textBooks);
        directoryList.addAll(directoryList1);
        directoryList.add(indexFile.indexData(FileConfig.wikiData));
        Directory[] directory = directoryList.toArray(new Directory[directoryList.size()]);

        int correct = 0;
        int sum = questionList.size();
        List<String> couldAnswer = new ArrayList<>();

        //��һ��������
        //Question: questionStr, questionType, options, exceptAnswer
        //Answer: answerStr
        for(int i = 0; i < questionList.size(); i++){
            Question question = questionList.get(i);
            System.out.println("******************************");
            System.out.println("��Ŀ��"+ question.getQuestionStr());

            /*�Բ���������и�д*/
            question = AnswerRewriteFilter.answerRewrite(question);

            /*Ϊ�ʾ��Լ�ѡ��ͳһ���зִ�*/
            question = Words.setWordList(question);

            /*�����ؼ���*/
            question = KeyWords.setKeyWord(question);

            /*��ȡ��ѯ(ͨ���ʾ���ɽ��в�ѯ)��term query*/
//            Query query1 = GenerateQuery.generateDescQueryFromKeyWords(question);
//            Query query2 = GenerateQuery.generateTitleQueryFromKeyWords(question);
            Query query1 = GenerateQuery.generateDescQueryFromKeyWords(question);

            /*��ȡ֤���б�*/
//            List<TextEvidence> evidenceList = QuestionEvidence.getEvidence(query1, DataSource.FREQUENTLY_ASKED_QUESTIONS, directories);
            List<TextEvidence> evidenceList = QuestionEvidence.getEvidence(query1, DataSource.WIKI, directory);

            /*ѡ����ŵ�֤��*/
            question = new TermMatchEvidenceScore().score(question, evidenceList);

            /*��ȡ��ѯ(ͨ���𰸻�ȡ��ѯ)��term query*/
            List<Answer> options = question.getOptions();
            for(int j = 0; j < options.size(); j++){
                Answer option = options.get(j);
                /*�ɴ𰸹ؼ������ɲ�ѯ*/
                Query query = GenerateQuery.generateDescQueryFromAnswerKeyWords(option);
                /*�����ı�֤��*/
                List<TextEvidence> optionEvidenceList = AnswerEvidence.getEvidence(query, DataSource.WIKI, directory);
                /*ѡ����ŵ�֤��*/
                option = new TermMatchEvidenceScore().score(option, optionEvidenceList);
                options.set(j, option);
            }
            question.setOptions(options);

            //����֤�ݶ�ѡ���������
            question = new TermFrequencyCandidateScore().score(question);
            question = new TextualAlignmentCandidateAnswerScore().score(question);
//            question = new TermFrequencyCandidateScore().reverseScore(question);

//            for(Answer option: question.getOptions()){
//                System.out.println(option.getAnswerStr()+": "+option.getScore());
//            }

            //��ȡ���Ŷ���ߵĴ�
            question = ReturnAnswer.getAnswer(question);

            System.out.println("Ԥ�ڴ�: "+question.getExpectAnswer().getAnswerStr());
            System.out.println("ϵͳ����: "+question.getReturnAnswer().getAnswerStr());

            if(question.getExpectAnswer().getAnswerStr().equals(question.getReturnAnswer().getAnswerStr())){
                correct += 1;
                couldAnswer.add(i + ": " + question.getQuestionStr()+"  ��: "+question.getExpectAnswer().getAnswerStr());
            }

            System.out.println("****************************************************");
        }

        LOG.info("���Իش�������У�");
        for(int i = 0; i < couldAnswer.size(); i++){
            System.out.println(couldAnswer.get(i));
        }
        LOG.info("ϵͳ׼ȷ�ʣ�");
        System.out.println("correct number: "+ correct);
        System.out.println("sum number: " + sum);
    }
}
