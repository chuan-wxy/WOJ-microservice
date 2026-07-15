package com.chuan.wojmodel.pojo.dto.judge;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 判题上下文
 */
@Data
public class JudgeContextDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long problemSubmitId;

    private Long problemId;

    private String problemCode;

    private Long userId;

    private String language;

    private String code;

    private Integer timeLimit;

    private Integer memoryLimit;

    private Integer stackLimit;

    private String judgeMode;

    private String spjCode;

    private String spjLanguage;

    private Integer isFileIo;

    private String ioReadFileName;

    private String ioWriteFileName;
}
