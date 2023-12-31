package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import java.util.EnumMap;
import java.util.Map;

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
                        "Nota fiscal de serviço", "EMITENTE DA NFS-e", "Competência da NFS-e", "Data e Hora da emissão da NFS-e",
                        "Número da NFS-e", "Número da DPS", "Chave de Acesso da NFS-e", "Documento Auxiliar da NFS-e", "DANFSe v1.0",
                        "Nota fiscal avulsa", "EMITENTE DA NFS-e", "SERVIÇO PRESTADO", "VALOR TOTAL DA NFS-E", "TOMADOR DO SERVIÇO" }),


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
                        "Agente Receptor SERPRO", "Relação de Cálculo", "Tipo: Cálculo Mensal", "MODALIDADE : \"BRANCO\"",
                        "RENDIMENTOS TRIBUTÁVEIS", "RECIBO DE ENTREGA DA DECLARAÇÃO", "DEDUÇÕES LEGAIS",
                        "Demonstrativo de Pagamento de Salário", "SALÁRIO BASE", "data admissão", "cargo", "Horas Normais",
                        "IDENTIFICAÇÃO DO DECLARANTE", "RELAÇÃO DOS TRABALHADORES", "QUANTIDADE TRABALHADORES",
                        "IMPOSTO A RESTITUIR", "GANHO DE CAPITAL", "RESUMO DO FECHAMENTO - EMPRESA" });

        private final String[] keyWords;


        private PdfType(String[] keyWords) {
                this.keyWords = keyWords;
        }

        public String[] getKeyWords() {
                return this.keyWords;
        }

        public ReadPdfInterface getReadPdf(){

                Map<PdfType, ReadPdfInterface> map = new EnumMap<>(PdfType.class);

                map.put(PdfType.NF_PRODUTO, new ReadPdfNfProduto());
                map.put(PdfType.NF_SERVICO, new ReadPdfNfServico());
                map.put(PdfType.BOLETO, new ReadPdfBoleto());
                map.put(PdfType.OUTRO, new ReadPdfOutro());

                return map.get(this);
        }

        public PdfActionInterface getPdfAction(){

                Map<PdfType, PdfActionInterface> map = new EnumMap<>(PdfType.class);

                map.put(PdfType.NF_PRODUTO, new PdfActionNfProduto());
                map.put(PdfType.NF_SERVICO, new PdfActionNfServico());
                map.put(PdfType.BOLETO, new PdfActionBoleto());
                map.put(PdfType.OUTRO, new PdfActionOther());

                return map.get(this);
        }

}
