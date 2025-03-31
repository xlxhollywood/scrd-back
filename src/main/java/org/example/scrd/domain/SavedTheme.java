package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.scrd.BaseEntity;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "themeId"})
        }
)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SavedTheme extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "themeId", nullable = false)
    private Theme theme;
}
