package com.ddang.walk.service;

import com.ddang.member.entity.Member;
import com.ddang.walk.service.request.CompleteWalkServiceRequest;
import com.ddang.walk.service.response.walk.CompleteWalkResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface WalkService {

    CompleteWalkResponse completeWalk(Member member, CompleteWalkServiceRequest completeWalkServiceRequest, MultipartFile walkImgFile) throws IOException;
}
