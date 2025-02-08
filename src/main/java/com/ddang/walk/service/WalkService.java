package com.ddang.walk.service;

import com.ddang.member.entity.Member;
import com.ddang.walk.service.request.CompleteWalkServiceRequest;
import com.ddang.walk.service.response.walk.CompleteWalkResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WalkService {

    void startWalk(Member member, List<Long> dogId);
    CompleteWalkResponse completeWalk(Member member, CompleteWalkServiceRequest completeWalkServiceRequest, MultipartFile walkImgFile) throws IOException;
}
