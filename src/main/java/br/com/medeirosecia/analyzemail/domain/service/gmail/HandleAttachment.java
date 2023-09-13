package br.com.medeirosecia.analyzemail.domain.service.gmail;


import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachment;
import br.com.medeirosecia.analyzemail.domain.service.pdf.AnalyzePDFText;
import br.com.medeirosecia.analyzemail.infra.email.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class HandleAttachment {
    private LocalFileSystem localFileSystem;
    private MyExcel myExcel;    
    
    private EmailAttachment att;


    public HandleAttachment(EmailAttachment att, LocalFileSystem localFileSystem){
        this.att = att;
        this.localFileSystem = localFileSystem;
    
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
                    
                    this.writeItAsExcel(analyzePDF);
                    this.myExcel.saveWorkbook();

                }else if(analyzePDF.isBoleto()){
                    
                    localFileSystem.savePdfBoleto(attachment, analyzePDF.getBoletoDate());
                    
                } 
                else{
                    
                    localFileSystem.savePdfOthers(attachment);
                    
                }

            }else if(extension.equals("XML")){
                localFileSystem.saveXml(attachment);
                
                
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
