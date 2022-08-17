package group5.freelancejob.repositories;

import group5.freelancejob.daos.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByNameEqualsIgnoreCase(String name);
}
