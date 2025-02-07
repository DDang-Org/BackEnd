package com.ddang.walk.service;


import com.ddang.walk.service.request.DecisionWalkServiceRequest;
import com.ddang.walk.service.request.ProposalWalkServiceRequest;
import com.ddang.walk.service.request.StartWalkServiceRequest;

public interface WalkLocationService {

    void startWalk(String email, StartWalkServiceRequest startWalkServiceRequest);

    void proposalWalk(String email, ProposalWalkServiceRequest proposalWalkServiceRequest);

    void decisionWalk(String email, DecisionWalkServiceRequest service);

    void startWalkWith(String email, StartWalkServiceRequest service);
}
