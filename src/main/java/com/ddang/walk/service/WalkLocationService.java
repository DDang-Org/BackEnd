package com.ddang.walk.service;


import com.ddang.walk.service.request.DecisionWalkServiceRequest;
import com.ddang.walk.service.request.ProposalWalkServiceRequest;
import com.ddang.walk.service.request.WalkServiceRequest;

public interface WalkLocationService {

    void startWalk(String email, WalkServiceRequest walkServiceRequest);

    void proposalWalk(String email, ProposalWalkServiceRequest proposalWalkServiceRequest);

    void decisionWalk(String email, DecisionWalkServiceRequest service);

    void startWalkWith(String email, WalkServiceRequest service);
}
