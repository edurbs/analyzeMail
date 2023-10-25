package br.com.medeirosecia.analyzemail.domain.service.email;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import br.com.medeirosecia.analyzemail.domain.repository.EmailAttachmentDAO;
import br.com.medeirosecia.analyzemail.infra.excel.MyExcel;
import br.com.medeirosecia.analyzemail.infra.filesystem.BaseFolders;
import net.rationalminds.LocalDateModel;
import net.rationalminds.Parser;
import net.sourceforge.tess4j.Tesseract;

public class HandlePDF implements HandleAttachmentType {

    private String pdfText;
    private String dateSplitter = "";
    private BaseFolders baseFolders;

    private PDDocument pdfDocument;
    private EmailAttachmentDAO emailAttachmentDAO;
    private int keywordsFoundToBeNF = 0;
    private int keywordsFoundToBeNfse = 0;
    private int keywordsFoundToBeBoleto = 0;
    private String[] nfKeywords = { "nota fiscal", "emissão", 
            "autenticidade", "danfe", "documento auxiliar", "controle do fisco",
            "chave de acesso", "natureza da operação", "protocolo de autorização", "destinatário", "emitente",
            "dados dos produtos" };
    private String[] nfseKeywords = { "tomador", "serviço", "prestador", "nfs-e", "rps", "iss", "prefeitura",
            "municipal", "issqn", "serviços eletrônica", "nota fiscal eletrônica de serviços", "Nota fiscal de serviço", "Nota fiscal avulsa" };
    private String[] boletoKeywords = { "vencimento", "cedente", "referência", "pagador", "beneficiário",
            "nosso número", "valor do documento", "data do processamento", "mora", "multa", "juros", "carteira",
            "título", "pagável", "sacado", "débito automático", "total a pagar", "mês de referência",
            "serviços contratados",
            "autenticação mecânica", "período de apuração", "número do documento", "pagar este documento até",
            "documento de arrecadação", "pagar até", "pague com o pix"
    };

    
    @Override
    public void analyzeAttachment(EmailAttachmentDAO emailAttachmentDAO, BaseFolders baseFolders) {        
        this.emailAttachmentDAO = emailAttachmentDAO;
        this.baseFolders = baseFolders;

        this.readPDF();
        this.checkKeyWords();        

        if(isNfs()){
            baseFolders.savePdfNfs(emailAttachmentDAO, this.getDateNfs());
        }else if(isNF()){                    
            baseFolders.savePdfNF(emailAttachmentDAO, this.getDateNf());            
            this.writeNfExcel();        
        }else if(isBoleto()){            
            baseFolders.savePdfBoleto(emailAttachmentDAO, this.getBoletoDate());
            this.writeBoletoExcel();
        }else{            
            baseFolders.savePdfOthers(emailAttachmentDAO);            
        }

    }

    private void writeBoletoExcel(){
        // layout CNPJ Fornecedor | Data Vencimento | Valor | Nome Arquivo
        String[] dataVencimentoBoleto = this.getBoletoDate();        
        String cnpjFornecedor =this.getCnpjNextTo("nosso número");

        StringBuilder sb = new StringBuilder();
        for (String line : this.pdfText.toLowerCase().split("\n")) {
            if (line.matches(".*\\b(,\\d{2})\\b.*")) {
                sb.append(line + " / ");
            }            
        }        
        String valorBoleto = sb.toString(); 

        String[] header = new String[]{"CNPJ Fornecedor",
            "Data Vencimento",
            "Valores encontrados",
            "Nome do arquivo"
        };

        var myExcelBoleto = new MyExcel(this.baseFolders, "PlanilhaBoleto-AnalyzedMail.xlsx");
        myExcelBoleto.openWorkbook(header);

        String[] row = new String[] { cnpjFornecedor,
                dataVencimentoBoleto[0] + "/" + dataVencimentoBoleto[1] + "/" + dataVencimentoBoleto[2],
                valorBoleto,
                getFileName()
        };
        myExcelBoleto.addRow(row);
        try {
            myExcelBoleto.saveAndCloseWorkbook();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void writeNfExcel() {
        String[] header = new String[]{"Dt.Emissão",
            "CNPJ Emitente",
            "Chave de acesso",
            "Nome do arquivo"
        };

        var myExcel = new MyExcel(this.baseFolders, "PlanilhaNF-AnalyzedMail.xlsx");
        myExcel.openWorkbook(header);

        String[] date = getDateNf();
        String dataEmissao = date[0] + "/" + date[1] + "/" + date[2];
        String[] row = new String[] { dataEmissao,
                getCnpjNextTo("chave de acesso"),
                getChaveDeAcesso(),
                getFileName()
        };
        myExcel.addRow(row);
        try {
            myExcel.saveAndCloseWorkbook();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void readPDF() {
        try {            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(this.emailAttachmentDAO.getData());
            this.processPDF(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    private void processPDF(InputStream inputStream) {
        
        try {
            this.pdfDocument = PDDocument.load(CloseShieldInputStream.wrap(inputStream));
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
           
            this.pdfText = pdfTextStripper.getText(this.pdfDocument).toLowerCase();
            
            if(this.pdfText.length()<10){
                String text = this.getOCR();
                this.pdfText = text;
            }  

            this.pdfDocument.close();
        } catch (IOException  e) {
            this.pdfText = "";
        }   
        
    }

    private String getOCR()  {
       
        try {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.GRAY);
    
            Tesseract tesseract = new Tesseract();
    
            URL url = HandlePDF.class.getResource("/tesseract/fast/");
            String tessractDataPath = Paths.get(url.toURI()).toString();
            tesseract.setDatapath(tessractDataPath);
    
            tesseract.setLanguage("por");
            tesseract.setPageSegMode(1); // Automatic Page Segmentation with OSD
            
            String result = tesseract.doOCR(bufferedImage);            
            return result.toLowerCase();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return "";
    }

    public String getFileName(){
        return this.emailAttachmentDAO.getFileName();

    }

    public boolean isNF() {
        if (keywordsFoundToBeNF > 6 
                && keywordsFoundToBeNF > keywordsFoundToBeBoleto) {
            return true;
        }
        return false;
    }

    public boolean isNfs() {
        if (keywordsFoundToBeNfse > 6
                && keywordsFoundToBeNfse > keywordsFoundToBeBoleto) {
            return true;
        }
        return false;
    }

    public boolean isBoleto() {
        if (keywordsFoundToBeBoleto > 5 
                && keywordsFoundToBeBoleto > keywordsFoundToBeNF 
                && keywordsFoundToBeBoleto > keywordsFoundToBeNfse) {
            return true;
        }
        return false;
    }
    
    
    private void checkKeyWords() {
        this.keywordsFoundToBeNF=0;
        this.keywordsFoundToBeBoleto=0;
        this.keywordsFoundToBeNfse=0;

        for (String keyword : this.nfKeywords) {
            if (this.pdfText.contains(keyword.toLowerCase())) {
                this.keywordsFoundToBeNF++;
            }
        }

        for (String keyword : this.boletoKeywords) {
            if (this.pdfText.contains(keyword.toLowerCase())) {
                this.keywordsFoundToBeBoleto++;
            }
        }

        for (String keyword : this.nfseKeywords) {
            if (this.pdfText.contains(keyword.toLowerCase())) {
                this.keywordsFoundToBeNfse++;
            }
        }

    }

    public String[] getBoletoDate() {
        String date = "00/00/0000";
        Parser parser = new Parser();        
        int vencimento = this.pdfText.indexOf("vencimento");
        String tempText = this.pdfText;
        if(vencimento>0){
            tempText = this.pdfText.substring(vencimento, this.pdfText.length());
        }

        List<LocalDateModel> dates;
        try {
          dates = parser.parse(tempText);  
        } catch (Exception e) {
            return date.split("/");
        } 


        // search for date 
        for (LocalDateModel localDateModel : dates) {
            if(this.isValidDate(localDateModel)){
                date = localDateModel.getOriginalText();                
                return date.split(this.dateSplitter);                
            }
        }
        return date.split("/");
    }

    private void setDateSplitter(String date){
        String[] splitters = {"/","-","."};
        // search for splitter
        for (String splitter : splitters) {
            if(date.contains(splitter)){
                if(splitter.equals(splitters[2]))
                {
                    this.dateSplitter = "\\.";
                }else{
                    this.dateSplitter = splitter;
                }
                return;
            }
        }
        this.dateSplitter = "";
    }

    private boolean isValidDate(LocalDateModel localDateModel){
        boolean isValid = false;
        

        String dateOriginal = localDateModel.getOriginalText();
        this.setDateSplitter(dateOriginal);
        String[] dateSplit = dateOriginal.split(this.dateSplitter);        
        String day = dateSplit[0];
        if(day.length() == 1 || day.isBlank()){
            return false;
        }

        String[] date = convertDateTime(localDateModel).split("/");
        int year;
        try {
            year = Integer.parseInt(date[2]);            
        } catch (Exception e) {
            return false;
        }
        int thisYear = java.time.LocalDate.now().getYear();
        if (year >= thisYear - 4 && year <= thisYear + 4) {
            return true;
        }

        return isValid;
    }

    private String convertDateTime(LocalDateModel localDateModel){
        String date = localDateModel.getOriginalText();
        this.setDateSplitter(date);
        String[] dateSplit = date.split(this.dateSplitter);
        try {
            date = String.format("%02d",Integer.parseInt(dateSplit[0]))
                    +"/"
                    +String.format("%02d",Integer.parseInt(dateSplit[1]))
                    +"/"
                    +String.format("%04d",Integer.parseInt(dateSplit[2]));            
        } catch (Exception e) {            
            return "00/00/0000";
        }
        
        return date;
    }

    public String getChaveDeAcesso(){   

        String[] regexPatterns = {
            "\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}\\s*\\d{4}", // 11 blocks of 4 digits with two or mores spaces as separators            
            "\\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4} \\d{4}", // 11 blocks of 4 digits with one space as the separator
            "\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}\\.\\d{4}", // 11 blocks with dot as separator
            "\\d{2}\\.\\d{4}\\.\\d{12}\\.\\d{2}\\.\\d{3}\\.\\d{9}\\.\\d{1}\\.\\d{8}\\.\\d{1}\\.\\d{8}" // números com outro padrão de blocos com ponto como separador
        };
        
        for (String regex : regexPatterns) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(this.pdfText);
            String found;
            if(matcher.find()){
                found = matcher.group();
                found = found.replaceAll("\\.", " ");
                found = found.replaceAll("\\\s+", " ");
                return found;
            }            
        }    
        return "";
    }
    

    public String getCnpjNextTo(String targetWord){
        //String targetWord = "chave de acesso";
        
        Pattern pattern1 = Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}[\\\u2212\\-]?\\d{2}");
        Matcher matcher = pattern1.matcher(this.pdfText.toLowerCase());
        
        String cnpj = "";        
        String cnpjFound="";
        int lastHowFar = Integer.MAX_VALUE;

        while (matcher.find()) {
            cnpjFound = matcher.group();

            int indexOfTargetWord = this.pdfText.indexOf(targetWord);
            int indexOfCnpjFound = this.pdfText.indexOf(cnpjFound);
            int howFar = Math.abs(indexOfCnpjFound - indexOfTargetWord);

            if (howFar < lastHowFar) {
                lastHowFar = howFar;
                cnpj = cnpjFound;
            }
        }

        if(cnpj.isBlank() || cnpj.isEmpty()){
            Pattern pattern2 = Pattern.compile("\\d{14}");
            if(matcher.find()){
                cnpj = matcher.group();
            }else{
                matcher = pattern2.matcher(this.pdfText);
                if(matcher.find()){
                    cnpj = matcher.group();
                }
            }            
        }

        return cnpj;
    }

    public String[] getDateNf(){
        return this.getDateNear("autorização");
    }

    public String[] getDateNfs(){

        String[] date = {"01", "01", "0001"};
        String[] findStrings = {"Data e Hora da emissão da NFS-e",
                "Data e Hora de emiss",
                "Emissão da nota",
                "Data emissão",
                "Data"
        };
        for (String string : findStrings) {
            var dateNear = getDateNear(string);
            if(!dateNear[2].equals("0001")){
                date = dateNear;
                break;
            }
        }
      
        return date;
    }

    public String[] getDateNear(String targetWord){
        
        targetWord = targetWord.toLowerCase();        

        // date with - / or . as separator
        Pattern pattern = Pattern.compile("\\d{2}[-/\\.]\\d{2}[-/\\.]\\d{4}");
        Matcher matcher = pattern.matcher(this.pdfText);

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd.MM.yyyy");

        Date closestDate = null;
        int lastHowFar = Integer.MAX_VALUE;
        try {
            
            Date date = dateFormat1.parse("01-01-0001");

            while (matcher.find()) {
                String dataStr = matcher.group();
                
             
                if (dataStr.contains("-")) {
                    date = dateFormat1.parse(dataStr);
                } else if (dataStr.contains("/")) {
                    date = dateFormat2.parse(dataStr);
                } else if (dataStr.contains(".")) {
                    date = dateFormat3.parse(dataStr);
                } 
    
                // Encontre a posição da palavra "emissão" no texto
                int indexOfEmissao = this.pdfText.indexOf(targetWord);
    
                // Encontre a posição da data atual no texto
                int indexOfDate = this.pdfText.indexOf(dataStr);
    
                // Calcule a distância entre a data atual e a palavra "emissão"
                int howFar = Math.abs(indexOfDate - indexOfEmissao);
    
                // Verifique se esta data está mais próxima da palavra "emissão" do que a anterior
                if (howFar < lastHowFar) {
                    lastHowFar = howFar;
                    closestDate = date;
                }
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  

        if (closestDate == null) {
            return new String[]{"","",""};
        }

        LocalDate closestLocalDate = closestDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int year = closestLocalDate.getYear();
        
        int thisYear = java.time.LocalDate.now().getYear();
        if (year < thisYear - 5 || year > thisYear + 5){
            return new String[]{"","",""};
        }

        return new String[]{(String.format("%02d",closestLocalDate.getDayOfMonth())),
                String.format("%02d",closestLocalDate.getMonthValue()),
                String.format("%04d",year)};
    
    }



    
}
