package com.buddhism.qa.util.segmentation;

import com.buddhism.qa.files.FileConfig;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

/**
 * Created by TT. Wu on 2017/5/15.
 */
public class AnsjTest1 {
    public static void main(String[] args) throws Exception {

        // ���û��ʵ�
        String str = "��̽������ⲿ��Ӱ����.��һ��dota��";
        System.out.println(ToAnalysis.parse(str));
        // �����ʻ� ��̽����� douta��
        Forest dic1 = new Forest();
        Library.insertWord(dic1, new Value("��̽�����", "define", "1000"));
        Forest dic2 = new Forest();
        Library.insertWord(dic2, new Value("dota��", "define", "1000"));
        Result result = ToAnalysis.parse(str, dic1, dic2);
        List<Term> terms = result.getTerms();
        for (Term term: terms){
            System.out.println(term.getName());
            System.out.println(term.getNatureStr());
            System.out.println(term.getRealName());
            System.out.println(term.getOffe());
            System.out.println(term.from());
            System.out.println("***************");
        }
    }

}
