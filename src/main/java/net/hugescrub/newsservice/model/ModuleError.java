package net.hugescrub.newsservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "errors")
public class ModuleError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Integer responseCode;

    private LocalDateTime timestamp;

    public ModuleError(String description, Integer responseCode, LocalDateTime timestamp) {
        this.description = description;
        this.responseCode = responseCode;
        this.timestamp = timestamp;
    }
}
