package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionBoleto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionInterface;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfProduto;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionNfServico;
import br.com.medeirosecia.analyzemail.domain.service.pdfaction.PdfActionOther;

public enum PdfType {

        NF_PRODUTO(new String[] { "nota fiscal", "emissão",
                        "autenticidade", "danfe", "documento auxiliar", "controle do fisco",
                        "chave de acesso", "natureza da operação", "protocolo de autorização", "destinatário",
                        "emitente",
                        "dados dos produtos" }),

        NF_SERVICO(new String[] { "tomador", "serviço", "prestador", "nfs-e", "rps", "iss", "prefeitura",
                        "municipal", "issqn", "serviços eletrônica", "nota fiscal eletrônica de serviços",
                        "Nota fiscal de serviço",
                        "Nota fiscal avulsa" }),


        BOLETO(new String[] { "vencimento", "cedente", "referência", "pagador", "beneficiário",
                        "nosso número", "valor do documento", "data do processamento", "mora", "multa", "juros",
                        "carteira", "título", "pagável", "sacado", "débito automático", "total a pagar",
                        "mês de referência",
                        "autenticação mecânica", "período de apuração", "número do documento",
                        "gru simples", "guia de recolhimento do fgts", "guia de recolhimento rescisório",
                        "SEM RESPONSABILIDADE DO BANCOOB",
                        "PAGAVEL PREFERENCIALMENTE NO SICOOB", "Cooperativa contratante",
                        "texto de responsabilidade do beneficiário",
                        "pagar este documento até", "consumo faturado", "dar modelo", "documento de arrecadação",
                        "pagar até",
                        "Energia ativa em kWh",
                        "DISTRIBUIDORA DE ENERGIA S.A.",
                        "CONSUMO FATURADO",
                        "Consumo kWh",
                        "DOCUMENTO AUXILIAR DA NOTA FISCAL DE ENERGIA ELÉTRICA ELETRÔNICA",
                        "ENERGISA MATO GROSSO" }),

        OUTRO(new String[] { "Declaração de Ajuste Anual", "Declaração IRPF", "Renda da Pessoa Física",
                        "NÚMERO DO RECIBO de sua declaração", "quotas do imposto em atraso", "ANO-CALENDÁRIO",
                        "informação da situação do processamento", "Declaração recebida via Internet",
                        "Agente Receptor SERPRO",
                        "RENDIMENTOS TRIBUTÁVEIS", "RECIBO DE ENTREGA DA DECLARAÇÃO", "DEDUÇÕES LEGAIS",
                        "IDENTIFICAÇÃO DO DECLARANTE",
                        "IMPOSTO A RESTITUIR", "GANHO DE CAPITAL" });

        private final String[] keyWords;

        private PdfType(String[] keyWords) {
                this.keyWords = keyWords;
        }

        public String[] getKeyWords() {
                return this.keyWords;
        }

        public ReadPdfInterface getReadPdf(){
                switch(this){
                        case NF_PRODUTO:
                                return new ReadPdfNfProduto();
                        case NF_SERVICO:
                                return new ReadPdfNfServico();
                        case BOLETO:
                                return new ReadPdfBoleto();
                        default:
                                return new ReadPdfOutro();
                }
        }

        public PdfActionInterface getPdfAction(){
                switch(this){
                        case NF_PRODUTO:
                                return new PdfActionNfProduto();
                        case NF_SERVICO:
                                return new PdfActionNfServico();
                        case BOLETO:
                                return new PdfActionBoleto();
                        default:
                                return new PdfActionOther();
                }
        }

}
