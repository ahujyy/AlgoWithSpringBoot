package edjon.example.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    Task findById(long id);

//    Task updateTask(Long id, String name, String description);
}
