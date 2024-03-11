package br.com.medeirosecia.analyzemail.domain.service.readpdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ReadPdfNfServicoTest {

    ReadPdfNfServico readPdfNfServico;
    PdfTestUtils pdfUtils = new PdfTestUtils();

    void setUpOnePage(String fileName) {
        String pdfText = pdfUtils.getAllPages(fileName).keySet().iterator().next();
        readPdfNfServico = new ReadPdfNfServico();
        readPdfNfServico.setText(pdfText);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
               nfseBlumenau.pdf,    01/12/2023
               nfseBoaEsperanca.pdf, 06/09/2023
               nfseCaceres.pdf,      13/09/2023
               nfseCampoLargo.pdf,  01/02/2024
               nfseConfresa.pdf,    20/12/2023
               nfseCuiaba.pdf,  14/12/2023
               nfseCuiabaImagem.pdf, 02/09/2023
               nfseGoiania.pdf, 20/09/2023
               nfseGoianorte.pdf, 06/01/2024
               nfseNacional.pdf, 27/09/2023
               nfseNX.pdf, 18/09/2023
               nfseNX2.pdf, 02/03/2023
               nfsePontesLacerda.pdf, 16/08/2023
               nfseRio.pdf, 10/07/2023
               nfseRondonopolis.pdf, 17/01/2024
               nfseRondonopolis2.pdf, 13/01/2024
               nfseRondonopolis3.pdf, 17/01/2024
               nfseRondonopolis4.pdf, 27/02/2024
               nfseSJQM.pdf, 06/09/2023
               nfseUmuarama.pdf, 02/01/2024
            """)
    void date(String fileName, String date) {
        setUpOnePage(fileName);
        Assertions.assertEquals(date, pdfUtils.getFinalDate(readPdfNfServico.date()));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
               nfseBlumenau.pdf,    80.680.093/0001-81
               nfseBoaEsperanca.pdf, 07.652.413/0001-08
               nfseCaceres.pdf,      01016616000466
               nfseCampoLargo.pdf,  22.128.209/0001-78
               nfseConfresa.pdf,    33.963.449/0001-04
               nfseCuiaba.pdf,  11.587.975/0001-84
               nfseCuiabaImagem.pdf, 30.713.395/0001-30
               nfseGoiania.pdf, 27.706.427/0001-66
               nfseGoianorte.pdf, 34.612.751/0001-80
               nfseNacional.pdf, 49.226.286/0001-17
               nfseNX.pdf, 17.513.186/0001-85
               nfseNX2.pdf, 49.226.286/0001-17
               nfsePontesLacerda.pdf, 42.625.296/0001-86
               nfseRio.pdf, 03.486.424/0001-04
               nfseRondonopolis.pdf, 43.352.808/0001-40
               nfseRondonopolis2.pdf, 40.067.481/0003-39
               nfseRondonopolis3.pdf, 80732928004782
               nfseRondonopolis4.pdf, 14700980000220
               nfseSJQM.pdf, 18.252.944/0001-11
               nfseUmuarama.pdf, 23.070.686/0001-92
            """)
    void cnpjSupplier(String fileName, String cnpj) {
        setUpOnePage(fileName);
        cnpj=cnpj.replace(".", "").replace("/","").replace("-","");
        Assertions.assertEquals(cnpj, readPdfNfServico.cnpjSupplier());
    }

    @Test
    void value() {
    }

    @Test
    void accessKey() {
    }
}