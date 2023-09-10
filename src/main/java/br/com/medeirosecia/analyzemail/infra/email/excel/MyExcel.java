package br.com.medeirosecia.analyzemail.infra.email.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class MyExcel {

    private Workbook workbook;
    private Sheet sheet;
    private LocalFileSystem localFileSystem;
    private String filePath;

    public MyExcel(LocalFileSystem localFileSystem){
        this.localFileSystem = localFileSystem;
        this.filePath = localFileSystem.getBaseFolder()+"PlanilhaNF-AnalyzedMail.xlsx";
        this.openWorkbook();
    }

    private void openWorkbook(){
        // check if file exists        
        File file = new File(this.filePath);
        if(file.exists()){
            try  {
                this.workbook = WorkbookFactory.create(file);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }else{
            try {
                this.workbook = new XSSFWorkbook();
                if(this.workbook.getNumberOfSheets() == 0){
                    this.workbook.createSheet("PlanilhaNF-AnalyzedMail");                    
                }
                this.sheet = this.workbook.getSheetAt(0);
                // check if there is a header, if not create the first row as header
                if(headerNotExists()){
                    this.addHeader();
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        
    }

    private boolean headerNotExists(){
        if(this.sheet.getRow(0) == null){
            return true;
        }
        return false;
    }

    private void addHeader(){
        this.sheet.createRow(0);
        this.sheet.getRow(0).createCell(0).setCellValue("Dt.Emissão");
        this.sheet.getRow(0).createCell(1).setCellValue("CNPJ Emitente");
        this.sheet.getRow(0).createCell(2).setCellValue("Chave de acesso");

    }


    public void saveWorkbook(){
        
        try (FileOutputStream outputStream = new FileOutputStream(this.filePath)) {
            this.workbook.write(outputStream);
            this.workbook.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }   

    private int getLastRow(){
        return this.sheet.getLastRowNum();
    }

    public void addRow(String[] row){
        int lastRow = this.getLastRow();
        this.sheet.createRow(lastRow + 1);
        for(int i = 0; i < row.length; i++){
            this.sheet.getRow(lastRow + 1).createCell(i).setCellValue(row[i]);
        }    
    }
}
