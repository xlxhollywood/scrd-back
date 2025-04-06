package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.scrd.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "party_post")
public class PartyPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int maxParticipants;
    private int currentParticipants;
    private LocalDateTime deadline;
    private boolean isClosed;

    @OneToMany(mappedBy = "partyPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyJoin> joins = new ArrayList<>();

    public void increaseParticipantCount() {
        this.currentParticipants++;
        if (this.currentParticipants >= this.maxParticipants) {
            this.isClosed = true;
        }
    }

    public void decreaseParticipantCount() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
            this.isClosed = false;
        }
    }
}
