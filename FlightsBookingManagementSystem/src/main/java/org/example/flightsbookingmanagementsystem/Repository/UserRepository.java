package org.example.flightsbookingmanagementsystem.Repository;


import org.example.flightsbookingmanagementsystem.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserById(Integer id);

    User findUserByUsernameAndPassword(String username, String password);

    User findUserByEmailAndPassword(String email, String password);
}
