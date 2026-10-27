package algoPractice.addtionalPractice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CompactorPractice {

    static class InnerCompactorPractice {
        
        private String cmpCond1;
        private  InbedClass inbedClass;

        public String getCmpCond1(){
            return  cmpCond1;
        }

        public InbedClass getInbedClass(){
            return  inbedClass;
        }
        
    }

    static class InbedClass {

        private Date date1;
        private Date date2;

        public Date getDate1(){
            return  date1;
        }

        public Date getDate2(){
            return date2;
        }

    }


    public static void main(String[] args) {

        List<InnerCompactorPractice> lisPractices = new ArrayList<>();

        // 按cmpCond1 排序, 下面两个一样
        lisPractices.sort(Comparator.comparing(InnerCompactorPractice :: getCmpCond1));
        lisPractices.sort( (t1, t2)-> t1.getCmpCond1().compareTo(t2.getCmpCond1()));
        //按cmpCond1 倒排
        lisPractices.sort(Comparator.comparing(InnerCompactorPractice::getCmpCond1).reversed());


        // 按 date1 排序， comparing 接收转换的 function， 上面 InnerCompactorPractice :: getCmpCond1 中的 InnerCompactorPractice告诉了入参类型，
        // 而这里lambda 需要明确类型InnerCompactorPractice。
       lisPractices.sort(Comparator.comparing( (InnerCompactorPractice t)->t.getInbedClass().getDate1()) ); 
       //Comparator reversed 会用装饰者模式创建新的Comparator， override compare 方法
       lisPractices.sort(Comparator.comparing( (InnerCompactorPractice t)->t.getInbedClass().getDate1()).reversed() ); 

       //等效于上面的 按 date1 倒排， Comparator comparing 把转换 function 应用于 Comparator.reverseOrder()创建的反向排序Comparator，构成一个新的Comparator
       lisPractices.sort(Comparator.comparing( (InnerCompactorPractice t)->t.getInbedClass().getDate1(), Comparator.reverseOrder()  ) ); 


       // 先按date1 排序， 如果 date1 相等， 再按date2 排序， 第一个comparing 得出按date1 排序的Comparator1， thenComparing 随后 得出按date2 排序的Comparator2，
       // 最后 新建Comparator3， 它先运行Comparator1 的 compare ，如果结果为 0（相等），就返回Comparator2 的 compare 结果。
       lisPractices.sort(
        Comparator.comparing( (InnerCompactorPractice t)-> t.getInbedClass().getDate1() )
        .thenComparing( (InnerCompactorPractice t)-> t.getInbedClass().getDate2())
        );

        // 先按date1 降序，然后按date1 升序
        lisPractices.sort(
            Comparator.comparing( (InnerCompactorPractice t)-> t.getInbedClass().getDate1(),  Comparator.reverseOrder())
            .thenComparing( (InnerCompactorPractice t)-> t.getInbedClass().getDate2())
            );

        //按date1 升序， 空 date1 放在最后。 nullsLast 表示空的放在非空的后面。
        // Comparator.nullsLast(Comparator.naturalOrder()) 就是外出处理 null 的Comparator 内部包装了一个 默认顺序的 Comparator， 当c1 和 c2 都非空的时候由这个Comparator处理。
        lisPractices.sort(
                Comparator.comparing( (InnerCompactorPractice t)-> t.getInbedClass().getDate1(),  Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing( (InnerCompactorPractice t)-> t.getInbedClass().getDate2())
                );


        //Comparator.comparing(Function) 和 Comparator.comparing(Function，Comparator ) 的区别：
        // comparing(Function) 直接新建一个Comparator， c1 和 c2 转换后直接 c1_t.compareTo(c2_t) 就是默认升序
        // comparing(Function，Comparator ) 直接新建一个Comparator， 这个Comparator 调用 入参Comparator的 compare（c1_t， c2_t）, 逻辑由入参Comparator定义。



        

    }
}
