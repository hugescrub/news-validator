package net.hugescrub.newsservice.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatchArticleRequest {
    private String classificationResult;

    private Boolean isApproved;

    private Boolean isFake;

    private Long classificationId;
}
