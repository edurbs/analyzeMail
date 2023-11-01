package br.com.medeirosecia.analyzemail.infra.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.medeirosecia.analyzemail.infra.filesystem.ConfigFile;

public class MyExcel {

    private Workbook workbook;
    private Sheet sheet;

    private String filePath;


    public MyExcel(String fileName ){

        this.filePath = new ConfigFile().getBaseFolder()+"\\"+fileName;

    }

    public void justOpen() throws IOException{
        // check if file exists
        File file = new File(this.filePath);
        if(file.exists()){
            this.workbook = new XSSFWorkbook(new FileInputStream(this.filePath));
        }else{
            this.workbook = new XSSFWorkbook();
        }
    }

    public void openWorkbook(String[] header){
        try {
            this.justOpen();
            if(this.workbook.getNumberOfSheets() == 0){
                this.workbook.createSheet("PlanilhaNF-AnalyzedMail");
            }
            this.sheet = this.workbook.getSheetAt(0);

            // check if there is a header, if not create the first row as header
            if(headerNotExists() && header!=null){
                this.addHeader(header);
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

    private void addHeader(String[] header){
        this.sheet.createRow(0);
        this.sheet.getRow(0).createCell(0).setCellValue(header[0]);
        this.sheet.getRow(0).createCell(1).setCellValue(header[1]);
        this.sheet.getRow(0).createCell(2).setCellValue(header[2]);
        this.sheet.getRow(0).createCell(3).setCellValue(header[3]);

    }



    public void saveAndCloseWorkbook() {

        try (FileOutputStream outputStream = new FileOutputStream(this.filePath)) {
            this.workbook.write(outputStream);
            this.workbook.close();

            outputStream.close();
        } catch (IOException e) {
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
