package com.fym.utils.test;

import lombok.Data;
import com.fym.utils.interfaces.ExcelCell;
import com.fym.utils.interfaces.ExcelSheet;
import com.fym.utils.interfaces.MarketingExcelExport;

/**
 * Created by fengyiming on 2017/6/27.
 */
@Data
@ExcelSheet(sheetName = "第%s推送报名统计")
public class AudienceShopInfoDTO implements MarketingExcelExport {

    /**
     * 推送店铺id
     */
    @ExcelCell(cellHeadame = "店铺id")
    private Long shopId;

    /**
     * 推送店铺名称
     */
    @ExcelCell(cellHeadame = "店铺名称", width = 300)
    private String shopName;

    /**
     * 是否报名
     */
    @ExcelCell(cellHeadame = "是否报名")
    private Boolean applyStatus;
}
