package com.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.hsqldb.lib.FileUtil;
import com.VO.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WebFileUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static int fileNameSeq;


    private static String getFileNameSeq() {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
        return dataFormat.format(new Date()) + String.format("%03d", (++fileNameSeq % 1000));

    }

    /**
     * 파일 업로드
     * @param uploadFile 파일
     * @param imagePath 파일 저장 디렉토리
     * @param uploadPath 업로드 경로
     * @param pathType 업로드 타입[ex) temp, contents]
     * @return FileVO
     * @throws IOException
     */
    public FileVO uploadFile(MultipartFile uploadFile, String imagePath, String uploadPath, String pathType) throws IOException {
        if (uploadFile.isEmpty()) {
            return null;
        }

        FileVO fileDetailVO = new FileVO();
        try {
            fileDetailVO.setFileOriNm(uploadFile.getOriginalFilename());
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            fileDetailVO.setFilePath(imagePath + "/" + pathType);
            String filePrefix = fileDetailVO.getFileOriNm().substring(fileDetailVO.getFileOriNm().lastIndexOf("."));
            fileDetailVO.setFileNm(getFileNameSeq() + filePrefix);
            fileDetailVO.setFileExt(filePrefix);
            fileDetailVO.setFileFullPath(uploadPath + imagePath + "/" + pathType + "/" + fileDetailVO.getFileNm());
            fileDetailVO.setFileSize(uploadFile.getSize());


            FileUtil.makeDirectories(uploadPath + imagePath + "/" + pathType);
            File file = new File(uploadPath + imagePath + "/" + pathType + "/" + fileDetailVO.getFileNm());

            /*jeus 업로드 경로 -anyccall*/
            //uploadFile.transferTo(file);
            org.springframework.util.FileCopyUtils.copy(uploadFile.getBytes(), file);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }// try end;

        return fileDetailVO;
    }

    /**
     * 임시파일 이동
     * @param filePath 파일 경로
     * @param replaceStr 옮길 디렉토리 명
     */
    public void tempFileMove (String filePath, String replaceStr) {
        File file = new File(filePath);
        File newFile = new File(filePath.replace("/temp", ("/" + replaceStr)));

        try {
            FileUtils.moveFile(file, newFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 파일 삭제
     * @param filePath 파일 경로
     * @return boolean
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * 파일 다운로드
     * @param request
     * @param response
     * @param file 실제 파일 명
     * @param fileName 다운로드 할 파일 명
     * @param downloadPath 파일 경로
     * @throws Exception
     */
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, String file, String fileName, String downloadPath) throws Exception {
        //file.replaceAll("", "");
        logger.info("==============================");
        logger.info(downloadPath+file);
        logger.info("==============================");
        File f = new File(downloadPath+file);
        //System.out.println(downloadPath+file);
        downloadFile(request, response, f, fileName);
    }

    /**
     * 실파일 다운로드
     * @param request
     * @param response
     * @param file 파일
     * @param fileName 파일 명
     * @throws UnsupportedEncodingException
     */
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, String fileName) throws UnsupportedEncodingException {

        try {
            int size = (int) file.length();

            if (size > 0) {
                String encodedFileName = "attachment; filename*=" + "UTF-8" + "''" + URLEncoder.encode(fileName, "UTF-8");

                response.setContentType("application/octet-stream; charset=utf-8");

                response.setHeader("Content-Disposition", encodedFileName);

                response.setContentLengthLong(size);

                BufferedInputStream in = null;
                BufferedOutputStream out = null;

                in = new BufferedInputStream(new FileInputStream(file));

                out = new BufferedOutputStream(response.getOutputStream());

                try {
                    byte[] buffer = new byte[4096];
                    int bytesRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    out.flush();
                } finally {
                    in.close();
                    out.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
