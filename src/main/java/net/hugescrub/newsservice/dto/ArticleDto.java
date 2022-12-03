package net.hugescrub.newsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hugescrub.newsservice.model.Source;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleDto {
    private String title;
    private String body;
    private Source source;
}
