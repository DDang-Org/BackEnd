package com.ddang.global.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {

        if(multipartFile == null || multipartFile.isEmpty()){
            return null;
        }

        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new S3Exception(ErrorCode.FILE_TRANSACTION_FAIL));

        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName){

        String fileName = setFileName(dirName);

        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile); // convert() 과정에서 로컬에 생성된 파일 삭제

        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName){

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead) // PublicRead 권한으로 upload
        );

        return amazonS3Client.getUrl(bucket, fileName).toString(); // File의 URL return
    }

    private void removeNewFile(File targetFile){

        String name = targetFile.getName();

        // convert() 과정에서 로컬에 생성된 파일을 삭제
        if (targetFile.delete()){
            log.info(name + "파일 삭제 완료");
        } else {
            log.info(name + "파일 삭제 실패");
        }
    }

    public Optional<File> convert(MultipartFile multipartFile) throws IOException{

        // 기존 파일 이름으로 새로운 File 객체 생성
        // 해당 객체는 프로그램이 실행되는 로컬 디렉토리(루트 디렉토리)에 위치하게 됨
        File convertFile = new File(multipartFile.getOriginalFilename());

        if (convertFile.createNewFile()){ // 해당 경로에 파일이 없을 경우, 새 파일 생성

            try (FileOutputStream fos = new FileOutputStream(convertFile)) {

                // multipartFile의 내용을 byte로 가져와서 write
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }

        // 새파일이 성공적으로 생성되지 않았다면, 비어있는 Optional 객체를 반환
        return Optional.empty();
    }

    private String setFileName(String dirName){
        String fileName = dirName + "/" + UUID.randomUUID();

        if(amazonS3Client.doesObjectExist(bucket, fileName)){
            fileName = dirName + "/" + UUID.randomUUID();
        }

        return fileName;
    }

    public boolean uploadChatFile(String keyName, File file) {
        try {
            PutObjectRequest request = new PutObjectRequest(bucket, keyName, file)
                    .withCannedAcl(CannedAccessControlList.Private);
            amazonS3Client.putObject(request);

            log.info("파일 업로드 성공: 버킷={}, 키={}", bucket, keyName);
            return true;
        } catch (Exception e) {
            log.error("파일 업로드 실패: 버킷={}, 키={}, 에러={}", bucket, keyName, e.getMessage());
            throw new S3Exception(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    public File downloadChatFile(String keyName) {
        try {
            File tempFile = File.createTempFile("s3-download-", "-" + keyName.replaceAll("/", "_"));
            GetObjectRequest request = new GetObjectRequest(bucket, keyName);
            amazonS3Client.getObject(request, tempFile);

            log.info("파일 다운로드 성공: 버킷={}, 키={}, 경로={}", bucket, keyName, tempFile.getAbsolutePath());
            return tempFile;
        } catch (Exception e) {
            log.error("파일 다운로드 실패: 버킷={}, 키={}, 에러={}", bucket, keyName, e.getMessage());
            throw new S3Exception(ErrorCode.FILE_DOWNLOAD_FAIL);
        }
    }
}
