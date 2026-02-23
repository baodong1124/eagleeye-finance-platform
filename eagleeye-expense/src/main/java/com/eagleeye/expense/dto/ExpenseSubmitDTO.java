package com.eagleeye.expense.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报销单提交DTO
 */
@Data
@Schema(description = "报销单提交请求")
public class ExpenseSubmitDTO {

    @Schema(description = "报销类型：1-差旅费，2-办公费，3-招待费，4-其他")
    @NotNull(message = "报销类型不能为空")
    private Integer expenseType;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "报销日期")
    private LocalDateTime expenseDate;

    @Schema(description = "报销说明")
    @NotBlank(message = "报销说明不能为空")
    private String description;

    @Schema(description = "报销明细列表")
    @NotNull(message = "报销明细不能为空")
    private List<ExpenseItemDTO> items;

    @Schema(description = "附件URL列表")
    private List<String> attachmentUrls;

    @Schema(description = "备注")
    private String remark;

    /**
     * 报销明细DTO
     */
    @Data
    @Schema(description = "报销明细")
    public static class ExpenseItemDTO {

        @Schema(description = "费用名称")
        @NotBlank(message = "费用名称不能为空")
        private String itemName;

        @Schema(description = "费用金额")
        @NotNull(message = "费用金额不能为空")
        private BigDecimal amount;

        @Schema(description = "费用日期")
        private String expenseDate;

        @Schema(description = "发票类型：1-增值税专用发票，2-增值税普通发票，3-其他")
        private Integer invoiceType;

        @Schema(description = "发票号码")
        private String invoiceNo;

        @Schema(description = "发票图片URL")
        private String invoiceImage;

        @Schema(description = "备注")
        private String remark;
    }
}
