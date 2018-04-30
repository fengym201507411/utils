package com.fym.utils.common;

import com.fym.utils.base.MarketingExcelExport;
import com.fym.utils.interfaces.ExcelCell;
import com.fym.utils.interfaces.ExcelSheet;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Created by fengyiming on 2017/12/13.
 */
public class ExcelUtils {

    /**
     * 根据List生成HSSFWorkbook
     * @param excelDataList
     * @return
     */
    public static HSSFWorkbook getExportExcelForSingle(List<MarketingExcelExport> excelDataList) {
        List<List<MarketingExcelExport>> excelDataListList = Arrays.asList(excelDataList);
        return getExportExcel(excelDataListList);
    }

    /**
     * 根据List生成HSSFWorkbook
     * @param excelDataList
     * @return
     */
    public static HSSFWorkbook getExportExcel(List<List<MarketingExcelExport>> excelDataList) {
        try {
            if (CollectionUtils.isEmpty(excelDataList) || CollectionUtils.isEmpty(excelDataList.get(0))) {
                return null;
            }
            MarketingExcelExport excelInterface = excelDataList.get(0).get(0);
            Class excelClass = excelInterface.getClass();
            ExcelSheet excelSheet = (ExcelSheet) excelClass.getDeclaredAnnotation(ExcelSheet.class);
            Field[] fields = excelClass.getDeclaredFields();
            HSSFWorkbook workbook = new HSSFWorkbook();
            int sheetSize = excelDataList.size();
            for (int index = 1; index <= sheetSize; index++) {
                HSSFSheet sheet = workbook.createSheet(String.format(excelSheet.sheetName(), index));
                setHeadRow(sheet, fields);
                List<MarketingExcelExport> subList = excelDataList.get(index - 1);
                setSheetData(subList, sheet);
            }
            return workbook;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置单页数据
     * @param excelDataList
     * @param sheet
     * @throws IllegalAccessException
     */
    private static void setSheetData(List<MarketingExcelExport> excelDataList, HSSFSheet sheet) throws IllegalAccessException {
        for (int dataIndex = 1; dataIndex <= excelDataList.size(); dataIndex++) {
            Object dataClass = excelDataList.get(dataIndex - 1);
            Field[] dataFields = dataClass.getClass().getDeclaredFields();
            Row headRow = sheet.createRow(dataIndex);
            for (int fieldIndex = 0; fieldIndex < dataFields.length; fieldIndex++) {
                Field field = dataFields[fieldIndex];
                Cell cell = headRow.createCell(fieldIndex);
                field.setAccessible(true);
                cell.setCellValue(field.get(dataClass).toString());
            }
        }
    }

    /**
     * 设置单行数据
     * @param sheet
     * @param fields
     */
    private static void setHeadRow(HSSFSheet sheet, Field[] fields) {
        Row headRow = sheet.createRow(0);
        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            ExcelCell excelCell = field.getDeclaredAnnotation(ExcelCell.class);
            Cell cell = headRow.createCell(index);
            cell.setCellValue(excelCell.cellHeadame());
        }
    }

    /**
     * 将数据转化为string
     * @param wb
     * @return
     */
    public static String excelToBase64(HSSFWorkbook wb) {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        try {
            wb.write(baOutputStream);
        } catch (IOException e) {
            TraceUtils.incr(MetricEnum.EXPORT_EXCEL_ERROR);
            return null;
        }
        return Base64.getEncoder().encodeToString(baOutputStream.toByteArray());
    }
}
