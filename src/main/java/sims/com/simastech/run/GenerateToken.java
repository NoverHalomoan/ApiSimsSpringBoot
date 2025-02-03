package sims.com.simastech.run;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public String generatedInvoice(Integer jumlahtransaksi) {
        DecimalFormat df = new DecimalFormat("0000");
        String formattedId = df.format(jumlahtransaksi);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        String invoicenumber = "INV" + LocalDate.now().format(formatter) + "-" + formattedId;
        return invoicenumber;

    }

}
