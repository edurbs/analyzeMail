package br.com.medeirosecia.analyzemail.infra.email.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public class MyExcel {

    private Workbook workbook;
    private Sheet sheet;
    
    private String filePath;
    private String[] header;

    public MyExcel(LocalFileSystem localFileSystem, String fileName, String[] header){
        this.header = header;
   
        this.filePath = localFileSystem.getBaseFolder()+"\\"+fileName;
        this.openWorkbook();
    }

    private void openWorkbook(){
        // check if file exists        
        File file = new File(this.filePath);
        if(file.exists()){            
            try {
                this.workbook = new XSSFWorkbook(new FileInputStream(this.filePath));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
        }else{
            this.workbook = new XSSFWorkbook();
        }
        try {
            if(this.workbook.getNumberOfSheets() == 0){
                this.workbook.createSheet("PlanilhaNF-AnalyzedMail");                    
            }
            this.sheet = this.workbook.getSheetAt(0);

            // check if there is a header, if not create the first row as header
            if(headerNotExists() && this.header!=null){
                this.addHeader();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
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
        this.sheet.getRow(0).createCell(0).setCellValue(this.header[0]);
        this.sheet.getRow(0).createCell(1).setCellValue(this.header[1]);
        this.sheet.getRow(0).createCell(2).setCellValue(this.header[2]);
        this.sheet.getRow(0).createCell(3).setCellValue(this.header[3]);

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
