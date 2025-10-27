package com.example.Cleanus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

// 자바에서 블록체인과 통신할 수 있는 클라이언트 라이브러리인 Web3j를 전역 Bean으로 등록하는 설정 파일.
@Configuration
public class BlockchainConfig {

    @Bean
    public Web3j web3j(@Value("${cleanus.chain.rpc-url}") String rpcUrl) {
        return Web3j.build(new HttpService(rpcUrl));
    }
}
