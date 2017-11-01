import com.gmail.socraticphoenix.tioj.Tio;
import com.gmail.socraticphoenix.tioj.TioRequest;
import com.gmail.socraticphoenix.tioj.TioResult;

import java.util.concurrent.ExecutionException;

public class QuickTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TioRequest request = Tio.newRequest()
                .setCode("public class Main {\n" +
                        "\n" +
                        "    public static void main(String[] args) {\n" +
                        "        System.out.println(\"Hello, World!\");\n" +
                        "    }\n" +
                        "    \n" +
                        "}\n")
                .setLang("java-openjdk");

        Tio tio = Tio.MAIN;
        TioResult result = tio.send(request).get().getResult().get();
        System.out.println(result.get(TioResult.Field.OUTPUT));
        System.out.println(result.get(TioResult.Field.DEBUG));
    }

}
