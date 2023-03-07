package tn.esprit.marketplace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.marketplace.entities.Favorite;

@Repository
public interface FavoriteReopository extends JpaRepository<Favorite,Long> {


}