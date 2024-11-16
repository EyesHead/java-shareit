package ru.practicum.shareit.request.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.entity.User;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    User requestor;
}

//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "comments")
//public class Comment {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "item_id", nullable = false)
//    private Item item;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "author_id", nullable = false)
//    private User author;
//
//    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
//    private String text;
//
//    @Column(name = "created_at", nullable = false)
//    @Builder.Default
//    private LocalDateTime created = LocalDateTime.now();
//
//    @PrePersist
//    public void prePersist() {
//        if (created == null) {
//            created = LocalDateTime.now();
//        }
//    }
//}
