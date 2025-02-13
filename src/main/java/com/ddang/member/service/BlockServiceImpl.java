package com.ddang.member.service;

import com.ddang.global.exception.BlockException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.global.exception.MemberException;
import com.ddang.global.service.RedisService;
import com.ddang.member.entity.Block;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.BlockRepository;
import com.ddang.member.repository.MemberRepository;
import com.ddang.member.service.response.BlockListResponse;
import com.ddang.member.service.response.BlockResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ddang.global.service.RedisKey.BLOCK_LIST_KEY;
import static com.ddang.global.service.RedisKey.WALK_DOG_KEY;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BlockServiceImpl implements BlockService {

    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;
    private final RedisService redisService;

    @Override
    public BlockResponse createBlock(Long blockerId, Long blockedId) {
        Member blocker = findMemberById(blockerId);
        Member blocked = findMemberById(blockedId);

        validateBlockRequest(blocker, blocked);
        saveBlockedMemberIfWalking(blocker.getEmail(), blocked.getEmail());

        Block newBlock = Block.of(blocker, blocked);
        blockRepository.save(newBlock);

        return BlockResponse.from(newBlock);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<BlockListResponse> getBlockList(Long memberId, Pageable pageable) {
        Member member = findMemberById(memberId);

        Slice<Block> blocks = blockRepository.findAllByBlocker(member, pageable);

        return blocks.map(BlockListResponse::from);
    }

    @Override
    public void deleteBlock(Long blockId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new BlockException(ErrorCode.BLOCK_NOT_FOUND));

        blockRepository.delete(block);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateBlockRequest(Member blocker, Member blocked) {
        if (isSameFamily(blocker, blocked)) {
            throw new BlockException(ErrorCode.BLOCKED_MEMBER_IS_FAMILY_MEMBER);
        }

        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            throw new BlockException(ErrorCode.ALREADY_BLOCKED_MEMBER);
        }
    }

    private boolean isSameFamily(Member blocker, Member blocked) {
        if (blocker.getFamily() == null || blocked.getFamily() == null) {
            return false;
        }
        return blocker.getFamily().getFamilyId().equals(blocked.getFamily().getFamilyId());
    }

    private void saveBlockedMemberIfWalking(String email, String blockedEmail){
        if(redisService.checkHasKey(WALK_DOG_KEY + email)){ // 산책 중인 멤버가 차단 한 경우
            redisService.setListValues(BLOCK_LIST_KEY + email,blockedEmail);
        }
    }
}
