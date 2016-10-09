package sweiss.SS16;

/**
 * Created by Nelson on 25.09.2016.
 */
public class javaTest {

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {

        }


        IPP ipp1 = new IPP();
        IPP ipp2 = new IPP();
        IPP ipp3 = new IPP();
        ipp2.setNextIPP(ipp3);
        ipp3.setNextIPP(ipp1);
        ipp1.setNextIPP(ipp2);


    }
}
