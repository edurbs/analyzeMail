package br.com.medeirosecia.analyzemail.domain.service.cnpj;

public class CnpjValidator {

    String cnpj = "";

    public CnpjValidator(String cnpj) {
        this.cnpj = cnpj;
    }

    public boolean isValid() {
        // Remove any non-digit characters
        cnpj = cnpj.replaceAll("\\D", "");

        // Check if the CNPJ has the correct length
        if (cnpj.length() != 14) {
            return false;
        }

        // Check if the CNPJ is all zeros
        if (cnpj.equals("00000000000000")) {
            return false;
        }

        // Calculate the first verification digit
        int sum = 0;
        int weight = 5;
        for (int i = 11; i > 0; i--) {
            sum += Integer.parseInt(cnpj.substring(11 - i, 12 - i)) * weight;
            weight--;
        }
        int verificationDigit1 = (sum % 11) < 2 ? 0 : 11 - (sum % 11);

        // Calculate the second verification digit
        sum = 0;
        weight = 6;
        for (int i = 12; i > 0; i--) {
            sum += Integer.parseInt(cnpj.substring(12 - i, 13 - i)) * weight;
            weight--;
        }
        int verificationDigit2 = (sum % 11) < 2 ? 0 : 11 - (sum % 11);

        // Check if the verification digits match the input CNPJ
        return cnpj.equals(cnpj.substring(0, 12) + verificationDigit1 + verificationDigit2);
    }


}
