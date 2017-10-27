import com.gmail.socraticphoenix.tioj.Tio;
import com.gmail.socraticphoenix.tioj.TioRequest;
import com.gmail.socraticphoenix.tioj.TioResult;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class QuickTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TioRequest request = Tio.newRequest()
                .setCode("println(sys.args[0]) println(sys.input()) import(\"test\")")
                .addFile("test.shnap", "println(\"Hello World\")")
                .setArguments("hi", "hello")
                .setInput("Hello world\n")
                .setLang("shnap");

        Tio tio = Tio.MAIN;
        Set<String> langs = tio.queryLanguages().get().getResult().get();
        System.out.println(langs);

        System.out.println(tio.send(request).get().getResult().get().get(TioResult.Field.OUTPUT));
    }

}
