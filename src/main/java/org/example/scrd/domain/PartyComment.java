package org.example.scrd.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.scrd.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "party_comment")
@Getter
@Setter
@NoArgsConstructor
public class PartyComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PartyPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PartyComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyComment> children = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


}

