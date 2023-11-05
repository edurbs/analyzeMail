package br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoBarCodeTool;
import br.com.medeirosecia.analyzemail.domain.service.readpdf.boleto.BoletoType;

class BoletoBarCodeToolTest {

    @Test
    void testValueWithComum(){
        var key = "03399699255870003141033532101012995540000243012";
        var accessKeyTool = new BoletoBarCodeTool(key, BoletoType.COMUM);
        Double value = accessKeyTool.getValue();
        Assertions.assertEquals(2430.12, value);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        846900000015099800550016104349435198092332309258, 109.98
        858800000520844801232026310147241037281571538001, 5284.48
        858800000253847701792318007681050842330806400014, 2584.77
        858800000024072903631072880471400008585758822309, 207.29
        858000000127850003852323630716232510763395456617, 1285.0
    """)
    void testValuesConsumo(String key, String stringValue){
        var accessKeyTool = new BoletoBarCodeTool(key, BoletoType.DARF);
        Double value = accessKeyTool.getValue();
        Assertions.assertEquals(Double.parseDouble(stringValue), value);
    }
}
