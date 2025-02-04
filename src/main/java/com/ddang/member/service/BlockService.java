package com.ddang.member.service;

import com.ddang.member.service.response.BlockListResponse;
import com.ddang.member.service.response.BlockResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface BlockService {

    BlockResponse createBlock(Long blockerId, Long blockedId);

    Slice<BlockListResponse> getBlockList(Long memberId, Pageable pageable);

    void deleteBlock(Long blockId);
}
