package test.com.buddhism.qa.analysis.answer;

import com.buddhism.qa.analysis.answer.AnswerRewriteFilter;
import com.buddhism.qa.analysis.answer.OptionRewrite;
import com.buddhism.qa.model.Answer;
import com.buddhism.qa.model.Question;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AnswerRewriteFilter Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 17, 2017</pre>
 */
public class AnswerRewriteFilterTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: answerRewrite(Question question)
     */
    @Test
    public void testAnswerRewrite() throws Exception {
//TODO: Test goes here...
        Question question = new Question("Ϊ��ף��ʱ�����ʺ�()��ף��");
        List<Answer> options = new ArrayList<>();
        Answer answer = new Answer("����Ʒ������");
        options.add(answer);
        answer = new Answer("��վ�");
        options.add(answer);
        answer = new Answer("�ľ�");
        options.add(answer);
        answer = new Answer("���Ͻ���");
        options.add(answer);
        question.setOptions(options);

        question = AnswerRewriteFilter.answerRewrite(question);
        for(Answer option: question.getOptions()){
            System.out.println(option.getAnswerStr());
        }

//        String regEx = "[����|����][��|��|ȫ][��|��|��|��]";
//        Pattern pattern = Pattern.compile(regEx);
//        String s = "���Ͻ���";
//        Matcher matcher = pattern.matcher(s);
//        System.out.println(matcher.matches());
    }


} 
