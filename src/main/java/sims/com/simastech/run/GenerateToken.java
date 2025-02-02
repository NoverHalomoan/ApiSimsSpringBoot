package sims.com.simastech.run;

import java.util.Random;

public class GenerateToken {

    String TotalChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    Random rnd = new Random();
    StringBuilder strbld;

    public String geneteratedtoken(Integer lengths) {
        strbld = new StringBuilder();
        while (strbld.length() < lengths) {
            int index = rnd.nextInt(TotalChar.length());
            strbld.append(TotalChar.charAt(index));
        }
        return strbld.toString();
    }

}
