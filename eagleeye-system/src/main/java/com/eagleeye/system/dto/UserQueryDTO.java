package com.eagleeye.system.dto;

import com.eagleeye.common.base.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询参数")
public class UserQueryDTO extends BaseQueryDTO {

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "真实姓名（模糊查询）")
    private String realName;

    @Schema(description = "邮箱（模糊查询）")
    private String email;

    @Schema(description = "手机号（模糊查询）")
    private String phone;

    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "用户状态：0-禁用，1-启用")
    private Integer status;
}
