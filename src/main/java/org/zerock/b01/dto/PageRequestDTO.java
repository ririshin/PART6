package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO { // -- 페이징 관련 정보(page/size), 검색 종류(type), keyword

    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;

    private String type; // -- 검색 종류 t, c, w, tc, tw, twc
    private String keyword;

    private String link;

    public String[] getTypes() { // -- type이라는 문자열을 배열로 반환
        if(type == null || type.isEmpty())
            return null;
        return type.split("");
    }

    public Pageable getPageable(String...props) { // -- 페이징 처리 위한 페이징 정보 객체 반환
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending());
    }

    public String getLink() { // -- 검색, 페이징 조건이 유지될 수 있도록 링크 생성
        if(link == null) {
            StringBuilder builder = new StringBuilder();

            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }

            if (keyword != null) {
                try{
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                }catch (UnsupportedEncodingException e) {}
            }

            link = builder.toString();
        }

        return link;
    }
}
