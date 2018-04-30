package com.fym.utils;

import com.fym.utils.base.MarketingExcelExport;
import com.fym.utils.test.AudienceShopInfoDTO;
import com.fym.utils.common.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyiming on 2018/4/27.
 * 基于注解的DTO类导出excel
 */
public class Test1 {

    public static void main(String[] args) {

        AudienceShopInfoDTO audienceShopInfoDTO = new AudienceShopInfoDTO();
        audienceShopInfoDTO.setShopId(123L);
        audienceShopInfoDTO.setApplyStatus(false);
        audienceShopInfoDTO.setShopName("213123");

        List<MarketingExcelExport> single = new ArrayList<>(1);
        single.add(audienceShopInfoDTO);
        HSSFWorkbook hssfWorkbook = ExcelUtils.getExportExcelForSingle(single);

        List<List<MarketingExcelExport>> many = new ArrayList<>(1);
        many.add(single);
        HSSFWorkbook hssfWorkbook2 = ExcelUtils.getExportExcel(many);
        System.out.println(ExcelUtils.excelToBase64(hssfWorkbook));
        System.out.println(ExcelUtils.excelToBase64(hssfWorkbook2));
    }
}
