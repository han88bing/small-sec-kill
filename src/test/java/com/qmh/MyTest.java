package com.qmh;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class MyTest {
    @Test
    public void test1(){
        String s1 = "a"+"b"+"c";
        String s2 = "abc";
        /**
         执行细节：
         常量池中创建了三个变量 "a" "ab" "abc"
         **/
        System.out.println(s1==s2);//true
    }

    @Test
    public void test2_1(){
        String s1 = "a";
        String s2 = s1 + "b";
        String s3 = "ab";
        /**
         执行细节：
         1  StringBuilder s = new StringBuilder();
         2  s.append("a")
         3  s.append("b")
         4. s.toString(); 类似于 new String("ab")
         补充 jdk5.0之后使用的是StringBuilder,以前使用StringBuffer
         **/
        System.out.println(s3==s2);
    }


    @Test
    public void test2(){
        String s1 = "a";
        String s2 = "b";
        String s3 = "ab";
        /**
          执行细节：
            1  StringBuilder s = new StringBuilder();
            2  s.append("a")
            3  s.append("b")
            4. s.toString(); 类似于 new String("ab")
          补充 jdk5.0之后使用的是StringBuilder,以前使用StringBuffer
         **/
        String s4 = s1+s2;
        System.out.println(s3==s4);
    }

    @Test
    public void test3(){
        String s1 = null;
        String s2 = "b";
        String s3 = s1+s2;
        /**
         * 执行细节：
         *  1 StringBuilder s = new StringBuilder()
         *  2 s.append(s1)
         *  3 s.append("b")
         *  4 s.toString(); 类似于new String("nullb")
         */
        System.out.println(s3);
    }


    @Test
    public void test4(){
        final String s1 = "a";
        final String s2 = "b";
        String s3 = "ab";
        String s4 =s1 + s2; //从字符串常量池中取的
        System.out.println(s3==s4);
    }

    @Test
    public void test5(){
        /**
         * 执行细节：
         *   1  StringBuilder s = new StringBuilder()
         *   2  s.append("ABC");
         *   3. s.append("ABC");
         *   4. s.toString(); 类似于new String("ABCABC")
         */
        String str1 = new String("ABC") + "ABC";
    }

    @Test
    public void test6(){
        String str = new String("A")+new String("B");
    }

    @Test
    public void test7(){
        String str = new String("A"+"B");
    }

    @Test
    public void testIntern(){
        String  s = new String("1");
        s.intern();
        String s2 = "1";
        System.out.println(s==s2);

        String s3 = new String("1")+new String("1");
        s3.intern();
        String s4 = "11";
        System.out.println(s3==s4);
    }


    @Test
    public void test(){
        String s1 = "hello";
        String s2 = "world";
        String s3 = "helloworld";
        String s4 = "hello" + "world";
        String s5 = s1 + "world";
        String s6 = "hello" + s2;
        String s7 = s1 + s2;

        System.out.println(s3==s4);//true
        System.out.println(s3==s5);//false
        System.out.println(s3==s6);//false
        System.out.println(s3==s7);//false
        System.out.println(s5==s6);//false
        System.out.println(s5==s7);//false
        System.out.println(s6==s7);//false

        String s8 = s6.intern();
        System.out.println(s3==s8);//true
    }





}
