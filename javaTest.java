/**
 * Created by Nelson on 25.09.2016.
 */
public class javaTest {

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {

        }


        IPP ipp1 = new IPP();
        IPP ipp2 = new IPP();
        ipp1.setNextIPP(ipp2);
        IPP ipp3 = new IPP();
        ipp3.setNextIPP(ipp1);


    }
}
