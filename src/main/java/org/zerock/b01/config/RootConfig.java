package org.zerock.b01.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // -- 해당 클래스가 스프링 설정 클래스임을 명시
public class RootConfig {

    @Bean // -- ModelMapper를 Spring bean으로 설정
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper(); // -- modelMapper 객체 생성

        modelMapper.getConfiguration() // -- 환경설정
                .setFieldMatchingEnabled(true) // -- 필드 매칭 활성화
                .setFieldAccessLevel(org.modelmapper.config.Configuration
                        .AccessLevel.PRIVATE) // -- private 필드에도 접근 가능하도록 설정
                .setMatchingStrategy(MatchingStrategies.STRICT); // -- STRICT(엄격한) 전략

        return modelMapper;
    }

}
