package com.app.oauth2.authentication.application;

import org.springframework.stereotype.Service;

import com.app.oauth2.authentication.domain.AuthTokens;
import com.app.oauth2.authentication.domain.AuthTokensGenerator;
import com.app.oauth2.authentication.domain.oauth.OAuthInfoResponse;
import com.app.oauth2.authentication.domain.oauth.OAuthLoginParams;
import com.app.oauth2.authentication.domain.oauth.RequestOAuthInfoService;
import com.app.oauth2.member.domain.Member;
import com.app.oauth2.member.domain.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final MemberRepository memberRepository = null;
    private final AuthTokensGenerator authTokensGenerator = new AuthTokensGenerator();
    private final RequestOAuthInfoService requestOAuthInfoService = null;

    public AuthTokens login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long memberId = findOrCreateMember(oAuthInfoResponse);
        return authTokensGenerator.generate(memberId);
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(Member::getId)
                .orElseGet(() -> newMember(oAuthInfoResponse));
    }

    private Long newMember(OAuthInfoResponse oAuthInfoResponse) {
        Member member = Member.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickname())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .build();

        return memberRepository.save(member).getId();
    }
}