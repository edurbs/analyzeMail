package br.com.medeirosecia.analyzemail.domain.service.gmail;

import br.com.medeirosecia.analyzemail.console.LocalConsole;
import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.domain.service.pdf.AnalyzePDFText;
import br.com.medeirosecia.analyzemail.infra.email.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;
import javafx.scene.control.TextArea;

public class HandleAttachment {
    private LocalFileSystem localFileSystem;
    private MyExcel myExcel;    
    private LocalConsole console;
    private EmailAttachment att;
    private TextArea debugArea;

    public HandleAttachment(EmailAttachment att, LocalConsole console, LocalFileSystem localFileSystem, TextArea debugArea ){
        this.att = att;
        this.localFileSystem = localFileSystem;
        this.debugArea = debugArea;
        this.console = console;
        this.myExcel = new MyExcel(this.localFileSystem, "PlanilhaNF-AnalyzedMail.xlsx", null);
        saveIfInteresting(this.att);
        
    }

    private void saveIfInteresting(EmailAttachment attachment) {
        String filename = attachment.getFileName();
        String extension = getExtension(filename);       

        if(!extension.isEmpty()){

            if(extension.equals("PDF")){
                
                AnalyzePDFText analyzePDF = new AnalyzePDFText(attachment);

                if(analyzePDF.isNF()){                    
                    localFileSystem.savePdfNF(attachment, analyzePDF.getDataEmissao());
                    this.debugArea.appendText("\nSalvando PDF como NF "+attachment.getFileName());
                    this.writeItAsExcel(analyzePDF);
                    this.myExcel.saveWorkbook();

                }else if(analyzePDF.isBoleto()){
                    
                    localFileSystem.savePdfBoleto(attachment, analyzePDF.getBoletoDate());
                    this.debugArea.appendText("\nSalvando PDF como Boleto "+attachment.getFileName());
                } 
                else{
                    
                    localFileSystem.savePdfOthers(attachment);
                    this.debugArea.appendText("\nSalvando PDF como outro "+attachment.getFileName());
                }

            }else if(extension.equals("XML")){
                localFileSystem.saveXml(attachment);
                this.debugArea.appendText("\nSalvando XML "+attachment.getFileName());
                
            } 
        }
    }

    
    private String getExtension(String filename) {
        if (filename.length() == 3) {
            return filename.toUpperCase();
          } else if (filename.length() > 3) {
            return filename.substring(filename.length() - 3).toUpperCase();
          }
        return "";
          
    }

    private void writeItAsExcel(AnalyzePDFText analyzePDF) {       
        
        String[] date = analyzePDF.getDataEmissao();
        String dataEmissao = date[0]+"/"+date[1]+"/"+date[2];
        String row[] = new String[]{ dataEmissao,
                    analyzePDF.getCNPJEmitente(), 
                    analyzePDF.getChaveDeAcesso(),
                    analyzePDF.getFileName()
                };
        this.myExcel.addRow(row);
    }



    
}
