package br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto;

public enum BoletoType {
        CONSUMO_VIVO(new String[] { "Cód. Débito Automático",
                        "Planos Anatel",
                        "Telefonica Brasil S.A." }),
        ENERGISA(new String[]{"Energia ativa em kWh",
                        "DISTRIBUIDORA DE ENERGIA",
                        "conta de energia",
                        "energisa",
                        "unidade consumidora",
                        "CONSUMO FATURADO",
                        "Consumo kWh",
                        "Sistema de Distribuição",
                        "ENERGISA MATO GROSSO"}),
        DARF(new String[] { "Documento de Arrecadação",
                        "de Receitas Federais",
                        "Documento de Arrecadação de Receitas Federais",
                        "Composição do Documento de Arrecadação",
                        "Pague com o PIX" }),
        DARF_SICALC(new String[] { "Documento de Arrecadação",
                        "de Receitas Federais", "Sicalc", "sicalc contribuinte",
                        "Documento de Arrecadação de Receitas Federais",
                        "Composição do Documento de Arrecadação",
                        "Pague com o PIX" }),
        GRU_SIMPLES(new String[] { "MINISTÉRIO DA FAZENDA",
                        "SECRETARIA DO TESOURO NACIONAL",
                        "Código do Recolhimento",
                        "Guia de Recolhimento da União - GRU",
                        "GRU SIMPLES" }),
        SEFAZ_MT(new String[] { "GOVERNO DO ESTADO DE MATO GROSSO",
                        "SECRETARIA DE ESTADO DE FAZENDA",
                        "DOCUMENTO DE ARRECADAÇÃO - DAR MODELO 1 - AUT",
                        "OBRIGATÓRIO O USO DO",
                        "SELO FISCAL NA SAÍDA",
                        "Nº T.P.A.R.",
                        "PARA OUTRA U.F.",
                        "Modelo aprovada pela Portaria nº 085/2002" }),
        GRRF(new String[] { "14 - Qtde Trabalhadores",
                        "GRRF - Guia de Recolhimento Rescisório do FGTS",
                        "Versão do Aplicativo",
                        "09 - FPAS",
                        "10 - Simples" }),
        GRF(new String[] { "GRF - GUIA DE RECOLHIMENTO DO FGTS",
                        "GFIP - SEFIP",
                        "09-ID RECOLHIMENTO",
                        "10-INSCRIÇÃO/TIPO ( 8 )",
                        "07-ALÍQUOTA FGTS" }),
        COMUM(new String[0]);

        private final String[] keyWords;

        private BoletoType(String[] keyWords) {
                this.keyWords = keyWords;
        }

        public String[] getBoletoKeyWords() {
                return this.keyWords;
        }
}
