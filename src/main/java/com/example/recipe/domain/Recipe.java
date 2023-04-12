package com.example.recipe.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @ManyToMany
    @ToString.Exclude
    List<Ingredient> ingredients;

    @OneToMany
    @ToString.Exclude
    List<Instruction> instructions;

    RecipeType type;

    int portion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Recipe recipe = (Recipe) o;
        return getId() != null && Objects.equals(getId(), recipe.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
