package com.VO;

import lombok.Data;

@Data
public class FileVO {

    private Integer fileInfoSeq;

    private String mdiType;

    private Integer fkSeq;

    private Integer sort;

    private String gubun;

    private String fileUrl;

    private String fileNm;

    private String fileOriNm;

    private String filePath;

    private String fileFullPath;

    private String fileExt;

    private Long fileSize;

    private String fileDesc;

    private String delYn;

    private String thumbYn;

    private String regDate;

    private String register;

    private String updDate;

    private String updater;

    private String replaceStr;

    private Integer downloadCnt;

    private Integer limit;

}
