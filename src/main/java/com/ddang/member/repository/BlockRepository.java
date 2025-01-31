package com.ddang.member.repository;

import com.ddang.member.entity.Block;
import com.ddang.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

    @EntityGraph(attributePaths = {"blocked"}, type = EntityGraph.EntityGraphType.FETCH)
    Slice<Block> findAllByBlocker(Member blocker, Pageable pageable);
}
