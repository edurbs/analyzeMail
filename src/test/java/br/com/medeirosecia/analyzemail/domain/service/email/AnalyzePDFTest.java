package br.com.medeirosecia.analyzemail.domain.service.email;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnalyzePDFTest {
    @Test
    void testGetBoletoDate() {
        String boleto="\"Pague agora com o seu Pix\r\n" + //
                "Para efetuar o pagamento via Pix, utilize a op\u00E7\u00E3o Pix de seu aplicativo\r\n" + //
                "e aponte a c\u00E2mera do seu aparelho para o QR code ao lado.\r\n" + //
                "001-9 00190.00009  03581.058009  00000.046177  1  94690000888889\r\n" + //
                "Recibo do Pagador\r\n" + //
                "Nome do Pagador / Endere\u00E7o CNPJ Data de Vencimento\r\n" + //
                "LUXOR AGRO S.A 44.120.365/0001-25\r\n" + //
                "AVENIDA AVENIDA NIEMEYER, DE 1 ATE 550 \r\n" + //
                "22450-220 RIO DE JANEIRO RJ\r\n" + //
                "10/09/2023\r\n" + //
                "Ag\u00EAncia/C\u00F3digo do Benefici\u00E1rio\r\n" + //
                "3325-1/67084-7\r\n" + //
                "Nome do Benefici\u00E1rio / Endere\u00E7o CNPJ Nosso N\u00FAmero\r\n" + //
                "VAGNER RODRIGUES TOLEDO\r\n" + //
                "HAITI 703   APT   703 - JARDIM DAS AMERI\r\n" + //
                "78060-618 CUIABA MT\r\n" + //
                "30.713.395/0001-30 00035810580000000046\r\n" + //
                "Valor do Documento\r\n" + //
                "8.888,89\r\n" + //
                "Uso do Banco Nr. do documento Esp\u00E9cie Doc Aceite Data Processamento (=) Valor Pago\r\n" + //
                "00315 DM N 02/09/2023\r\n" + //
                "Autentica\u00E7\u00E3o mec\u00E2nica\r\n" + //
                "........................................................................................................................................................................................\r\n" + //
                "001-9 00190.00009  03581.058009  00000.046177  1  94690000888889\r\n" + //
                "Local do Pagamento Data de Vencimento\r\n" + //
                "Pagar preferencialmente nos canais de autoatendimento do Banco do Brasil. 10/09/2023\r\n" + //
                "Nome do Benefici\u00E1rio CNPJ Ag\u00EAncia/C\u00F3digo do Benefici\u00E1rio\r\n" + //
                "VAGNER RODRIGUES TOLEDO 30.713.395/0001-30 3325-1/67084-7\r\n" + //
                "Data do Documento Nr. do documento Esp\u00E9cie Doc Aceite Data Processamento Nosso N\u00FAmero\r\n" + //
                "02/09/2023 00315 DM N 02/09/2023 00035810580000000046\r\n" + //
                "Uso do Banco Carteira Esp\u00E9cie Quantidade (x) Valor (=) Valor do Documento\r\n" + //
                "17 R$ 8.888,89\r\n" + //
                "Informa\u00E7\u00F5es de Responsabilidade do Benefici\u00E1rio (-) Desconto/Abatimento\r\n" + //
                "0,00\r\n" + //
                "JUROS: Taxa Mensal         :   1,00 % APOS 10/09/2023\r\n" + //
                "MULTA DE     2,00% A PARTIR DE   11/09/2023\r\n" + //
                "REFERENTE NF N. 315\r\n" + //
                "(+) Juros/Multa\r\n" + //
                "0,00\r\n" + //
                "(=) Valor Cobrado\r\n" + //
                "8.888,89\r\n" + //
                "Nome do Pagador / Endere\u00E7o CNPJ\r\n" + //
                "LUXOR AGRO S.A 44.120.365/0001-25\r\n" + //
                "AVENIDA AVENIDA NIEMEYER, DE 1 ATE 550 \r\n" + //
                "22450-220 RIO DE JANEIRO RJ\r\n" + //
                "Benefici\u00E1rio Final CPF / CNPJ\r\n" + //
                "Autentica\u00E7\u00E3o mec\u00E2nica - Ficha de Compensa\u00E7\u00E3o\r\n" + //
                "\"";
        AnalyzePDF analyzePDF = new AnalyzePDF();
        analyzePDF.setPdfText(boleto);           
        String[] date = analyzePDF.getBoletoDate();                
        Assertions.assertEquals(date[0], "10");
        Assertions.assertEquals(date[1], "09");
        Assertions.assertEquals(date[2], "2023");
    }

    @Test
    public void testGetBoletoDate2(){
        String boleto="Vencimento\r\n" + //
                "30/08/2023\r\n" + //
                "Benefici\u00E1rio: A. VOLPATO & CIA LTDA Ag\u00EAncia/Benefici\u00E1rio\r\n" + //
                "\r\n" + //
                "Data do Documento N\u00FAmero do Documento Esp.Doc. Aceite\r\n" + //
                "17/02/2023 66692/0\r\n" + //
                "\r\n" + //
                "DM N\r\n" + //
                "\r\n" + //
                "Nosso N\u00FAmero\r\n" + //
                "\r\n" + //
                "Uso do Banco Carteira Valor (=) Valor Documento\r\n" + //
                "\r\n" + //
                "(-) Desconto/Abatimento\r\n" + //
                "\r\n" + //
                "(+) Mora/Multa\r\n" + //
                "\r\n" + //
                "(+) Outros Acr\u00E9scimos\r\n" + //
                "\r\n" + //
                "Instru\u00E7\u00F5es de Responsabilidade do benefici\u00E1rio:           *** Valores expressos em R$ ***\r\n" + //
                "Valor cobrado por dia de atraso: R$ 47,00\r\n" + //
                "Valor multa a pagar ap\u00F3s vencimento: R$ 564,00\r\n" + //
                "Ap\u00F3s o vencimento o cr\u00E9dito ser\u00E1 bloqueado e o t\u00EDtulo encaminhado para cart\u00F3rio.\r\n" + //
                "N\u00E3o conceder desconto sem autoriza\u00E7\u00E3o.\r\n" + //
                "ESTE BOLETO REPRESENTA DUPLICATA CEDIDA FIDUCIARIAMENTE AO BANCO SAFRA S/A, FICANDO\r\n" + //
                "VEDADO O PAGAMENTO DE QUALQUER OUTRA FORMA QUE N\u00C3O ATRAV\u00C9S DO PRESENTE BOLETO.\r\n" + //
                "Pagador LUXOR LOTUS LTDA\r\n" + //
                "CNPJ/CPF 44.262.241/0001-84\r\n" + //
                "R FAZ RANCHO DO COURO,S/N    ROD ROD\r\n" + //
                "78.250-000-Pontes e Lacerda-MT\r\n" + //
                "\r\n" + //
                "RECIBO DO PAGADOR\r\n" + //
                "\r\n" + //
                "Local do Pagamento:\r\n" + //
                "Pag\u00E1vel em qualquer Banco do Sistema de Compensa\u00E7\u00E3o.\r\n" + //
                "\r\n" + //
                "Vencimento\r\n" + //
                "30/08/2023\r\n" + //
                "Ag\u00EAncia/Benefici\u00E1rio\r\n" + //
                "\r\n" + //
                "Data do Documento N\u00FAmero do Documento Aceite Dt. Processamento\r\n" + //
                "17/02/2023 66692/0 N\r\n" + //
                "\r\n" + //
                "Nosso N\u00FAmero\r\n" + //
                "\r\n" + //
                "Uso do Banco Carteira Esp\u00E9cie Quantidade Valor (=) Valor Documento\r\n" + //
                "\r\n" + //
                "(-) Desconto/Abatimento\r\n" + //
                "\r\n" + //
                "(+) Mora/Multa\r\n" + //
                "\r\n" + //
                "(+) Outros Acr\u00E9scimos\r\n" + //
                "\r\n" + //
                "Instru\u00E7\u00F5es de Responsabilidade do benefici\u00E1rio:           *** Valores expressos em R$ ***\r\n" + //
                "Valor cobrado por dia de atraso: R$ 47,00\r\n" + //
                "Valor multa a pagar ap\u00F3s vencimento: R$ 564,00\r\n" + //
                "Ap\u00F3s o vencimento o cr\u00E9dito ser\u00E1 bloqueado e o t\u00EDtulo encaminhado para cart\u00F3rio.\r\n" + //
                "N\u00E3o conceder desconto sem autoriza\u00E7\u00E3o.\r\n" + //
                "ESTE BOLETO REPRESENTA DUPLICATA CEDIDA FIDUCIARIAMENTE AO BANCO SAFRA S/A, FICANDO\r\n" + //
                "VEDADO O PAGAMENTO DE QUALQUER OUTRA FORMA QUE N\u00C3O ATRAV\u00C9S DO PRESENTE BOLETO.\r\n" + //
                "Pagador LUXOR LOTUS LTDA\r\n" + //
                "CNPJ/CPF 44.262.241/0001-84\r\n" + //
                "R FAZ RANCHO DO COURO,S/N    ROD ROD\r\n" + //
                "78.250-000-Pontes e Lacerda-MT\r\n" + //
                "\r\n" + //
                "Dt. Processamento\r\n" + //
                "\r\n" + //
                "42297.14508  00010.139467  00011.713724  2 94580002820000\r\n" + //
                "\r\n" + //
                "17137\r\n" + //
                "\r\n" + //
                "17137\r\n" + //
                "\r\n" + //
                "28.200,00\r\n" + //
                "\r\n" + //
                "08/09/2023\r\n" + //
                "\r\n" + //
                "08/09/2023\r\n" + //
                "\r\n" + //
                "R$ 28.200,00\r\n" + //
                "\r\n" + //
                "R$\r\n" + //
                "\r\n" + //
                "Esp\u00E9cie\r\n" + //
                "\r\n" + //
                "Esp.Doc.\r\n" + //
                "DM\r\n" + //
                "\r\n" + //
                "CNPJ:\r\n" + //
                "\r\n" + //
                "Local do Pagamento:\r\n" + //
                "Pag\u00E1vel em qualquer Banco do Sistema de Compensa\u00E7\u00E3o.\r\n" + //
                "\r\n" + //
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -- -  Corte Aqui - - - - - - - - - - - - - -  - - - - - - - - - - - -\r\n" + //
                "\r\n" + //
                "00.319.835/0001-09\r\n" + //
                "\r\n" + //
                "422-7\r\n" + //
                "\r\n" + //
                "| |\r\n" + //
                "\r\n" + //
                "| 422-7 |\r\n" + //
                "\r\n" + //
                "BANCO SAFRA S/A\r\n" + //
                "\r\n" + //
                "BANCO SAFRA S/A\r\n" + //
                "\r\n" + //
                "R CEARA,1288                         CENTRO               Pontes e Lacerda-MT CEP: 78.250-000\r\n" + //
                "\r\n" + //
                "Benefici\u00E1rio: A. VOLPATO & CIA LTDA\r\n" + //
                "R CEARA,1288                         CENTRO               Pontes e Lacerda-MT CEP: 78.250-000\r\n" + //
                "\r\n" + //
                "CNPJ: 00.319.835/0001-09\r\n" + //
                "\r\n" + //
                "    2\r\n" + //
                "\r\n" + //
                "    2\r\n" + //
                "\r\n" + //
                " 14500 / 00SAFRA S.A\r\n" + //
                "\r\n" + //
                " 14500 / 00SAFRA S.A\r\n" + //
                "\r\n" + //
                "Autentica\u00E7\u00E3o Mecanica\r\n" + //
                "FICHA DE COMPENSA\u00C7\u00C3O";
        AnalyzePDF analyzePDF = new AnalyzePDF();        
        analyzePDF.setPdfText(boleto);           
        String[] date = analyzePDF.getBoletoDate();                
        Assertions.assertEquals(date[0], "30");
        Assertions.assertEquals(date[1], "08");
        Assertions.assertEquals(date[2], "2023");
    }

    @Test
    public void testGetBoletoDate3(){
        String boleto = "Vencimento Ag\u00EAncia/C\u00F3digo do Benefici\u00E1rio N\u00FAmero do Documento Nosso N\u00FAmero\r\n" + //
                "\r\n" + //
                "Valor do Documento (-) Descontos (+) Acr\u00E9scimos (=) Valor Cobrado\r\n" + //
                "\r\n" + //
                "Pagador\r\n" + //
                "\r\n" + //
                "Benefici\u00E1rio Autentica\u00E7\u00E3o Mec\u00E2nica\r\n" + //
                "\r\n" + //
                "Pagador\r\n" + //
                "\r\n" + //
                "Local de Pagamento Vencimento\r\n" + //
                "\r\n" + //
                "Benefici\u00E1rio Ag\u00EAncia/C\u00F3digo do Benefici\u00E1rio\r\n" + //
                "\r\n" + //
                "Data do Documento Esp\u00E9cie Doc Data do Processamento Nosso N\u00FAmero\r\n" + //
                "\r\n" + //
                "Uso do Banco Carteira Esp\u00E9cie Moeda Qtde Moeda (=) Valor do Documento\r\n" + //
                "\r\n" + //
                "(-) Desconto\r\n" + //
                "\r\n" + //
                "(-) Outras Dedu\u00E7\u00F5es/Abatimento\r\n" + //
                "\r\n" + //
                "(+) Mora/Multa/Juros\r\n" + //
                "\r\n" + //
                "(+) Outros Acr\u00E9scimos\r\n" + //
                "\r\n" + //
                "(=) Valor Cobrado\r\n" + //
                "\r\n" + //
                "Autentica\u00E7\u00E3o Mec\u00E2nica - Ficha de Compensa\u00E7\u00E3o\r\n" + //
                "\r\n" + //
                "N\u00FAmero do Documento Aceite\r\n" + //
                "\r\n" + //
                "(x) Valor\r\n" + //
                "\r\n" + //
                " | 001-9 | 00190.00009 02431.194006 00000.803171 2 95010000079613\r\n" + //
                "12/10/2023 2480-5/02431194-4 226499149 24311940000000803\r\n" + //
                "796,13\r\n" + //
                "PASTO VIVO S.A. - CNPJ 43.308.064/0001-67\r\n" + //
                "RODOVIA BR 174, SN - KM 219 A ESQUERDA - 78240-000 PORTO ESPERIDIAO-MT\r\n" + //
                "C\u00F3d. Cliente:16257 /  Agrupamento de t\u00EDtulos Ref. t\u00EDtulos:  226499143-01 (796,13), Pedidos/Notas: 23\r\n" + //
                "\r\n" + //
                "Sacador/Avalista\r\n" + //
                "\r\n" + //
                "Favorito Supermercado Ltda - CNPJ 00.954.305/0001-32\r\n" + //
                "Av. Marechal Rondon, 1738, Centro - 78250-000 Pontes e Lacerda-MT\r\n" + //
                "\r\n" + //
                " | 001-9 | 00190.00009 02431.194006 00000.803171 2 95010000079613\r\n" + //
                "12/10/2023\r\n" + //
                "Favorito Supermercado Ltda 00.954.305/0001-32\r\n" + //
                "Av. Marechal Rondon, 1738, Centro - 78250-000 Pontes e Lacerda-MT 2480-5/02431194-4\r\n" + //
                "06/09/2023 226499149 DM N 06/09/2023 24311940000000803\r\n" + //
                "17 / 019 R$ 796,13\r\n" + //
                "Instru\u00E7\u00F5es - Texto de Responsabilidade do Benefici\u00E1rio\r\n" + //
                "Protesto correspondente a 7 dias corridos - N\u00E3o receber apos 7 dias vencido\r\n" + //
                "Ap\u00F3s o Vencimento cobrar Juros de R$0,33 ao Dia.\r\n" + //
                "Cobrar multa de R$15.92 ap\u00F3s o Vencimento.\r\n" + //
                "Protestar 7 dias apos o Vencimento\r\n" + //
                "\r\n" + //
                "PASTO VIVO S.A. - CNPJ 43.308.064/0001-67\r\n" + //
                "RODOVIA BR 174, SN - KM 219 A ESQUERDA - 78240-000 PORTO ESPERIDIAO-MT\r\n" + //
                "C\u00F3d. Cliente:16257 /  Agrupamento de t\u00EDtulos Ref. t\u00EDtulos:  226499143-01 (796,13), Pedidos/Notas: 23\r\n" + //
                "\r\n" + //
                "Sacador/Avalista";
        AnalyzePDF analyzePDF = new AnalyzePDF();
        analyzePDF.setPdfText(boleto);
                
        
        String[] date = analyzePDF.getBoletoDate();                
        Assertions.assertEquals(date[0], "12");
        Assertions.assertEquals(date[1], "10");
        Assertions.assertEquals(date[2], "2023");
    }

    @Test
    public void testGetBoletoDate4(){
        String boleto="P\u00E1gina: 1/\r\n" + //
                "Data:  24/08/2023\r\n" + //
                "\r\n" + //
                "SECRETARIA MUNICIPAL DE ADMINISTRA\u00C7\u00C3O E FINAN\u00C7AS\r\n" + //
                "\r\n" + //
                "PREFEITURA MUNICIPAL VILA BELA DA SANT\u00CDSSIMA TRINDADE\r\n" + //
                "\r\n" + //
                "Estado de Mato Grosso\r\n" + //
                "\r\n" + //
                "1\r\n" + //
                "\r\n" + //
                "PIX\r\n" + //
                "\r\n" + //
                "GER\u00CANCIA MUNICIPAL DE TRIBUTOS\r\n" + //
                "\r\n" + //
                "ACEITE\r\n" + //
                "\r\n" + //
                "AG\u00CANCIA\r\n" + //
                "\r\n" + //
                "AL\u00CDQUOTA\r\n" + //
                "\r\n" + //
                "AUTENTICA\u00C7\u00C3O MEC\u00C2NICA\r\n" + //
                "\r\n" + //
                "CARTEIRA\r\n" + //
                "\r\n" + //
                "CEDENTE\r\n" + //
                "\r\n" + //
                "DATA DO DOCUMENTO ESP\u00C9CIE DE DOCUMENTO DATA DO PROCESSAMENTO\r\n" + //
                "\r\n" + //
                "MOEDA\r\n" + //
                "\r\n" + //
                "NOSSO N\u00DAMERO\r\n" + //
                "\r\n" + //
                "N\u00BA DO DOCUMENTO\r\n" + //
                "\r\n" + //
                "PARCELA\r\n" + //
                "\r\n" + //
                "CONTRIBUINTE:\r\n" + //
                "\r\n" + //
                "VALOR\r\n" + //
                "\r\n" + //
                "VENCIMENTO\r\n" + //
                "\r\n" + //
                "%  X\r\n" + //
                "\r\n" + //
                "81610000311-9 13744760202-0 30920000238-9 68300000011-0\r\n" + //
                "\r\n" + //
                "BETHA SISTEMAS LTDA\r\n" + //
                "\r\n" + //
                "20/09/2023\r\n" + //
                "\r\n" + //
                "REFERENTE\r\n" + //
                "\r\n" + //
                "24/08/2023\r\n" + //
                "1/1\r\n" + //
                "\r\n" + //
                "PREFEITURA MUNICIPAL VILA BELA DA SANT\u00CDSSIMA TRINDADE\r\n" + //
                "\r\n" + //
                "1095-\r\n" + //
                "\r\n" + //
                "CARN\u00CA\r\n" + //
                "\r\n" + //
                "0\r\n" + //
                "\r\n" + //
                "000238683 24/08/2023\r\n" + //
                "R$\r\n" + //
                "\r\n" + //
                "238683\r\n" + //
                "\r\n" + //
                "31.113,74\r\n" + //
                "\r\n" + //
                "REFERENTE AO(S) ANO(S): 2023\r\n" + //
                "61194 - BMS PARTICIPACOES LTDA\r\n" + //
                "\r\n" + //
                "81610000311-9 13744760202-0 30920000238-9 68300000011-0\r\n" + //
                "\r\n" + //
                "VENCIMENTO ORIGINAL: 20/09/2023\r\n" + //
                "\r\n" + //
                "Receita diversa\r\n" + //
                "\r\n" + //
                "SIM\r\n" + //
                "\r\n" + //
                "RECEITA VALOR\r\n" + //
                "ITBIRR 31.113,74\r\n" + //
                "\r\n" + //
                "N\u00C3O RECEBER AP\u00D3S VENCIMENTO.\r\n" + //
                "\r\n" + //
                "Receita diversa 109178174\r\n" + //
                "\r\n" + //
                "LOTE QUADRA\r\n" + //
                "\r\n" + //
                "CPF/CNPJ:\r\n" + //
                "\r\n" + //
                "INSCRI\u00C7\u00C3O IMOBILIARIA\r\n" + //
                "\r\n" + //
                "40.122.259/0001-20\r\n" + //
                "ENDERE\u00C7O: Fazenda FAZ SILMAR - GLEBA TARUMA, N\u00BA 0 - N\u00E3o Informado - Vila Bela da Sant\u00EDssima Trindade(MT)(78245000)\r\n" + //
                "\r\n" + //
                "TIPO DE SERVI\u00C7O: ITBI RURAL PMVB\r\n" + //
                "OBSERVA\u00C7\u00C3O: RECOLHIMENTO REFERENTE AO PEDIDO DE\r\n" + //
                "IMUNIDADE DE ITBI - BMS PARTICIPA\u00C7\u00D5ES LTDA - CNPJ\r\n" + //
                "40.122.259/0001-20\r\n" + //
                "\r\n" + //
                "Pag\u00E1vel: Pix de qualquer ag\u00EAncia ou Banco do Brasil, Caixa, Bradesco, Sicredi e Casa Lot\u00E9rica.\r\n" + //
                "\r\n" + //
                "(-) DESCONTO\r\n" + //
                "\r\n" + //
                "(+) CORRE\u00C7\u00C3O MONET\u00C1RIA\r\n" + //
                "\r\n" + //
                "0,00\r\n" + //
                "\r\n" + //
                "(+) JUROS\r\n" + //
                "\r\n" + //
                "0,00\r\n" + //
                "\r\n" + //
                "(+) MULTA\r\n" + //
                "\r\n" + //
                "0,00\r\n" + //
                "\r\n" + //
                "(=) VALOR TOTAL\r\n" + //
                "\r\n" + //
                "RECOLHIMENTO REFERENTE AO\r\n" + //
                "PEDIDO DE IMUNIDADE DE ITBI -\r\n" + //
                "BMS PARTICIPA\u00C7\u00D5ES LTDA - CNPJ\r\n" + //
                "40.122.259/0001-20 ITBI RURAL\r\n" + //
                "PMVB\r\n" + //
                "\r\n" + //
                "31113,74\r\n" + //
                "\r\n" + //
                "VALOR TRIBUTO\r\n" + //
                "\r\n" + //
                "ACEITE\r\n" + //
                "\r\n" + //
                "AG\u00CANCIA\r\n" + //
                "\r\n" + //
                "AL\u00CDQUOTA\r\n" + //
                "\r\n" + //
                "AUTENTICA\u00C7\u00C3O MEC\u00C2NICA\r\n" + //
                "\r\n" + //
                "CARTEIRA\r\n" + //
                "\r\n" + //
                "CEDENTE\r\n" + //
                "\r\n" + //
                "DATA DO DOCUMENTO ESP\u00C9CIE DE DOCUMENTO DATA DO PROCESSAMENTO\r\n" + //
                "\r\n" + //
                "MOEDA\r\n" + //
                "\r\n" + //
                "NOSSO N\u00DAMERO\r\n" + //
                "\r\n" + //
                "N\u00BA DO DOCUMENTO\r\n" + //
                "\r\n" + //
                "PARCELA\r\n" + //
                "\r\n" + //
                "CONTRIBUINTE:\r\n" + //
                "\r\n" + //
                "VALOR\r\n" + //
                "\r\n" + //
                "VENCIMENTO\r\n" + //
                "\r\n" + //
                "%  X\r\n" + //
                "\r\n" + //
                "20/09/2023\r\n" + //
                "\r\n" + //
                "REFERENTE\r\n" + //
                "\r\n" + //
                "24/08/2023\r\n" + //
                "1/1\r\n" + //
                "\r\n" + //
                "PREFEITURA MUNICIPAL VILA BELA DA SANT\u00CDSSIMA TRINDADE\r\n" + //
                "\r\n" + //
                "1095-\r\n" + //
                "\r\n" + //
                "CARN\u00CA\r\n" + //
                "\r\n" + //
                "0\r\n" + //
                "\r\n" + //
                "000238683 24/08/2023\r\n" + //
                "R$\r\n" + //
                "\r\n" + //
                "238683\r\n" + //
                "\r\n" + //
                "31.113,74\r\n" + //
                "\r\n" + //
                "REFERENTE AO(S) ANO(S): 2023\r\n" + //
                "61194 - BMS PARTICIPACOES LTDA\r\n" + //
                "\r\n" + //
                "VENCIMENTO ORIGINAL: 20/09/2023\r\n" + //
                "\r\n" + //
                "Receita diversa\r\n" + //
                "\r\n" + //
                "SIM\r\n" + //
                "\r\n" + //
                "RECEITA VALOR\r\n" + //
                "ITBIRR 31.113,74\r\n" + //
                "\r\n" + //
                "N\u00C3O RECEBER AP\u00D3S VENCIMENTO.\r\n" + //
                "\r\n" + //
                "Receita diversa 109178174\r\n" + //
                "\r\n" + //
                "LOTE QUADRA\r\n" + //
                "\r\n" + //
                "CPF/CNPJ:\r\n" + //
                "\r\n" + //
                "INSCRI\u00C7\u00C3O IMOBILIARIA\r\n" + //
                "\r\n" + //
                "40.122.259/0001-20\r\n" + //
                "ENDERE\u00C7O: Fazenda FAZ SILMAR - GLEBA TARUMA, N\u00BA 0 - N\u00E3o Informado - Vila Bela da Sant\u00EDssima Trindade(MT)(78245000)\r\n" + //
                "\r\n" + //
                "TIPO DE SERVI\u00C7O: ITBI RURAL PMVB\r\n" + //
                "OBSERVA\u00C7\u00C3O: RECOLHIMENTO REFERENTE AO PEDIDO DE\r\n" + //
                "IMUNIDADE DE ITBI - BMS PARTICIPA\u00C7\u00D5ES LTDA - CNPJ\r\n" + //
                "40.122.259/0001-20\r\n" + //
                "\r\n" + //
                "Pag\u00E1vel: Pix de qualquer ag\u00EAncia ou Banco do Brasil, Caixa, Bradesco, Sicredi e Casa Lot\u00E9rica.\r\n" + //
                "\r\n" + //
                "(-) DESCONTO\r\n" + //
                "\r\n" + //
                "(+) CORRE\u00C7\u00C3O MONET\u00C1RIA\r\n" + //
                "\r\n" + //
                "0,00\r\n" + //
                "\r\n" + //
                "(+) JUROS\r\n" + //
                "\r\n" + //
                "0,00\r\n" + //
                "\r\n" + //
                "(+) MULTA\r\n" + //
                "\r\n" + //
                "0,00\r\n" + //
                "\r\n" + //
                "(=) VALOR TOTAL\r\n" + //
                "\r\n" + //
                "RECOLHIMENTO REFERENTE AO\r\n" + //
                "PEDIDO DE IMUNIDADE DE ITBI -\r\n" + //
                "BMS PARTICIPA\u00C7\u00D5ES LTDA - CNPJ\r\n" + //
                "40.122.259/0001-20 ITBI RURAL\r\n" + //
                "PMVB\r\n" + //
                "\r\n" + //
                "31113,74\r\n" + //
                "\r\n" + //
                "VALOR TRIBUTO\r\n" + //
                "\r\n" + //
                "CNPJ 03.214.160/0001-21\r\n" + //
                "\r\n" + //
                "CNPJ 03.214.160/0001-21\r\n" + //
                "\r\n" + //
                "PIX";
        AnalyzePDF analyzePDF = new AnalyzePDF();
        analyzePDF.setPdfText(boleto);               
        
        String[] date = analyzePDF.getBoletoDate();                
        Assertions.assertEquals(date[0], "20");
        Assertions.assertEquals(date[1], "09");
        Assertions.assertEquals(date[2], "2023");
    }
}
