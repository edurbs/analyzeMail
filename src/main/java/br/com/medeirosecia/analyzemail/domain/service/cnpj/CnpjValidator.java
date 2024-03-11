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

        // Calcula o primeiro dígito verificador
        int soma = 0;
        int peso = 2;
        for (int i = 11; i >= 0; i--) {
            soma += (cnpj.charAt(i) - '0') * peso;
            peso++;
            if (peso == 10) {
                peso = 2;
            }
        }
        int resto = soma % 11;
        int digito1 = resto < 2 ? 0 : 11 - resto;

        // Calcula o segundo dígito verificador
        soma = 0;
        peso = 2;
        for (int i = 12; i >= 0; i--) {
            soma += (cnpj.charAt(i) - '0') * peso;
            peso++;
            if (peso == 10) {
                peso = 2;
            }
        }
        resto = soma % 11;
        int digito2 = resto < 2 ? 0 : 11 - resto;

        // Verifica se os dígitos calculados coincidem com os dígitos informados
        return (cnpj.charAt(12) - '0' == digito1) && (cnpj.charAt(13) - '0' == digito2);
    }


}
